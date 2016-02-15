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
 * This sample demonstrates how to read a file sequentially.
 * The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class ReadFile {

    public static void main(String[] args) {

        // expects only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // call the read method with the input file
        readFile(fileName);
    }


    public static void readFile(String fileName) {
        ZFile dsnFile = null;

        try {

            // open the dataset
            dsnFile = new ZFile(fileName, "rb,type=record,noseek");
            
            int nRead;
            byte[] recBuf = new byte[dsnFile.getLrecl()];
            
            while((nRead = dsnFile.read(recBuf)) != -1) {
                System.out.println("bytesRead=" + nRead + " data=" + new String(recBuf));
            }
        }
        catch (Exception e) {
            System.out.println("Unable to read " + fileName);
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
        System.out.println("ReadFile -- Demonstrates how to read a file sequentially.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.ReadFile fileName");
        System.out.println("\tfileName: The name of the file to read");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\t\tPDS Member: //'USERID.PRIVATE.PDS(SAMPLE)'");
        System.out.println();
        System.exit(0);
    }
}
