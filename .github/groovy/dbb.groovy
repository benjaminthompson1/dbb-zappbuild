// Import the necessary classes from the com.ibm.dbb.build package
import com.ibm.dbb.build.*

/**
 * The script is used for creating and managing data sets within IBM mainframe environments,
 * as well as handling COBOL source code files. 
 */

// Define the names of the datasets that will be created
def cobolDataset = "IBMUSER.DBB.COBOL" // The name of the COBOL dataset
def objDataset = "IBMUSER.DBB.OBJ"     // The name of the OBJ dataset

// Define common options for dataset creation. These are standard parameters for creating 
// a Partitioned Data Set (PDS) on an IBM mainframe. The options specified include:
// - Allocation size (cylindrical)
// - Space requirements
// - Logical record length (lrecl)
// - Dataset organization (PO)
// - Record format (F,B)
// - Dataset type (library)
// - Message level for system output
def commonOptions = "cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)"

// Create COBOL PDS using the defined dataset name and common options
new CreatePDS().dataset(cobolDataset).options(commonOptions).execute()
println("Created dataset: $cobolDataset")

// Create OBJ PDS using the defined dataset name and common options
new CreatePDS().dataset(objDataset).options(commonOptions).execute()
println("Created dataset: $objDataset")

// Define the path to the source COBOL file which needs to be copied into the COBOL PDS
def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Copy the source COBOL file to the newly created COBOL PDS. 
// The member name in the PDS where the source file will be copied to is named "HELLOW"
def copy = new CopyToPDS()
copy.setFile(file)             // Set the source file to be copied
copy.setDataset(cobolDataset)  // Specify the target dataset
copy.setMember("HELLOW")       // Set the member name within the PDS
copy.execute()
println("Copied $file to $cobolDataset(HELLOW)")

// Prepare to execute an MVS command to compile the COBOL source code using the IGYCRCTL compiler
def compile = new MVSExec()
compile.setPgm("IGYCRCTL")  // Set the program name for the COBOL compiler
compile.setParm("LIB")      // Set parameter to specify the library

// Create a DD (Data Definition) statement which will be used during the compile step.
// This DD statement defines the dataset and member that the compiler should use as its input.
def sysin = new DDStatement()
sysin.setName("SYSIN")                       // Name of the DD statement
sysin.setDsn("IBMUSER.DBB.COBOL(HELLOW)")    // Dataset and member to be used
sysin.setOptions("shr")                      // Set options for the DD statement
compile.addDDStatement(sysin)                // Add the DD statement to the compile command

