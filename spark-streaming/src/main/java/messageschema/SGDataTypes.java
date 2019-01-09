package messageschema;

import org.apache.spark.sql.types.DataTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cloudera on 7/18/17.
 */
public class SGDataTypes {
    public Map<String, org.apache.spark.sql.types.DataType> dataTypesMap = new HashMap<>();

    public SGDataTypes() {
        dataTypesMap.put("String", DataTypes.StringType);
        dataTypesMap.put("Binary", DataTypes.BinaryType);
        dataTypesMap.put("Boolean", DataTypes.BooleanType);
        dataTypesMap.put("Date", DataTypes.DateType);
        dataTypesMap.put("TimeStamp", DataTypes.TimestampType);
        dataTypesMap.put("Double", DataTypes.DoubleType);
        dataTypesMap.put("Float", DataTypes.FloatType);
        dataTypesMap.put("Byte", DataTypes.ByteType);
        dataTypesMap.put("Integer", DataTypes.LongType);
        dataTypesMap.put("Long", DataTypes.LongType);
        dataTypesMap.put("Short", DataTypes.ShortType);
    }
}
