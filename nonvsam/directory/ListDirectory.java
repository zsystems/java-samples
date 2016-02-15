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

import java.util.Iterator;
import java.io.File;
import java.io.FileFilter;
import com.ibm.jzos.PdsDirectory;

/**
 * This sample demonstrates how to list the contents of a directory in two different 
 * ways. One approach lists all of the objects in the directory and the other 
 * approach lists only the sub-directories. The directory name is 
 * given as an argument to main, and it may be either a HFS directory or a PDS directory.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.PdsDirectory
 */
public class ListDirectory {
	
    /**
     * The main method accepts a directory name as the only
     * argument and calls both <code>listAll(String)</code> 
     * and <code>listSubdirectories(String)</code> to list the 
     * directory contents.
     * 
     * @param args The name of the directory to list
     */
    public static void main(String[] args) {

        // this sample expects only one input argument
        if (args.length != 1) {
            usage();
        }

        // input is expected to be a directory name
        String dirName = args[0];
		
        // approach 1: List all of the objects in the directory, which include files and sub-directories
        listAll(dirName);

        // approach 2: List only the sub-directories
        listSubdirectories(dirName);
    }


    /**
     * Lists all of the objects (files and sub-directories) in a directory. 
	 * 
	 * @param dirName The name of the directory to list
	 */ 
    private static void listAll(String dirName) {
        try {
        	// list the PDS directory
            if (dirName.startsWith("//")) {
            	
            	int length = 0;
                PdsDirectory dir = new PdsDirectory(dirName);
                System.out.println("Objects in the directory " + dirName + ": ");
                
                for (Iterator iter = dir.iterator(); iter.hasNext(); length++) {
                    PdsDirectory.MemberInfo info = (PdsDirectory.MemberInfo)iter.next();
                    System.out.println(info);
                }
                
                System.out.println("Total number of objects in the directory " + dirName + ": " + length);
            }
            // list the HFS directory
            else {
            	
                File dir = new File(dirName);
                String[] objects = dir.list();

                if (objects != null &&
                        objects.length > 0) {

                    System.out.println("Total number of objects in the directory " + dirName + ": " + objects.length);
                    System.out.println("Objects in the directory " + dirName + ": ");
                    
                    for (int i = 0; i < objects.length; i++) {
                        System.out.println(objects[i]);
                    }
                }
            }
        } catch (Exception e) {	
            System.out.println("Failed to list " + dirName + ". Exception caught: ");
            e.printStackTrace();
        }
    }


    /**
     * Lists only the sub-directories.
     * 
     * @param dirName The name of the directory to list
     */ 
    public static void listSubdirectories(String dirName) {
        
        try {
            
            // only list sub-directories for HFS directories
            if (!dirName.startsWith("//")) {

                File dir = new File(dirName);
                FileFilter fileFilter = new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                };

                File[] subdirectories = dir.listFiles(fileFilter);

                if (subdirectories != null &&
                        subdirectories.length > 0) {

                    System.out.println("Total number of subdirectories in the directory " + dirName + ": " + subdirectories.length);
                    System.out.println("Subdirectories in the directory " + dirName + ": ");
                    for (int i = 0; i < subdirectories.length; i++) {
                        System.out.println(subdirectories[i]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to list " + dirName + ". Exception caught: ");
            e.printStackTrace();
        }
    }


    private static void usage() {
        System.out.println("ListDirectory -- Demonstrates how to list the contents of a directory in two different ways.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.directory.ListDirectory directoryName");
        System.out.println("\tdirectoryName: The name of the directory to list");
        System.out.println("\t\tExample directoryName");
        System.out.println("\t\t\tHFS Directory: /etc");
        System.out.println("\t\t\tPDS Directory: //PRIVATE.PDS or //'USERID.PRIVATE.PDS'");
        System.out.println();
        System.exit(0);
    }
}
