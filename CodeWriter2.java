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
	    CopyAToR14,
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
	    PushD, 
	    AdvanceStack
	    
	}

	private ArrayList<String> asmLines; 	// get from the original CodeWriter
	private HashMap<AssemblyFunction, String>	codeFileMap;

	private int labelCounter;
	
	public CodeWriter2(ArrayList<String> asmLines){
		labelCounter = 0;
		this.asmLines = asmLines;
		codeFileMap = new HashMap<>();
		String dirPath = "assemblyCode";//"C:\\Users\\omri\\workspace\\ex7\\assemblyCode";
		codeFileMap.put(AssemblyFunction.CopyAToR13, dirPath + "\\CopyAToR13.asm");
		codeFileMap.put(AssemblyFunction.CopyAToR14, dirPath + "\\CopyAToR14.asm");
		codeFileMap.put(AssemblyFunction.CopyFromR13ToRamAddressInR14, dirPath + "\\CopyFromR13ToRamAddressInR14.asm");
		codeFileMap.put(AssemblyFunction.CopyFromR13ToRamAddressInR14, dirPath + "\\CopyFromR13ToRamAddressInR14.asm");
		codeFileMap.put(AssemblyFunction.LoadArgumentAddressToA, dirPath + "\\LoadArgumentAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadLocalAddressToA,  dirPath + "\\LoadLocalAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadStackAddressToA, dirPath + "\\LoadStackAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadThatAddressLoA, dirPath + "\\LoadThatAddressToA.asm");
		codeFileMap.put(AssemblyFunction.LoadThisAddressToA, dirPath + "\\LoadThisAddressToA.asm");
		codeFileMap.put(AssemblyFunction.PopR13, dirPath + "\\PopR13.asm");
		codeFileMap.put(AssemblyFunction.PopToD, dirPath + "\\PopToD.asm");
		codeFileMap.put(AssemblyFunction.PushR13, dirPath + "\\pushR13.asm");
		codeFileMap.put(AssemblyFunction.PushD, dirPath + "\\PushD.asm");
		codeFileMap.put(AssemblyFunction.AdvanceStack, dirPath + "\\AdvanceStack.asm");
		

		
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
		asm("// ---add---");
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M+D");
		asm("//// ---add-end---");
	}
	private void writeSub(){
		asm("//// ---sub---");
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M-D");
		asm("//// ---sub-end---");
	}
	private void writeNeg(){
		asm("//// ---neg---");
		writeFunctionFromFile(AssemblyFunction.LoadStackAddressToA);
		asm("A=A-1");
		asm("M=-M");
		asm("//// ---neg-end---");
	}
	private void writeAnd(){
		asm("//// ---and---");
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M&D");
		asm("//// ---and-end---");
	}
	private void writeOr(){
		asm("//// ---or---");
		writeFunctionFromFile(	AssemblyFunction.PopToD					);
		writeFunctionFromFile(	AssemblyFunction.LoadStackAddressToA	);
		asm("A=A-1");
		asm("M=M|D");
		asm("//// ---or-end---");
	}
	private void writeNot(){
		asm("//// ---not---");
		writeFunctionFromFile(AssemblyFunction.LoadStackAddressToA);
		asm("A=A-1");
		asm("M=!M");
		asm("//// ---not-end---");
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
		asm("A=-A");
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
    	switch (memory){
    	case "constant":
    		asm("//// ----- push constant -------");
    		writeInsertConstantToR13(arg2);
    		writeFunctionFromFile(AssemblyFunction.PushR13);
    		asm("//// ----- push constant end -------");
    		break;
    	case "argument":
    		asm("//// ----- push argument -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadArgumentAddressToA);
    		pushSub_arg_const_this_that();
    		asm("//// ----- push argument end-------");
    		break;
    	case "local":
    		asm("//// ----- push local -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadLocalAddressToA);
    		pushSub_arg_const_this_that();
    		asm("//// ----- push local end-------");
    		break;
    	case "this":
    		asm("//// ----- push this -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadThisAddressToA);
    		pushSub_arg_const_this_that();
    		asm("//// ----- push this end-------");
    		break;
    	case "that":
    		asm("//// ----- push that -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadThatAddressLoA);
    		pushSub_arg_const_this_that();
    		asm("//// ----- push that end-------");
    		break;
    	case "temp":
    		asm("//// ----- push temp -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		asm("@5");
    		pushSub_arg_const_this_that();
    		asm("//// ----- push temp end-------");
    		break;
    	case "pointer":
    		asm("//// ----- push pointer -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		asm("@3");
    		pushSub_arg_const_this_that();
    		asm("//// ----- push pointer end-------");
    		break;
    	case "static":
    		asm("//// ----- push static -------");
    		asm("@" + String.valueOf(arg2));
    		asm("D=A");
    		asm("@16");
    		pushSub_arg_const_this_that();
    		asm("//// ----- push static end-------");
    		break;    		
    		
    	}
    }
    
    private void pushSub_arg_const_this_that(){
		asm("A=A+D");
		asm("// A have now the address of the data to take from the ram");
		asm("D=M");
		asm("@R13");
		asm("M=D");
		asm("// R13 now have the data");
		
		writeFunctionFromFile(AssemblyFunction.LoadStackAddressToA);
		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
		writeFunctionFromFile(AssemblyFunction.AdvanceStack);
    }
    
    private void writePop(String memory, int address){
    	switch (memory){
    	
    	case "argument":
    		asm("//// -- pop argument -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadArgumentAddressToA);
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop argument end -- ");
    		break;
    	case "local":
    		asm("//// -- pop local -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadLocalAddressToA);
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop local end -- ");
    		break;
    	case "this":
    		asm("//// -- pop this -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadThisAddressToA);
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop this end -- ");
    		break;
    	case "that":
    		asm("//// -- pop that -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		writeFunctionFromFile(AssemblyFunction.LoadThatAddressLoA);
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop that end -- ");
    		break;
    	case "temp":
    		asm("//// -- pop temp -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		asm("@5");
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop temp end -- ");
    		break;
    	case "pointer":
    		asm("//// -- pop pointer -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		asm("@3");
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop pointer end -- ");
    		break;
    	case "static":
    		asm("//// -- pop static -- ");
    		asm("@" + String.valueOf(address));
    		asm("D=A");
    		asm("@16");
    		asm("A=D+A");
    		writeFunctionFromFile(AssemblyFunction.CopyAToR14);
    		writeFunctionFromFile(AssemblyFunction.PopR13);
    		writeFunctionFromFile(AssemblyFunction.CopyFromR13ToRamAddressInR14);
    		asm("//// -- pop static end -- ");
    		break;
    	//case (null):
    	default:
    		writeFunctionFromFile(AssemblyFunction.PopToD);
    		break;
    		
    	}
    	/*if(memory.equals(null)){
    		writeFunctionFromFile(AssemblyFunction.PopToD);
    	}
    	else{  		
   		}*/	
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
