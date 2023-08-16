// Importing required packages for IBM DBB operations
import com.ibm.dbb.build.*

// Define a variable for High-Level Qualifier (HLQ)
def hlq

// Parse the passed arguments to extract the HLQ value using '--hlq' flag
args.eachWithIndex { arg, idx ->
    // Check for '--hlq' argument and ensure it has a subsequent value
    if (arg == '--hlq' && args.size() > idx + 1) {
        hlq = args[idx + 1]
    }
}

// If HLQ is not set, inform the user and exit the script
if (!hlq) {
    println "High-Level Qualifier (--hlq) not provided!"
    return
}

println "High-Level Qualifier: $hlq"

// Create a new PDS (Partitioned Data Set) for COBOL source code
println("Creating COBOL PDS...")
new CreatePDS().dataset("${hlq}.COBOL").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()

// Create a new PDS for COBOL object code
println("Creating OBJ PDS...")
new CreatePDS().dataset("${hlq}.OBJ").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()

// Define the file (COBOL program) to be copied to the COBOL PDS
def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Copy the defined COBOL program to the created COBOL PDS
println("Copying file to COBOL PDS...")
def copy = new CopyToPDS()
copy.setFile(file)
copy.setDataset("${hlq}.COBOL")
copy.setMember("HELLOW")
copy.execute()

// Setup parameters for COBOL compile using IBM COBOL compiler (IGYCRCTL)
println("Setting up compile parameters...")
def compile = new MVSExec()
compile.setPgm("IGYCRCTL")
compile.setParm("LIB")
compile.dd(new DDStatement().name("SYSIN").dsn("${hlq}.COBOL(HELLOW)").options("shr"))
compile.dd(new DDStatement().name("SYSLIN").dsn("${hlq}.OBJ(HELLOW)").options("shr"))

// Define utility datasets for the COBOL compile operation
(1..17).each { num ->
    compile.dd(new DDStatement().name("SYSUT$num").options("cyl space(5,5) unit(vio) new"))
}
compile.dd(new DDStatement().name("SYSMDECK").options("cyl space(5,5) unit(vio) new"))
compile.dd(new DDStatement().name("TASKLIB").dsn("IGY640.SIGYCOMP").options("shr"))

// Setup the SYSPRINT dataset to capture compile logs and then copy its content to an HFS file for review
println("Setting up SYSPRINT...")
compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio) new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("/tmp/hellow.log")))

// Execute the COBOL compile
println("Executing compile...")
def rc = compile.execute()

// Check the compile's return code to determine if the compile was successful or if it failed
if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")