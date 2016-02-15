/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * Copyright IBM Corp. 2010. All Rights Reserved
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
 * This sample demonstrates how to create a VSAM Alternate Index (AIX).
 * The arguments to main are the VSAM cluster name, AIX dataset name, and a
 * flag indicating key uniqueness.
 * 
 * @since 2.4.0
 * @see com.ibm.jzos.AccessMethodServices
 */
public class DefineKsdsAIX {

	public final static java.lang.String VSAM_VOLUME = "PRV000";

	/**
	 * The main method accepts the VSAM cluster name, AIX dataset
	 * name, and a flag indicating key uniqueness as the only arguments
	 * and calls <code>defineAltIndex(String,String,String,int,int)</code>
	 * to create the Alternate Index (AIX).
	 * 
	 * @param args The arguments to main
	 */
	public static void main(String[] args) {

		// this sample expects three input arguments
		if (args.length != 3) {
			usage();
		}

		// VSAM cluster name
		String clusterName = args[0];
		
		// VSAM AIX dataset name
		String indexFileName = args[1];
		
		// unique key flag
		String unique = args[2];

		defineAltIndex(ZFile.getFullyQualifiedDSN(clusterName),
		        ZFile.getFullyQualifiedDSN(indexFileName), unique, 8, 4);
	}

	
	private static void defineAltIndex(
	        String         clusterName,
	        String         indexFileName, 
			String         unique,
			int            keyLength,
			int            keyOffset) {

		try {

			if (clusterName.startsWith("//")) {
				clusterName = clusterName.substring(2);
			}
			
			if (indexFileName.startsWith("//")) {
                indexFileName = indexFileName.substring(2);
            }
			
			clusterName = clusterName.toUpperCase();
			indexFileName = indexFileName.toUpperCase();
			unique = unique.toUpperCase();
			
			System.out.println("clusterName: " + clusterName);
			System.out.println("indexFileName: " + indexFileName);
			System.out.println("unique: " + unique);

			String def1 = "DEFINE AIX  ";
			def1 = def1.concat("(NAME(").concat(indexFileName).concat(".AIX)    - ");
			
			String def2 = "RELATE(";
			def2 = def2.concat(clusterName).concat(") - ");
			
			String def3 = "RECORDS(80)    -  ";
			
			String def4 = "KEYS(";
			if (unique.equals("T")) {
				def4 = def4.concat("8").concat(" ").concat("4").concat(")      -  ");
			} else {
				def4 = def4.concat("12").concat(" ").concat("3").concat(")      -  ");
			}
			
			String def5;
			if (unique.equals("T")) {
				def5 = "UNIQUEKEY     - ";
			} else {
				def5 = "NONUNIQUEKEY     - ";
			}
			
			String def6 = "VOLUMES(";
			def6 = def6.concat(VSAM_VOLUME).concat(")) - ");
			
			String def7 = "DATA  -  ";
			String def8 = "(NAME(";
			def8 = def8.concat(indexFileName).concat(".AIX.DATA)").concat(")    - ");
			
			String def9 = "INDEX  -  ";
			String def10 = "(NAME(";
			def10 = def10.concat(indexFileName).concat(".AIX.INDEX))");
			
			String def11 = "                                             ";
			
			String def12 = "DEFINE  PATH  -    ";
			String def13 = "(NAME(";
			def13 = def13.concat(indexFileName).concat(".PATH) - ");
			
			String def14 = "PATHENTRY(";
			def14 = def14.concat(indexFileName).concat(".AIX) - ");
			String def15 = "UPDATE )";

			String def16 = "                                             ";
			String def17 = "BLDINDEX   -       ";
			String def18 = "INDATASET(";
			def18 = def18.concat(clusterName).concat(") -  ");
			String def19 = "OUTDATASET(";
			def19 = def19.concat(indexFileName).concat(".AIX)");
			
			AccessMethodServices ams = new AccessMethodServices();
			ams.addInputLine(def1);
			ams.addInputLine(def2);
			ams.addInputLine(def3);
			ams.addInputLine("UPGRADE -");
			ams.addInputLine("REUSE - ");
			ams.addInputLine(def4);
			ams.addInputLine(def5);
			ams.addInputLine(def6);
			ams.addInputLine(def7);
			ams.addInputLine(def8);
			ams.addInputLine(def9);
			ams.addInputLine(def10);
			ams.addInputLine(def11);
			ams.addInputLine(def12);
			ams.addInputLine(def13);
			ams.addInputLine(def14);
			ams.addInputLine(def15);
			ams.addInputLine(def16);
			ams.addInputLine(def17);
			ams.addInputLine(def18);
			ams.addInputLine(def19);
			
			int rc = ams.execute();
			String out = ams.getOutputLines();

			System.out.println("IDCAMS return code: " + rc);
			System.out.println("IDCAMS return text: " + out);

		} catch (Exception e) {
			System.out.println("Failed to create VSAM AIX: Exception caught: ");
			e.printStackTrace();
		}
	}

	/**
	 * Print sample usage and exit
	 */
	private static void usage() {
	    System.out.println("DefineKsdsAIX -- Define a VSAM Alternate Index (AIX).");
		System.out.println("Usage:");
		System.out.println("\tjava com.ibm.jzos.sample.vsam.file.DefineKsdsAIX clusterName indexName uniqueKey");
		System.out.println("\tclusterName: The name of the VSAM cluster the alternate index is based on");
		System.out.println("\tindexName: The name of the VSAM alternate index");
		System.out.println("\tuniqueKey: Whether unique keys are to be used");
		System.out.println("\t\tExample clusterName:");
		System.out.println("\t\tVSAM Dataset: //'TSOUSER.PRIVATE.JZOS.SAMPLES.VSAM'");
		System.out.println();
		System.exit(0);
	}
}
