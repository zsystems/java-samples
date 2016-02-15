/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2005. All Rights Reserved
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

import com.ibm.jzos.ZFile;

/**
 * Sample program that uses BPXWDYN to dynamically allocate a new dataset
 * with the same attributes as a given dataset, and then copy the
 * original to the new dataset.  The name of the source dataset and 
 * target dataset names are given as arguments.
 * <p>
 * @see com.ibm.jzos.ZFile#bpxwdyn(String)
 */
public class DynallocCopyDataset {
	
    public static void main(String[] args) throws Exception {
    	
    	if (args.length < 2) {
    		System.out.println("Usage: inputDataset outputDataset");
    		System.exit(8);
    	}
    	
    	// Allocate new DDNAMEs to "DUMMY" that we will reuse to allocate the output dataset
		String sourceDD = ZFile.allocDummyDDName();
		String targetDD = ZFile.allocDummyDDName();
		
		// bpxwdyn requires fully-qualfied dsn (it will not add uid).
		// so we make sure that we have a fully qualified DSNs
		String sourceDSN = ZFile.getFullyQualifiedDSN(args[0]); 
		String targetDSN = ZFile.getFullyQualifiedDSN(args[1]); 

		// Allocate the input dataset using BPXWDYN.
		// The "reuse" keyword allows us to reuse the previous DD DUMMY that we allocated
		// This will throw a RcException if fails, and issue a "Write-to-programmer" message
		// to the job log.
		ZFile.bpxwdyn("alloc fi(" + sourceDD + ") da(" + sourceDSN 
				+ ") reuse shr msg(wtp)");

		// Allocate the output dataset using BPXWDYN.
		ZFile.bpxwdyn("alloc fi(" + targetDD + ") da(" + targetDSN 
				+ ") like(" + sourceDSN + ") reuse new catalog msg(wtp)");
		
		ZFile zFileIn = null;
		ZFile zFileOut = null;
        try {
            zFileIn = new ZFile("//DD:" + sourceDD, "rb,type=record,noseek");
            if (zFileIn.getDsorg() != ZFile.DSORG_PS) {
            	throw new IllegalStateException("Input dataset must be DSORG=PS");
            }
            zFileOut = new ZFile("//DD:" + targetDD, "wb,type=record,noseek");
            long count = 0;
            byte[] recBuf = new byte[zFileIn.getLrecl()];
            int nRead;
            while((nRead = zFileIn.read(recBuf)) >= 0) {
            	zFileOut.write(recBuf, 0, nRead);
            	count++;
            };
            System.out.println("DynallocCopyDataset: " + count + " records copied");
            
        } finally {
        	// best effort to close and free datasets
           try { 
        	   if (zFileIn != null) zFileIn.close(); 
           } catch(Exception ignore) {}
           try { 
        	   if (zFileOut != null) zFileOut.close(); 
           } catch(Exception ignore) {}
           try { 
        	   ZFile.bpxwdyn("free fi(" + sourceDD + ") msg(wtp)"); 
           } catch(Exception ignore) {}
           try { 
        	   ZFile.bpxwdyn("free fi(" + targetDD + ") msg(wtp)"); 
           } catch(Exception ignore) {}
        }
    }
}
