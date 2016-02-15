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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.ibm.jzos.FileFactory;
import com.ibm.jzos.MvsJobSubmitter;

/**
 * Sample program which submits a job to the internal reader.
 * 
 * Getting status for an executing job requires an APF authorized program interface
 * to the subsystem API. The TSO "STATUS" command can be executed via the REXX "TSO" command
 * processor to obtain this information. See the sample "jobStatus" REXX script.
 * 
 * @since 2.4.0
 */
public class MvsSubmitJob {

    public static final long ONE_SECOND = 1000;
    public static final long TWO_SECONDS = 2 * ONE_SECOND;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
	
    /**
     * A sample main method that submits JCL read from a file and then 
     * polls its status (for up to a minute) until it is complete.
     * The first argument to main can be a Unix file/path name or a "//DATASET.NAME".
     */
    public static void main(String[] args) throws IOException {
		
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Missing main argument: filename");
        }
		
        String jobname = null;
        MvsJobSubmitter jobSubmitter = new MvsJobSubmitter();
        BufferedReader rdr = FileFactory.newBufferedReader(args[0]);
		
        try {
            String line;
            while ((line = rdr.readLine()) != null) {
                
                if (jobname == null) {
                    StringTokenizer tok = new StringTokenizer(line);
                    String jobToken = tok.nextToken();
                    
                    if (jobToken.startsWith("//")) {
                        jobname = jobToken.substring(2);
                    }
                }
                
                jobSubmitter.write(line);
            }
        }
        finally {
            if (rdr != null) {
                rdr.close();
            }
        }
        
        // Submits the job to the internal reader
        jobSubmitter.close();
        
        boolean completed = false;
		long begin = System.currentTimeMillis();
		MvsJob job = new MvsJob(jobname, jobSubmitter.getJobid());
		
		do {
			String status = job.getStatus();
			System.out.println(job + " " + status);
			
			try {
			    Thread.sleep(TWO_SECONDS);
			} catch (InterruptedException ignore) {}

			completed = !status.startsWith("WAITING") &&
			            !status.startsWith("EXECUTING");
			
		} while (!completed && System.currentTimeMillis() - begin < ONE_MINUTE);
	}
}
