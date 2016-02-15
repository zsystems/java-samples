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
 * This sample demonstrates how to copy data from one file to 
 * another, appending to the second file if it already exists.
 * The file names are given as arguments.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class CopyFile {
	
    /**
     * The main method accepts two file names as the only arguments and calls 
     * <code>copy(String, String)</code> to copy records from the source file to the destination file. 
     * 
     * @param args The names of the files to read from and write to
     */
    public static void main(String[] args) {
	  
        // this sample expects two input arguments
        if (args.length != 2) {
            usage();
        }
		
        // input is expected to be non-VSAM MVS dataset names
        String source = args[0];
        String destination = args[1];
		
        // call the copy method with the input file
        copy(source, destination);
    }


    /**
     * Copies a non-VSAM file as source to the other non-VSAM file as destination.
     * @param source The name of the file to read from
     * @param destination The name of the file to create and copy to
     */
    public static void copy(String source, String destination) {
        ZFile fileIn = null;
        ZFile fileOut = null;

        try {
            
            // open the source and destination datasets
            if (ZFile.exists(source)) {
                fileIn = new ZFile(source, "rb,type=record,noseek");
                
                if (ZFile.exists(destination)) {
                    
                    fileOut = new ZFile(destination, "r+b,type=record,noseek");
                    
                    // read until EOF
                    byte[] buf = new byte[fileOut.getLrecl()];
                    while (fileOut.read(buf) != -1) {}
                    fileOut.flush();
                    
                } else {
                    fileOut = new ZFile(destination, "wb,type=record,noseek");
                }
                
                int length = 0;
                long count = 0;
                byte[] recBuf = new byte[fileIn.getLrecl()];

                while((length = fileIn.read(recBuf)) >= 0) {
                    fileOut.write(recBuf, 0, length);
                    count++;
                };

                System.out.println(count + " records copied");
            }
            else {
                System.out.println("Source file " + source + " does not exist.");
            }
        } catch (Exception e) {
            System.out.println("Failed to copy " + source + " to " + destination);
            e.printStackTrace();
        }
        finally {
            try {
                if (fileIn != null) {
                    fileIn.close();
                }
                
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (Exception e) {}
        }
    }

    /**
     * Print sample usage and exit
     */
    private static void usage() {
        System.out.println("CopyFile -- Demonstrates how to copy data from one file to another");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.CopyFile source destination");
        System.out.println("\tsource: The name of the file to read from");
        System.out.println("\tdestination: The name of the file to write to");
        System.out.println("\t\tExamples");
        System.out.println("\t\tPS Dataset: //'USERID.PRIVATE.PS'");
        System.out.println("\t\tPDS Member: //'USERID.PRIVATE.PDS(MEMBER)'");
        System.out.println();
        System.exit(0);
    }
}
