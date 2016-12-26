import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * parse the vm language into asm language
 */
public class Parser {
    private static final String ONE_LINER_COMMENT ="^/{2}";
    private static final String EMPTY_LINE= "^\\s*+$";
    private static final Pattern EMPTY_LINE_PATTERN= Pattern.compile(EMPTY_LINE);
    private static final Pattern COMMENT_PATTERN= Pattern.compile(ONE_LINER_COMMENT);
    private static final String DOT ="[a-zA-Z]{1}\\w*+(\\.){1}";
    private static final Pattern DOT_PATTERN= Pattern.compile(DOT);

    private static final String PUSH_AND_POP ="\\b(push|pop)\\b";
    private static final Pattern PUSH_AND_POP_PATTERN = Pattern.compile(PUSH_AND_POP);



    private static final String MEMORY= "\\b(constant|local|argument|this|that|pointer|temp|static)\\b";
    private static final Pattern MEMORY_PATTERN= Pattern.compile(MEMORY);

    private static final String DECIMAL_NUMBER = "\\d++";
    private static final Pattern DECIMAL_NUMBER_PATTERN= Pattern.compile(DECIMAL_NUMBER);

    private static final String ARITHMETIC ="\\b(add|sub|neg|eq|gt|lt|and|or|not)\\b";
    private static final Pattern ARITHMETIC_PATTERN= Pattern.compile(ARITHMETIC);


    /** A string that represent the current memory address*/
    private String curMemory;
    /** A string that represent the current number being processed*/
    private int curNumber=0;
    /** the current matcher*/
    private Matcher curMatcher;
    /**A string that represent the operation on the stack*/
    private String operation;
    /** this ArrayList contains all of the vm file lines*/
    private ArrayList<String> vmLines;
    /**a string that represent the current vm line*/
    private String curLine;
    /** a string that represent the current class name*/
    private String className;
    /** a constructor*/
    public Parser(){
        this.operation=this.curMemory="";
        this.vmLines= new ArrayList<>();

    }

    public ArrayList<String> getVmLines(){
        return this.vmLines;
    }

    /**
     * parse the vm file
     */
    public void parseVmFile(String className){
        parseClassName(className);
        for(int i=0; i<vmLines.size();i++){
            this.curLine= vmLines.get(i); // assign the current line
            if(deleteOneLinerComment(this.curLine)||deleteBlankLines(this.curLine))
            { //skip if the row is a comment or a blank line
                continue;
            }
            if(signifyOperation()) // check which instruction to operate
            {
                signifyMemorySegment(); //check which memory segment
                insertDecimalNumber(); //assign a decimal number
                CodeWriter.getCodeWriter().writePushPop(this.operation,
                        this.curMemory,this.curNumber,this.className); //translate the operation
            }
            else
            { //translate the arithmetic operation
                CodeWriter.getCodeWriter().translateArithmetic(this.operation);
            }
        }
    }

    /**
     * check for push or pop operations
     * @return return true if is was not an arithmetic operation
     */
    public boolean signifyOperation(){
        this.curMatcher=ARITHMETIC_PATTERN.matcher(this.curLine);
        if(this.curMatcher.find())
        { //check for arithmetic operation
            this.operation=this.curLine.substring(this.curMatcher.start(),this.curMatcher.end());
            return false;
        }else{
            this.curMatcher= PUSH_AND_POP_PATTERN.matcher(curLine);
            if(this.curMatcher.find()) {
                this.operation = this.curLine.substring(this.curMatcher.start(), this.curMatcher.end());
                return true;
            }
        }
        return true;
    }

    /**
     * parse the memory segment where the push/pop operations should address
     */
    private void signifyMemorySegment(){
        this.curMatcher= MEMORY_PATTERN.matcher(curLine);
        if(this.curMatcher.find())
        { //find the memory specified
            this.curMemory = curLine.substring(this.curMatcher.start(), this.curMatcher.end());
            this.curLine = this.curLine.substring(this.curMatcher.end()); //delete the prefix
        }
    }

    /** parse the decimal number*/
    private void insertDecimalNumber(){
        this.curMatcher=DECIMAL_NUMBER_PATTERN.matcher(this.curLine);
        if(this.curMatcher.find())
        { //find the data
            this.curLine = this.curLine.substring(this.curMatcher.start(), this.curMatcher.end());
            this.curNumber = Integer.parseInt(this.curLine); //convert into int
        }
    }

    /**
     * check if the line is blank
     * @param line a string that represent the specific line in the asm file
     * @return return true if it was a blank line, false otherwise
     */
    private boolean deleteBlankLines(String line){
        Matcher m=EMPTY_LINE_PATTERN.matcher(line);
        return m.find(); // check for an empty line

    }

    /**
     * check if the line is a comment
     * @param line a string that represent the specific line in the asm file
     * @return return true if it was a comment, false otherwise
     */
    private boolean deleteOneLinerComment(String line)
    {
        Matcher m= COMMENT_PATTERN.matcher(line);
        return m.lookingAt();
    }

    /**
     * parse the class name
     * @param className the name of the file that being parsed
     */
    private void parseClassName(String className){
        this.curMatcher=DOT_PATTERN.matcher(className);
        if(this.curMatcher.find()){
            this.className= className.substring(0,this.curMatcher.end());
        }
    }
}
