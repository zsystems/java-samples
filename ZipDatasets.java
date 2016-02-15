/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2008. All Rights Reserved
 * 
 * DISCLAIMER: 
 * The following [enclosed] code is sample code created by IBM 
 * Corporation.  This sample code is not part of any standard IBM product 
 * and is provided to you solely for the purpose of assisting you in the 
 * development of your applications.  The code is provided 'AS IS', 
 * without warranty of any kind.  IBM shall not be liable for any damages 
 * arising out of your use of the sample code, even if they have been 
 * advised of the possibility of such damages.
 * =========================================================================
 */
package com.ibm.jzos.sample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import com.ibm.jzos.ZFile;

/**
 * A sample Java main class that can be invoked to create a Zip archive from one
 * or more datasets or PDS members.  Datasets are treated as text and converted
 * from EBCDIC to ASCII (ISO8859-1) or to the specified target codepage.
 * 
 * <p>
 * Details on the input arguments to this class are available by executing
 * the main method with no arguments. (see {@link #usage()})
 * </p>
 * 
 * <p>
 * Example: Zip several partitioned datasets to a Unix zip file:
 * <pre>com.ibm.jzos.sample.ZipDatasets test.zip sys1.proclib(asm*) hlq.**.jcl </pre>
 * </p>
 * 
 * <p>
 * Example: Zip all datasets matching two patterns to a dataset:
 * <pre>com.ibm.jzos.sample.ZipDatasets //hlq.backup.zip payroll.*.data gl.**.dat*</pre>
 * </p>
 * 
 * <p>
 * Example: Zip data using DDs and input and output:
 * <pre>com.ibm.jzos.sample.ZipDatasets //DD:ZIPOUT //DD:INSEQ1 //DD:INPDS1 //DD:INPDS2(FOO*)</pre>
 * </p>
 * 
 * @see com.ibm.jzos.ZFile
 * @see com.ibm.jzos.ZUtil
 * @see com.ibm.jzos.CatalogSearch
 * @see java.util.zip.ZipOutputStream java.util.zip.ZipOutputStream
 * @see java.util.regex.Pattern java.util.regex.Pattern
 * @see java.util.regex.Matcher java.util.regex.Matcher
 * @see ZipDatasetSource ZipDatasetSource (the class which creates Zip archive entries for each input dataset/pattern)
 *  
 * @since 2.3.0
 */
public class ZipDatasets {

	/**
	 * Display usage syntax for invoking this class as a java main() method.
	 */
	public static void usage() {
		System.err.println("Usage: com.ibm.jzos.sample.ZipDatasets [-t targetEncoding] outfile indsname...");
		System.err.println("  where:  ");
		System.err.println("  -t targetEncoding can optionally specify the codepage name to encode the");
		System.err.println("      text data as it is written to the Zip file.  If not specified,");
		System.err.println("      this defaults to ISO8859-1 (Latin/ASCII)");
		System.err.println("  and outfile is either:");
		System.err.println("    - a Unix file path name: /path/to/some/file.zip");
		System.err.println("    - a dataset name:  //A.B.C");
		System.err.println("    - a PDS member name:  //A.B.C(MEM)");
		System.err.println("    - a DD name: //DD:XYZ");
		System.err.println("    - a DD name and member:  //DD:XYZ(MEM)");
		System.err.println("  and each (at least one) indsname is either:");
		System.err.println("    - a dataset name:  //A.B.C");
		System.err.println("    - a dataset pattern:  //A.*.D");
		System.err.println("    - a PDS member name:  //A.B.C(MEM)");
		System.err.println("    - a PDS member pattern:  //A.B.C(D*X)");
		System.err.println("    - a DD name: //DD:XYZ");
		System.err.println("    - a DD name and member:  //DD:XYZ(MEM)");
		System.err.println("    - a DD name and member pattern:  //DD:XYZ(D*X)");
		System.err.println("  \"//\" prefixes may be omitted from indsnames");			
		System.err.println("  All dataset names are assumed to be fully qualified.");
	}
	
	/**
	 * A Java main method for invoking this class.
	 * @param args
	 * @throws Exception
	 * @see ZipDatasets#usage()
	 */
	public static void main(String[] args) throws Exception {
		String targetEncoding = null;
		int iOutfileName = 0;

		// need at least one outdsname and one indsname
		if (args == null || args.length < 2) {
			usage();
			System.exit(12);
		}
		
		// handle the -t targetEncoding option
		if (args[0].equals("-t")) {
			targetEncoding = args[1];
			iOutfileName = 2;
		}
		// still need at least one outdsname and one indsname
		if (args.length < iOutfileName + 2) {
			usage();
			System.exit(12);			
		}
		
		// copy the indsnames to a separate String[] 
		String[] indsnames = new String[args.length - iOutfileName - 1];
		System.arraycopy(args, iOutfileName+1, indsnames, 0, indsnames.length);
		
		// construct an instance and run it
		ZipDatasets instance = new ZipDatasets(args[iOutfileName], indsnames);
		if (targetEncoding != null) {
			instance.setTargetEncoding(targetEncoding);
		}
		int errors = instance.run();
		if (errors > 0) {
			System.exit(8);
		}
	}

	public static final String DEFAULT_TARGET_ENCODING = "ISO8859-1";
	
	private String outFileName;
	private String[] indsnames;
	private String targetEncoding;
	private int errors = 0;

	/**
	 * Construct an instance
	 * @param outFileName the name of the /output/file, //DATASET, //DD:name, etc
	 * where the Zip archive is written.
	 * @param indsnames an array of input dataset names / patterns
	 * @see ZipDatasetSource for more details on allowed input dataset names
	 */
	public ZipDatasets(String outFileName, String[] indsnames) {
		this.outFileName = outFileName;
		this.indsnames = indsnames;
		this.targetEncoding = DEFAULT_TARGET_ENCODING;
	}
	
	/**
	 * Sets the name of the codepage used to encode the text data written
	 * to the Zip file.  If not called, defaults to {@link #DEFAULT_TARGET_ENCODING}.
	 * @param targetEncoding
	 */
	public void setTargetEncoding(String targetEncoding) {
		this.targetEncoding = targetEncoding;
	}
	
	/**
	 * Process the given input datasets and create a Zip archive on the
	 * given output file or dataset.
	 */
	public int run() throws IOException {
		ZipOutputStream zipOutStream = openZipOutputStream();
		try {
			processInputFiles(zipOutStream);
			zipOutStream.finish();
			System.out.println("   done: " + errors + " errors");
			return errors;
		} finally {
			try {
				zipOutStream.close();
			} catch (Throwable ignore) {}
		}
	}
	
	/**
	 * Open a ZipOutputStream on the given target file or dataset.
	 * @return ZipOutputStream
	 * @throws IOException
	 */
	private ZipOutputStream openZipOutputStream() throws IOException {
		// First create a (binary) OutputStream on either a Unix file
		// or dataset, depending on the name given.
		OutputStream os;
		if (outFileName.startsWith("//"))  {
			os = openDataset(outFileName);
		} else {
			os = new FileOutputStream(outFileName);
		}
		// Wrap the OutputStream in a new ZipOutputStream
		return new ZipOutputStream(os);
	}

	/**
	 * Given a "//data.set" or "//DD:name" or even //data.set(member)
	 * or "//DD:name(member), open a ZFile on it in binary streaming mode.
	 * The dataset is opened with DCB RECFM=VB,LRECL=1028,BLKSIZE=0.
	 * mode and return it as an {@link OutputStream}.
	 * Dataset names are assumed to be fully qualified.
	 * 
	 * @param name the dataset name
	 * @return OutputStream
	 * @throws IOException
	 * @see ZFile#getSlashSlashQuotedDSN(String, boolean)
	 */
	private OutputStream openDataset(String name) throws IOException {	
		
		ZFile zfile = new ZFile(
							ZFile.getSlashSlashQuotedDSN(name, true),
							"wb,recfm=vb,lrecl=1028,blksize=0"); 
		return zfile.getOutputStream();
	}
	
	/**
	 * Loop over each input dataset name (or pattern) given and
	 * add entries for it to the Zip file.
	 * If an exception occurs processing one entry, try to clean it
	 * up and continue with the next entry. 
	 * The {@link ZipDatasetSource} class does all of the real work.
	 * <p/>
	 * @param zipOutStream
	 */
	private void processInputFiles(ZipOutputStream zipOutStream) {
		for (int i=0; i<indsnames.length; i++) {
			String inputName = indsnames[i];
			ZipDatasetSource source = new ZipDatasetSource(inputName);
			try {
				source.addTo(zipOutStream, targetEncoding);
			} catch( Throwable t) {
				errors++;
				try { 
					zipOutStream.closeEntry(); 
				} catch (IOException ignore) {}
				System.out.println(">>> Error occuring processing input dataset: " + inputName);
				System.err.println(">>> Error occuring processing input dataset: " + inputName);
				t.printStackTrace();
			}
		}
	}
}
