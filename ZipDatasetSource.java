/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * Copyright IBM Corp. 2008. All Rights Reserved
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.jzos.CatalogSearch;
import com.ibm.jzos.CatalogSearchField;
import com.ibm.jzos.PdsDirectory;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZUtil;

/**
 * A class that is used by {@link ZipDatasets} to handle the creation of
 * zip file entries for z/OS datasets.
 * <p/>
 * Instances of this class are constructed using a dataset name or pattern, 
 * which can include:
 * <ul>
 * <li>A sequential dataset or PDS name: //A.B.C </li>
 * <li>A dataset pattern name: //A.*.D  
 *     as defined by the z/OS Catalog Search facility (IGGCSI00).
 *     See {@link CatalogSearch} for more information. </li>
 * <li>A PDS member name: //A.B.C(MEM) </li>
 * <li>A PDS member pattern: //A.B.C(D*X) </li>
 * <li>A DD name: //DD:XYZ  which might refer to a sequential dataset, or PDS, 
 * 	   or concatenation. </li>
 * <li>A DD name and member: //DD:XYZ(MEM) </li>
 * <li>A DD name and member pattern: //DD:XYZ(D*X) </li>
 * </ul>
 * The leading "//" prefix may be omitted and names are case insensitive.
 * <p/>
 * Each dataset is zipped to the ZipOutputStream by reading the source
 * dataset as text encoded in the default 
 * EBCDIC codepage ({@link ZUtil#getDefaultPlatformEncoding()})
 * and then writing the text to ZipOutputStream encoded using
 * the supplied target encoding.
 * <p/>
 * The name given to each entry is the actual MVS dataset name in upper case.
 * If the entry is for a PDS member, then the dataset name is used as
 * a directory name followed by the member name as a file name.
 * <p/> 
 * @see ZipDatasets ZipDatasets the main class used to zip z/OS datasets
 * @see #addTo(ZipOutputStream, String)
 * @since 2.3.0
 */
public class ZipDatasetSource  {
		
	// Some constants used to build regular expression
	static final String SLASH_SLASH_PREFIX 	= "//";
	static final String DD_PREFIX 			= "DD:";
	static final String DSNAME_CHAR 		= "[\\w[#\\$\\.]]";
	static final String DSNAME_PATTERN_CHAR = "[\\w[#\\$\\.\\*]]";
	static final String MEMBER_CHAR 		= "[\\w[#\\$]";
	static final String MEMBER__PATTERN_CHAR = "[\\w[#\\$\\*]]";
	static final String DSNAME_PIECE 		= DSNAME_CHAR + "{1,44}";
	static final String DDNAME_PIECE 		= DD_PREFIX + "\\w{1,8}";

	/** A dataset pattern has at least one asterisk, but may not start with an asterisk */
	static final String DSNAME_PATTERN 		= "^" 
											+ DSNAME_CHAR 
											+ "+\\*" 
											+ DSNAME_PATTERN_CHAR 
											+ "*$";
	
	/** A member pattern is a dataset(pat) or DD:name(pat) where mpat is a member name 
	 	or pattern that includes an asterisk */
	static final String DSNAME_WITH_MEMBER_OR_PATTERN = 
											"^(" + DSNAME_PIECE	+ "|" + DDNAME_PIECE + ")"
											+ "\\("  
											+ "(" + MEMBER__PATTERN_CHAR + "+)"
											+ "\\)$";
	
	/** The buffer size used to read/write blocks of data */
	static final int BUFSIZE = 64 * 1024;

	/** 
	 *  The name of the input dataset source.  
	 *  Uppercased, with any "//" prefix removed.
	 */
	private String name;
	
	/**
	 * The name of the member or member pattern name, which is 
	 * extracted from the original dataset source name.
	 * May be null if no member name or pattern was given
	 */
	private String memberPattern;
		
	/**
	 * Construct an instance given a dataset/pattern name.
	 * We also convert the name to uppercase and drop any
	 * "//" prefix.
	 */
	public ZipDatasetSource(String nm) {
		name = nm.toUpperCase();
		if (name.startsWith(SLASH_SLASH_PREFIX)) {
			name = name.substring(SLASH_SLASH_PREFIX.length());
		}
	}

	/**
	 * Answer the dataset/pattern name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Add one or more entries to the given ZipOutputStream for the 
	 * dataset or datasets described by this ZipDatasetSource.
	 * <p/>
	 * @param zipOutStream the output ZipOutputStream
	 * @param targetEncoding the codepage used to encode the data written to the zipOutStream
	 * @throws IOException
	 */
	public void addTo(ZipOutputStream zipOutStream, String targetEncoding) throws IOException  {
		
		if (name.matches(DSNAME_PATTERN)) {
			// Process a dataset name that includes a pattern character ('*')
			addMatchingDatasets(zipOutStream, targetEncoding);
			
		} else {
			// If a member name or member pattern was given, 
			// split it out from the dataset name
			Pattern mempat = Pattern.compile(DSNAME_WITH_MEMBER_OR_PATTERN); 
			Matcher matcher = mempat.matcher(name);
			if (matcher.matches()) {
				name = matcher.group(1); // get the dsname | dd:name
				memberPattern = matcher.group(2);  // get the member/pattern
			}
			// Process the dataset, pds, or dataset(member)
			addDatasetOrPds(zipOutStream, targetEncoding);
		}
	}
	
	/**
	 * Add a single dataset, single member, complete PDS, or a pattern of PDS members
	 * to the given ZipOutputStream.
	 * <p/>
	 * @param zipOutStream the target ZipOutputStream
	 * @param targetEncoding the target text encoding
	 * @throws IOException
	 */
	private void addDatasetOrPds(ZipOutputStream zipOutStream, String targetEncoding) 
		throws IOException 
	{
		// allocate a DD to point to the base dsname (or return the name if //DD:name given)
		String ddname = allocDD();  
				
		// If we are given no member name or a member name pattern, 
		// we try to process the dataset as a PDS directory.
		// This fails if the dataset was not a PDS.
		PdsDirectory pdsDir = null;
		if (memberPattern == null || memberPattern.indexOf('*') >= 0) {
			try { 
				pdsDir = new PdsDirectory(SLASH_SLASH_PREFIX + DD_PREFIX + ddname);
			} catch (IOException ioe) { } // fall through with pdsDir == null
		}
		
		// If its not a PdsDirectory, then assume that it is a regular dataset or single member
		if (pdsDir == null) {
			addDatasetOrMember(zipOutStream, targetEncoding, ddname, memberPattern);
			return;
		}
		
		// Process a PDS directory...
		
		// If we are given a pattern string to filter members, then build
		// a regular expression to use.
		Pattern memberRegex = null;
		if (memberPattern != null) {
			memberRegex = makeRegexPattern(memberPattern);
		}
		
		// Loop over the entries in the directory and add all/matching
		// members to the zipOutStream
		try {
			for (Iterator i=pdsDir.iterator(); i.hasNext(); ) {
				PdsDirectory.MemberInfo member = (PdsDirectory.MemberInfo)i.next();
				String memberName = member.getName();
				if (memberRegex == null || memberRegex.matcher(memberName).matches()) {
					addDatasetOrMember(zipOutStream, targetEncoding, ddname, memberName);
				}
			}
		} finally {
			// Faithfully close the directory and free the DD when we are done,
			// even if an exception is thrown.
			try { 
				pdsDir.close(); 
			} catch (IOException ignore) {} 
			freeDD(ddname);
		}
	}		
		
	/**
	 * Add an single {@link ZipEntry} for a single dataset or member.
	 * 
	 * @param zipOutStream the target ZipOutputStream
	 * @param targetEncoding the target text encoding
	 * @param ddname the DD allocated to the dataset or member
	 * @param memberName the member name; used to create the entry name
	 * @throws IOException
	 */
	private void addDatasetOrMember(ZipOutputStream zipOutStream, String targetEncoding, 
										String ddname, String memberName) throws IOException {

		Reader reader = null;
		try {	
			reader = openInputFile(ddname, memberName);
			// Construct the name of the Zip entry that we will add
			String entryName = memberName == null 
								? name
								: name + "/" + memberName;
			// Start a new ZipEntry in the Zip file,
			// copy the dataset/member data into the Zip entry,
			// and close the entry
			ZipEntry entry = new ZipEntry(entryName);
			zipOutStream.putNextEntry(entry);
			copyData(reader, zipOutStream, targetEncoding);
			zipOutStream.closeEntry();

			System.out.println("  added: " + entryName 
								+ "  (" + entry.getSize() + " -> " + entry.getCompressedSize() + ")");
		} finally {
			closeInputFile(reader);
			freeDD(ddname);
		}
	}
	
	/**
	 * Given a dataset source name that included wild card ('*') characters, 
	 * use the z/OS CatalogSearch facility (IGGCSI00) to find and process all
	 * of the matching sequential or GDS datasets that match.
	 * @param zipOutStream
	 * @param targetEncoding
	 * @throws IOException
	 */
	private void addMatchingDatasets(ZipOutputStream zipOutStream, String targetEncoding) throws IOException {
		
		CatalogSearch catSearch = new CatalogSearch(name);
		catSearch.setEntryTypes("AH");  // only NON-VSAM and Generation Datasets
		catSearch.addFieldName("ENTNAME");
		catSearch.search();
		while (catSearch.hasNext()) {
			CatalogSearch.Entry entry = (CatalogSearch.Entry)catSearch.next();
			if (entry.isDatasetEntry()) {
				CatalogSearchField field = entry.getField("ENTNAME");
				String dsn = field.getFString().trim();
				// make a new ZipSource with the next dsn and add it
				ZipDatasetSource source = new ZipDatasetSource(dsn);
				source.addTo(zipOutStream, targetEncoding);
			}
		}		
	}

	/**
	 * Make a regular expression pattern than matches the given 
	 * member name pattern that includes literal characters and
	 * zero or more asterisks.
	 */
	private Pattern makeRegexPattern(String memberPattern) {

		StringBuffer patBuf = new StringBuffer("^");
		for (int i=0; i<memberPattern.length(); i++) {
			char c = memberPattern.charAt(i);
			switch (c) {
			case '*':
				patBuf.append(".*");
				break;
			case '$':
				patBuf.append("\\$");
				break;
			default:
				patBuf.append(c);
			}
		}
		patBuf.append('$');
		return Pattern.compile(patBuf.toString());
	}
	
	/**
	 * Copy data from a reader to a ZipOutputStream.
	 * @param reader a Reader open on the input dataset/member in the default EBCDIC encoding
	 * @param zipOutStream the target ZipOutputStram
	 * @param targetEncoding the target encoding for the ZipOutputStream
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void copyData(Reader reader, 
							ZipOutputStream zipOutStream, 
							String targetEncoding) 
		throws IOException, UnsupportedEncodingException 
	{
		char[] cbuf = new char[BUFSIZE];
		int nRead;
		// wrap the zipOutputStream in a Writer that encodes to the target encoding
		OutputStreamWriter osw = new OutputStreamWriter(zipOutStream, targetEncoding);
		while ((nRead = reader.read(cbuf)) != -1) {
			osw.write(cbuf, 0, nRead);
		}
		osw.flush(); // flush any buffered data to the ZipOutputStream
	}

	
	/**
	 * Allocate a new DD with DISP=SHR to point to the source dataset, 
	 * or of a DD:name was given, return the ddname.
	 * @return the ddname given/allocated
	 * @throws IOException
	 */
	private String allocDD() throws IOException {
		String ddname = null;
		// See if a DD:name was given
		if (name.startsWith(DD_PREFIX)) {
			ddname = name.substring(DD_PREFIX.length());
			return ddname;
		}
		// Otherwise we allocate a temporary DD to the given dataset
		// using DISP=SHR
		try {
			// get a new SYSnnnnnn DD name allocated to dummy
			ddname = ZFile.allocDummyDDName();
			// reallocate it to the dataset with DISP=SHR
			ZFile.bpxwdyn("alloc fi("+ddname+") da("+name+") shr reuse msg(2)");
			return ddname;
		} catch (RcException rce) {
			freeDD(ddname);  // free the temp dd 
			throw new IOException("Unable to allocate input dataset: "
					+ name
					+ " - "
					+ rce);
		}		
	}

	/**
	 * Do our best to free a DD that we allocated
	 * @param ddname
	 */
	private void freeDD(String ddname) {
		if (ddname == null || name.startsWith(DD_PREFIX)) {
			return;
		}
		try {
			// Omit the 'msg' keyword to suppress error messages.
			// We might not actually be able to free the DD if
			// if is still open as a PDS directory
			ZFile.bpxwdyn("free fi("+ddname+")");
		} catch(RcException ignore) {}
	}	

	/**
	 * Open a Reader to point to the previously allocated
	 * DD and optionally a given member.  The encoding for the
	 * reader is set to the default EBCDIC encoding 
	 * (see {@link ZUtil#getDefaultPlatformEncoding()})
	 * <p/>  
	 * @param ddname the DD allocated to the dataset
	 * @param memberName if not null, the member name to open
	 * @return a Reader
	 * @throws IOException
	 */
	private Reader openInputFile(String ddname, String memberName) throws IOException {

		String sourceEncoding = ZUtil.getDefaultPlatformEncoding();
		String filename = SLASH_SLASH_PREFIX + DD_PREFIX 
							+ ddname 
							+ (memberName==null ? "": "("+memberName+")");
		
		// We open the file in text mode, so that new-line characters are inserted
		// at record boundaries and trailing spaces are removed from records.
		ZFile zFile = new ZFile(filename, "rt");
		
		// If the open file's actual filename can be determined, use it as our name
		String actualFileName = zFile.getActualFilename();
		if (actualFileName != null) {
			name = actualFileName;
		}
		
		// Strip the member name off of the actual file name
		int ilparen = name.indexOf('(');
		if (ilparen > 0) {
			name = name.substring(0,ilparen);
		}
		
		InputStream is = zFile.getInputStream();
		return new InputStreamReader(is, sourceEncoding);		
	}

	/**
	 * Close the input reader.
	 */
	private void closeInputFile(Reader reader) {
		if (reader == null) return;
		try {
			reader.close();
		} catch (IOException ignore) {}
	}
	
	

}
