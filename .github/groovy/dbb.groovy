// Required classes from the com.ibm.dbb.build package are imported
import com.ibm.dbb.build.*

println("Initializing mainframe dataset management and COBOL compilation script...")

new CreatePDS().dataset("IBMUSER.DBB.COBOL").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()
new CreatePDS().dataset("IBMUSER.DBB.OBJ").options("cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)").execute()

def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")
def copy = new CopyToPDS()
copy.setFile(file)
copy.setDataset("IBMUSER.DBB.COBOL")
copy.setMember("HELLOW")
copy.execute()

def compile = new MVSExec()
compile.setPgm("IGYCRCTL")
compile.setParm("LIB")

def sysin = new DDStatement()
sysin.setName("SYSIN")
sysin.setDsn("IBMUSER.DBB.COBOL(HELLOW)")
sysin.setOptions("shr")
compile.addDDStatement(sysin)

compile.dd(new DDStatement().name("SYSPRINT").options("cyl space(5,5) unit(vio) new"))
compile.copy(new CopyToHFS().ddName("SYSPRINT").file(new File("/tmp/hellow.log")))

def rc = compile.execute()

if (rc > 4)
    println("Compile failed!  RC=$rc")
else
    println("Compile successful!  RC=$rc")