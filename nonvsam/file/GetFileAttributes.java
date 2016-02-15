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
 * This sample demonstrates how to retrieve file attributes.
 * The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class GetFileAttributes {

    public static void main(String[] args) {

        // this sample expects only one input argument
        if (args.length != 1)  {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // call the create method with the input file
        getAttributes(fileName);
    }


    public static void getAttributes(String fileName) {
        
        try {
            ZFile dsnFile = new ZFile(fileName, "rb,type=record,noseek");
    
            System.out.println("\tName: " + dsnFile.getFilename());
            System.out.println("\tRecord Format: " + dsnFile.getRecfm());
            System.out.println("\tRecord Length: " + dsnFile.getLrecl());
            System.out.println("\tBlock Size: " + dsnFile.getBlksize());
        }
        catch (Exception e) {
            System.out.println("Unable to get attributes for file " + fileName);
            e.printStackTrace();
        }
    }


    private static void usage() {
        System.out.println("GetFileAttributes -- Demonstrates how to retrieve file attributes.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.GetFileAttributes fileName");
        System.out.println("\tfileName: The name of the file to retrieve attributes");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\t\tPDS Member: //'USERID.PRIVATE.PDS(SAMPLE)'");
        System.out.println();
        System.exit(0);
    }
}
