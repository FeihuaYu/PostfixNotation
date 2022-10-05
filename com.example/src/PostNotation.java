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
    private Set<String> operatorsSet = new HashSet<>();
    private String error = "#ERR";

    public PostNotation() {
        operatorsSet.add("+");
        operatorsSet.add("-");
        operatorsSet.add("*");
        operatorsSet.add("/");
    }


    public List<List<String>> readFromCSV() throws IOException {
        List<List<String>> csvList = new LinkedList<>();
        List<String> list = new LinkedList<>();
        String filePath = "postfix_notation​.csv";

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

        return csvList;
    }


    public List<String> getPostNotation(String operations) {
        List<String> list = new LinkedList<>();
        String[] strArray = operations.split("\\s+");
        for(String str : strArray) {
            if (str.trim().length() > 0) {
                list.add(str);
            } 
                


        }
        return list;
    }


    public String calculatePostNotation(List<String> list, List<List<String>> csvList, int rowNum, int colNum) {
        Stack<String> stack = new Stack<>();
        double value = 0;
        boolean hasOperator = false;

        // Check initial format of post notation
        if(!checkPostNotation(list)) {
            return error;
        }

        for(int i=0;i<list.size();i++) {
            String str = list.get(i);
            System.out.println("str is " + str);
            if(operatorsSet.contains(str)){
                hasOperator = true;
                if(!stack.isEmpty() && stack.size() > 1) {
                    double a = Double.parseDouble(stack.pop());
                    double b = Double.parseDouble(stack.pop());
                    value = operatePostNotation(str, a, b);
                    stack.add(String.valueOf(value));
                }
            }else {
                while(containsLetterAndNum(str)) {
                    int[] index = new int[2];
                    index = parseLetter(str);
                    if(index[0]==rowNum && index[1]==colNum){
                        System.out.println(error);
                        return error;
                    }
                    if(csvList.size() > index[0] && csvList.get(i).size() > index[1]) {
                        str = csvList.get(index[0]).get(index[1]);
                    }else {
                        str = error;
                    }
                }
                stack.add(str);
            }
            
        }

        if(!hasOperator && list.size()>1) {
            System.out.println(error);
            return error;
        }

        if(hasOperator && list.size()==2) {
            System.out.println(error);
            return error;
        }

        String valString = stack.pop();

        System.out.println(valString);

        return valString; 

    }


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


    public boolean containsLetterAndNum(String str) {
        str = str.toLowerCase();
        String regex = "[a-z][0-9]*"; 
        if(str.matches(regex)) {
            return true;
        }
        return false;
    }


    //  Check initial format of post notation 
    public boolean checkPostNotation(List<String> list) {
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

    public void implementFunction() throws IOException {
        List<List<String>> csvList = new LinkedList<>();
        List<String> postNotationList = new LinkedList<>();

        csvList = readFromCSV();

        for(int i=0;i<csvList.size();i++) {
            for(int j=0;j<csvList.get(i).size();j++) {
                postNotationList = getPostNotation(csvList.get(i).get(j));
                String strResult = calculatePostNotation(postNotationList, csvList, i, j);
                csvList.get(i).set(j, strResult);
            }
        }

        writeToCSV(csvList);

    }



    public double operatePostNotation(String str, double a, double b) {
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
}