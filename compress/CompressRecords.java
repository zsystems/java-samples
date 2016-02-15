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

import com.ibm.jzos.BufferCompressor;
import com.ibm.jzos.CompressionFactory;
import com.ibm.jzos.RecordReader;
import com.ibm.jzos.RecordWriter;
import com.ibm.jzos.ZFile;

/**
 * Use CompressionFactory to compress individual records from one data set
 * to another data set. 
 * 
 * @see CompressionFactory
 * @see ExpandRecords
 * @since 2.4.4
 */
public class CompressRecords {
    
	public static void main(java.lang.String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: com.ibm.jzos.test.CompressRecords inDSN outDSN");
			System.err.println("   Note: System property com.ibm.jzos.compression.type may be used to configure compression type");
			System.exit(8);
		}
		// Here are the input arguments:
		// 1: the fully qualified input dataset name
		String fnIn   = ZFile.getSlashSlashQuotedDSN(args[0], true);
		// 2: the fully qualified output dataset name
		//    This dataset must have variable length records (RECFM=V*) 
		String fnOut  = ZFile.getSlashSlashQuotedDSN(args[1], true);

		// Allocate and open the input and output datasets
		RecordReader reader = null;
		RecordWriter writer = null;
		CompressionFactory factory = null;
		
		try {
			reader = RecordReader.newReader(fnIn, RecordReader.FLAG_DISP_SHR);
			writer = RecordWriter.newWriter(fnOut, RecordWriter.FLAG_DISP_OLD);
			// the output data set must contain variable length records.
			if (writer.getRecfm().startsWith("F")) {
				throw new IllegalStateException("Illegal output dataset RECFM="+writer.getRecfm());
			}
			
			byte inBuf[] = new byte[reader.getLrecl()];
			byte outBuf[] = new byte[writer.getLrecl()];
			int nRead, clen;
			long nRecs = 0;
			long nTotalRead = 0;
			long nTotalWritten = 0;

			factory = CompressionFactory.getDefault();
			BufferCompressor comp = factory.getBufferCompressor();
			System.out.println("Using: "+ comp.getClass());
			
			long time = System.currentTimeMillis();
			
			// Copy and compress each record.
			//   BufferCompressor.compressBuffer() is used, and it will treat 
			//   any partially used final compressed byte as a whole output byte
			while ((nRead = reader.read(inBuf)) >= 0) {
				clen = comp.compressBuffer(outBuf, 0, inBuf, 0, nRead);
				if (clen <= 0) {
					throw new RuntimeException("Compressed buffer of length "+nRead
												+" would not fit in output buffer with length "+outBuf.length);
				}
				writer.write(outBuf, 0, clen);
				nRecs++;
				nTotalRead += nRead;
				nTotalWritten += clen;
			}
			float secs = (System.currentTimeMillis() - time);
			secs = secs / 1000;
			System.out.println("Compressed "+nRecs+" records; bytes read/written="
								+nTotalRead+"/"+nTotalWritten+
								" in "+secs+" seconds");
		
		} finally {
			// Clean up on completion or if something goes wrong
			if (reader != null) {
				try { reader.close(); } catch (Throwable ignore) {}
			}
			if (writer != null) {
				try { writer.close(); } catch (Throwable ignore) {}
			}
			if (factory != null) {
				factory.release();  // does nothing for the default factory
			}
		}
	}
}
