/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * Copyright IBM Corp. 2005. All Rights Reserved
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

import java.util.Arrays;

import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;

/**
 * Sample program that inserts, updates, locates, and deletes records in a VSAM
 * KSDS using {@link ZFile}.
 * <p>
 * Refer to the C++ Programmer's guide for more information on processing VSAM
 * files with the C library.
 * 
 * This sample assumes that the //KSDS DD points to a VSAM cluster that
 * was created something like this:
 * <pre><code>
   DEFINE CLUSTER - 
      (NAME(SOMENAME.CLUSTER) - 
      TRK(4 4) - 
      RECSZ(80 80) - 
      INDEXED - 
      NOREUSE - 
      KEYS(8 0) - 
      OWNER(YYYYYY) ) - 
    DATA - 
      (NAME(SOMENAME.KSDS.DA)) - 
    INDEX - 
      (NAME(SOMENAME.KSDS.IX)) 
   </code></pre>
 * 
 * @see com.ibm.jzos.ZFile
 */
public class ZFileKSDS {
	public static void main(String[] args) throws Exception {
		
		String filename = "//DD:KSDS";
		String options = "ab+,type=record";
		int lrecl = 80;  
		int keyLen = 8;
		ZFile zfile = new ZFile(filename, options);
		
		try {
			// construct some records with key prefixes
			byte[] rec_1 = padToLength("AAAAAAAARecord 1", lrecl)
								.getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
			byte[] rec_2 = padToLength("BBBBBBBBRecord 2", lrecl)
								.getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
			byte[] rec_3 = padToLength("CCCCCCCCRecord 3", lrecl)
								.getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
			
			byte[] recBuf = new byte[lrecl];
			int nRead, nUpdated;
			
			zfile.write(rec_1);
			zfile.write(rec_2, 0, rec_2.length);  // alternate form 
			zfile.write(rec_3);
			
			// point to the first record
			check("Found first record", 
					zfile.locate(rec_1, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			
			// read back the record and verify its contents
			nRead = zfile.read(recBuf);
			check("read len", lrecl == nRead);
			check("rec_1 contents", Arrays.equals(rec_1, recBuf));
			
			// update the record
			byte[] rec_1U = padToLength("AAAAAAAARecord 1 updated", lrecl)
								.getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
			nUpdated = zfile.update(rec_1U);
			check("update len", lrecl == nUpdated);
			
			// read back the record and verify its contents
			check("Locate rec_1", 
					zfile.locate(rec_1, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			nRead = zfile.read(recBuf);
			check("read len", lrecl == nRead);
			check("rec_1U contents", Arrays.equals(rec_1U, recBuf));
			
			// point to the second record, using alternate form of locate()
			byte[] keybuf = new byte[keyLen];
			System.arraycopy(rec_2, 0, keybuf, 0, keyLen);
			check("Locate rec_2",  
					zfile.locate(keybuf, ZFile.LOCATE_KEY_EQ));
			
			// read back the record and verify its contents
			nRead = zfile.read(recBuf);
			check("read len", lrecl == nRead);
			check("rec_2 contents", Arrays.equals(rec_2, recBuf));
			
			// update the second record, using the alternate update() form
			byte[] rec_2U = padToLength("BBBBBBBBRecord 2 updated", lrecl)
								.getBytes(ZFile.DEFAULT_EBCDIC_CODE_PAGE);
			nUpdated = zfile.update(rec_2U, 0, rec_2U.length);
			check("update len", lrecl == nUpdated);
			
			// read back the record and verify its contents
			check("Locate rec_2", 
					zfile.locate(rec_2, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			nRead = zfile.read(recBuf);
			check("read len", lrecl == nRead);
			check("rec_2U contents", Arrays.equals(rec_2U, recBuf));
			
			// delete all of the records
			check("Locate rec_1", 
					zfile.locate(rec_1, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			nRead = zfile.read(recBuf);  // have to read a rec b4 updating
			zfile.delrec();
			
			check("Locate rec_2", 
					zfile.locate(rec_2, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			nRead = zfile.read(recBuf);
			zfile.delrec();
			
			check("Locate rec_3", 
					zfile.locate(rec_3, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			nRead = zfile.read(recBuf);
			zfile.delrec();
			
			// verify that we get an exeception if we try to delete without
			// reading one first
			try {
				zfile.delrec();
				check("Expected exception from delrec()", false);
			} catch (ZFileException zfe ) {
				System.out.println("Expected exception: " + zfe); 
				check("zfe.getErrno = " + zfe.getErrno(), 
						76 == zfe.getErrno());
			}
			
			// check that a record that was deleted cannot be found
			check("Locate rec_2 after deleting", 
					! zfile.locate(rec_2, 0, keyLen, ZFile.LOCATE_KEY_EQ));
			
			
		} finally {
			zfile.close();
		}
	}
    
	/**
	 * Pad a string with spaces to a specified length
	 */
	static String padToLength(String s, int len) {
		StringBuffer sb = new StringBuffer(len);
		sb.append(s);
		for (int i = s.length(); i < len; i++) sb.append(' ');
		return sb.toString();
	}
	
	/**
	 * Check a condition and throw an Exception with message if not true
	 */
	static void check(String msg, boolean value) {
		if (!value) throw new RuntimeException(msg);
	}
}
