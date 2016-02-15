/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2007. All Rights Reserved
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
package com.ibm.jzos.sample.fields;

/**
 * A class which maps a Format 5 DSCB (VTOC free space)
 * <p>
 * For more information, see: 'SYS1.MODGEN(IECSDSL1)'
 *
 * <p/>
 * @since 2.1.0
 */
public class Format5DSCB extends Format5DSCBBase {

	/**
	 * Construct an instance on a buffer containing raw record bytes
	 */
	public Format5DSCB(byte[] buffer) {
		super(buffer, 0);
	}

	/**
	 * Answer one of the available extents from this record
	 * 
	 * @param index the extent number (1-26)
	 */
	public Format5Extent getExtent(int index) {
		
		if (index < 1 || index > 26) {
			throw new IllegalArgumentException();
		}
		int offset;
		if (index <= 8) {
			offset = DS5AVEXT.getOffset() + ((index-1) * Format5Extent.EXTLEN);
		} else {
			offset = DS5MAVET.getOffset() + ((index-9) * Format5Extent.EXTLEN);
			
		}
		return new Format5Extent(bytes, offset);
	}

}
