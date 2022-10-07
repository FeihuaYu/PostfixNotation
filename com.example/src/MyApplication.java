package src;

import java.io.IOException;

public class MyApplication { 
    // "postfix_notation​.csv"
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Please input file name");
            return;
        }else {
            PostNotation postNotation = new PostNotation(args[0]);
            postNotation.implementFunction();  
        }
  
    }
}
