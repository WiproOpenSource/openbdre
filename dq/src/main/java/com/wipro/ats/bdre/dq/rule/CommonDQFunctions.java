package com.wipro.ats.bdre.dq.rule;
import org.apache.commons.lang.StringUtils;
/**
 * Created by IshitaParekh on 31-03-2015.
 */
public class CommonDQFunctions {
    public String checkString(String min, String max, String left_pad, String right_pad, String word)
    {
        int min_len = Integer.parseInt(min);
        int max_len = Integer.parseInt(max);
        if (word.length()>min_len && word.length()<max_len)
        {
            System.out.println("The string entered falls in the acceptable range. ");
            return word;
        }
        // if smaller than minimum length
        else if (word.length() > max_len)
        {
            return "String entered exceeds maximum length";
        }
        else if (word.length() < min_len)
        {
            System.out.println("String entered is smaller than the minimum length. ");
            String result = StringUtils.leftPad(word,min_len,left_pad);
            return "Modified string:" +result;
        }
        else
            return null;
    }
    public String checkInteger(String num, String range_min, String range_max)
    {
        try {
            int n = Integer.parseInt(num);
            int min = Integer.parseInt(range_min);
            int max = Integer.parseInt(range_max);
            if (n > min && n < max)
                return num;
            else return "Integer not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid integer!");
            return "null";
        }
    }
    public String checkLong(String num, String range_min, String range_max) {
        try {
            Long n = Long.parseLong(num);
            Long min = Long.parseLong(range_min);
            Long max = Long.parseLong(range_max);
            if (n > min && n < max)
                return num;
            else return "Long number not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid long number!");
            return "null";
        }
    }
    public String checkDouble(String num, String range_min, String range_max) {
        try {
            Double n = Double.parseDouble(num);
            Double min = Double.parseDouble(range_min);
            Double max = Double.parseDouble(range_max);
            if (n > min && n < max)
                return num;
            else return "Double number not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid double number!");
            return "null";
        }
    }
    public String checkFloat(String num, String range_min, String range_max) {
        try {
            Float n = Float.parseFloat(num);
            Float min = Float.parseFloat(range_min);
            Float max = Float.parseFloat(range_max);
            if (n > min && n < max)
                return num;
            else return "Float number not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid float number!");
            return "null";
        }
    }
}