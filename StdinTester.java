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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ibm.jzos.ZUtil;

/**
 * Simple class which reads from System.in (//STDIN DD in batch), 
 * and copies to System.out (//STDOUT)
 */
public class StdinTester {
    
    public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(
									new InputStreamReader(
											System.in, 
											ZUtil.getDefaultPlatformEncoding()));
		System.out.println("Reading from System.in:");
		String line;
		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		}
    }

}
