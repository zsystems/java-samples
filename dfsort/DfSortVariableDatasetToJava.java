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
import java.util.Iterator;
import java.util.List;

import com.ibm.jzos.DfSort;
import com.ibm.jzos.RDWInputRecordStream;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZUtil;

/**
 * This example creates an instance of {@link DfSort} to sort an existing Dataset
 * and read the sort output.
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
 * <li>Specify an existing RECFM=V existing dataset as input (SORTIN) to DFSORT
 * <li>Sort the records in Ascending order
 * </ul>
 * @param args containing sortin=<dsn>, the name of RECFM=V dataset to sort 
 * @param args containing encoding=<codepage> (optional), the character set of the sortin dataset.
 */
public class DfSortVariableDatasetToJava {

	public static void main(String[] args) throws Exception {
		DfSortArguments dfsortArgs = new DfSortArguments(args);
		if ((dfsortArgs.getSortinDataset() == null)) { 
    		System.err.println("Usage: " + DfSortVariableDatasetToJava.class.getName() + " sortin=<dsn> [encoding=<codepage>] [shareas=<yes|NO>]");
    		System.err.println("Where:");
    		System.err.println("\tsortin is a RECFM=F dataset");
    		System.err.println("\tencoding is the source character set.  If not supplied, binary data is assumed.");
    		System.err.println("\tshareas = yes: DFSORT executes in the same address space as the JVM");
			System.exit(4);
    	}

		DsInfo inDs = dfsortArgs.getSortinDataset();
		if (!inDs.isVariableRecfm()) {
			System.err.println("Dataset " + inDs.getFullyQualifiedDsn() + " is not RECFM=V");
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

		//Direct DFSORT to write output to stdout, and that the resulting
		//records will be prefixed by a 4 byte RDW
		dfSort.setOutputStreamHasRdws();
		
		//For this example, we sort the first 25 bytes of the input records.  
		//Since this SORTIN is RECFM=V, the record data begins in column 5 
		//(the first 4 bytes make up the RDW).  The VLSHRT option is used to 
		//allow records smaller than the sort field length.  Results are in 
		//ascending order (A)
		dfSort.addControlStatement("OPTION VLSHRT");
		dfSort.addControlStatement("SORT FIELDS=(5,25,CH,A)");
		
		//Specify whether the DFSORT child process should be run in a separate address space.
		//This allows multiple DFSORT processes to run simultaneously as each instance of
		//the DFSORT DDs (SORTIN, SORTOUT, etc...) will be in their own address space.
		dfSort.setSameAddressSpace(dfsortArgs.isSameAddressSpace());
		
		//Kick off the sort. 
		long startTime = System.currentTimeMillis();
		dfSort.execute();
		
		//Use a RDWInputRecordStream wrapper around a BufferedInputStream wrapper around
		//the InputStream used to read the sorted output from DFSORT.
		//The RDWInputRecordStream will pick off records delineated by RDWs as required
		//by DFSORT for variable length records.
		RDWInputRecordStream rdwis = new RDWInputRecordStream(new BufferedInputStream(dfSort.getChildStdoutStream()));
		int recordCount = 0;
		byte[] record = new byte[inDs.getLrecl()]; //Allocate a byte array big enough for largest record
		int recLen = 0;
		try {
			while ((recLen = rdwis.read(record)) != -1) {
				if (dfsortArgs.getEncoding() != null) {
					String line = new String(record,0,recLen,dfsortArgs.getEncoding());
					//Process encoded string...
				} else {
					//Process raw bytes...
				}
				recordCount++;
			}
		} finally {
			rdwis.close();
		}

		//Wait for dfSort to finish and check the result
		int rc =0;
		try {
			rc = dfSort.getReturnCode();
		} catch (RcException rce) {
			System.out.println("Caught RcException: " + rce.getMessage());
			rc = -1;
		}
		long runtime = System.currentTimeMillis() - startTime;

		if (rc != 0 || dfsortArgs.getLogLevel() >= 0) {
			List stderrLines = dfSort.getStderrLines();
			for (Iterator i=stderrLines.iterator(); i.hasNext(); ) {
				System.err.println(i.next());
			}
		}

		System.out.println("RC=" + rc + " TIME=" + runtime + " RECORD COUNT=" + recordCount + " "
				+ DfSortVariableDatasetToJava.class.getName());		
	}

}
