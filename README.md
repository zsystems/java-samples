# java-samples
This repository contains a batch launcher and toolkit for Java applications running on z/OS.
# JZOS Toolkit API

A batch launcher and toolkit for Java applications running on z/OS.

The IBM JZOS Batch Toolkit for z/OS SDKs is a set of tools that improves many of the functional and environmental characteristics of the current Java batch capabilities on z/OS. It includes a native launcher for running Java applications directly as batch jobs or started tasks, and a set of Java methods that make access to traditional z/OS data and key system services directly available from Java applications. Additional system services include console communication, multiline WTO (write to operator), and return code passing capability. In addition, JZOS provides facilities for flexible configuration of the run-time environment, and it allows intermediate data to be seen via z/OS System Display and Search Facility (SDSF). Java applications can be fully integrated as job steps in order to augment existing batch applications.

The combination of the launcher, data access, added system services, and environmental enhancements make running Java on z/OS as batch jobs easier, particularly for traditional z/OS programmers. The net result of these enhancements is that the look and feel of running Java applications is much closer to other z/OS batch jobs, and the way Java batch can be managed is now like other z/OS batch applications written in COBOL, PL/I, or other compiled languages.

The batch launcher and toolkit extends the z/OS SDK products with a set of Java classes and additional C++ code. Java applications can be launched directly as batch jobs on z/OS along with using the custom launcher. The JZOS set of Java class libraries extends the function available in the standard Java product. The extensions provide APIs for Java access to z/OS operating system services and access to z/OS-specific data types, including VSAM data.

## JZOS Toolkit API Sample Jobs

### Class CatalogSearchSample

Sample program that uses CatalogSearch, LOCATE and OBTAIN to display information about datasets matching a filter key. The filter key is given as an argument to main().

The sample program first uses CatalogSearch to get a list of datasets matching the supplied filter key. Then, for each dataset, ZFile.locateDSN(String) is used to get the first entry of the list of MVS volumes that contain the dataset. Finally ZFile.obtainDSN(String, String) is used to obtain the format 1 DSCB information for the dataset.

If the complete lookup cannot be completed for a dataset (e.g. the volume not being mounted) a message is written and the dataset is skipped. 

### Class DynallocCopyDataset

Sample program that uses BPXWDYN to dynamically allocate a new dataset with the same attributes as a given dataset, and then copy the original to the new dataset. The name of the source dataset and target dataset names are given as arguments. 

### Class EnqUpdatePdsMember

Sample program that ENQs on a PDS / member in a manner compatible with ISPF and then updates the PDS member.

The fully qualified dataset name is given as the first argument and the member name is given as the second argument.

### Class FileFactoryCopy

  Sample program that uses the FileFactory class to copy a text file or dataset. The input and output file names are given as arguments to main, and they main be either POSIX (HFS) file names or MVS dataset names.

  If the target file is an MVS dataset, then its LRECL should be compatible with the source file/dataset.
  
  Example file names:
  > /etc/profile
  
  > //DD:INPUT
  
  > //'SYS1.MACLIB(ABEND)'
  
  > //MY.DATASET

### Class HelloWorld

  Simple class that says hello on System.out and System.err
  
### Class MvsConsoleInteraction

  This sample shows how JZOS can be used to interact with the MVS console.

  The main program enters a loop waiting for dataset names to be sent via the MVS modify command.
  > If a modify command with APPL=DSN is received, its record count is written to the console via a WTO and the datasetCount is incremented.
  
  > If a modify command with APPL=EXIT is received, the loop is abandond, the number of datasets processed is written to the MVS console and the program completes normally.
  
  > If a stop command is received, the number of datasets processed is written to the MVS console and the program is exited via System.exit()
  
### Class MvsConsoleWrapper

  This sample demonstrates a main program that can be used to wrap another main program while redirecting System.in and System.out to the MVS console.
  
### Class MvsJob

  Simple bean which holds a MVS jobname and id.
  
### Class MvsJobOutput

  Sample program which reads all sysout data for a MvsJob (jobname and jobid), and writes the output to a specified Writer. The class relies on the sample REXX script "jobOutput", spawned as a child process via the Exec class.
  
### Class MvsSubmitJob

  Sample program which submits a job to the internal reader. Getting status for an executing job requires an APF authorized program interface to the subsystem API. The TSO "STATUS" command can be executed via the REXX "TSO" command processor to obtain this information. See the sample "jobStatus" REXX script.

### Class PdsLister

  Sample program that lists a PDS directory.
  
### Class PeekOSMemory

  Sample program that dumps 500 bytes from the CVT to System.out 
  
### Class ShowJavaProperties

  Simple class used to display Java system properties and selected system environment variables
  
### Class StdinTester

  Simple class which reads from System.in ('//STDIN DD in batch'), and copies to System.out ('//STDOUT')
  
### Class ZFileCopy

  Sample program that uses the ZFile class to copy an MVS dataset in record mode from 'DD INPUT' to 'DD OUTPUT'. Note that "noseek" is used so that the file is opened in sequential mode, which dramatically increases I/O performance.
  
### Class ZFileKSDS

  Sample program that inserts, updates, locates, and deletes records in a VSAM KSDS using ZFile.

  Refer to the C++ Programmer's guide for more information on processing VSAM files with the C library. This sample assumes that the '//KSDS DD' points to a VSAM cluster that was created something like this:
  
 >       DEFINE CLUSTER - 
          (NAME(SOMENAME.CLUSTER) - 
          TRK(4 4) - 
          RECSZ(80 80) - 
          INDEXED - 
          NOREUSE - 
          KEYS(8 0) - 
          OWNER(YYYYYY) ) - 
        DATA - 
          (NAME(SOMENAME.KSDS.DA)) - 
        INDEX - 
          (NAME(SOMENAME.KSDS.IX)) 

### Class ZFilePrint

  Sample program that prints an EBCDIC MVS dataset pointed to by '//INPUT DD' to System.out (stdout).

  The dataset is opened using the ZFile class in record mode. Note that "noseek" is used so that the file is opened in sequential mode, which dramatically increases I/O performance.
  
### Class ZipDatasetSource

  A class that is used by ZipDatasets to handle the creation of zip file entries for z/OS datasets.
  Instances of this class are constructed using a dataset name or pattern, which can include:
  >    A sequential dataset or PDS name: //A.B.C
  
  >    A dataset pattern name: //A.*.D as defined by the z/OS Catalog Search facility (IGGCSI00). See CatalogSearch for more information.
  
  >    A PDS member name: //A.B.C(MEM)
   
  >    A PDS member pattern: //A.B.C(D*X)
  
  >    A DD name: //DD:XYZ which might refer to a sequential dataset, or PDS, or concatenation.
  
  >    A DD name and member: //DD:XYZ(MEM)
  
  >    A DD name and member pattern: //DD:XYZ(D*X)
  The leading "//" prefix may be omitted and names are case insensitive.

  Each dataset is zipped to the ZipOutputStream by reading the source dataset as text encoded in the default EBCDIC codepage (ZUtil.getDefaultPlatformEncoding()) and then writing the text to ZipOutputStream encoded using the supplied target encoding.

  The name given to each entry is the actual MVS dataset name in upper case. If the entry is for a PDS member, then the dataset name is used as a directory name followed by the member name as a file name. 
  
### Class ZipDatasets

  A sample Java main class that can be invoked to create a Zip archive from one or more datasets or PDS members. Datasets are treated as text and converted from EBCDIC to ASCII (ISO8859-1) or to the specified target codepage.

  Details on the input arguments to this class are available by executing the main method with no arguments. (see usage())

  Example: Zip several partitioned datasets to a Unix zip file:
  > com.ibm.jzos.sample.ZipDatasets test.zip sys1.proclib(asm*) hlq.**.jcl 

  Example: Zip all datasets matching two patterns to a dataset:
  > com.ibm.jzos.sample.ZipDatasets //hlq.backup.zip payroll.*.data gl.**.dat*
  
  Example: Zip data using DDs and input and output:
  > com.ibm.jzos.sample.ZipDatasets //DD:ZIPOUT //DD:INSEQ1 //DD:INPDS1 //DD:INPDS2(FOO*)

