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

import com.ibm.jzos.MvsCommandCallback;
import com.ibm.jzos.MvsConsole;
import com.ibm.jzos.WtoConstants;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;
import com.ibm.jzos.ZUtil;

/**
 * This sample shows how JZOS can be used to interact with the MVS console.
 * <p>The main program enters a loop waiting for dataset names to be sent
 * via the MVS modify command.
 * <ul>
 * <li>If a modify command with APPL=DSN is received, its record count is written
 * to the console via a WTO and the datasetCount is incremented.</li>
 * <li>If a modify command with APPL=EXIT is received, the loop is abandond,
 * the number of datasets processed is written to the MVS console and the program
 * completes normally.
 * <li>If a stop command is received, the number of datasets processed is written
 * to the MVS console and the program is exited via System.exit()</li>
 * </ul>
 */
public class MvsConsoleInteraction {

	static class MyMvsCommandCallback implements MvsCommandCallback {
		public String modifyCommand = null;
		public String startParameters = null;
		public int datasetCount = 0;

		synchronized public void handleModify(String modifyCommand) {
			this.modifyCommand = modifyCommand;
			//Notify the waiting caller that a command has been received.
			notify();
		}

		public void handleStart(String startParameters) {
			this.startParameters = startParameters;
			MvsConsole.wto("START command received.  Parameters=" + 
					startParameters != null ? startParameters : "<NONE>", 
					WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
					WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);				
		}

		public boolean handleStop() {
			MvsConsole.wto("STOP command received.  Processed " + datasetCount + " dataset(s)", 
					WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
					WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);				
			System.out.println("Finished processing.");
			return true; // Allow System.exit() to be performed.
		}
		
	};
	
	/*
	 * Check to see if the supplied DSN exists.  If not, return false.  If it exists,
	 * open a ZFile on it, count the records and return true.
	 */
	private static boolean processDataset(String dsn) throws ZFileException {
		String fqDsn = ZFile.getSlashSlashQuotedDSN(dsn);
		if (!ZFile.exists(fqDsn)) {
			MvsConsole.wto("Dataset " + dsn + " does not exist.", 
					WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
					WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);	
			return false;
		}
		ZFile zFile = new ZFile(fqDsn, "rb,type=record,noseek");
		long count = 0;
		long byteCount = 0;
		try {
			byte[] recBuf = new byte[zFile.getLrecl()];
			int nRead;
			while((nRead = zFile.read(recBuf)) > 0) {
				//Process record here...
				byteCount += nRead;
				count++;
			};
			MvsConsole.wto("Processed " + byteCount + " bytes and "+ count + " records in " + zFile.getActualFilename(), 
					WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
					WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);	
		} finally {
			zFile.close();
		}
		return true;
	}
	
	/**
	 * This program issues a WTO requesting that a dataset name be sent via
	 * an operator modify command.  The resulting string is used to open
	 * a ZFile that reads the dataset and counts the number of records.
	 * <p>The program loops until the string "EXIT" is sent via the modify command
	 * or a STOP command is received.  The STOP command is handled entirely by
	 * the mvsCallback.
	 */
	public static void main(String[] args) throws Exception {
		MyMvsCommandCallback mvsCallback = new MyMvsCommandCallback();
		if (!MvsConsole.isListening()) {
			MvsConsole.startMvsCommandListener();
		}
		MvsConsole.registerMvsCommandCallback(mvsCallback);
		System.out.println("Check system console for messages.");
		String jobname = ZUtil.getCurrentJobname();
		while (true) {
			MvsConsole.wto("Java console interaction:\n   f " + jobname + ",APPL=(DSN | EXIT)\n   p " + jobname, 
					WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
					WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);				
			synchronized (mvsCallback) {
				// wait until a modify command is received.  If a stop command is received,
				// the program will exit from the callback and will not return here.
				mvsCallback.wait();
			}
			if ("EXIT".equalsIgnoreCase(mvsCallback.modifyCommand)) {
				break;
			}
			if (processDataset(mvsCallback.modifyCommand)) {
				mvsCallback.datasetCount++;
			}
		}
		MvsConsole.wto("EXIT modify command received.  Processed " + mvsCallback.datasetCount + " dataset(s)", 
				WtoConstants.ROUTCDE_PROGRAMMER_INFORMATION,
				WtoConstants.DESC_IMPORTANT_INFORMATION_MESSAGES);				
		System.out.println("Finished processing.");
	}
}
