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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.ibm.jzos.ZUtil;


/**
 * Simple class used to display Java system properties and selected system environment variables
 * 
 * @see ZUtil#getEnv(String)
 */
public class ShowJavaProperties {
    
    public static void main(String[] args) {
    	
    	Properties props = System.getProperties();
    	System.out.println("System Properties:\n");
    	for (Iterator i=props.entrySet().iterator(); i.hasNext();) {
    		Map.Entry e = (Map.Entry)i.next();
    		System.out.println(e.getKey() + " -> " + e.getValue());
    	}
    	System.out.println("\nEnvironment:\n");
    	props = ZUtil.getEnvironment();
    	for (Iterator i=props.entrySet().iterator(); i.hasNext();) {
    		Map.Entry e = (Map.Entry)i.next();
    		System.out.println(e.getKey() + "=" + e.getValue());
    	}
    }

}
