package messageformat;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cloudera on 5/17/17.
 */
public class RegexParser implements MessageParser{
    // Example Apache log line:
    //   127.0.0.1 - - [21/Jul/2014:9:55:27 -0800] "GET /home.html HTTP/1.1" 200 2048
    private static final Logger logger = Logger.getLogger("Access");
    // 1:IP  2:client 3:user 4:date time                   5:method 6:req 7:proto   8:respcode 9:size
    private static final String LOG_ENTRY_PATTERN ="^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+)";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

    @Override
    public String[] parseRecord(String record,Integer pid) throws Exception{
        try {
            Matcher m = PATTERN.matcher(record);

            if (!m.find()) {
                logger.log(Level.ALL, "Cannot parse logline" + record);
                throw new RuntimeException("Error parsing logline");
            }
            String[] arguments = {m.group(1), m.group(2), m.group(3), m.group(4),
                    m.group(5), m.group(6), m.group(7), m.group(8), m.group(9)};
            return arguments;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e);
            throw e;
        }
    }
}
