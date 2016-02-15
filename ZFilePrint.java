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
import com.ibm.jzos.ZUtil;

/**
 * Sample program that prints an EBCDIC MVS dataset pointed to by //INPUT DD
 * to System.out (stdout).
 * <p>
 * The dataset is opened using the ZFile class in record mode.
 * Note that "noseek" is used so that the file is opened in sequential 
 * mode, which dramatically increases I/O performance.
 * 
 * @see com.ibm.jzos.ZFile
 */
public class ZFilePrint {
    public static void main(String[] args) throws Exception {
        ZFile zFile = new ZFile("//DD:INPUT", "rb,type=record,noseek");
        try {
            byte[] recBuf = new byte[zFile.getLrecl()];
            int nRead;
            String encoding = ZUtil.getDefaultPlatformEncoding();
            while((nRead = zFile.read(recBuf)) >= 0) {
            	String line = new String(recBuf,0,nRead, encoding);
                System.out.println(line);
            };
        } finally {
           zFile.close();
        }
    }
}
