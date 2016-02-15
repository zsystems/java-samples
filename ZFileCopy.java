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
 * Sample program that uses the ZFile class to copy an MVS dataset in 
 * record mode from DD INPUT to DD OUTPUT.
 * Note that "noseek" is used so that the file is opened in sequential 
 * mode, which dramatically increases I/O performance.
 * 
 * @see com.ibm.jzos.ZFile
 */
public class ZFileCopy {
    public static void main(String[] args) throws Exception {
        ZFile zFileIn = new ZFile("//DD:INPUT", "rb,type=record,noseek");
        ZFile zFileOut = new ZFile("//DD:OUTPUT", "wb,type=record,noseek");
        long count = 0;
        try {
            byte[] recBuf = new byte[zFileIn.getLrecl()];
            int nRead;
            while((nRead = zFileIn.read(recBuf)) >= 0) {
            	zFileOut.write(recBuf, 0, nRead);
            	count++;
            };
            System.out.println("ZFileCopy: " + count + " records copied");
        } finally {
           zFileIn.close();
           zFileOut.close();
        }
    }
}
