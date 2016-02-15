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
 * This sample demonstrates how to delete a directory.
 * The directory name is given as an argument to main.
 * The directory to be deleted may be either a HFS directory or a PDS directory.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class DeleteDirectory {

     public static void main(String[] args) {

         // expects only one argument
         if (args.length != 1) {
        	 usage();
         }

         // input is expected to be a directory name
         String dirName = args[0];

         // call the delete method with the input directory name
         deleteDirectory(dirName);
     }


     private static void deleteDirectory(String dirName) {

         try {
        	 // directory name refers to a PDS directory
             if (dirName.startsWith("//")) {
            	 
            	 if (ZFile.exists(dirName)) {
            		 
            		 // delete the PDS directory
            		 ZFile.remove(dirName);
            		 System.out.println("Directory " + dirName + " was successfully deleted.");
            		 
            	 } else {
            		 System.out.println("Directory " + dirName + " does not exist.");
            	 }
             }
             // directory name refers to a HFS directory
             else {

                 File dir = new File(dirName);
                 
                 if (dir.exists()) {
                	 
                	 // delete the HFS directory
                     if (dir.delete()) {
                         System.out.println("Directory " + dirName + " was successfully deleted.");
                     } else {
                         System.out.println("Directory " + dirName + " cannot be deleted. Possibly invalid directory name, inadequate authority, or the directory is not empty.");
                     }
                     
                 } else {
                     System.out.println("Directory " + dirName + " does not exist.");
                 }
             }
         } catch (Exception e) {
             System.out.println("Failed to delete the directory " + dirName + ":");
             e.printStackTrace();
         }
     }


     private static void usage() {
         System.out.println("DeleteDirectory -- Demonstrates how to delete a directory.");
         System.out.println("Usage:");
         System.out.println("\tjava com.ibm.jzos.sample.nonvsam.directory.DeleteDirectory directoryName");
         System.out.println("\tdirectoryName: The name of the directory to delete");
         System.out.println("\t\tExample directoryName");
         System.out.println("\t\t\tHFS Directory: /tmp/USERID/hfsdir");
         System.out.println("\t\t\tPDS Directory: //PRIVATE.PDS or //'USERID.PRIVATE.PDS'");
         System.out.println();
         System.exit(0);
     }
}
