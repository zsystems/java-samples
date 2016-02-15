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
 * This example creates an instance of {@link DfSort} to sort an existing Dataset
 * and write the result to an existing dataset.
 * <p>
 * Arguments (supplied as key=value pairs):
 * <dl>
 * <dt>sortin=&lt;dsn&gt;</dt>
 * <dd>The name of a RECFM=F dataset to sort.  Supplied to DFSORT as SORTIN.</dd>
 * <dt>sortout=&lt;dsn&gt;</dt>
 * <dd>The name of a RECFM=F dataset to receive the sorted data.  Supplied to DFSORT as SORTOUT.</dd>
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
 * <li>Sort the records in Ascending order
 * </ul>
 */
public class DfSortFixedDatasetToFixedDataset {

	public static void main(String[] args) throws Exception {
		DfSortArguments dfsortArgs = new DfSortArguments(args);
		if ((dfsortArgs.getSortinDataset() == null) || (dfsortArgs.getSortoutDataset() == null)) { 
    		System.err.println("Usage: " + DfSortFixedDatasetToFixedDataset.class.getName() + " sortin=<dsn> sortout=<dsn> [shareas=<yes|NO>]");
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
		
		//For this example, we sort the entire record (starting in column 1 for
		//a length of lrecl.  The data is treated as character data (CH) and the
		//results are in ascending order (A)
		dfSort.addControlStatement("SORT FIELDS=(1,"+inDs.getLrecl()+",CH,A)");
		
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
		long runtime = System.currentTimeMillis() - startTime;

		if (rc != 0 || dfsortArgs.getLogLevel() >= 0) {
			List stderrLines = dfSort.getStderrLines();
			for (Iterator i=stderrLines.iterator(); i.hasNext(); ) {
				System.err.println(i.next());
			}
		}
		
		//Count records in input dataset
		ZFile zfile = new ZFile(inDs.getZFileDsn(), "rb,type=record,noseek");
		byte[] record = new byte[inDs.getLrecl()];
		int recordCount = 0;
		while (zfile.read(record) != -1) {
			recordCount++;
		}
		zfile.close();

		System.out.println("RC=" + rc + " TIME=" + runtime+ " RECORD COUNT=" + recordCount + " "
				+ DfSortFixedDatasetToFixedDataset.class.getName());		
	}
}
