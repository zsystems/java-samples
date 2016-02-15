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
 * This sample demonstrates how to delete a file.
 * The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class DeleteFile {

    public static void main(String[] args) {

        // expect only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // call the delete method with the input file
        delete(fileName);
    }


    public static void delete(String fileName) {

        try {
            if (ZFile.exists(fileName)) {
                // delete the dataset
                ZFile.remove(fileName);
                
                if (ZFile.exists(fileName)) {
                    System.out.println("File " + fileName + " cannot be deleted");
                } else {
                    System.out.println("File " + fileName + " was successfully deleted");
                }
            } else {
                System.out.println("File " + fileName + " does not exist");
            }
        }
        catch (Exception e) {
            System.out.println("Failed to delete " + fileName);
            e.printStackTrace();
        }
    }
    

    private static void usage() {
        System.out.println("DeleteFile -- Demonstrates how to delete a file.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.DeleteFile fileName");
        System.out.println("\tfileName: The name of the file to delete");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\t\tPS dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\t\tPDS member: //'USERID.PRIVATE.PDS(SAMPLE)'");
        System.out.println();
        System.exit(0);
    }
}
