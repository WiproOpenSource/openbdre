package com.wipro.ats.bdre;


import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AS294216 on 1/5/2016.
 */
public class resolvePath {
    private static Logger LOGGER=Logger.getLogger(resolvePath.class);
    private static final String PATTERN = "%.";
    private static final String REPLACEMENT = "";
    private static final Pattern COMPILED_PATTERN = Pattern.compile(PATTERN);


    public static String getResolvedPath (String path) {
        LOGGER.debug("path before resolving: "+path);


        LOGGER.debug("path after resolving: "+path);
        return path;
    }

    public static void main (String par[]) {
        findMatches("/hoem/alkjsbg2347/%ksj/%/djfh%Hs/%H/%Hr/%HH/%ss/%s/%m/%M");
    }

    public static void findMatches(String html){
        Matcher matcher = COMPILED_PATTERN.matcher(html);
        // Check all occurance
        System.out.println("------------------------");
        System.out.println("Following Matches found:");
        int count = 0;
        while (matcher.find()) {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            //System.out.println(matcher.group());
            System.out.println(matcher.group());
            //replaceMatches(matcher,matcher.group());
            //if(count++ == 3)
               // break;
        }
        System.out.println(matcher);
        System.out.println("------------------------");
    }

    /*public static String replaceMatches(String html){
        //Pattern replace = Pattern.compile("\\s+");
        Matcher matcher = COMPILED_PATTERN.matcher(html);
        html = matcher.replaceAll(REPLACEMENT);
        return html;
    }*/

    private static Matcher replaceMatches(Matcher matcher, String toBeMatched) {
        SimpleDateFormat sdf;
        switch(toBeMatched) {
            case "H":   sdf = new SimpleDateFormat(toBeMatched);
                        System.out.println(matcher.group().replace(toBeMatched,sdf.format(new Date())));
                        return matcher;
            case "s":   sdf = new SimpleDateFormat(toBeMatched);
                        System.out.println(matcher.replaceAll(sdf.format(new Date())));
                        return matcher;
            default:    return matcher;
        }
    }
}
