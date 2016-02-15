/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2005. All Rights Reserved
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
package com.ibm.jzos.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import com.ibm.jzos.FileFactory;

/**
 * Sample program that uses the FileFactory class to copy a text file or dataset.
 * The input and output file names are given as arguments to main, 
 * and they main be either POSIX (HFS) file names or MVS dataset names.
 * <p>
 * If the target file is an MVS dataset, then its LRECL should be compatible
 * with the source file/dataset. 
 * <p>
 * Example file names:
 * <ul>
 * <li>/etc/profile</li>
 * <li>//DD:INPUT</li>
 * <li>//'SYS1.MACLIB(ABEND)'</li>
 * <li>//MY.DATASET</li>
 * </ul>
 * 
 * @see com.ibm.jzos.FileFactory
 */
public class FileFactoryCopy {
	
    public static void main(String[] args) throws IOException {
    	if (args.length != 2) {
    		System.out.println("Usage: inputfileOrDataset outputFileOrDataset");
    		System.exit(8);
    	}
        BufferedReader rdr = null;
        BufferedWriter wtr = null;
        long count = 0;
		try {
			rdr = FileFactory.newBufferedReader(args[0]);
			wtr = FileFactory.newBufferedWriter(args[1]);
		
			String line;
			while ((line = rdr.readLine()) != null) {
				wtr.write(line);
				wtr.write("\n");
				count++;
			}
			System.out.println("Copied " + count + " lines from: " + args[0] + " to: " + args[1] );
		} finally {
			if (wtr != null) wtr.close();
			if (rdr != null) rdr.close();
		}
    }
}
