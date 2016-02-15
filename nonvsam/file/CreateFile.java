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
 * This sample demonstrates how to create a file with default
 * attribute values. The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class CreateFile {

    public static void main(String[] args) {

        // expects only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // call the create method with the input file
        create(fileName);
    }


    public static void create(String fileName) {
        ZFile newFile = null;

        try {
            // create a new file to write to
            newFile = new ZFile(fileName, "wb,type=record,noseek");
        }
        catch (Exception e) {
            System.out.println("Failed to create " + fileName + ":");
            e.printStackTrace();
        }
        finally {
            
            try {
                if (newFile != null) {
                    newFile.close();
                }
            } catch (Exception e) {}
        }
    }


    private static void usage() {
        System.out.println("CreateFile -- Demonstrates how to create a file with default attribute values.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.CreateFile fileName");
        System.out.println("\tfileName: The name of the file to create");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\tPDS Member: //'USERID.PRIVATE.SAMPLE.PDS(MEMBER)'");
        System.out.println();
        System.exit(0);
    }
}
