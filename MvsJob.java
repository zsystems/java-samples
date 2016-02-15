/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2011. All Rights Reserved
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

import java.io.IOException;

/**
 * Simple bean which holds a MVS jobname and id.
 *
 * @since 2.1.0
 */
public class MvsJob {

	String jobname;
	String jobid;
	
	public MvsJob(String name, String id) {
		this.jobname = name;
		this.jobid = id;
	}
	
	public String getJobid() {
		return jobid;
	}

	public String getJobname() {
		return jobname;
	}

	public String toString() {
		return "" + jobname + "(" + jobid + ")";
	}
	
	public String getStatus() throws IOException {
		return MvsJobOutput.getStatus(this);
	}
}
