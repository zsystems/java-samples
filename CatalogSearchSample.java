/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2006. All Rights Reserved
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

import java.io.PrintWriter;

import com.ibm.jzos.CatalogSearch;
import com.ibm.jzos.CatalogSearchField;
import com.ibm.jzos.Format1DSCB;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZUtil;

/**
 * Sample program that uses CatalogSearch, LOCATE and OBTAIN to display information about
 * datasets matching a filter key.  The filter key is given as an argument to main().
 * <p>
 * The sample program first uses {@link CatalogSearch} to get a list of datasets matching the supplied
 * filter key.  Then, for each dataset, {@link ZFile#locateDSN(String)} is used to get the first entry
 * of the list of MVS volumes that contain the dataset.  Finally {@link ZFile#obtainDSN(String, String)}
 * is used to obtain the format 1 DSCB information for the dataset.
 * <p>
 * If the complete lookup cannot be completed for a dataset (e.g. the volume not being mounted) a message
 * is written and the dataset is skipped.
 * <p/>
 * @since 2.1.0
 */
public class CatalogSearchSample {

	private static int INVALID_FILTER_KEY = 122;
	private static int CATALOG_ERROR = 100;
	
	public static void main(String[] args) throws Exception {
		PrintWriter writer = new PrintWriter(System.out);
		if (args.length < 1) {
			writer.println("USAGE: CatalogSearchSample <filter_key> [entry_types]");
			writer.flush();
			System.exit(1);
		}
		String filterKey = args[0].toUpperCase();
		writer.println("Performing Catalog Search with filter key: " + filterKey);

		CatalogSearch catSearch = new CatalogSearch(filterKey, 64000);
		if (args.length == 2) {
			catSearch.setEntryTypes(args[1]);
		}
		int datasetCount = 0;

		try {
			catSearch.addFieldName("ENTNAME");
			catSearch.addFieldName("VOLSER");
			catSearch.search();

			while (catSearch.hasNext()) {
				CatalogSearch.Entry entry = (CatalogSearch.Entry)catSearch.next();
				if (entry.isDatasetEntry()) {
					datasetCount++;
					CatalogSearchField field = entry.getField("ENTNAME");
					String dsn = field.getFString().trim();
					String qdsn = "'" + dsn + "'";	//Specify that the dsn is fully qualified.
					field = entry.getField("VOLSER");
					String volser = field.getFString().trim();
					if (volser == null || volser.length() == 0) {
						writer.println(qdsn + " has no VOLSER");
						continue;
					}

					//If the VOLSER is a system symbol, attempt to resolve it.
					String resolvedName = volser;
					if (volser.indexOf('&') != -1) {
						try {
							resolvedName = ZUtil.substituteSystemSymbols(volser, true);
						} catch (RcException rce) {
							writer.println("Could not resolve symbolic VOLSER '" + volser + "'.  Skipping...");
							continue;
						}
						writer.println("...Resolved '"+volser+"' as '"+ resolvedName);
					}						

					//Use OBTAIN to get the dataset's format 1 DSCB
					try {
						Format1DSCB dscb = ZFile.obtainDSN(qdsn, resolvedName);
						writer.println(qdsn + " on " + resolvedName + 
								" LRECL=" + dscb.getDS1LRECL() +
								" BLKSIZE=" + dscb.getDS1BLKL());
					} catch (RcException rce) {
						String reason = "";
						if (rce.getRc() == 4) {
							reason = " Volume not mounted";						
						} else if (rce.getRc() == 8) {
							reason = " Volume does not contain a format 1 DSCB for dataset";						
						}
						writer.println(qdsn + " on " + resolvedName + reason);					
					}
				}
			}
		} catch(RcException rce) {
			if (rce.getRc() == 4 && catSearch.getRc() == INVALID_FILTER_KEY) {
				//Invalid filter key case
				writer.println("Invalid filter key provided: " + filterKey);
				writer.flush();
			} else if (rce.getRc() == 4 && catSearch.getRc() == CATALOG_ERROR) {
				//See if some information can be obtained from the entries.
				try {
					while (catSearch.hasNext()) {
						CatalogSearch.Entry entry = (CatalogSearch.Entry)catSearch.next();
						if (entry.hasError()) {
							writer.println("Entry Exception: ENTRY_RC=" + entry.getRc() + ", ENTRY_Reason=" + entry.getReason());
							writer.flush();
						}
					}
				} catch (RcException rce2) {
					throw rce2;
				}
			} else {
				throw rce;
			}
		}
		writer.println(datasetCount + " datasets matched filter key " + filterKey + ".");
		writer.flush();
	}
}
