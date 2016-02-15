/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * Copyright IBM Corp. 2010. All Rights Reserved
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
package com.ibm.jzos.sample.vsam.file;

import com.ibm.jzos.ZFile;

/**
 * Sample program that reads records in a VSAM using {@link 
 * ZFile}. 
 * 
 * <p> Refer to the C++ Programmer's guide for more information
 * on processing VSAM files with the C library.
 * 
 * <p> This sample assumes that the VSAM cluster was created 
 * with something like this: 
 * 
 * <pre><code>
   DEFINE CLUSTER - 
      (NAME(SOMENAME.CLUSTER) - 
      TRK(4 4) - 
      RECSZ(80 80) - 
      INDEXED - 
      NOREUSE - 
      KEYS(8 0) - 
      OWNER(YYYYYY) ) - 
    DATA - 
      (NAME(SOMENAME.KSDS.DA)) - 
    INDEX - 
      (NAME(SOMENAME.KSDS.IX))
   </code></pre>
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class SeqReadKsds {

    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            usage();
        }
		
        // input is expected to a VSAM dataset name
        String fileName = args[0];		       

        // read records in the file
        readRecords(fileName);
    }

    
    public static void readRecords(String fileName) throws Exception {
        int lrecl = 80;
        String options = "rb+,type=record";

        ZFile zfile = new ZFile(fileName, options);
        byte[] recBuf = new byte[lrecl];

        try {
            
            // read the records sequentially
            while (zfile.read(recBuf) != -1) {
                String record = new String(recBuf);
                System.out.println("Record=" + record);
            }
        }
        finally {
            zfile.close();
        }
    }


    private static void usage() {
        System.out.println("SeqReadKsds - Demonstrates how to read records from a VSAM KSDS dataset.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.vsam.file.SeqReadKsds file");
        System.out.println("\tfile: The name of the VSAM dataset to read records from");
        System.out.println("\t\tExample file");
        System.out.println("\t\tVSAM Dataset: //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS'");
        System.out.println();
        System.exit(0);
    }
}
