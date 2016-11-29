import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** the purpose of this class is to read and write the IO*/
public class IO {
    private static final String IO_ERROR_MESSAGE= "IO error has happened";
    private static final String FILE_NOT_EXISTS="enter another file, since the path was wrong";
    private static final String ASM="asm";
    private static final String DOT="\\w++\\.";
    private static final Pattern DOT_PATTERN= Pattern.compile(DOT);


    /** the main method which control the progress of the assembler*/
    public static void main(String[] args)
    {
        //todo check what is the input( xxx.asm or dir or another file)
        File file= new File(args[0]);
        String outputFileName=setOutputFileName(args[0]);
        if(file.exists()) // check if the file exists
        {
            try (FileReader asmFile = new FileReader(file);// define the BufferReader and BufferWriter
                 BufferedReader reader =new BufferedReader(asmFile);
                 FileWriter hackFile =new FileWriter((outputFileName));
                 BufferedWriter writer= new BufferedWriter(hackFile)){


                Parser parser= new Parser(); //define a new parser
                String text;

                while((text= reader.readLine())!=null) // add the lines to the container
                {
                    parser.getVmLines().add(text);
                }
                parser.parseVmFile(); // parse the asm text

                //write the binary code to an output file
                for(int i=0;i<CodeWriter.getCodeWriter().getAsmLines().size();i++){
                    writer.write(CodeWriter.getCodeWriter().getAsmLines().get(i)+"\n");
                }

            }
            catch(IOException e){
                System.out.println(IO_ERROR_MESSAGE);
            }
        }
        else{
            System.out.println(FILE_NOT_EXISTS);
        }
    }

    /**
     * get the name of the asm file and exchange it to hack file
     * @param inputFileName the input file name
     * @return the file name but with a suffix of a hack file
     */
    private static String setOutputFileName(String inputFileName){
        Matcher m= DOT_PATTERN.matcher(inputFileName);
        if(m.lookingAt()){ // find the dot char
            inputFileName= inputFileName.substring(0,m.end()); // delete the asm suffix
            return inputFileName+ASM; //add the hack suffix
        }
        return "didn't find the dot"; //todo maybe do an exception if not write something meaningful
    }
}
