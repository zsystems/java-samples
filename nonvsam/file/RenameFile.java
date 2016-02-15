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
 * This sample demonstrates how to rename a file.
 * The old and new file names are given as arguments.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class RenameFile {

    public static void main(String[] args) {

        // this sample expects two arguments
        if (args.length != 2) {
            usage();
        }

        // the first argument is the name of an existing file to rename
        String oldFileName = args[0];

        // the second argument is the new name to replace the old file name
        String newFileName = args[1];

        // call the rename method to rename the file name to the new name
        rename(oldFileName, newFileName);
    }


    public static void rename(String oldFileName, String newFileName) {
        
        try {
            ZFile.rename(oldFileName, newFileName);
        }
        catch (Exception e) {
            System.out.println("Unable to rename " + oldFileName + " to " + newFileName);
            e.printStackTrace();
        }
    }


    private static void usage() {
        System.out.println("RenameFile -- Demonstrates how to rename a file.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.RenameFile oldFileName newFileName");
        System.out.println("\toldFileName: the file name to replace");
        System.out.println("\tnewFileName: the new file name to use");
        System.out.println("\tBoth file names must use the same format");
        System.out.println("\t\tExample oldFileName, newFileName");
        System.out.println("\t\t\tPS dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println();
        System.exit(0);
    }
}
