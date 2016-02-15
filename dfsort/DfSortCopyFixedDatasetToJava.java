/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2007. All Rights Reserved
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
package com.ibm.jzos.sample.dfsort;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.ibm.jzos.DfSort;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;

/**
 * This example creates an instance of {@link DfSort} to sort an existing Dataset
 * and read the sort output into Java.
 * <p>
 * Arguments (supplied as key=value pairs):
 * <dl>
 * <dt>sortin=&lt;dsn&gt;</dt>
 * <dd>The name of a RECFM=F dataset to sort.  Supplied to DFSORT as SORTIN.</dd>
 * <dt>[encoding=&lt;codepage&gt;]</dt>
 * <dd>The character set of the SORTIN dataset.  If not supplied, binary data is assumed.</dd>
 * <dt>[shareas=&lt;yes | no&gt;]</dt>
 * <dd>Determines that address space for DFSORT.  If yes, DFSORT will run in the same address
 * space as the JVM.  If no, it will run in a separate address space.</dd>
 * <dt>[loglevel=&lt;n&gt;]</dt>
 * <dd>Sets the logging level for the child process and prints the resulting
 * child process output is written to System.err.  The valid levels are those defined
 * in the ZUtil.LOG_* constants.</dd>
 * </dl>
 * <p>
 * Features illustrated:
 * <ul>
 * <li>Specify an existing RECFM=F existing dataset as input (SORTIN) to DFSORT
 * <li>Sort the records in Ascending order
 * <li>Read the DFSORT (SORTOUT) results and process in Java
 * </ul>
 */
public class DfSortCopyFixedDatasetToJava {

	public static void main(String[] args) throws Exception {
		DfSortArguments dfsortArgs = new DfSortArguments(args);
		if ((dfsortArgs.getSortinDataset() == null)) { 
    		System.err.println("Usage: " + DfSortCopyFixedDatasetToJava.class.getName() + " sortin=<dsn> [encoding=<codepage>] [shareas=<yes|NO>]");
    		System.err.println("Where:");
    		System.err.println("\tsortin is a RECFM=F dataset");
    		System.err.println("\tencoding is the source character set.  If not supplied, raw bytes are processed.");
    		System.err.println("\tshareas = yes: DFSORT executes in the same address space as the JVM");
    		System.exit(4);
    	}

		DsInfo inDs = dfsortArgs.getSortinDataset();
		if (!inDs.isFixedRecfm()) {
			System.err.println("Dataset " + inDs.getFullyQualifiedDsn() + " is not RECFM=F");
			System.exit(8);
		}

		doSort(dfsortArgs);
		
	}

	private static void doSort(DfSortArguments dfsortArgs) throws Exception {
		DsInfo inDs = dfsortArgs.getSortinDataset();
		
		DfSort dfSort = new DfSort();
		
		if (dfsortArgs.getLogLevel() != -1) {
			dfSort.setLoggingLevel(dfsortArgs.getLogLevel());
		}

		//Direct DFSORT to get its input (SORTIN) from the supplied dataset.
		dfSort.addAllocation("alloc fi(sortin) da("+inDs.getFullyQualifiedDsn()+") reuse shr msg(2)");

		//Direct DFSORT to write output the the output named pipe.
		dfSort.setOutputStreamRecLen(inDs.getLrecl());
		
		//For this example, we sort the entire record (starting in column 1 for
		//a length of lrecl.  The data is treated as character data (CH) and the
		//results are in ascending order (A)
		dfSort.addControlStatement("SORT FIELDS=COPY");
		
		//Specify whether the DFSORT child process should be run in a separate address space.
		//This allows multiple DFSORT processes to run simultaneously as each instance of
		//the DFSORT DDs (SORTIN, SORTOUT, etc...) will be in their own address space.
		dfSort.setSameAddressSpace(dfsortArgs.isSameAddressSpace());
		
		//Kick off the sort. 
		long startTime = System.currentTimeMillis();
		dfSort.execute();
		
		//Once the child starts, open a BufferedInputStream on the child process' stdout
		//and read the sort result.
		BufferedInputStream bis = new BufferedInputStream(dfSort.getChildStdoutStream());
		byte[] bytes = new byte[inDs.getLrecl()];
		int recordCount = 0;
		while (readRecord(bis,bytes)) {
			//Process data
			if (dfsortArgs.getEncoding() != null) {
				String line = new String(bytes,dfsortArgs.getEncoding());
				//Process encoded string...
			} else {
				//Process raw bytes...
			}
			recordCount++;
		}
		bis.close();
	
		//Wait for dfSort to finish and check the result
		int rc =0;
		try {
			rc = dfSort.getReturnCode();
		} catch (RcException rce) {
			System.out.println("Caught RcException: " + rce.getMessage());
			rc = -1;
		}
		long dfsortRuntime = System.currentTimeMillis() - startTime;

		if (rc != 0 || dfsortArgs.getLogLevel() >= 0) {
			List stderrLines = dfSort.getStderrLines();
			for (Iterator i=stderrLines.iterator(); i.hasNext(); ) {
				System.err.println(i.next());
			}
		}
		
		startTime = System.currentTimeMillis();
        ZFile zFileIn = new ZFile(inDs.getZFileDsn(), "rb,type=record,noseek");
        recordCount = 0;
        try {
            byte[] recBuf = new byte[zFileIn.getLrecl()];
            int nRead;
            while((nRead = zFileIn.read(recBuf)) >= 0) {
            	recordCount++;
            };
        } finally {
           zFileIn.close();
        }
		long zfileRuntime = System.currentTimeMillis() - startTime;

		System.out.println("RC=" + rc + " DFSORT TIME=" + dfsortRuntime + " ZFILE TIME=" + zfileRuntime +" RECORD COUNT=" + recordCount + " "
				+ DfSortCopyFixedDatasetToJava.class.getName());		
		
	}

	/*
	 * Read exactly bytesToRead from the underlying stream (this call may block).  Throw
	 * an IOException if EOF is encountered before all of the bytes are read.
	 */
	private static boolean readRecord(InputStream is, byte[] bytes) throws IOException {
		int offset = 0;
		int bytesToRead = bytes.length;
		int c = is.read();
		if (c == -1) {
			return false;
		} else {
			bytes[offset++] = (byte) c;
			--bytesToRead;
		}
		
		while (bytesToRead > 0) {
			int bytesRead = is.read(bytes, offset, bytesToRead);
			if (bytesRead == -1) {
				throw new IOException("EOF encountered before all record bytes read");
			}
			bytesToRead -= bytesRead;
			offset += bytesRead;
		}
		return true;
	}

}
