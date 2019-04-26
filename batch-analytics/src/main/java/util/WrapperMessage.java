package util;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import java.io.Serializable;

public class WrapperMessage implements Serializable{
    Row row;
    public WrapperMessage(Row row){
        this.row=row;
    }
    public Row getRow() {
        return row;
    }
    public void setRow(Row row) {
        this.row = row;
    }

    // public WrapperMessage convertRowToWrapperMessage(JavaRDD<Row> )
    public static WrapperMessage convertToWrapperMessage(Row record){
        return new WrapperMessage(record);
    }

    //this method converts a WrapperMessage to a Spark SQL Row
    public static Row convertToRow(WrapperMessage record){
        return record.getRow();
    }
    @Override
    public String toString() {
        return "WrapperMessage{" +
                "row=" + row +
                '}';
    }
}