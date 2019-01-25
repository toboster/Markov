import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 ^
 * @author Tony Nguyen  11/9/17
 * Uses StringChain class to generate gibberish given 1 or 2 input files
 * Example of use on terminal: 
 * java Markov 3 500 char MobyDick.txt sonnet.txt
 */
public class Markov {

    /** Regular expression for breaking up words. */
    private static final String WORD_REGEX = "(?<=\\w\\W)";

    /** Regular expression for getting individual characters. */
    private static final String CHAR_REGEX = "(?<=.)";

    /** Random number generator for picking random items. */
    private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {

        int Order = 0;
        int numWords = 0;
        String regex = "";

        // Check is number of args is valid.
        if (!((args.length == 4) || (args.length == 5))) {
            System.out.println("Invalid number of args: " + args.length);
            System.exit(0);
        }
        // Check if Order given is Integer
        if(isStringInt(args[0])){
            Order = Integer.parseInt(args[0]);
        }
        // Check if number of Words is valid.
        if(isStringInt(args[1])){
            numWords = Integer.parseInt(args[1]);           
        }
        
       // sets what type of delimiter should be used.
        if(isValidBreak(args[2])){
            switch(args[2]){
            case "char": 
                regex = CHAR_REGEX;
                break;
            case "word":
                regex = WORD_REGEX;
                break;
        }
        } else{
            System.out.println(
                    "Invalid delimiter, should be \"char\" or \"word\".");
            System.exit(0);

        }
            
        
        
        StringChain chain = new StringChain(Order);
        int limit = args.length - 1;
        // possibly 2 given files
        for (int i = 3; i <= limit; i++) {

            try (BufferedReader in = new BufferedReader(
                    new FileReader(args[i]));) {
                String line;
                while ((line = in.readLine()) != null) {
                    // Make a scanner that uses String as source
                    Scanner strScan = new Scanner(line);
                    // Use word boundaries to break up input.
                    strScan.useDelimiter(regex);
                    // Add words
                    chain.addItems(strScan);
                    
                }

            }
      
      }
      
      // generates output, i.e. gibberish  
      List<String> output = chain.generate(numWords, rand);
      
      // Print out the result.
      System.out.println("Generated gibberish: ");
      for(String word : output) {
          System.out.print(word);
      }
      System.out.println();
        

    }

    /**
     * Gotten from stackOverflow to check if number is valid
     * @param s
     * @return
     */
    private static boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * Checks if args is word or char, if it's neither return false.
     * @param s
     * @return
     */
    private static boolean isValidBreak(String s){
        
        boolean result = false;
        
        switch(s.toLowerCase()){
            case "char": 
                result = true;
                break;
            case "word":
                result = true;
                break;
        }
        
        return result;
    }


}


