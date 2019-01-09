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

package com.wipro.ats.bdre;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AS294216 on 1/5/2016.
 */
public class ResolvePath {

    private static Pattern pattern = Pattern.compile("%(.)");

    private ResolvePath(){
    }

    public static String replaceVars(String line) {
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(line);
        while (m.find()) {
            String code = m.group(1);
            m.appendReplacement(sb, resolveCode(code));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String resolveCode(String argCode) {
        String code = argCode;
        switch (argCode) {
            case "t":
                return new Date().getTime()+"";
            case "a":
                code = "EEE";
                break;
            case "A":
                code = "EEEEEEE";
                break;
            case "b":
                code = "MMM";
                break;
            case "B":
                code = "MMMMMM";
                break;
            case "c":
            case "e":
            case "m":
                code = "MM";
                break;
            case "d":
            case "D":
            case "n":
                code = "M";
                break;
            case "H":
                code = "HH";
                break;
            case "I":
                code = "hh";
                break;
            case "j":
                code = "D";
                break;
            case "k":
                code = "h";
                break;
            case "M":
                code = "mm";
                break;
            case "p":
                code = "a";
                break;
            case "s":
                return new Date().getTime()+"";
            case "S":
                code = "ss";
                break;
            case "y":
                code = "yy";
                break;
            case "Y":
                code = "yyyy";
                break;
            case "z":
                code = "Z";
                break;
            default:
                return "unknown";
        }
        return new SimpleDateFormat(code).format(new Date());
    }
}