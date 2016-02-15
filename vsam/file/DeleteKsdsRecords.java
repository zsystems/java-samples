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
import com.ibm.jzos.ZFileException;

/**
 * Sample program that deletes records in a VSAM KSDS using 
 * {@link ZFile}.
 *  
 * <p> Refer to the C++ Programmer's guide for more information
 * on processing VSAM files with the C library.
 *  
 * <p> This sample assumes that the VSAM
 * cluster was created with something like this: 
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
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class DeleteKsdsRecords {

    public static void main(String[] args) throws Exception {
	  
        if (args.length != 1) {
            usage();
        }
		
        // input is expected to a VSAM dataset name
        String fileName = args[0];

        // delete records in the file
        deleteRecords(fileName);
    }

    
    static void deleteRecords(String fileName) throws Exception {
        String options = "ab+,type=record";
        ZFile zfile = new ZFile(fileName, options);

        int lrecl = zfile.getLrecl();
        byte[] recBuf = new byte[lrecl];

        // get a default key, will be ignored nonethless
        byte[] ignoreKey = " ".getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);

        // record keys
        byte[] key = "30000003".getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);

        try {

            // delete last record
            check("Locate LAST", zfile.locate(ignoreKey, ZFile.LOCATE_KEY_LAST));
            zfile.read(recBuf);
            zfile.delrec(); // delete

            // delete record with key '3003'
            check("Locate Key 30000003", zfile.locate(key, ZFile.LOCATE_KEY_EQ));
            zfile.read(recBuf);
            zfile.delrec(); // delete

            // reposition to the beginning
            check("Locate FIRST", zfile.locate(ignoreKey, ZFile.LOCATE_KEY_FIRST));

            // delete the records sequentially
            // read each record, followed by a delete, one by one
            while (zfile.read(recBuf) != -1) {
                zfile.delrec(); // delete
            }

            // verify that we get an exeception if we try to delete without
            // reading one first
            try {
                zfile.delrec(); // delete
                check("Expected exception from delrec()", false);
            }
            catch (ZFileException zfe) {
                System.out.println("Expected exception: " + zfe); 
                check("zfe.getErrno=" + zfe.getErrno(), 76 == zfe.getErrno());
            }

            // check that a record that was deleted cannot be found
            check("Locate key 30000003 after deleting", !zfile.locate(key, ZFile.LOCATE_KEY_EQ));
    
        } finally {
            zfile.close();
        }
    }
	

    /**
     * check a condition. throw an Exception with message if the 
     * condition is not true
     */
    static void check(String msg, boolean value) {
        if (!value) throw new RuntimeException(msg);
    }


    private static void usage() {
        System.out.println("DeleteKsdsRecords - Demonstrates how to delete records from a VSAM dataset.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.vsam.file.DeleteKsdsRecords file");
        System.out.println("\tfile: The name of the VSAM dataset containing the records to delete");
        System.out.println("\t\tExample file");
        System.out.println("\t\tVSAM Dataset: //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS'");
        System.out.println();
        System.exit(0);
    }
}
