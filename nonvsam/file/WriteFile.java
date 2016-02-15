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
import com.ibm.jzos.ZUtil;

/**
 * This sample demonstrates how to write data to a file sequentially.
 * The file name is given as an argument.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.ZFile
 */
public class WriteFile {

    public static void main(String[] args) {

        // expects only one input argument
        if (args.length < 1) {
            usage();
        }

        // input is expected to be a non-VSAM MVS dataset name
        String fileName = args[0];

        // an optional second parameter can specify the number of records to write
        int count = 10; 
        if (args.length >= 2) {
        	count = Integer.parseInt(args[1]);
        }
        // call the write method with the input file
        writeFile(fileName, count);
    }


    public static void writeFile(String fileName, int count) {
        ZFile dsnFile = null;
        int numWrites;
        String s = new String("test string");

        long elapsed = System.currentTimeMillis();
        long cpu = ZUtil.getCpuTimeMicros();
        try {
            
            // open the dataset
            dsnFile = new ZFile(fileName, "wb,type=record,noseek");
            
            for (numWrites = 0; numWrites < count; numWrites++) {
                String t = numWrites + " " + s;
                dsnFile.write(t.getBytes());
            }

            System.out.println("numWrites = " + numWrites);
            elapsed = System.currentTimeMillis() - elapsed;
            cpu = (ZUtil.getCpuTimeMicros() - cpu) / 1000;
            System.out.println("Elapsed: "+ elapsed + "ms, CPU: "+ cpu+ "ms");
        }
        catch (Exception e) {
            System.out.println("Unable to write to " + fileName);
            e.printStackTrace();
        }
        finally {
            try {
                if (dsnFile != null) {
                    dsnFile.close();
                }
            } catch (Exception e) {}
        }
    }


    private static void usage() {
        System.out.println("WriteFile -- Demonstrates how to write data to a file sequentially.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.WriteFile fileName <numrecs>");
        System.out.println("\tfileName: The name of the file to write.");
        System.out.println("\t\tExample fileName");
        System.out.println("\t\t\tPS Dataset: //'USERID.PRIVATE.SAMPLE'");
        System.out.println("\t\t\tPDS Member: //'USERID.PRIVATE.PDS(SAMPLE)'");
        System.out.println();
        System.exit(0);
    }
}
