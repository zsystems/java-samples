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
 * A class which maps an SMF 83 Record (RACF security information).
 * The superclass {@link Smf83BaseRecord} describes the base section,
 * whereas {@link Smf83Product} and {@link Smf83Security} describe
 * relocatable sections.
 *
 * @see Smf83Product
 * @see Smf83Security 
 * @since 2.1.0
 */
public class Smf83Record extends Smf83BaseRecord {

	/**
	 * Construct an instance using the given raw bytes
	 * @param buffer 
	 */
	public Smf83Record(byte[] buffer) {
		super(buffer);
	}

	/**
	 * Answer one of possibly several Product/Subsystem section
	 * relocatable subrecords.
	 * @param index the 0-based index of the product section 
	 * @return Smf83Product
	 * @throws Index 
	 */
	Smf83Product getProductSection(int index) {
		if (index > getSmf83npd() - 1) {
			throw new IndexOutOfBoundsException();
		}
		long offset = getSmf83opd() + (index * getSmf83lpd());
		return new Smf83Product(bytes, (int)offset);
	}
	
	/**
	 * Answer one of possibly several Security section
	 * relocatable subrecords.
	 * @param index the 0-based index of the security section 
	 * @return Smf83Security
	 * @throws Index 
	 */
	Smf83Security getSecuritySection(int index) {
		if (index > getSmf83nd1() - 1) {
			throw new IndexOutOfBoundsException();
		}
		long offset = getSmf83od1() + (index * getSmf83ld1());
		return new Smf83Security(bytes, (int)offset);
	}
	
	
}
