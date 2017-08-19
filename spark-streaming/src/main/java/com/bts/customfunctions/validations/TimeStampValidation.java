package com.bts.customfunctions.validations;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cloudera on 8/6/17.
 */
public class TimeStampValidation implements Function,Serializable{

    @Override
    public Object call(Object o) throws Exception {
        Tuple2<String,Row> input = (Tuple2<String,Row>) o;

        Row inputRow = input._2;
        Row headerRow = inputRow.getStruct(inputRow.fieldIndex("Header"));
        String sourceTimeStamp = headerRow.getString(headerRow.fieldIndex("SourceTimeStamp"));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");

        try {
           Date result = df.parse(sourceTimeStamp);
            return true;
        }catch (Exception e){
            System.out.println(" TimeStamp is not Valid" );
            return false;
        }


    }
}
