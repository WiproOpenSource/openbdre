package persistentstores.persistIdgeneration;

import org.apache.spark.sql.Row;

import java.io.Serializable;

/**
 * Created by cloudera on 7/12/17.
 */
public interface PersistIDGen extends Serializable {
    public String generatePersistID(Row row);
}
