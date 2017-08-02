package messageformat;

/**
 * Created by cloudera on 5/17/17.
 */
public interface MessageParser<MessageType> {

    public Object[] parseRecord(String line,Integer sourcePid) throws Exception;

}
