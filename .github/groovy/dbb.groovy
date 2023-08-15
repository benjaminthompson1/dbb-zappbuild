/**
 * Mainframe Dataset Management and COBOL Compilation Script
 * 
 * This script is responsible for initializing datasets, copying COBOL source files
 * from the local repository to mainframe datasets, and then invoking the COBOL compiler
 * to compile these sources.
 */

// Import the required classes from the com.ibm.dbb.build package.
import com.ibm.dbb.build.*

println("Initializing mainframe dataset management and COBOL compilation script...")

println("Creating datasets...")

// Creation of Datasets.
new CreatePDS().dataset("IBMUSER.DBB.COBOL").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()
println("Dataset IBMUSER.DBB.COBOL created.")

new CreatePDS().dataset("IBMUSER.DBB.OBJ").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()
println("Dataset IBMUSER.DBB.OBJ created.")

println("Copying COBOL source file to dataset...")

// Define the path to the COBOL source file.
def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Copy the COBOL source file to the dataset.
def copy = new CopyToPDS()
copy.setFile(file)
copy.setDataset("IBMUSER.DBB.COBOL")
copy.setMember("HELLOW")
copy.execute()

println("COBOL source file copied to IBMUSER.DBB.COBOL(HELLOW).")

println("Setting up COBOL compiler settings...")

// Initialize the COBOL compiler for compilation.
def compile = new MVSExec()
compile.setPgm("IGYCRCTL")
compile.setParm("LIB")

// Create a DD statement for the COBOL source.
def sysin = new DDStatement()
sysin.setName("SYSIN")
sysin.setDsn("IBMUSER.DBB.COBOL(HELLOW)")
sysin.setOptions("shr")
compile.addDDStatement(sysin)

println("Setting up compiler output...")

// Create a DD statement for compiler outputs and copy them to an HFS file.
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio) new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("/tmp/hellow.log")))

println("Executing compilation process...")

// Execute the compilation process.
def rc = compile.execute()

// Check the return code and notify the user.
if (rc > 4) {
    println("Compile failed!  RC=$rc")
} else {
    println("Compile successful!  RC=$rc")
}