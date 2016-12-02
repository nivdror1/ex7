/*
 * R13 data register
 * R14 address register
 */
import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class CodeWriter2 {
	public enum AssemblyFunction {
	    CopyAToR13,
	    CopyFromRamAddressInR14ToR13, 
	    CopyFromR13ToRamAddressInR14,
	    LoadArgumentAddressToA,
	    LoadLocalAddressToA,
	    LoadStackAddressToA,
	    LoadThatAddressLoA,
	    LoadThisAddressToA,
	    PopR13,
	    PopToD,
	    PushR13,
	    PushD
	    
	}
	private ArrayList<String> asmLines; 	// get from the original CodeWriter
	private HashMap<AssemblyFunction, String>	codeFileMap;
	private int labelCounter;
	
	public CodeWriter2(ArrayList<String> asmLines){
		labelCounter = 0;
		this.asmLines = asmLines;
		codeFileMap = new HashMap<>();
		String dirPath = "C:\\Users\\omri\\workspace\\ex7\\assemblyCode";
		codeFileMap.put(AssemblyFunction.CopyAToR13, dirPath + "\\CopyAToR13.asm");
		codeFileMap.put(AssemblyFunction.CopyFromR13ToRamAddressInR14, dirPath + "\\CopyFromR13ToRamAddressInR14.asm");
		codeFileMap.put(AssemblyFunction.CopyFromR13ToRamAddressInR14, dirPath + "\\CopyFromR13ToRamAddressInR14.asm");
		codeFileMap.put(AssemblyFunction.LoadArgumentAddressToA, dirPath + "\\LoadArgumentAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadLocalAddressToA,  dirPath + "\\LoadLocalAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadStackAddressToA, dirPath + "\\LoadStackAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadThatAddressLoA, dirPath + "\\LoadThatAddressLoA.asm");
		codeFileMap.put(AssemblyFunction.LoadThisAddressToA, dirPath + "\\LoadThisAddressToA.asm");
		codeFileMap.put(AssemblyFunction.PopR13, dirPath + "\\PopR13.asm");
		codeFileMap.put(AssemblyFunction.PopToD, dirPath + "\\PopToD.asm");
		codeFileMap.put(AssemblyFunction.PushR13, dirPath + "\\pushR13.asm");
		codeFileMap.put(AssemblyFunction.PushD, dirPath + "\\PushD.asm");
		
	}
	// ---------------------------general functions---------------------

	
	// ---------------------------arithmetic----------------------------
	public void writeArithnetic(String operation){
		switch (operation){
		case "add":
			writeAdd();
			break;
		case "sub":
			writeSub();
			break;
		case "neg":
			writeNeg();
			break;
		case "and":
			writeAnd();
			break;
		case "or":
			writeOr();
			break;
		case "not":
			writeNot();
			break;
		}
	}
	private void writeAdd(){
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M+D");
	}
	private void writeSub(){
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M-D");
	}
	private void writeNeg(){
		writeFunctionFromFile(AssemblyFunction.LoadStackAddressToA);
		asm("A=A-1");
		asm("M=-M");
	}
	private void writeAnd(){
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M&D");
	}
	private void writeOr(){
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M|D");
	}
	private void writeNot(){
		writeFunctionFromFile(AssemblyFunction.LoadStackAddressToA);
		asm("A=A-1");
		asm("M=!M");
	}
	
	// ---------------------------boolean------------------------------- 
    public void writeBoolean(String operation){
    	switch (operation){
    	case "eq":
    		writeEQ();
    		break;
    	case "lt":
    		writeLT();
    		break;
    	case "gt":
    		writeGT();
    		break;
    	default:
    		System.out.println("the boolean operation " + operation + "is not recognized");
    			
    	}
    }
	private void writeEQ(){
		writeBool("D; JEQ");
	}
	private void writeGT(){
		writeBool("D; JGT");
	}
	private void writeLT(){
		writeBool("D; JLT");
	}
	private void writeBool(String RuleOnD){
		writeSub();
		asm("@1");
		writeFunctionFromFile(AssemblyFunction.CopyAToR13);
		writeFunctionFromFile(AssemblyFunction.PopToD);
		asm("@label" + String.valueOf(this.labelCounter));
		asm(RuleOnD);//asm("D; JEQ");
		asm("@0");
		writeFunctionFromFile(AssemblyFunction.CopyAToR13);
		asm("(label" + String.valueOf(this.labelCounter) + ")");
		writeFunctionFromFile(AssemblyFunction.PushR13);
		this.labelCounter++;
		
	}
	// ---------------------------memory access functions --------------
	private void writeInsertConstantToR13(int value){
		asm("@" + String.valueOf(value));
		asm("D=A");
		asm("@R13");
		asm("M=D");
		
	}
    public void writePushPop(String operation, String memory, int address){
        if(operation.equals("push")) {
        	writePush(memory, address);
        }
        else if(operation.equals("pop")){
        	writePop(memory, address);
        }

    }
    private void writePush(String memory, int arg2){
    	if(memory.equals("constant")){
    		writeInsertConstantToR13(arg2);
    		writeFunctionFromFile(AssemblyFunction.PushR13);
    	}
    	else{	
    	}
    }
    private void writePop(String memory, int address){
    	if(memory.equals(null)){
    		writeFunctionFromFile(AssemblyFunction.PopToD);
    	}
    	else{  		
   		}	
    }

	
	private void writeFunctionFromFile(AssemblyFunction function){
		String path = this.codeFileMap.get(function);
		File file= new File(path);
		if(file.exists()){
			FileReader asmFile;
			try {
				asmFile = new FileReader(file);
				BufferedReader reader =new BufferedReader(asmFile);
				String line;
				while((line= reader.readLine())!=null) // add the lines to the container
                {
                    asm(line);
                }
				asmFile.close();
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Files error   " + path);
				e.printStackTrace();
			}// define the BufferReader and BufferWriter
            
		}else{
			System.out.println("error! function file " + path + "not exist!!!");
		}
	}
	/**
	 * write single command
	 */
	private void asm(String asmLine){
		this.asmLines.add(asmLine);
	}

}
