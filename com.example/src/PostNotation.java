package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PostNotation{
    private final Set<String> operatorsSet = new HashSet<>();
    private final String error = "#ERR";
    private final String filePath;

    public PostNotation(String filePath) {
        operatorsSet.add("+");
        operatorsSet.add("-");
        operatorsSet.add("*");
        operatorsSet.add("/");
        this.filePath = filePath;
    }

    // read data from CSV file
    public List<List<String>> readFromCSV() throws IOException {
        List<List<String>> csvList = new LinkedList<>();
        List<String> list = new LinkedList<>();

        File file = new File(filePath);
        if(file.exists()) {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    line = br.readLine();
    
                    while (line != null) { 
                        String[] attributes = line.split(","); 
                        list = Arrays.asList(attributes);
                        csvList.add(list); 
                        line = br.readLine();
                    }
                }
    
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }  
        }else {
            System.out.println("No file found!!");
        }

        return csvList;
    }


    // save each postfix notation into a list
    public List<String> getPostfixNotation(String operations) {
        List<String> list = new LinkedList<>();
        String[] strArray = operations.split("\\s+");
        for(String str : strArray) {
            if (str.trim().length() > 0) {
                list.add(str.trim());
            } 

        }
        return list;
    }


    // first time calculate only numbers in postfix notaion
    public String calculateNumPostfixNotation(List<String> list, List<List<String>> csvList) {
        String valString = "";
        // Check initial format of postfix notation
        if(!isPostfixNotationValid(list)) {
            return error;
        }
        valString = calculateNumCell(list, csvList);

        return valString; 
    }


    // parse letters in postfix notaion
    public int[] parseLetter(String str) {
        // a2[1 0]   b1[0 1]
        int[] index = new int[2];
        str = str.toLowerCase();
        char[] chArray = str.toCharArray();
        String rowString = "";

        int column = chArray[0] - 'a';
        for(int i=1;i<chArray.length;i++){
            rowString += chArray[i];
        }
        int row = Integer.parseInt(rowString) - 1;
        index[0] = row;
        index[1] = column;

        return index;
    }


    // check if contains letters and numbers(eg. a1 b3)
    public boolean containsLetterAndNum(String str) {
        str = str.toLowerCase();
        String regex = "[a-z][0-9]+"; 
        if(str.matches(regex)) {
            return true;
        }
        return false;
    }


    //  Check initial format of postfix notation 
    public boolean isPostfixNotationValid(List<String> list) {
        String regexNum = "[0-9]+"; 
        String regexLetter = "[a-zA-Z]";
        if(list.isEmpty() || list.size()==0) {
            return false;
        }

        if(list.size() == 1) {
            if(operatorsSet.contains(list.get(0))){
                return false;
            }
            if(!list.get(0).matches(regexNum) && !containsLetterAndNum(list.get(0))) {
                return false;
            }
            if(list.get(0).matches(regexLetter)) {
                return false;
            }
        }

        if(list.contains(error)){
            return false;
        }

        return true;
    }


    // calculate the result of each cell with reference
    public String calculateReferPostfixNotation(List<String> list, List<List<String>> csvList, int rowNum, int colNum) {
        Boolean hasOperator = false;
        Stack<String> stack = new Stack<>();
        double value = 0;
        String valString = "";
        for(int i=0;i<list.size();i++) {
            String str = list.get(i);
            if(operatorsSet.contains(str)){
                hasOperator = true;
                if(!stack.isEmpty() && stack.size() > 1) {
                    double a = Double.parseDouble(stack.pop());
                    double b = Double.parseDouble(stack.pop());
                    value = operatePostfixNotation(str, a, b);
                    stack.add(String.valueOf(value));
                }
            }else {
                str = convertToNum(str, csvList, i, rowNum, colNum);
                stack.add(str);
            }
        }

        if(!checkOutputError(hasOperator, list)) {
            valString = stack.pop();
        }else {
            valString = error;
        }

        return valString;
    }


    // if it contains letters(refer), convert to numbers
    public String convertToNum(String str, List<List<String>> csvList, int i, int rowNum, int colNum) {
        while(containsLetterAndNum(str)) {
            int[] index = new int[2];
            index = parseLetter(str);
            if(index[0]==rowNum && index[1]==colNum){
                return error;
            }

            if(csvList.size() > index[0] && csvList.get(i).size() > index[1]) {
                str = csvList.get(index[0]).get(index[1]);
            }else {
                str = error;
            }
        }
        return str;
    }


    // calculate cell with numbers
    public String calculateNumCell(List<String> list, List<List<String>> csvList) {
        String regexLetter = ".*[a-z].*";
        boolean hasOperator = false;
        boolean hasLetter = false;
        String valString = "";
        Stack<String> stack = new Stack<>();
        double value = 0;

        for(int i=0;i<list.size();i++) {
            String str = list.get(i);
            if(!str.matches(regexLetter)) {
                if(operatorsSet.contains(str)){
                    hasOperator = true;
                    if(!stack.isEmpty() && stack.size() > 1) {
                        double a = Double.parseDouble(stack.pop());
                        double b = Double.parseDouble(stack.pop());
                        value = operatePostfixNotation(str, a, b);
                        stack.add(String.valueOf(value));
                    }
                }else {
                    stack.add(str);
                }
            } else{
                hasLetter = true;
                break;
            }
        }

        if(!hasLetter) {
            if(!checkOutputError(hasOperator, list)) {
                valString = stack.pop();
            }else {
                valString = error;
            }
        }

        return valString;
    }


    // check output error
    public boolean checkOutputError(boolean hasOperator, List<String> list) {
        if(!hasOperator && list.size()>1) {
            return true;
        }

        if(hasOperator && list.size()==2) {
            return true;
        }

        return false;
    }

    
    // operate postfix notation
    public double operatePostfixNotation(String str, double a, double b) {
        double value = 0;
        switch(str) {
            case "+":
                value = b + a;
                break;
            case "-": 
                value = b - a;
                break;
            case "*": 
                value = b * a;
                break;    
            case "/": 
                value = b / a;
                break; 
        }

        return value;
    }


    // write result to CSV file
    public void writeToCSV(List<List<String>> csvList) throws IOException {
        File csvFile = new File("result.csv");
        FileWriter fileWriter = new FileWriter(csvFile);

        for(List<String> list : csvList) {
            StringBuilder stringBuilder = new StringBuilder(list.size());
            for(int i=0;i<list.size();i++) {
                stringBuilder.append(list.get(i));
                if(i != list.size()-1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("\n");
            fileWriter.write(stringBuilder.toString());
        }   
        fileWriter.close();
    }


    // implement the whole function
    public void implementFunction() throws IOException {
        List<List<String>> csvList = new LinkedList<>();
        List<String> postfixNotationList = new LinkedList<>();

        csvList = readFromCSV();

        // first time only calculate cell with numbers
        for(int i=0;i<csvList.size();i++) {
            for(int j=0;j<csvList.get(i).size();j++) {
                postfixNotationList = getPostfixNotation(csvList.get(i).get(j));
                String strResult = calculateNumPostfixNotation(postfixNotationList, csvList);
                if(!strResult.equals("")) {
                    csvList.get(i).set(j, strResult);
                }
            }
        }

        // then calcualte each cell with reference
        for(int i=0;i<csvList.size();i++) {
            for(int j=0;j<csvList.get(i).size();j++) {
                postfixNotationList = getPostfixNotation(csvList.get(i).get(j));
                String strResult = calculateReferPostfixNotation(postfixNotationList, csvList, i, j);
                csvList.get(i).set(j, strResult);
                System.out.println(csvList.get(i).get(j));
            }
        }

        writeToCSV(csvList);

    }

}