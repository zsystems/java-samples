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
 * This sample demonstrates how to create a non-VSAM
 * MVS sequential dataset with specific file attributes.
 * The file name is given as an argument to main, and
 * must be a non-VSAM MVS sequential dataset name.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class CreateFileWithFileAttributes {
	
    /**
     * The main method accepts a file name as the only argument and calls 
     * <code>createFileWithAttributes(String)</code> to create the file.
     * 
     * @param args The name of the file to create
     */
    public static void main(String[] args) {
	  
        // this sample expects only one input argument
        if (args.length != 1) {
            usage();
        }
		
        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];
		
        // call the create method with the input file
        createFileWithAttributes(fileName);
    }


    /**
     * Creates a non-VSAM MVS dataset with specified attributes.
     * @param fileName The name of the file to create
     */
    public static void createFileWithAttributes(String fileName) {

        try {
            
            // bpxwdyn requires fully-qualfied DSN
            // so we make sure that we have a fully qualified DSN
            String dsn = ZFile.getFullyQualifiedDSN(fileName);
            
            // allocate new DDNAME to "DUMMY" that we will reuse to allocate the new file
            String targetDD = ZFile.allocDummyDDName();
            
            // file creation
            ZFile.bpxwdyn("alloc fi(" + targetDD + ") da(" + dsn + ") reuse new " +
                    "dsorg(ps) lrecl(1028) recfm(v,b) tracks space(1,1) blksize(6144) catalog msg(wtp)");
            
            // free the ddname associated with the dataset
            ZFile.bpxwdyn("free fi(" + targetDD + ") msg(wtp)");
    
            if (ZFile.exists(fileName)) {
                System.out.println("File " + fileName + " was successfully created");
            } else {
                System.out.println("File " + fileName + " cannot be created");
            }
        }
        catch (Exception e) {
            System.out.println("Failed to create " + fileName + ":");
            e.printStackTrace();
        }
    }


    /**
	   * Print sample usage and exit
	   */
    private static void usage() {
        System.out.println("CreateFileWithFileAttributes -- Demonstrates how to create a non-VSAM MVS sequential dataset with specific file attributes.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.CreateFileWithFileAttributes fileName");
        System.out.println("\tfileName: The name of the file to create");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\tPS Dataset: //PRIVATE.SAMPLE or //'USERID.PRIVATE.SAMPLE'");
        System.out.println();
        System.exit(0);
    }
}
