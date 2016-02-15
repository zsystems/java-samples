/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2004. All Rights Reserved
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

import com.ibm.jzos.PdsDirectory;

/**
 * Sample program that lists a PDS directory.
 * 
 * @see com.ibm.jzos.PdsDirectory
 */
public class PdsLister {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("USAGE: PdsLister DSN");
        } else {
            PdsDirectory dir = new PdsDirectory(args[0]);
            for (Iterator iter = dir.iterator(); iter.hasNext(); ) {
                PdsDirectory.MemberInfo info = (PdsDirectory.MemberInfo)iter.next();
                System.out.println(info);
            }
        }
    }
}
