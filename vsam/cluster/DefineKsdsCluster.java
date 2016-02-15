/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2010. All Rights Reserved
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
package com.ibm.jzos.sample.vsam.cluster;

import com.ibm.jzos.*;

/**
 * This sample allows the creation of a VSAM cluster.
 * The argument to main is a valid dataset name.
 * The same operation can be performed using JCL by calling the IDCAMS system utility.
 * <br><br>
 * For example, given the following valid dataset name:
 * <ul>
 *  <li>TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS
 * </ul>
 * This sample will create:
 * <ul>
 *  <li>TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS
 *  <li>TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS.DATA
 *  <li>TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS.INDEX
 * </ul>
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.AccessMethodServices
 */
public class DefineKsdsCluster {
	
    public final static java.lang.String VSAM_VOLUME = "PRV000";
	
    public static void main(String[] args) {

        // this sample expects one input argument
        if (args.length != 1) {
            usage();
        }
      
        // input is expected to be a VSAM cluster name   
        String dsName = args[0];
      
        // call the create method with the input file                               
        defineCluster(ZFile.getFullyQualifiedDSN(dsName));
    }
    

    public static void defineCluster(String dsname) {
    
        try {
            System.out.println("dsName: " + dsname);  
    		
            if (dsname.startsWith("//")) {
                dsname = dsname.substring(2);
            }
            
    		dsname = dsname.toUpperCase();
    	   	    
    		String def1 = "DEFINE CLUSTER (NAME(";
    		def1 = def1.concat(dsname).concat(") -");
    		String def2 = "VOLUMES(";
    		def2 = def2.concat(VSAM_VOLUME).concat(")) - ");
    		String def3 = "DATA (NAME(";
    		def3 = def3.concat(dsname).concat(".DATA)) - ");
    		String def4 = "INDEX (NAME(";
    		def4 = def4.concat(dsname).concat(".INDEX))");
    		
    		AccessMethodServices ams = new AccessMethodServices();                      
    		ams.addInputLine(def1);
    		ams.addInputLine("INDEXED NOREUSE -");  
    		ams.addInputLine("TRK(4 4) RECSZ(80 80) KEYS(8 0) - ");      
    		ams.addInputLine(def2);
    		ams.addInputLine(def3);
    		ams.addInputLine(def4);
    
    		int rc = ams.execute();                                                     
    
    		String out = ams.getOutputLines();
    		System.out.println("IDCAMS return code: " + rc);     
    		System.out.println("IDCAMS return text: " + out);                           

        } catch (Exception e) {                                                      
            System.out.println("Failed to create VSAM: Exception caught: ");
            e.printStackTrace();
        }
    }

    
    /**                                                                           
     * Print sample usage and exit                                                
     */                                                                           
    private static void usage() {
        System.out.println("DefineKsdsCluster -- Define a VSAM KSDS Cluster.");
        System.out.println("Usage:");                                                
        System.out.println("\tjava com.ibm.jzos.sample.vsam.cluster.DefineKsdsCluster dsName");
        System.out.println("\tdsName: The fully qualified dataset name");
        System.out.println("\t\tExample dsName:");
        System.out.println("\t\t//'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS'");  
        System.out.println("\t\t    creates //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS'");
        System.out.println("\t\t            //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS.DATA'");
        System.out.println("\t\t            //'TSOUSER.PRIVATE.JZOS.SAMPLES.KSDS.INDEX'");
        System.exit(0);
    }  
}
