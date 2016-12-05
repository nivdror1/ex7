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

    
    private CodeWriter2 writer;
    /** a singleton constructor*/
    private CodeWriter(){
        this.asmLines= new ArrayList<>();
        this.segment=new MemorySegment();

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
    
    public void translate(String operation, String memory, int number){
        writer.writePushPop(operation, memory, number);

    }

    public void translateArithmetic(String operation) {
    	if (operation.equals(EQ)|| operation.equals(GT)|| operation.equals(LT)){
    		writer.writeBoolean(operation);
    	}else{
        	writer.writeArithnetic(operation);

    	}
    }
}