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
        findMatches("/hoem/alkjsbg2347/%ksj/%/%H/%HH/%ss/%s/%m/%M");
    }

    public static void findMatches(String html){
        Matcher matcher = COMPILED_PATTERN.matcher(html);
        // Check all occurance
        System.out.println("------------------------");
        System.out.println("Following Matches found:");
        int count = 0;
        StringBuffer htmlCopy = new StringBuffer(html);
        Integer Indexes[]=new Integer[50];
        Integer Values[]=new Integer[50];
        Integer counter=0;
        Integer value=null;
        while (matcher.find()) {
            //System.out.print("Start index: " + matcher.start());
            //System.out.print(" End index: " + matcher.end() + " ");
            //System.out.println("yo " + replaceMatches(matcher, html));
            value=Integer.parseInt(replaceMatches(matcher,matcher.group()).substring(1));

            if(value!=200) {
                System.out.println(value);
                Indexes[counter] = matcher.end();
                LOGGER.debug("123"+Indexes[counter]);
                Values[counter++] = value;
            }
            //System.out.println(matcher.group());
            //System.out.println(matcher.group());
            //replaceMatches(matcher,matcher.group());
            //if(count++ == 3)
               // break;
        }
        for (int i=0; i < counter;i++) {
            //LOGGER.debug(htmlCopy.charAt(Indexes[i]-1)+" "+htmlCopy.charAt(Indexes[i]-2));
            htmlCopy.deleteCharAt(Indexes[i] - 2);
            htmlCopy.deleteCharAt(Indexes[i] - 2);
            htmlCopy.insert(Indexes[i]-2,Values[i].toString());
            //break;
        }
        LOGGER.debug("origstr "+html);
        LOGGER.debug("mod str "+htmlCopy);
        /*String replacer
        char replacer;
        for(int i=0;i<counter;i++)
        {
          System.out.println((htmlCopy.charAt(Indexes[i]-1)));

        }*/

       // System.out.println(matcher);
       // System.out.println("------------------------");
    }

    /*public static String replaceMatches(String html){
        //Pattern replace = Pattern.compile("\\s+");
        Matcher matcher = COMPILED_PATTERN.matcher(html);
        html = matcher.replaceAll(REPLACEMENT);
        return html;
    }*/

    private static String replaceMatches(Matcher matcher, String toBeMatched) {
        SimpleDateFormat sdf;
        String timeReturn="-1";
        switch(toBeMatched) {
            case "%H":   sdf = new SimpleDateFormat(toBeMatched);
                        //System.out.println(matcher.group().replace(toBeMatched,sdf.format(new Date())));
                        timeReturn=sdf.format(new Date());
                        System.out.println("yoo"+timeReturn);
                        return timeReturn;
            case "%s":   sdf = new SimpleDateFormat(toBeMatched);
                       // System.out.println(matcher.replaceAll(sdf.format(new Date())));
                        timeReturn=sdf.format(new Date());
                        System.out.println("yoo"+timeReturn);
                        return timeReturn;
            case "%m":   sdf=new SimpleDateFormat(toBeMatched);
                        //System.out.println(matcher.group().replace(toBeMatched,sdf.format(new Date())));
                        timeReturn=sdf.format(new Date());
                        System.out.println("yoo"+timeReturn);
                        return timeReturn;
            case "%M":   sdf=new SimpleDateFormat(toBeMatched);
                //System.out.println(matcher.group().replace(toBeMatched,sdf.format(new Date())));
                timeReturn=sdf.format(new Date());
                System.out.println("yoo"+timeReturn);
                return timeReturn;

            default:    return "-200";
        }
    }
}
