import com.ibm.dbb.build.*

// Define the names of the datasets to be created
def cobolDataset = "IBMUSER.DBB.COBOL"
def objDataset = "IBMUSER.DBB.OBJ"

// Define common options for dataset creation
def commonOptions = "cyl space(1,1) lrecl(80) dsorg(PO) recfm(F,B) dsntype(library) msg(1)"

// Create COBOL PDS
new CreatePDS().dataset(cobolDataset).options(commonOptions).execute()
println("Created dataset: $cobolDataset")

// Create OBJ PDS
new CreatePDS().dataset(objDataset).options(commonOptions).execute()
println("Created dataset: $objDataset")

// Define the source file
def file = new File("/u/ibmuser/zGit-Repositories/dbb-zappbuild/samples/MortgageApplication/cobol/hellow.cbl")

// Copy source file to COBOL PDS
def copy = new CopyToPDS()
copy.setFile(file)
copy.setDataset(cobolDataset)
copy.setMember("HELLOW")
copy.execute()
println("Copied $file to $cobolDataset(HELLOW)")