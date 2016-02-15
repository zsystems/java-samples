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
 * Sample program that updates records in a VSAM using {@link
 * ZFile}. 
 * 
 * <p> Refer to the C++ Programmer's guide for more information
 * on processing VSAM files with the C library.
 * 
 * <p> This sample assumes that the VSAM cluster was created with 
 * something like this:
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
public class UpdateKsdsRecords {

    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            usage();
        }

        // input is expected to a VSAM dataset name
        String fileName = args[0];

        // update records in the file
        updateRecords(fileName);
    }

    
    public static void updateRecords(String fileName) throws Exception {
        int nRead;
        int lrecl = 80;
        int keyLen = 8;
        String options = "rb+,type=record";

        System.out.println("fileName=" + fileName);
        System.out.println("options=" + options);

        ZFile zfile = new ZFile(fileName, options);
        byte[] recBuf = new byte[lrecl];

        // record keys
        byte[] key = "50000005".getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
    		
        try {
            
            // check if the file is empty
            zfile.rewind();
            nRead = zfile.read(recBuf);

            if (nRead != -1) {
                // position the record
                boolean located = zfile.locate(key, 0, keyLen, ZFile.LOCATE_KEY_EQ);
                System.out.println("located=" + located);

                // read the record
                nRead = zfile.read(recBuf);
                String record50000005 = new String(recBuf);
                System.out.println("nRead=" + nRead + " Record with Primary Key 50000005=" + record50000005);

                // build data to update
                String pk = record50000005.substring(0, 8);
                byte[] updateRecord = padToLength(pk + "This record is modified", lrecl).getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);

                // update the record with the new data
                zfile.update(updateRecord);
                System.out.println("Record 50000005 updated to=" + new String(updateRecord));
            }
            else {
                System.out.println("nRead=" + nRead);
            }
        }
        finally {
            zfile.close();
        }
    }


    /**
     * Pad a string with spaces to a specified length
     */
    static String padToLength(String s, int len) {
        StringBuffer sb = new StringBuffer(len);
        sb.append(s);
        
        for (int i = s.length(); i < len; i++) sb.append(' ');
        return sb.toString();
    }


    private static void usage() {
        System.out.println("UpdateKsdsRecords - Demonstrates how to update records in a VSAM dataset.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.vsam.file.UpdateKsdsRecords file");
        System.out.println("\tfile: The name of the VSAM dataset to update");
        System.out.println("\t\tExample file");
        System.out.println("\t\tVSAM Dataset: //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS'");
        System.out.println();
        System.exit(0);
    }
}
