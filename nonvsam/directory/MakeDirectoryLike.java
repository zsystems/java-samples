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
package com.ibm.jzos.sample.nonvsam.directory;

import com.ibm.jzos.ZFile;

/**
 * This sample demonstrates how to create a directory using the
 * attributes of an existing directory. The directory names are
 * given as arguments to main, and both must be PDS directories.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class MakeDirectoryLike {

    /**
     * The main method accepts the name of the new directory 
     * and the name of an existing directory, and then calls 
     * <code>makeDirectoryLike(String, String)</code> to create the 
     * new directory using the attributes of the existing directory.
     * 
     * @param args The first argument must be the name of the 
     * directory to create and the second argument must be the name 
     * of an existing directory
     */
    public static void main(String[] args) {

        // this sample expects two input arguments
        if (args.length != 2) {
            usage();
        }

        // the first argument is the name of the new directory to create
        String newDirname = args[0];

        // the second argument is the name of an existing directory 
        String modelDirName = args[1];

        // call the make method to create a new directory using the existing directory as a model
        makeDirectoryLike(newDirname, modelDirName);
    }


    /**
     * Creates a new directory using an existing directory as a model.
     * 
     * @param newDirname The name of the directory to create
     * @param modelDirname The name of an existing directory to use as a model 
     */
    public static void makeDirectoryLike(String newDirname, String modelDirname) {
        
        try {
            
            // use PDS directories only
            if (modelDirname.startsWith("//") &&
                    newDirname.startsWith("//")) {

                // allocate new DDNAME to "DUMMY" that we will reuse to allocate the new PDS
                String targetDD = ZFile.allocDummyDDName();
            
                // bpxwdyn requires fully-qualfied dsn,
                // so we make sure that we have fully qualified DSNs
                String sourceDSN = ZFile.getFullyQualifiedDSN(modelDirname);
                String targetDSN = ZFile.getFullyQualifiedDSN(newDirname);
                
                // allocate the new PDS using BPXWDYN
                ZFile.bpxwdyn("alloc fi(" + targetDD + ") da(" + targetDSN +
                              ") like(" + sourceDSN + ") reuse new catalog msg(wtp)");
                
                // free the ddname associated with the dataset
                ZFile.bpxwdyn("free fi(" + targetDD + ") msg(wtp)");

                if (ZFile.exists(newDirname)) {
                    System.out.println("Directory " + newDirname + " was successfully created.");
                } else {
                    System.out.println("Directory " + newDirname + " cannot be created. Possibly invalid file name or inadequate authority.");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to create " + newDirname);
            e.printStackTrace();
        }
    }


    /**
	   * Print sample usage and exit
	   */
    private static void usage() {
        System.out.println("MakeDirectoryLike -- Demonstrates how to create a directory using the attributes of an existing directory.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.directory.MakeDirectoryLike newDirectoryName modelDirectoryName");
        System.out.println("\tnewDirectoryName: The name of the directory to create");
        System.out.println("\tmodelDirectoryName: The name of an existing directory to use as a model");
        System.out.println("\tBoth directory names must use the same format");
        System.out.println("\t\tExample newDirectoryName, modelDirectoryName");
        System.out.println("\t\t\tPDS Directory: //PRIVATE.PDS or //'USERID.PRIVATE.PDS'");
        System.out.println();
        System.exit(0);
    }
}
