/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2010. All Rights Reserved
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
package com.ibm.jzos.sample.nonvsam.file;

import com.ibm.jzos.ZFile;

/**
 * This sample demonstrates how to randomly access a file.
 * The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class RandomAccessFile {

    public static void main(String[] args) {

        // expects only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // call the random access method with the input file
        randomAccess(fileName);
    }


    public static void randomAccess(String fileName) {
        ZFile dsnFile = null;

        try {
            
            dsnFile = new ZFile(fileName, "rb,type=record");
            byte[] recBuf = new byte[dsnFile.getLrecl()];

            int bytesRead = dsnFile.read(recBuf);
            System.out.println("bytesRead=" + bytesRead + " 1st record (record 0):" + new String(recBuf));

            dsnFile.seek(-1, ZFile.SEEK_END);

            bytesRead = dsnFile.read(recBuf);
           	System.out.println("bytesRead=" + bytesRead + " last record (record n-1):" + new String(recBuf));

            dsnFile.seek(1, ZFile.SEEK_SET);

            bytesRead = dsnFile.read(recBuf); 
            System.out.println("bytesRead=" + bytesRead + " 2nd record (record 1):" + new String(recBuf));

            dsnFile.seek(0, ZFile.SEEK_SET);

            bytesRead = dsnFile.read(recBuf); 
            System.out.println("bytesRead=" + bytesRead + " 1st record (record 0):" + new String(recBuf));

            dsnFile.seek(5, ZFile.SEEK_SET);

            bytesRead = dsnFile.read(recBuf); 
            System.out.println("bytesRead=" + bytesRead + " 6th record (record 5):" + new String(recBuf));

            // rewind the cursor to the beginning of the file
            dsnFile.rewind();

            // read the file until the end-of-file is reached
            bytesRead = dsnFile.read(recBuf);

            while (bytesRead != -1) {
                System.out.println("bytesRead=" + bytesRead + " data:" + new String(recBuf));
                bytesRead = dsnFile.read(recBuf);
            }
        }
        catch (Exception e) {
            System.out.println("Unable to randomly access " + fileName);
            e.printStackTrace();
        }
        finally {
            try {
                if (dsnFile != null) {
                    dsnFile.close();
                }
            } catch (Exception e) {}
        }
    }


    private static void usage() {
        System.out.println("RandomAccessFile -- Demonstrates how to randomly access a file.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.RandomAccessFile fileName");
        System.out.println("\tfileName: The name of the file to access");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\t\tPDS Member: //'USERID.PRIVATE.PDS(SAMPLE)'");
        System.out.println();
        System.exit(0);
    }
}
