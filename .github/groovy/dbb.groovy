// Required classes from the com.ibm.dbb.build package are imported
import com.ibm.dbb.build.*

println("Debug: " + commonOptions)

/**
 * This script facilitates the creation and management of datasets in IBM mainframe environments. 
 * It is also responsible for handling COBOL source code files, including their compilation.
 */

// Define dataset names for creation
def cobolDataset = "IBMUSER.DBB.COBOL"  // COBOL dataset name
def objDataset = "IBMUSER.DBB.OBJ"      // OBJ dataset name

// Common dataset creation options
// These parameters are typically used for creating a Partitioned Data Set (PDS) on IBM mainframes.
def commonOptions = [
    "cyl space(1,1)",   // Allocation size
    "lrecl(80)",        // Logical record length
    "dsorg(PO)",        // Dataset organization
    "recfm(F,B)",       // Record format
    "dsntype(library)", // Dataset type
    "msg(1)"            // Message level for system output
].join(' ')

// Function to create a PDS with specified name and common options
def createPDS(name) {
    new CreatePDS().dataset(name).options(commonOptions).execute()
    println("Created dataset: $name")
}

// Creating COBOL and OBJ PDS
createPDS(cobolDataset)
createPDS(objDataset)

// Define path to the source COBOL file to be copied into COBOL PDS
def sourceFile = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Copy source COBOL file to COBOL PDS with "HELLOW" as the member name
new CopyToPDS(sourceFile, cobolDataset, "HELLOW").execute()
println("Copied $sourceFile to $cobolDataset(HELLOW)")

// Initialize MVS command to compile the COBOL source using IGYCRCTL compiler
def compile = new MVSExec().pgm("IGYCRCTL").parm("LIB")

// Add DD (Data Definition) statement for the compile step
compile.addDDStatement(new DDStatement().name("SYSIN").dsn("IBMUSER.DBB.COBOL(HELLOW)").options("shr"))
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio) new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("/tmp/hellow.log")))

// Execute compilation and print status based on return code
def returnCode = compile.execute()
if (returnCode > 4) {
    println("Compile failed! RC=$returnCode")
} else {
    println("Compile successful! RC=$returnCode")
}