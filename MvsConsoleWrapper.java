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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;

import com.ibm.jzos.MvsCommandCallback;
import com.ibm.jzos.MvsConsole;

/**
 * This sample demonstrates a main program that can be used
 * to wrap another main program while redirecting System.in and 
 * System.out to the MVS console.
 */
public class MvsConsoleWrapper {

	/**
	 * The main method accepts as arguments the name of the
	 * wrapped class followed by its arguments.
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length <1 ){
			System.err.println("Usage: com.ibm.jzos.sample.MvsConsoleWrapper <class> <args>...");
			System.exit(8);
		}
		
		redirectSystemOut();
		redirectSystemIn();
		invokeMain(args);
	}
	
	static void redirectSystemOut() throws Exception {
		PipedOutputStream pos = new PipedOutputStream();
		PrintStream ps = new PrintStream(pos);
		PipedInputStream pis = new PipedInputStream(pos);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(pis));

		new Thread() {
			public void run() {
				try {
					String line = null;
					while ((line = reader.readLine()) != null) {
						MvsConsole.wto(line, 
										MvsConsole.ROUTCDE_SYSPROG_INFORMATION,
										MvsConsole.DESC_JOB_STATUS);
					}
				} catch(IOException ioe) {
					// Pipe breaks when shutting down; ignore
				}
			}
		}.start();
		System.setOut(ps);
	}
	
	static void redirectSystemIn() throws Exception {
		// This starts the MvsConsole listener if it's not already started (by the JZOS Batch Launcher)
		if (!MvsConsole.isListening()) {
			MvsConsole.startMvsCommandListener();
		}

		PipedOutputStream pos = new PipedOutputStream();
		final Writer writer = new OutputStreamWriter(pos);   // use default file.encoding
		
		MvsConsole.registerMvsCommandCallback(
				new MvsCommandCallback() {
					public void handleStart(String parms) {};
					public void handleModify(String cmd) {
						try {
							writer.write(cmd + "\n");
							writer.flush();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
					public boolean handleStop() { return true; } // System.exit() 
				});
		PipedInputStream pis = new PipedInputStream(pos);
		System.setIn(pis);
	}

	
	static void invokeMain(String[] args) throws Exception {		
		String[] mainArgs = new String[args.length-1];
		System.arraycopy(args,1,mainArgs, 0, mainArgs.length);
		Class mainClass = Class.forName(args[0]);
		Method mainMethod = mainClass.getMethod("main", new Class[]{String[].class});
		mainMethod.invoke(mainClass, new Object[]{mainArgs});
	}
}
