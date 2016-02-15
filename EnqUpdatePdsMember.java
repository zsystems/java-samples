/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * Copyright IBM Corp. 2008. All Rights Reserved
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
import java.util.Date;

import com.ibm.jzos.Enqueue;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZUtil;

/**
 * Sample program that ENQs on a PDS / member in a manner
 * compatible with ISPF and then updates the PDS member.
 * <p>
 * The fully qualified dataset name is given as the first
 * argument and the member name is given as the second argument.
 * <p>
 * @see com.ibm.jzos.Enqueue
 * @see com.ibm.jzos.ZFile#allocDummyDDName()
 * @see com.ibm.jzos.ZFile#getFullyQualifiedDSN(String, boolean)
 * @see com.ibm.jzos.ZFile#bpxwdyn(String)
 * @since 2.3.0
 */
public class EnqUpdatePdsMember {
	
	private String ddname;
	private boolean allocated;
	private String pdsName;
	private String memberName;
	private Enqueue enqMember;
	private Enqueue enqDSN;
	

	public static void main(String[] args) throws Exception {
    	
    	if (args.length < 2) {
    		System.out.println("Usage: fully.qualfied.pds.name  membername");
    		System.exit(8);
    	}

    	EnqUpdatePdsMember instance = new EnqUpdatePdsMember(args[0], args[1]);
		instance.update();
    }
	
    public EnqUpdatePdsMember(String pdsName, String memberName) {
    	this.pdsName = ZFile.getFullyQualifiedDSN(pdsName, true);
    	this.memberName = memberName.toUpperCase();
    }
    
    /**
     * Update a given fully qualified PDS name and membername.
     * Catch exceptions and unwind/cleanup any resources that were obtained.
     * <p>
     * @return true if updated, false if in use
     */
    public synchronized boolean update() throws IOException {
	   
	   try {
		   doAllocate();
		   if (!doEnqMember()) return false;
		   doEnqDSN();
		   doUpdateMember();
		   return true;
	   } finally {
		   doReleaseEnqDSN();
		   doReleaseEnqMem();
		   doUnallocate();
	   }
    }
	
    
    /**
     * Allocate the PDS  using BPXWDYN with DISP=SHR
	 * The "reuse" keyword allows us to reuse the previous DD DUMMY that we allocated
	 * This will throw a RcException if fails, and issue a "Write-to-programmer" message
	 * to the job log.
	 */
    private void doAllocate() {
		ddname = ZFile.allocDummyDDName();
		ZFile.bpxwdyn("alloc fi(" + ddname + ") da(" + pdsName	+ ") reuse shr msg(wtp)");
		allocated = true;
		System.out.println("Allocated " + pdsName + " to DD:" + ddname + " DISP=SHR");
    }
    
    /**
     * Obtain an ENQ on RNAME=SPFEDIT, QNAME=PDSNAME + Member name.
     * This enqueue will fail by throwing an RcException if the member is in use. 
     * @return true if the ENQ is obtained (not in use), false if in use
     */
    private boolean doEnqMember() {
		// Following the ISPF convention, we first ENQ on the PDS and member name,
		// padding with blanks between and after.
		StringBuffer rnameMem = new StringBuffer();
		rnameMem.append(pdsName);
		while (rnameMem.length() < 44) rnameMem.append(' ');
		rnameMem.append(memberName);
		while (rnameMem.length() < 52) rnameMem.append(' ');
		
		enqMember = new Enqueue("SPFEDIT", rnameMem.toString());
		enqMember.setScope(Enqueue.ISGENQ_SCOPE_SYSTEMS);
		enqMember.setControl(Enqueue.ISGENQ_CONTROL_EXCLUSIVE);
		enqMember.setContentionActFail();  // fail if in use
		try {
			enqMember.obtain();
			System.out.println("Obtained ENQ on SPFEDIT/\"" + rnameMem + "\"");
		} catch (RcException rce) {
			if (rce.getRc() == Enqueue.ISGENQ_RSN_NOTIMMEDIATELYAVAILABLE) {
				System.out.println(pdsName + "(" + memberName + ") is being used by someone else");
				return false;
			} else if (rce.getRc() == Enqueue.ISGENQ_RSN_UNPROTECTEDQNAME) {
				// this Rc means that an APF authorized called obtained a regular QNAME,
				// and is not an error (the ENQ was obtained)
			} else {
				throw rce; 
			}
		}
		return true;
    }
    
    
    /**
     * Obtain an ENQ on RNAME=SPFEDIT, QNAME=PDSNAME.
     * This enqueue will wait if there is contention with another job, 
     * since the resource should only be held while the member is actually being updated. 
     */
    private void doEnqDSN() {
    	
		StringBuffer rnameDSN = new StringBuffer();
		rnameDSN.append(pdsName);
		while (rnameDSN.length() < 44) rnameDSN.append(' ');
		
		enqDSN = new Enqueue("SPFEDIT", rnameDSN.toString());
		enqDSN.setScope(Enqueue.ISGENQ_SCOPE_SYSTEMS);
		enqDSN.setControl(Enqueue.ISGENQ_CONTROL_EXCLUSIVE);
		enqDSN.setContentionActWait();  // wait if in use
		try {
			enqDSN.obtain();
			System.out.println("Obtained ENQ on SPFEDIT/\"" + rnameDSN + "\"");
		} catch (RcException rce) {
			if (rce.getRc() == Enqueue.ISGENQ_RSN_UNPROTECTEDQNAME) {
				// this Rc means that an APF authorized called obtained a regular QNAME,
				// and is not an error (the ENQ was obtained)
			} else {
				throw rce; 
			}
		}
    }
    
    
    /** 
     * Open the member and write a line to it
     */
    private void doUpdateMember() throws IOException {
    	
		ZFile zFile = null;
		try {
            zFile = new ZFile("//DD:" + ddname + "(" + memberName + ")", "wt");
            String line = "updated by EnqUpdatePdsMember at " + (new Date());
            zFile.write(line.getBytes(ZUtil.getDefaultPlatformEncoding()));
            
            System.out.println("Updated " + pdsName + "(" + memberName + ")");
        } finally {
        	if (zFile != null) zFile.close();
        }
    }
   
   /**
    * Free the DD allocation
    */
   private void doUnallocate() {
	   if (allocated) {
		   try {
			   ZFile.bpxwdyn("free fi(" + ddname + ") msg(wtp)");
			   System.out.println("Freed DD:" + ddname);
		   } catch(RcException rce) {
			   rce.printStackTrace();
		   }
	   }
   }
   
   /**
    * Release the Member ENQ
    */
   private void doReleaseEnqMem() {
	   if (enqMember != null && enqMember.getEnqToken() != null) {
		   try {
			   enqMember.release();
			   System.out.println("Released ENQ on \"" + enqMember.getRName() + "\"");
		   } catch(RcException rce) {
			   rce.printStackTrace();
		   }
	   }
   }
   
   /**
    * Release the DSN ENQ
    */
   private void doReleaseEnqDSN() {
	   
	   if (enqDSN != null && enqDSN.getEnqToken() != null) {
		   try {
			   enqDSN.release();
			   System.out.println("Released ENQ on \"" + enqDSN.getRName() + "\"");
		   } catch(RcException rce) {
			   rce.printStackTrace();
		   }
	   }	   
   }
   
}
