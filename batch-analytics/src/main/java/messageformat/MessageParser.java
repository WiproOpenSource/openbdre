package messageformat;

import java.io.Serializable;

/**
 * Created by cloudera on 5/17/17.
 */
public interface MessageParser<MessageType> extends Serializable{

    public Object[] parseRecord(String line,Integer sourcePid) throws Exception;

}
