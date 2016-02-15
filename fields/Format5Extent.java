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
import com.ibm.jzos.fields.*;

/**
 * A class which maps an available extent in a Format5 DSCB.
 * 
 * @see Format5DSCB
 * @since 2.1.0
 */
public class Format5Extent {

	protected static AssemblerDatatypeFactory factory = new AssemblerDatatypeFactory();

	/** F5EXTENT DSECT */
	public static int F5EXTENT = factory.getOffset();

	/** FIRSTTRK  DS XL2  RELATIVE TRACK ADDR OF FIRST TRACK */
	protected static BinaryAsIntField FIRSTTRK = factory.getBinaryAsIntField(2, false);

	/** UNUSEDCYL DS XL2  NUMBER OF UNUSED CYLINDERS */
	protected static BinaryAsIntField UNUSEDCYL = factory.getBinaryAsIntField(2, false);

	/** UNUSEDTRK DS XL1  NUMBER OF ADDTL UNUSED TRACKS */
	protected static BinaryAsIntField UNUSEDTRK = factory.getBinaryAsIntField(1, false);
	
	/** EXTLEN    EQU *   LENGTH OF EXTENT (5) */
	public static final int EXTLEN = factory.getOffset();

	protected byte[] bytes;
	protected int bufOffset;

	// Instance variables used to cache field values 
	private Integer firsttrk;
	private Integer unusedcyl;
	private Integer unusedtrk;


	public Format5Extent(byte[] buffer, int bufOffset) {
		this.bytes = buffer;
		this.bufOffset = bufOffset;
	}


	public int getFirsttrk() {
		if (firsttrk == null) {
			firsttrk = new Integer(FIRSTTRK.getInt(bytes, bufOffset));
		}
		return firsttrk.intValue();
	}

	public void setFirsttrk(int firsttrk) {
		if (FIRSTTRK.equals(this.firsttrk, firsttrk))
			return;
		FIRSTTRK.putInt(firsttrk, bytes, bufOffset);
		this.firsttrk = new Integer(firsttrk);
	}

	public int getUnusedcyl() {
		if (unusedcyl == null) {
			unusedcyl = new Integer(UNUSEDCYL.getInt(bytes, bufOffset));
		}
		return unusedcyl.intValue();
	}

	public void setUnusedcyl(int unusedcyl) {
		if (UNUSEDCYL.equals(this.unusedcyl, unusedcyl))
			return;
		UNUSEDCYL.putInt(unusedcyl, bytes, bufOffset);
		this.unusedcyl = new Integer(unusedcyl);
	}

	public int getUnusedtrk() {
		if (unusedtrk == null) {
			unusedtrk = new Integer(UNUSEDTRK.getInt(bytes, bufOffset));
		}
		return unusedtrk.intValue();
	}

	public void setUnusedtrk(int unusedtrk) {
		if (UNUSEDTRK.equals(this.unusedtrk, unusedtrk))
			return;
		UNUSEDTRK.putInt(unusedtrk, bytes, bufOffset);
		this.unusedtrk = new Integer(unusedtrk);
	}

}
