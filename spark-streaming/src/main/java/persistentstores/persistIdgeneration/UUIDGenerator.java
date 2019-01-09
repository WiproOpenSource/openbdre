package persistentstores.persistIdgeneration;

import org.apache.spark.sql.Row;

import java.util.UUID;

/**
 * Created by cloudera on 7/12/17.
 */
public class UUIDGenerator implements  PersistIDGen{
    @Override
    public String generatePersistID(Row row){
        return UUID.randomUUID().toString();
    }


}
