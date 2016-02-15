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
 * Sample program that uses BPXWDYN to dynamically allocate a 
 * new dataset with the same attributes as a given dataset.
 * The names of the source and target datasets are given as arguments.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class CreateFileLike {

    public static void main(String[] args) {

        // expects two input arguments
        if (args.length != 2) {
            usage();
        }

        // the first argument is the name of the new file to create
        String newFilename = args[0];

        // the second argument is the name of an existing file
        String modelFileName = args[1];

        // call the create method to create a new file using the existing file
        create(newFilename, modelFileName);
    }


    public static void create(String newFilename, String modelFilename) {

        try {
            
            // allocate a DDNAME to "DUMMY" that we will reuse to allocate the output dataset
            String targetDD = ZFile.allocDummyDDName();
    		
            // bpxwdyn requires fully-qualfied DSN
            // so we make sure that we have fully qualified DSNs
            String sourceDSN = ZFile.getFullyQualifiedDSN(modelFilename); 
            String targetDSN = ZFile.getFullyQualifiedDSN(newFilename);
            
            // allocate the output dataset using BPXWDYN
            ZFile.bpxwdyn("alloc fi(" + targetDD + ") da(" + targetDSN +
                          ") like(" + sourceDSN + ") reuse new catalog msg(wtp)");
    
            // free the ddname associated with the dataset
            ZFile.bpxwdyn("free fi(" + targetDD + ") msg(wtp)");
            
            if (ZFile.exists(newFilename)) {
                System.out.println("File " + newFilename + " was successfully created.");
            } else {
                System.out.println("File " + newFilename + " cannot be created. Possibly invalid file name or inadequate authority.");
            }
        }
        catch (Exception e) {
            System.out.println("Failed to create " + newFilename);
            e.printStackTrace();
        }
    }


    private static void usage() {
        System.out.println("CreateFileLike -- Demonstrates how to create a file using the attributes of an existing file.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.CreateFileLike newFileName modelFileName");
        System.out.println("\tnewFileName: The name of the file to create");
        System.out.println("\tmodelFileName: The name of an existing file to use as a model");
        System.out.println("\tBoth file names must use the same format");
        System.out.println("\t\tExample newFileName, modelFileName");
        System.out.println("\t\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println();
        System.exit(0);
    }
}
