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

import java.util.Iterator;
import java.util.List;

import com.ibm.jzos.DfSort;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;

/**
 * This example creates an instance of {@link DfSort} to copy an existing Dataset
 * and write the result to an existing dataset.
 * <p>
 * Arguments (supplied as key=value pairs):
 * <dl>
 * <dt>sortin=&lt;dsn&gt;</dt>
 * <dd>The name of a RECFM=F dataset to copy.  Supplied to DFSORT as SORTIN.</dd>
 * <dt>sortout=&lt;dsn&gt;</dt>
 * <dd>The name of a RECFM=F dataset to receive the copied data.  Supplied to DFSORT as SORTOUT.</dd>
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
 * <li>Specify an existing RECFM=F existing dataset as output (SORTOUT) to DFSORT
 * <li>Copy the records from SORTIN to SORTOUT
 * </ul>
 */
public class DfSortCopyFixedDataset {

	public static void main(String[] args) throws Exception {
		DfSortArguments dfsortArgs = new DfSortArguments(args);
		if ((dfsortArgs.getSortinDataset() == null) || (dfsortArgs.getSortoutDataset() == null)) { 
    		System.err.println("Usage: " + DfSortCopyFixedDataset.class.getName() + " sortin=<dsn> sortout=<dsn> [shareas=<yes|NO>]");
    		System.err.println("Where:");
    		System.err.println("\tsortin is a RECFM=F dataset");
    		System.err.println("\tsortout is a RECFM=F dataset");
    		System.err.println("\tshareas = yes: DFSORT executes in the same address space as the JVM");
    		System.exit(4);
    	}
		DsInfo inDs = dfsortArgs.getSortinDataset();
		DsInfo outDs = dfsortArgs.getSortoutDataset();
		if (!inDs.isFixedRecfm()) {
			System.err.println("Dataset " + inDs.getFullyQualifiedDsn() + " is not RECFM=F");
    		System.exit(8);
		}
		
		if (!outDs.isFixedRecfm()) {
			System.err.println("Dataset " + outDs.getFullyQualifiedDsn() + " is not RECFM=F");
    		System.exit(8);
		}
		
    	if (inDs.getLrecl() != outDs.getLrecl()) {
			System.err.println("Dataset " +inDs.getFullyQualifiedDsn() + " (" +inDs.getLrecl()+ ") and " +
								outDs.getFullyQualifiedDsn()+ " (" +outDs.getLrecl()+")  do not have the same LRECL");
    		System.exit(8);
    	}
    	
		doSort(dfsortArgs);
		
	}
	
	private static void doSort(DfSortArguments dfsortArgs) throws Exception {
		DsInfo inDs = dfsortArgs.getSortinDataset();
		DsInfo outDs = dfsortArgs.getSortoutDataset();
		
		DfSort dfSort = new DfSort();
		
		if (dfsortArgs.getLogLevel() != -1) {
			dfSort.setLoggingLevel(dfsortArgs.getLogLevel());
		}
		
		//Direct DFSORT to get its input (SORTIN) from the supplied dataset.
		dfSort.addAllocation("alloc fi(sortin) da("+inDs.getFullyQualifiedDsn()+") reuse shr msg(2)");

		//Direct DFSORT to send its output (SORTOUT) to the supplied DSN.
		dfSort.addAllocation("alloc fi(sortout) da("+outDs.getFullyQualifiedDsn()+") reuse shr msg(2)");
		
		//For this example, we copy the dataset by supplying the  
		//control statement: SORT FIELDS=COPY
		dfSort.addControlStatement("SORT FIELDS=COPY");
		
		//Specify whether the DFSORT child process should be run in a separate address space.
		//This allows multiple DFSORT processes to run simultaneously as each instance of
		//the DFSORT DDs (SORTIN, SORTOUT, etc...) will be in their own address space.
		dfSort.setSameAddressSpace(dfsortArgs.isSameAddressSpace());
		
		//Kick off the sort. 
		long startTime = System.currentTimeMillis();
		dfSort.execute();
				
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
        ZFile zFileOut = new ZFile(outDs.getZFileDsn(), "wb,type=record,noseek");
        long recordCount = 0;
        try {
            byte[] recBuf = new byte[zFileIn.getLrecl()];
            int nRead;
            while((nRead = zFileIn.read(recBuf)) >= 0) {
            	zFileOut.write(recBuf, 0, nRead);
            	recordCount++;
            };
        } finally {
           zFileIn.close();
           zFileOut.close();
        }
		long zfileRuntime = System.currentTimeMillis() - startTime;

		System.out.println("RC=" + rc + " DFSORT_TIME=" + dfsortRuntime + " ZFILE_TIME=" + zfileRuntime + " RECORD COUNT=" + recordCount + " "
				+ DfSortCopyFixedDataset.class.getName());		
	}
}
