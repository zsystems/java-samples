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
package com.ibm.jzos.sample.dfsort;

import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;

/**
 * Helper class for DFSORT samples.  
 * Used to open a dataset using ZFile and retrieve attributes from it.
 */
public class DsInfo {

	private String dsn;
	private String fullyQualifiedDsn;
	private String zFileDsn;
	private int recfmBits;
	private int lrecl;
	
	/**
	 * Construct a new instance on an (unqualified) MVS dataset name
	 * @param dsn the dateset name
	 * @throws ZFileException if unable to open the dataset
	 */
	public DsInfo(String dsn) throws ZFileException {
		this.dsn = dsn;
		this.fullyQualifiedDsn = ZFile.getFullyQualifiedDSN(dsn);
		this.zFileDsn = ZFile.getSlashSlashQuotedDSN(dsn);
		if (!ZFile.dsExists(zFileDsn)) {
			throw new RuntimeException("Dataset " + fullyQualifiedDsn + " does not exist.");
		}

		ZFile zfile = new ZFile(zFileDsn, "rb,type=record");
		try {
			recfmBits = zfile.getRecfmBits();
			lrecl = zfile.getLrecl();
		} finally {
			zfile.close();
		}
	}

	/**
	 * Answers the given unqualified DSN
	 * @return String the dsn
	 */
	public String getDsn() {
		return dsn;
	}
	
	/**
	 * Answers the fully qualified DSN
	 * @return String the fully qualified DSN
	 */
	public String getFullyQualifiedDsn() {
		return fullyQualifiedDsn;
	}
	
	/**
	 * Answers the fully-qualified, //-quoted, DSN
	 * @return String the dsn
	 */
	public String getZFileDsn() {
		return zFileDsn;
	}

	/**
	 * Answers the datasets LRECL
	 * @return int the logical record length
	 */
	public int getLrecl() {
		return lrecl;
	}

	/**
	 * Answers true if the dataset has fixed length records
	 * @return boolean 
	 */
	public boolean isFixedRecfm() {
		return (ZFile.RECFM_F & recfmBits) != 0;
	}

	/**
	 * Answers true if the dataset has variable length records
	 * @return boolean
	 */
	public boolean isVariableRecfm() {
		return (ZFile.RECFM_V & recfmBits) != 0;
	}

}
