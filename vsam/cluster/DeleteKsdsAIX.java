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
 * This sample deletes the VSAM Alternate Index (AIX) created by DefineKsdsAIX.
 * The argument to main must be a valid Alternate Index name created for use with the sample code.
 * This same operation can be performed using JCL by calling the IDCAMS system utility.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.AccessMethodServices
 */
public class DeleteKsdsAIX {
    
    public static void main(String[] args) {
                                                                                  
        // this sample expects only one input argument                          
        if (args.length != 1) {                                                       
            usage();
        }
                                                                                
        // input is expected to be a valid VSAM AIX dataset   
        String dsName = args[0];
                                                                                
        // call the delete method with the input file
        deleteAIX(ZFile.getFullyQualifiedDSN(dsName));
    }


    public static void deleteAIX(String dsname) {

        try {   
            System.out.println("dsName: " + dsname);   
	    
            if (dsname.startsWith("//")) {
                dsname = dsname.substring(2);
            }
            
            dsname = dsname.toUpperCase();
		
            String def1 = "DELETE ";
            def1 = def1.concat(dsname).concat(".AIX").concat(" -");
            String def2 = "ALTERNATEINDEX";
	    
            AccessMethodServices ams = new AccessMethodServices();         
            ams.addInputLine(def1);
            ams.addInputLine(def2);
	                                                                   
            int rc = ams.execute();                                        
            String out = ams.getOutputLines();                             	                                                                   
            System.out.println("IDCAMS return code: " + rc);
            System.out.println("IDCAMS return text: " + out);
	                                                                               
        } catch (Exception e) {                                                      
            System.out.println("Failed to delete VSAM: Exception caught: ");             
            e.printStackTrace();                                                         
        }                                                                             
    }                                                                              
	                                                                                
    /**                                                                           
     * Print sample usage and exit                                                
     */                                                                           
    private static void usage() {                                                 
        System.out.println("DeleteKsdsAIX -- Delete a VSAM AIX.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.vsam.cluster.DeleteKsdsAIX dsName");
        System.out.println("\tdsName: The fully qualified dataset name");
        System.out.println("\t\tExample dsName:");    
        System.out.println("\t\t//'TSOUSER.PRIVATE.JZOS.SAMPLES.AIX'"); 
        System.out.println();                                                        
        System.exit(0);                                                              
    }                                                                             
}
