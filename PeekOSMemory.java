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

import com.ibm.jzos.ByteUtil;
import com.ibm.jzos.ZUtil;

/**
 * Sample program that dumps 500 bytes from the CVT to System.out
 * <p>
 * @see com.ibm.jzos.ZUtil#peekOSMemory(long, int)
 * @see com.ibm.jzos.ZUtil#peekOSMemory(long, byte[])
 * @see com.ibm.jzos.ByteUtil#dumpHex(String, byte[], OutputStream)
 */
public class PeekOSMemory {
	
    public static void main(String[] args) throws Exception {
    	
		byte[] ba = new byte[500];
		long pPSA  = 0L;
		long pCVT  = ZUtil.peekOSMemory(pPSA+16, 4); // get address of CVT from PSA+16
		ZUtil.peekOSMemory(pCVT, ba);           	// get bytes from the CVT
		ByteUtil.dumpHex("CVT:", ba, System.out); 	// dump the bytes in hex
    }
}
