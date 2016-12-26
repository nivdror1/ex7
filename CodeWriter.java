import java.util.ArrayList;
import java.lang.String;


/**
 * this class translates the vm code into asm code
 */
public class CodeWriter {

    /** logic operations*/
    private static final String ADD= "add";
    private static final String SUB="sub";
    private static final String EQ="eq";
    private static final String LT="lt";
    private static final String GT="gt";
    private static final String NEG="neg";
    private static final String AND="and";
    private static final String OR="or";
    private static final String NOT="not";
    private static final String PUSH="push";
    private static final String POP="pop";

    /** the memory segment*/
    private static final String CONSTANT= "constant";
    private static final String LOCAL= "LOCAL";
    private static final String ARGUMENT= "ARGUMENT";
    private static final String THAT= "that";
    private static final String THIS= "this";
    private static final String POINTER= "pointer";
    private static final String STATIC= "static";
    private static final String TEMP= "temp";

    /** the unique instance of CodeWriter*/
    private static CodeWriter codeWriter= null;

    /** this ArrayList signify the output asm code*/
    private ArrayList<String> asmLines;


    /** a label counter*/

    private int labelCounter;
    

    /** a singleton constructor*/
    private CodeWriter(){
        this.asmLines= new ArrayList<>();
        labelCounter = 0;
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


    /** signify arithmetic instructions and logic operations
     * @param operation a string representing the arithmetic operation
     */
    public void translateArithmetic(String operation) {
    	if (operation.equals(EQ)|| operation.equals(GT)|| operation.equals(LT)){
    		writeBoolean(operation); // write boolean operations
    	}else{
        	writeArithmetic(operation); // write arithmetic operation
    	}
    }

        // ---------------------------arithmetic----------------------------

    /**
     * write arithmetic operation in asm language
     * @param operation  a string representing the arithmetic operation
     */
        public void writeArithmetic(String operation){
            switch (operation){
                case ADD:
                    writeAdd();
                    break;
                case SUB:
                    writeSub();
                    break;
                case NEG:
                    writeNeg();
                    break;
                case AND:
                    writeAnd();
                    break;
                case OR:
                    writeOr();
                    break;
                case NOT:
                    writeNot();
                    break;
            }
        }

    /**
     * write the add instruction in asm
     */
    private void writeAdd(){
            asm("// ---add---");
            popToD();
            loadStackAddressToA();
            asm("A=A-1");
            asm("M=M+D");
            asm("//// ---add-end---");
        }
    /**
     * write the sub instruction in asm
     */
    private void writeSub(){
        asm("//// ---sub---");
        popToD();
        loadStackAddressToA();
        asm("A=A-1");
        asm("M=M-D");
        asm("//// ---sub-end---");
    }
    /**
     * write the negate instruction in asm
     */
    private void writeNeg(){
        asm("//// ---neg---");
        loadStackAddressToA();
        asm("A=A-1");
        asm("M=-M");
        asm("//// ---neg-end---");
    }
    /**
     * write the and instruction in asm
     */
    private void writeAnd(){
        asm("//// ---and---");
        popToD();
        loadStackAddressToA();
        asm("A=A-1");
        asm("M=M&D");
        asm("//// ---and-end---");
    }
    /**
     * write the or instruction in asm
     */
    private void writeOr(){
        asm("//// ---or---");
        popToD();
        loadStackAddressToA();
        asm("A=A-1");
        asm("M=M|D");
        asm("//// ---or-end---");
    }
    /**
     * write the not instruction in asm
     */
    private void writeNot(){
        asm("//// ---not---");
        loadStackAddressToA();
        asm("A=A-1");
        asm("M=!M");
        asm("//// ---not-end---");
    }

   // ---------------------------boolean-------------------------------

    /**
     * check which logic operation to translate
     * @param operation a string representing the arithmetic operation
     */
    public void writeBoolean(String operation){
        switch (operation){
            case EQ:
                writeLogic("D; JEQ");
                break;
            case LT:
                writeLogic("D; JLT");
                break;
            case GT:
                writeLogic("D; JGT");
                break;
        }
    }

    /**
     * write the jump operation according to the input RuleOnD
     * @param RuleOnD the jump condition
     */
    private void writeLogic(String RuleOnD){
        writeSub();
        asm("@1");
        asm("A=-A");
        copyAToR13();
        popToD();
        asm("@label" + String.valueOf(this.labelCounter));
        asm(RuleOnD);//asm("D; JEQ");
        asm("@0");
        copyAToR13();
        asm("(label" + String.valueOf(this.labelCounter) + ")");
        pushR13();
        this.labelCounter++;

    }
    // ---------------------------memory access functions --------------

    /**
     * insert a constant to R13 which is a data register
     * @param value the constant value
     */
    private void writeInsertConstantToR13(int value){
        asm("@" + String.valueOf(value));
        asm("D=A");
        asm("@R13");
        asm("M=D");

    }


    /**
     * write push or pop operation
     * @param operation a string representing the arithmetic operation
     * @param memory a string representing the memory segment
     * @param address a string representing the address
     * @param className the current class name
     */
    public void writePushPop(String operation, String memory, int address, String className){
        if(operation.equals(PUSH)) {
            writePush(memory, address,className); //write push
        }
        else if(operation.equals(POP)){
            writePop(memory, address,className); //write pop
        }

    }

    /**
     * write push according to the memory segment
     * @param memory a string representing the memory segment
     * @param arg2 a data value
     * @param className the current class name
     */
    private void writePush(String memory, int arg2, String className){
        switch (memory){
            case CONSTANT:
                writePushToConstant(arg2);
                break;
            case ARGUMENT:
                writePushToArgument(arg2);
                break;
            case LOCAL:
                writePushToLocal(arg2);
                break;
            case THIS:
                writePushToThis(arg2);
                break;
            case THAT:
                writePushToThat(arg2);
                break;
            case TEMP:
                writePushToTemp(arg2);
                break;
            case POINTER:
                writePushToPointer(arg2);
                break;
            case STATIC:
                writePushToStatic(arg2,className);
                break;

        }
    }

    /**
    * this is sub-rutine of all the different "writePushToXXX" methods.
    * it writes to the assembly code some common nessecery lines of performing "push".
    */
    private void pushSub_arg_const_this_that(){
        asm("A=A+D");
        asm("// A have now the address of the data to take from the ram");
        asm("D=M");
        asm("@R13");
        asm("M=D");
        asm("// R13 now have the data");

        loadStackAddressToA();
        copyAToR14();
        copyFromR13ToRamAddressInR14();
        advanceStack();
    }

    /**
     * write pop operation in asm language
     * @param memory a string representing the memory segment
     * @param address a string representing the address
     * @param className the current class name
     */
    private void writePop(String memory, int address, String className){
        switch (memory){
            case ARGUMENT:
                writePopToArgument(address);
                break;
            case LOCAL:
                writePopToLocal(address);
                break;
            case THIS:
                writePopToThis(address);
                break;
            case THAT:
                writePopToThat(address);
                break;
            case TEMP:
                writePopToTemp(address);
                break;
            case POINTER:
                writePopToPointer(address);
                break;
            case STATIC:
                writePopToStatic(address,className);
                break;
            //case (null):
            default:
                popToD();
                break;

        }
    }

    /**
     * add an asm line to asmLines
     */
    private void asm(String asmLine){
        this.asmLines.add(asmLine);
    }

    /**
     * write push to the constant segment
     * @param arg2 a data value
     */
    private void writePushToConstant(int arg2){
        asm("//// ----- push constant -------");
        writeInsertConstantToR13(arg2);
        pushR13();
        asm("//// ----- push constant end -------");
    }
    /**
     * write push to the temp segment
     * @param arg2 a data value
     */
    private void writePushToTemp(int arg2){
        asm("//// ----- push temp -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        asm("@5");
        pushSub_arg_const_this_that();
        asm("//// ----- push temp end-------");
    }
    /**
     * write push to the static segment
     * @param arg2 a data value
     * @param className the current class name
     */
    private void writePushToStatic(int arg2,String className){
        asm("//// ----- push static -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        asm("@"+className+arg2);
        asm("D=M");
        asm("@R13");
        asm("M=D");
        asm("// R13 now have the data");

        loadStackAddressToA();
        copyAToR14();
        copyFromR13ToRamAddressInR14();
        advanceStack();
        asm("//// ----- push static end-------");
    }

    /**
     * write push to the "that" segment
     * @param arg2 a data value
     */
    private void writePushToThat(int arg2){
        asm("//// ----- push that -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        loadThatAddressToA();
        pushSub_arg_const_this_that();
        asm("//// ----- push that end-------");
    }
    /**
     * write push to the "this" segment
     * @param arg2 a data value
     */
    private void writePushToThis(int arg2){
        asm("//// ----- push this -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        loadThisAddressToA();
        pushSub_arg_const_this_that();
        asm("//// ----- push this end-------");
    }
    /**
     * write push to the local segment
     * @param arg2 a data value
     */
    private void writePushToLocal(int arg2){
        asm("//// ----- push local -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        loadLocalAddressToA();
        pushSub_arg_const_this_that();
        asm("//// ----- push local end-------");
    }
    /**
     * write push to the argument segment
     * @param arg2 a data value
     */
    private void writePushToArgument(int arg2){
        asm("//// ----- push argument -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        loadArgumentAddressToA();
        pushSub_arg_const_this_that();
        asm("//// ----- push argument end-------");
    }
    /**
     * write push to the pointer segment
     * @param arg2 a data value
     */
    private void writePushToPointer(int arg2){
        asm("//// ----- push pointer -------");
        asm("@" + String.valueOf(arg2));
        asm("D=A");
        asm("@3");
        pushSub_arg_const_this_that();
        asm("//// ----- push pointer end-------");
    }

    /**
     * write pop to the temp segment
     * @param address an address in the memory
     */
    private void writePopToTemp(int address){
        asm("//// -- pop temp -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        asm("@5");
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop temp end -- ");
    }
    /**
     * write pop to the "this" segment
     * @param address an address in the memory
     */
    private void writePopToThis(int address){
        asm("//// -- pop this -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        loadThisAddressToA();
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop this end -- ");
    }
    /**
     * write pop to the "that" segment
     * @param address an address in the memory
     */
    private void writePopToThat(int address){
        asm("//// -- pop that -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        loadThatAddressToA();
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop that end -- ");
    }
    /**
     * write pop to the pointer segment
     * @param address an address in the memory
     */
    private void writePopToPointer(int address){
        asm("//// -- pop pointer -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        asm("@3");
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop pointer end -- ");
    }

    /**
     * write pop to the static segment
     * @param address an address in the memory
     * @param className the current class name
     */
    private void writePopToStatic(int address, String className){
        asm("//// -- pop static -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        asm("@"+className+address);
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop static end -- ");
    }

    /**
     * write pop to the local segment
     * @param address an address in the memory
     */
    private void writePopToLocal(int address){
        asm("//// -- pop local -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        loadLocalAddressToA();
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop local end -- ");
    }
    /**
     * write pop to the argument segment
     * @param address an address in the memory
     */
    private void writePopToArgument(int address){
        asm("//// -- pop argument -- ");
        asm("@" + String.valueOf(address));
        asm("D=A");
        loadArgumentAddressToA();
        asm("A=D+A");
        copyAToR14();
        popR13();
        copyFromR13ToRamAddressInR14();
        asm("//// -- pop argument end -- ");
    }



    /**
     * advance SP
     */
    private void advanceStack(){
        asm("//// AdvanceStack");
        asm("@SP");
        asm("M=M+1");
    }

    /**
     * set R13 as the address
     */
    private void copyAToR13(){
        asm("//	Copy A To R13");
        asm("D=A");
        asm("@R13");
        asm("M=D");
    }

    /**
     * set R14 as the address
     */
    private void copyAToR14(){
        asm("//Copy A To R14");
        asm("D=A");
        asm("@R14");
        asm("M=D");
    }

    /**
     * set R14 as R13
     */
    private void copyFromR13ToRamAddressInR14(){
        asm("// copy from r13 to ram address in ram14");
        asm("@13");
        asm("D=M");
        asm("@14");
        asm("A=M");
        asm("M=D");
    }


    /**
     * load arg address to A
     */
    private void loadArgumentAddressToA(){
        asm("@ARG");
        asm("A=M");
    }

    /**
     * load local address to A
     */
    private void loadLocalAddressToA(){
        asm("@LCL");
        asm("A=M");
    }

    /**
     * load stack address to A
     */
    private void loadStackAddressToA(){
        asm("@SP");
        asm("A=M");
    }

    /**
     * load that address to A
     */
    private void loadThatAddressToA(){
        asm("@THAT");
        asm("A=M");
    }

    /**
     * load this address to A
     */
    private void loadThisAddressToA(){
        asm("@THIS");
        asm("A=M");
    }

    /**
     * pop R13
     */
    private void popR13(){
        asm("//pop to R13");
        asm("// first stage - pop to D");
        asm("@SP");
        asm("M=M-1");
        asm("A=M");
        asm("D=M");
        asm("//second stage - write D to R13");
        asm("@R13");
        asm("M=D");
    }

    /**
     * pop to D
     */
    private void popToD (){
        asm("// popToD");
        asm("@0");
        asm("M=M-1");
        asm("A=M");
        asm("D=M");
    }


    /**
     * push R13
     */
    private void pushR13(){
        asm("// PushR13");
        asm("@R13");
        asm("D=M");
        asm("@SP");
        asm("A=M");
        asm("M=D");
        asm("@SP");
        asm("M=M+1");
    }
}
