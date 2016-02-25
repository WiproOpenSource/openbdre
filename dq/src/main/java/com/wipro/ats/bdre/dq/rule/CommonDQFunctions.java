/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.dq.rule;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

/**
 * Created by IshitaParekh on 31-03-2015.
 */
public class CommonDQFunctions {
    private static final Logger LOGGER = Logger.getLogger(CommonDQFunctions.class);
    public String checkString(String min, String max, String leftPad, String word)
    {
        int minLen = Integer.parseInt(min);
        int maxLen = Integer.parseInt(max);
        if (word.length()>minLen && word.length()<maxLen)
        {
            LOGGER.info("The string entered falls in the acceptable range. ");
            return word;
        }
        // if smaller than minimum length
        else if (word.length() > maxLen)
        {
            return "String entered exceeds maximum length";
        }
        else if (word.length() < minLen)
        {
            LOGGER.info("String entered is smaller than the minimum length. ");
            String result = StringUtils.leftPad(word,minLen,leftPad);
            return "Modified string:" +result;
        }
        else
            return null;
    }
    public String checkInteger(String num, String rangeMin, String rangeMax)
    {
        try {
            int n = Integer.parseInt(num);
            int min = Integer.parseInt(rangeMin);
            int max = Integer.parseInt(rangeMax);
            if (n > min && n < max)
                return num;
            else return "Integer not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid integer!");
            return "null";
        }
    }
    public String checkLong(String num, String rangeMin, String rangeMax) {
        try {
            Long n = Long.parseLong(num);
            Long min = Long.parseLong(rangeMin);
            Long max = Long.parseLong(rangeMax);
            if (n > min && n < max)
                return num;
            else return "Long number not in range";
        } catch (NumberFormatException ex) {
            System.out.println("Not a valid long number!");
            return "null";
        }
    }
    public String checkDouble(String num, String rangeMin, String rangeMax) {
        try {
            Double n = Double.parseDouble(num);
            Double min = Double.parseDouble(rangeMin);
            Double max = Double.parseDouble(rangeMax);
            if (n > min && n < max)
                return num;
            else return "Double number not in range";
        } catch (NumberFormatException ex) {
            LOGGER.info("Not a valid double number!");
            return "null";
        }
    }
    public String checkFloat(String num, String rangeMin, String rangeMax) {
        try {
            Float n = Float.parseFloat(num);
            Float min = Float.parseFloat(rangeMin);
            Float max = Float.parseFloat(rangeMax);
            if (n > min && n < max)
                return num;
            else return "Float number not in range";
        } catch (NumberFormatException ex) {
            LOGGER.info("Not a valid float number!");
            return "null";
        }
    }
}