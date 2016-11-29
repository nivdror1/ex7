import java.util.Stack;

/**
 * represent the memory segments
 */
public class MemorySegment {
    private static int SP=256;
    /** an stack that contain the data*/
    private Stack<Integer> dataStack;
    /** sp contain the address of the memory register that is top of stack*/
    int sp=SP;

    /**a constuctor*/
    public MemorySegment(){
        this.dataStack=new Stack<>();
    }

    public Stack<Integer> getDataStack() {
        return this.dataStack;
    }
    /** push the data into the stack*/
    public void push(int data){
        this.dataStack.push(data);
        this.sp+=1;
    }

    /**
     * pop the top of the stack
     * @return return the top of the stack
     */
    public int pop(){
        this.sp-=1;
        return this.dataStack.pop();
    }

    /**
     * add or subtract num from sp
     * @param num a number
     */
    public void setSp(int num){
        this.sp+=num;
    }

    /**
     * get sp
     * @return  return sp
     */
    public int getSp(){
        return this.sp;
    }

    /**
     * check is the data is equal,greater or smaller than the top of the stack
     * @param data a number
     * @return  if data is bigger return -1, if it is smaller return 1 else return 0
     */
    public int logicalOperations(int data){
        if(data>this.dataStack.peek()){
            return -1;
        }else if (data<this.dataStack.peek()){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * a bitwise and operation
     * @param data a number that was on top of the stack
     * @return a bitwise and between data and the top of the stack
     */
    public int bitWiseAnd(int data){
        int value= this.dataStack.pop();
        return data&value;
    }

    /**
     * a bitwise or operation
     * @param data a number that was on top of the stack
     * @return a bitwise or between data and the top of the stack
     */
    public int bitWiseOr(int data){
        int value= this.dataStack.pop();
        return data|value;
    }

    /**
     * a bitwise not operation
     * @param data a number that was on top of the stack
     * @return a bitwise not of the data
     */
    public int bitWiseNot(int data){
        this.sp+=1;
        return ~data;
    }

    /**
     * arithmetic negation
     * @param data a number
     * @return the inverse of data
     */
    public int negate(int data){
        return -data;
    }

    /**
     * add or subtract data the current head of the stack
     * @param data a number
     */
    public void addOrSubtract(int data){
        int value=this.dataStack.pop();
        this.dataStack.push(value+data);
        this.sp+=1;
    }
}
