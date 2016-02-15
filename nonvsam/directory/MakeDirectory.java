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

import java.io.File;
import com.ibm.jzos.ZFile;

/**
 * This sample demonstrates how to create a new directory. The 
 * directory name is given as an argument to main, and it may be
 * either a HFS directory or a PDS directory.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class MakeDirectory {

    /**
     * The main method accepts a directory name as the only argument
     * and calls <code>makeDirectory(String)</code> to create that 
     * directory.
     * 
     * @param args The name of the directory to create
     */
    public static void main(String[] args) throws Exception {

        // this sample expects only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a directory name
        String dirName = args[0];  
		
        // call the make method with the input directory name
        makeDirectory(dirName);
    }


    /**
     *  Creates a directory.
     * 
     * @param dirName The name of the directory to create
     */
    private static void makeDirectory(String dirName) throws Exception {
        
        try {
            
            // make a PDS directory
            if (dirName.startsWith("//")) {
                
                if (ZFile.exists(dirName)) {
                    System.out.println("Directory " + dirName + " already exists.");
                }
                else {
                    
                    // bpxwdyn requires fully-qualfied DSN
                    // so we make sure that we have a fully qualified DSN
                    String dsn = ZFile.getFullyQualifiedDSN(dirName);
                    String ddname = ZFile.allocDummyDDName();
                    
                    // allocate the PDS directory
                    ZFile.bpxwdyn("alloc fi(" + ddname + ") da(" + dsn + ") reuse new " +
                                  "space(10,5) cyl blksize(6160) dir(10) catalog lrecl(80) recfm(f,b) msg(wtp)");
    
                    // free the ddname associated with the dataset
                    ZFile.bpxwdyn("free fi(" + ddname + ") msg(wtp)");
                    
                    if (ZFile.exists(dirName)) {
                        System.out.println("Directory " + dirName + " was successfully created");
                    } else {
                        System.out.println("Directory " + dirName + " cannot be created");
                    }
                }
            }
            // make a HFS directory
            else {
                
                File dir = new File(dirName);

                if (dir.exists() == false) {
                    
                    if (dir.mkdir()) {
                        System.out.println("Directory " + dirName + " was successfully created.");
                    } else {
                        System.out.println("Directory " + dirName + " cannot be created. Possibly invalid directory name or inadequate authority.");
                    }
                }
                else {
                    System.out.println("Directory " + dirName + " already exists.");
                }
            }
        } catch (Exception e) {	
            System.out.println("Failed to create " + dirName + ":");
            e.printStackTrace();
        }
    }


    private static void usage() {
        System.out.println("MakeDirectory -- Demonstrates how to create a new directory.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.directory.MakeDirectory directoryName");
        System.out.println("\tdirectoryName: The name of the directory to create");
        System.out.println("\t\tExample directoryName");
        System.out.println("\t\t\tHFS Directory: /tmp/USERID/hfsdir"); 
        System.out.println("\t\t\tPDS Directory: //PRIVATE.PDS or //'USERID.PRIVATE.PDS'");
        System.out.println();
        System.exit(0);
    }
}
