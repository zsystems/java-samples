/*
 * %Z%%W% %I%
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2012
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
package com.ibm.jzos.sample.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.ibm.jzos.CompressionFactory;

/**
 * Use CompressionFactory to expand a compressed z/OS UNIX file.
 *
 * @see com.ibm.jzos.CompressionFactory
 * @see com.ibm.jzos.ZCompressor
 * @since 2.4.4
 */
public class ExpandFile {
	
	public static void main(java.lang.String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: com.ibm.jzos.sample.compress.ExpandFile infile outfile");
			System.err.println("   Note: System property com.ibm.jzos.compression.type may be used to configure compression type");
			System.exit(8);
		}
		
		// here are the arguments
		// 1: the name of the compressed input z/OS Unix file
		String fnIn   = args[0];
		// 2: the name of the output (expanded) z/OS Unix file
		String fnOut  = args[1];

		CompressionFactory factory = null;
		InputStream input = null;
		OutputStream output = null;

		try {
			// get the default CompressionFactory
			factory = CompressionFactory.getDefault();
			
			// get an expanding InputStream wrapped on a FileInputStream 
			input = factory.getExpandingInputStream(new FileInputStream(fnIn));
			System.out.println("Using: "+ input.getClass());
			
			// a simple OutputStream for writing expanded data
			output = new FileOutputStream(fnOut);
			
			byte buf[] = new byte[16*1024];
			long time = System.currentTimeMillis();
			long totalExpanded = 0;
			int nRead;
			
			// simply copy data from the expanding InputStream to the FileOutputStream
			while ((nRead = input.read(buf)) >= 0) {
				output.write(buf, 0, nRead);
				totalExpanded += nRead;
			}
			input.close();
			output.close();
			
			float secs = (System.currentTimeMillis() - time);
			secs = secs / 1000;
			System.out.println("Expanded "
					+ (new File(fnIn).length()) + "->" + totalExpanded
					+" bytes in "+secs+" seconds");
			
		} finally {
			// clean up everything on normal exit or if something goes wrong
			if (input != null) {
				try { input.close(); } catch (Throwable ignore) {}
			}
			if (output != null) {
				try { output.close(); } catch (Throwable ignore) {}
			}
			if (factory != null) {
				factory.release(); // does nothing for default factory
			}
		}
	}
}
