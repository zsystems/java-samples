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
 * Sample program that writes records in a VSAM using {@link
 * ZFile}. 
 * 
 * <p> Refer to the C++ Programmer's guide for more information on
 * processing VSAM files with the C library.
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
public class SeqWriteKsds {

    public static void main(String[] args) {
        
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a VSAM dataset name
        String fileName = args[0];

        // write records in the file
        writeRecords(fileName);
    }


    public static void writeRecords(String fileName) {
        String options = "wb+,type=record";
        ZFile zfile = null;

        // test data to write to the file
        String[] testString = { 
            "10000001IIIIdddThis is test record 1",
            "20000002HHHHcccThis is test record 2", 
            "30000003GGGGbbbThis is test record 3",
            "40000004FFFFaaaThis is test record 4", 
            "50000005EEEEdddThis is test record 5",
            "60000006DDDDcccThis is test record 6", 
            "70000007CCCCbbbThis is test record 7",
            "80000008BBBBaaaThis is test record 8", 
            "90000009AAAAdddThis is test record 9"
        };

        try {
            
            System.out.println("fileName=" + fileName);
            System.out.println("options=" + options);

            // VSAM file
            zfile = new ZFile(fileName, options);

            int lrecl = 80;
            int keyLen = 8;
            int numRecords;

            System.out.println("getLrecl=" + lrecl + " keyLen=" + keyLen);

            // write records to the file
            for (numRecords = 0; numRecords < testString.length; numRecords++) {
                System.out.println("testString[numRecords]=" + testString[numRecords]);
                byte[] record = padToLength(testString[numRecords], lrecl).getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
                zfile.write(record);
            }

            System.out.println("number of records written: " + numRecords);
        }
        catch (Exception e) {
            System.out.println("Failed to write " + fileName + ". Exception caught: ");
            e.printStackTrace();
        }
        finally {
            try {
                if (zfile != null) {
                    zfile.close();
                }
            }
            catch (Exception e) {
                System.out.println("Failed to close the file " + fileName + ". Exception caught: ");
                e.printStackTrace();
            }
        }
    }


    static String padToLength(String s, int len) {
        
        StringBuffer sb = new StringBuffer(len);
        sb.append(s);
        
        for (int i = s.length(); i < len; i++) {
            sb.append(' ');
        }
        
        System.out.println("sb.length=" + sb.length());
        return sb.toString();
    }


    private static void usage() {
        System.out.println("SeqWriteKsds -- Demonstrates how to write records to a VSAM dataset.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.vsam.file.SeqWriteKsds fileName");
        System.out.println("\tfileName: The name of the VSAM dataset to write to");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\tVSAM Dataset:  //'TSOUSER.PRIVATE.JZOS.SAMPLES.VSAM.KSDS'");
        System.out.println();
        System.exit(0);
    }
}
