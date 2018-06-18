package com.wipro.ats.bdre.im;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class HiveUDF extends UDF {

    public IntWritable evaluate(IntWritable num ) {

        if(num == null) {
            return null;
        }

        String str = num.toString();

        List<Character> characters = new ArrayList<Character>();
        for (char c : str.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(str.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return new IntWritable(Integer.parseInt(output.toString()));
        //return new IntWritable(Integer.parseInt(output.toString()));
    }

    public String evaluate(String str) {

        if(str == null || str.equals("null")) {
            return null;
        }

        str = str.replaceAll(" ","");

        List<Character> characters = new ArrayList<Character>();
        for (char c : str.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(str.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }

        String tokenizedValue = output.toString();

        if(tokenizedValue.contains("-") || tokenizedValue.toString().contains(":")){
            tokenizedValue = tokenizedValue.replaceAll("-","").replaceAll(":","");
        }
        return tokenizedValue;
    }

}
