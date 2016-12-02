import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;


/**
 * this class translates the vm code into asm code
 */
public class CodeWriter {

    /**operations*/
    private static final String PUSH ="push";
    private static final String POP ="pop";
    private static final String ADD= "add";
    private static final String SUB="sub";
    private static final String EQ="eq";
    private static final String LT="lt";
    private static final String GT="gt";
    private static final String NEG="neg";
    private static final String AND="and";
    private static final String OR="or";
    private static final String NOT="not";
    
    // omri 
    private static final String LOCAL ="local";
    private static final String ARGUMENT="argument";
    private static final String THIS="this";
    private static final String THAT="that";
    private static final String POINTER="pointer";
    private static final String TEMP="TEMP";
    private static final String SP="sp";

    /**signify the address of the data */
    private static final String AT="@";

    /** asm language specification*/
    private static final String M="M";
    private static final String D="D";
    private static final String A="A";
    private static final String EQUAL="=";
    private static final String PLUS="+";
    private static final String MINUS="-";
    private static final String ZERO="0";
    private static final String ONE="1";

    /** the memory segment*/
    private static final String CONSTANT= "constant";

    /** the unique instance of CodeWriter*/
    private static CodeWriter codeWriter= null;

    /** this ArrayList signify the output asm code*/
    private ArrayList<String> asmLines;

    /** a memory segment instance*/
    private MemorySegment segment;

    private HashMap<String, Integer> segmentBaseMap;
    
    private CodeWriter2 writer;
    /** a singleton constructor*/
    private CodeWriter(){
        this.asmLines= new ArrayList<>();
        this.segment=new MemorySegment();
        this.segmentBaseMap = new HashMap<String, Integer>(100);
        segmentBaseMap.put(SP, 0);
        segmentBaseMap.put(LOCAL, 1);
        segmentBaseMap.put(ARGUMENT, 2);
        segmentBaseMap.put(THIS, 3);
        segmentBaseMap.put(THAT, 4);
        segmentBaseMap.put(TEMP, 5);
        this.writer = new CodeWriter2(this.asmLines);
    }


    /**
     * get the instance of CodeWriter
     * @return return codeWriter
     */
    public static CodeWriter getCodeWriter(){
        if(codeWriter==null) // if the instance hasn't been made then create it
        {
            codeWriter =new CodeWriter();
        }
        return codeWriter;
    }

    /**
     * get asmLines
     * @return return the translated asm text
     */
    public  ArrayList<String> getAsmLines(){
        return this.asmLines;
    }

    /**
     * translate the vm line into asm lines
     * @param operation the type of operation
     * @param memory a memory segment
     * @param number represent a data or an address
     */
    public void translate(String operation, String memory, int number){
        /*if(operation.equals(PUSH)) {
            pushData(memory, number);
        }
        else if(operation.equals(POP)){
            popData(memory);
        }*/ //omri
        writer.writePushPop(operation, memory, number);

    }
    // omri////////////////////////////////////////////////////////////////////////
    public void writePushPop(String operation, String memory, int address){
        if(operation.equals(PUSH)) {
        	writePush(memory, address);
        }
        else if(operation.equals(POP)){
        	writePop(memory, address);
        }

    }
    
    private void writePush(String memory, int arg2){
    	/*
    	 * case local, argument, this, that
    	 * 
    	 */
    	//asmLines.add();
    	if(memory.equals(CONSTANT)){
    		//@13
    		int tmpReg = 13;
    		writeInsertConstantToRx(arg2, tmpReg);
    		writePushRx(tmpReg);
    	}
    	else{//case local, argument, this, that

    		writeComputeFisicalAdrassStoreInRx(memory, arg2, 14);
    		//writeCopyFromAddressInRxToRx2(14, 13);
    		writePushRx(13);

    		    		
    	}
    	
    }
    private void writeInsertConstantToRx(int value, int x){
    	if((x<13)||(x>15)){
    		System.out.println("error! using R[13-15] , x wrong");
    	}
    	asmLines.add("@"+String.valueOf(value));
    	asmLines.add("D=A");
    	asmLines.add("@"+String.valueOf(x));
    	asmLines.add("M=D");
    	// @value
    	// D=A
    	// @x
    	// M=D
    }
    private void writePop(String memory, int address){
    	if(memory.equals(null)){
    		writePopRx(13);
   	}
   	else{//case local, argument, this, that
   		writeComputeFisicalAdrassStoreInRx(memory, address, 14);	// compute destination address
   		writePopRx(13);		// stack -> Ram[13]
   		//writeCopyFromRxToAddressIsInRx2(13, 14);
   		    		
   	}	
    }
    
    //	push the value from Ram[x] to the stack. x in [13,15]
    private void writePushRx(int x){
    	/*
    	 * check x in [13,15]
    	 * @x
    	 * D=M
    	 *	the value of R13 is in D register
    	 * @SP
    	 * A=M
    	 * 	A point to the stack
    	 * M=D
    	 * 	the value is written in the stack memory
    	 * @sp
    	 * M=M+1
    	 * 	the stack index is now ++
    	 */
    	if((x<13)||(x>15)){
    		System.out.println("error! using R[13-15] , x wrong");
    	}
    	asmLines.add("@"+String.valueOf(x));
    	asmLines.add("D=M");
    	asmLines.add("@"+String.valueOf(segmentBaseMap.get("SP")));
    	asmLines.add("A=M");
    	asmLines.add("M=D");
    	asmLines.add("@"+String.valueOf(segmentBaseMap.get("SP")));
    	asmLines.add("M=M+1");
    	
    }

    //	pop from the stack to Ram[x]. x in [13,15]
    private void writePopRx(int x){
    	/*
    	 * check x in [13,15]
    	 * @SP
    	 * M=M-1
    	 * 	stack pointer decreased
    	 * A=M
    	 * 	A points to the stack
    	 * D=M
    	 * 	D value is the needed value
    	 * @x
    	 * M=D
    	 * 	finito
    	 */
    	if((x<13)||(x>15)){
    		System.out.println("error! using R[13-15] , x wrong");
    	}
    	writePopToD();
    	asmLines.add("@" + String.valueOf(x));
    	asmLines.add("M=D");
    }
    
    private void writePopToD(){
    	asmLines.add("@"+String.valueOf(segmentBaseMap.get("SP")));
    	asmLines.add("M=M-1");
    	asmLines.add("A=M");
    	asmLines.add("D=M");
    }
    
    // x2 is empty memory cell in range [13,15], used by the method


    private void writeComputeFisicalAdrassStoreInRx(String segment, int subAdress, int x){
   	 /*
   	 * check x in [13,15]
   	 * @subAddress
   	 * D=A
   	 * @segment
   	 * D=M+D
   	 * 	D is now the memroy fisical address
   	 * @x2
   	 * M=D
   	 * 	now Ram[x] = src fisical address, Ram[x2] = fisical dest address
   	 */
    	if((x<13)||(x>15)){
    		System.out.println("error! using R[13-15] , x wrong");
    	}
    	asmLines.add("@"+String.valueOf(subAdress));
    	asmLines.add("D=A");
    	asmLines.add("@"+String.valueOf(segmentBaseMap.get(segment)));
    	asmLines.add("D=M+D");
    	asmLines.add("@"+String.valueOf(x));
    	asmLines.add("M=D");
    }

    /**
     * perform the operation push
     * @param memory a string that represent the memory segment.
     * @param data the data that being inserted into to the stack
     */
    private void pushData(String memory,int data){
    	System.out.println("pushData");
        if(memory.equals(CONSTANT)){
            asmLines.add(AT+data);
            asmLines.add(D+EQUAL+A); //assign d register with the data
            asmLines.add(AT+segment.getSp()); //get the address אחי, WTF????? מה נראה לך? זה עבודה זה?
            asmLines.add(M+EQUAL+D); // assign a M register with the data
            this.segment.push(data);
        }
    }

    /**
     * perform the pop operation
     * @param memory a memory segment
     */
    private int popData(String memory){
        if(memory==null){
        	writePopToD();/*
            asmLines.add(AT+segment.getSp()); //set the address of the m register as the stack's tpo
            asmLines.add(D+EQUAL+M); //assign D as M
            return this.segment.pop();*/
        }
        return 0;// todo change later on
    }
    
    public void translateArithmetic(String operation) {
    	if (operation.equals(EQ)|| operation.equals(GT)|| operation.equals(LT)){
    		writer.writeBoolean(operation);
    	}else{
        	writer.writeArithnetic(operation);

    	}
        /*segment.setSp(-1);
        int dRegister= popData(null); // pop the data from the stack and store it d register
        asmLines.add(AT + segment.getSp());

        if (operation.equals(EQ)|| operation.equals(GT)|| operation.equals(LT)) {
            checkLogicOp(operation, dRegister); // check for logic operations
        }else if (operation.equals(NEG)) {
            translateNegate(dRegister);
        }
        else if (operation.equals(ADD)) {
            asmLines.add(M + EQUAL + D + PLUS + M); //add d register to m register
            segment.addOrSubtract(dRegister); // add to the stack
        } else if (operation.equals(SUB)) {
            asmLines.add(M + EQUAL + M + MINUS + D); //subtract d register to m register
            segment.addOrSubtract(-dRegister); //subtract from the stack
        } else {
            checkBitWiseOp(operation, dRegister); // if the operation is a bitwise operation
        }
        advanceSP(); // translate the assignment of a new value at R0(SP)
        */
    }

    /**
     * translate logic operations
     * @param dRegister a data
     * @param num the numbers 0,1,-1
     */
    private void translateLogicOp(int dRegister,int num){
        if(segment.logicalOperations(dRegister)==num){ // if the operation returns true
            segment.getDataStack().pop();
            segment.push(-1);
            asmLines.add(M+EQUAL+MINUS+ONE); // assign to the M register the number -1
        }else{ //if the operation returns false
            segment.getDataStack().pop();
            segment.push(0);
            asmLines.add(M+EQUAL+ZERO);// assign to the M register the number 0
        }
    }

    /**
     * check if the operation bitWise and translate it
     * @param operation the operation
     * @param data a data
     */
    private void checkBitWiseOp(String operation,int data){
        int value=0;
        if (operation.equals(AND)){
            value =segment.bitWiseAnd(data);
        }else if (operation.equals(OR)){
            value=segment.bitWiseOr(data);
        }else{
            value= segment.bitWiseNot(data);
        }
        if(value<0){
            translateNegativeVariable(value);
        }else {
            pushData(CONSTANT, value);
        }
    }

    /**
     * translate the advancement of the sp variable to asm
     */
    private  void advanceSP(){
        asmLines.add(AT+segment.getSp()); // get the address
        asmLines.add(D+EQUAL+A);
        asmLines.add(AT + ZERO); //assign SP as the d register
        asmLines.add(M + EQUAL + D);
    }

    /**
     * translate negative values
     * @param value the data
     */
    private void translateNegativeVariable(int value){
        value=-value;
        asmLines.add(AT+value);
        asmLines.add(D+EQUAL+A); //assign d register with the data
        asmLines.add(AT+segment.getSp()); //get the address
        asmLines.add(M+EQUAL+ZERO); // assign a M register as zero
        asmLines.add(M+EQUAL+M+MINUS+D); //subtract the data from the m register
        this.segment.push(value);
    }

    /**
     * check if it is a logic operation
     * @param operation a string that represent the operation
     * @param dRegister a data
     */
    private void checkLogicOp(String operation, int dRegister){
        if(operation.equals(EQ)){ //check for the operation eq
            translateLogicOp(dRegister,0);
        }else if (operation.equals(GT)){ //check for the operation gt
            translateLogicOp(dRegister,1);
        }else if (operation.equals(LT)){ //check for the operation lt
            translateLogicOp(dRegister,-1);
        }
    }

    /**
     * translate the negate operation
     * @param dRegister a data
     */
    private void translateNegate(int dRegister){
        segment.setSp(1);
        int value = segment.negate(dRegister); // negate
        if (value<0){
            translateNegativeVariable(value); // if value is negative translate as so
        }else {
            pushData(CONSTANT, value); // if value is non negative push into the stack
        }
    }

}
