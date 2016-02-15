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
 * Use CompressionFactory to compress a z/OS UNIX file.
 * 
 * @see com.ibm.jzos.CompressionFactory
 * @see com.ibm.jzos.ZCompressor
 * @since 2.4.4
 */
public class CompressFile {
	
	public static void main(java.lang.String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: com.ibm.jzos.sample.compress.CompressFile infile outfile");
			System.err.println("   Note: System property com.ibm.jzos.compression.type may be used to configure compression type");
			System.exit(8);
		}
		// 1: the name of the input z/OS Unix file
		String fnIn   = args[0];
		// 2: the name of the compressed output z/OS Unix file
		String fnOut  = args[1];

		InputStream input = null;
		OutputStream output = null;
		CompressionFactory factory = null;

		try {
			// get the default CompressionFactory
			factory = CompressionFactory.getDefault();
			// a FileInputStream for reading the input file
			input = new FileInputStream(fnIn);
			// create a compressing OutputStream wrapping the FileOutputStream
			output = factory.getCompressingOutputStream(new FileOutputStream(fnOut));
			System.out.println("Using: "+ output.getClass());
			
			byte buf[] = new byte[16*1024];
			long time = System.currentTimeMillis();
			int nRead;
			long totalSourceBytes = 0;
			
			// Simply copy data from the InputStream to the compressing OutputStream
			while ((nRead = input.read(buf)) >= 0) {
				totalSourceBytes += nRead;
				output.write(buf, 0, nRead);
			}
			input.close();
			output.close();  // and flushes buffered compressed data 
			
			float secs = (System.currentTimeMillis() - time);
			secs = secs / 1000;
			System.out.println("Compressed "+totalSourceBytes
											+"->"
											+(new File(fnOut)).length()
											+" bytes in "+secs+" seconds");
			
		} finally {
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
