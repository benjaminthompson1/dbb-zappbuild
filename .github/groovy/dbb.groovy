// Importing required packages
import com.ibm.dbb.build.*

// Creating a new PDS for COBOL source
println("Creating COBOL PDS...")
new CreatePDS().dataset("IBMUSER.DBB.COBOL").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()

// Creating a new PDS for COBOL object code
println("Creating OBJ PDS...")
new CreatePDS().dataset("IBMUSER.DBB.OBJ").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()

// Defining the file to be copied to PDS
def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Initializing copy operation
println("Copying file to COBOL PDS...")
def copy = new CopyToPDS()
copy.setFile(file)
copy.setDataset("IBMUSER.DBB.COBOL")
copy.setMember("HELLOW")
copy.execute()

// Setting up COBOL compile parameters
println("Setting up compile parameters...")
def compile = new MVSExec()
compile.setPgm("IGYCRCTL")
compile.setParm("LIB")
compile.dd(new DDStatement().name("SYSIN").dsn("IBMUSER.DBB.COBOL(HELLOW)").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").dsn("IBMUSER.DBB.OBJ(HELLOW)").options("shr"))

// Defining utility datasets for the compile
(1..17).each { num ->
    compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(5,5) unit(vio) new"))
}
compile.dd(new DDStatement().name("SYSMDECK").options("cyl space(5,5) unit(vio) new"))
compile.dd(new DDStatement().name("TASKLIB").dsn("IGY640.SIGYCOMP").options("shr"))

// Setting up SYSPRINT and copying its content to an HFS file
println("Setting up SYSPRINT...")
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio) new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("/tmp/hellow.log")))

// Executing the compile
println("Executing compile...")
def rc = compile.execute()

// Checking the return code to determine success or failure
if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")