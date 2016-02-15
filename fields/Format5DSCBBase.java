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
 * A class which maps a Format 5 DSCB (VTOC free space)
 * <p>
 * @see Format5DSCB
 * @since 2.1.0
 */
public class Format5DSCBBase {

	protected static AssemblerDatatypeFactory factory = new AssemblerDatatypeFactory();

	/** F5DSCB   DSECT */
	public static int F5DSCB = factory.getOffset();

	/**          IECSDSL1 (5) <br/>
	  * VALID ONLY IF DS4EFVLD AND DS4DOSBT ARE OFF.                     @02A <br/>
	    IECSDSL5 EQU   *                   FORMAT 5 DSCB                         */
	public static int IECSDSL5 = factory.getOffset();

	/** IECSDSF5 EQU   IECSDSL5            SAME AS IECSDSL5                @P1C  */
	public static int IECSDSF5 = factory.getOffset();

	/** DS5KEYID DS    XL4                 KEY IDENTIFIER (X'05050505')          */
	protected static BinaryAsLongField DS5KEYID = factory.getBinaryAsLongField(4, false);

	/** DS5AVEXT DS    XL5                 AVAILABLE EXTENT                      */
	protected static ByteArrayField DS5AVEXT = factory.getByteArrayField(5);

	/** *        BYTES 1 - 2     RELATIVE TRACK ADDRESS OF THE FIRST TRACK <br/>
	    *                        IN THE EXTENT <br/>
	    *        BYTES 3 - 4     NUMBER OF UNUSED CYLINDERS IN THE EXTENT <br/>
	    *        BYTE  5         NUMBER OF ADDITIONAL UNUSED TRACKS <br/>
	    DS5EXTAV DS    XL35                SEVEN AVAILABLE EXTENTS               */
	protected static ByteArrayField DS5EXTAV = factory.getByteArrayField(35);

	/** DS5FMTID DS    CL1                 FORMAT IDENTIFIER (X'F5')             */
	protected static StringField DS5FMTID = factory.getStringField(1, false);

	/** DS5MAVET DS    XL90                EIGHTEEN AVAILABLE EXTENTS            */
	protected static ByteArrayField DS5MAVET = factory.getByteArrayField(90);

	/** DS5PTRDS DS    XL5                 FORWARD CHAIN POINTER (CCHHR)   @P1C  */
	protected static ByteArrayField DS5PTRDS = factory.getByteArrayField(5);

	/** *                                     TO FORMAT 5 DSCB OR ZERO     @P1A <br/>
	    DS5END   EQU   *                   EQUATE AT END OF DSCB5          @P1C  */
	public static int DS5END = factory.getOffset();

	/** DS5EXTLN EQU   DS5EXTAV-DS5AVEXT   LENGTH OF AN EXTENT             @L6A  */
	public static int DS5EXTLN = 0x5;

	/** *                                  DESCRIPTOR IN A FMT 5           @L6A <br/>
	    *                                  DSCB                            @L6A <br/>
	    DS5EXTMX EQU   26                  MAXIMUM NUMBER OF FREE          @L6A  */
	public static int DS5EXTMX = 0x1a;

	/** *                                  EXTENTS IN A FMT5 DSCB          @L6A <br/>
	    DS5RTALN EQU   2                   LENGTH OF AN RTA IN A FMT 5           */
	public static int DS5RTALN = 0x2;

	/** *                                  EXTENTS IN A FMT5 DSCB          @L6A <br/>
	    DS5IDC   EQU   X'F5'               FORMAT 5 IDENTIFIER CONSTANT    @L6A  */
	public static int DS5IDC = 0xf5;

	protected byte[] bytes;
	protected int bufOffset;

	// Instance variables used to cache field values 
	private Long ds5keyid;
	private byte[] ds5avext;
	private byte[] ds5extav;
	private String ds5fmtid;
	private byte[] ds5mavet;
	private byte[] ds5ptrds;


	public Format5DSCBBase(byte[] buffer, int bufOffset) {
		this.bytes = buffer;
		this.bufOffset = bufOffset;
	}


	public long getDs5keyid() {
		if (ds5keyid == null) {
			ds5keyid = new Long(DS5KEYID.getLong(bytes, bufOffset));
		}
		return ds5keyid.longValue();
	}

	public void setDs5keyid(long ds5keyid) {
		if (DS5KEYID.equals(this.ds5keyid, ds5keyid))
			return;
		DS5KEYID.putLong(ds5keyid, bytes, bufOffset);
		this.ds5keyid = new Long(ds5keyid);
	}

	public byte[] getDs5avext() {
		if (ds5avext == null) {
			ds5avext = DS5AVEXT.getByteArray(bytes, bufOffset);
		}
		return ds5avext;
	}

	public void setDs5avext(byte[] ds5avext) {
		if (DS5AVEXT.equals(this.ds5avext, ds5avext))
			return;
		DS5AVEXT.putByteArray(ds5avext, bytes, bufOffset);
		this.ds5avext = ds5avext;
	}

	public byte[] getDs5extav() {
		if (ds5extav == null) {
			ds5extav = DS5EXTAV.getByteArray(bytes, bufOffset);
		}
		return ds5extav;
	}

	public void setDs5extav(byte[] ds5extav) {
		if (DS5EXTAV.equals(this.ds5extav, ds5extav))
			return;
		DS5EXTAV.putByteArray(ds5extav, bytes, bufOffset);
		this.ds5extav = ds5extav;
	}

	public String getDs5fmtid() {
		if (ds5fmtid == null) {
			ds5fmtid = DS5FMTID.getString(bytes, bufOffset);
		}
		return ds5fmtid;
	}

	public void setDs5fmtid(String ds5fmtid) {
		if (DS5FMTID.equals(this.ds5fmtid, ds5fmtid))
			return;
		DS5FMTID.putString(ds5fmtid, bytes, bufOffset);
		this.ds5fmtid = ds5fmtid;
	}

	public byte[] getDs5mavet() {
		if (ds5mavet == null) {
			ds5mavet = DS5MAVET.getByteArray(bytes, bufOffset);
		}
		return ds5mavet;
	}

	public void setDs5mavet(byte[] ds5mavet) {
		if (DS5MAVET.equals(this.ds5mavet, ds5mavet))
			return;
		DS5MAVET.putByteArray(ds5mavet, bytes, bufOffset);
		this.ds5mavet = ds5mavet;
	}

	public byte[] getDs5ptrds() {
		if (ds5ptrds == null) {
			ds5ptrds = DS5PTRDS.getByteArray(bytes, bufOffset);
		}
		return ds5ptrds;
	}

	public void setDs5ptrds(byte[] ds5ptrds) {
		if (DS5PTRDS.equals(this.ds5ptrds, ds5ptrds))
			return;
		DS5PTRDS.putByteArray(ds5ptrds, bytes, bufOffset);
		this.ds5ptrds = ds5ptrds;
	}

}
