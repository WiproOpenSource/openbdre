package com.bts.customfunctions.validations;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.io.Serializable;

/**
 * Created by cloudera on 8/6/17.
 */
public class NullCheckValidation implements Function,Serializable {
    @Override
    public Object call(Object o) throws Exception {
        Tuple2<String,Row> input = (Tuple2<String,Row>) o;

        Row inputRow = input._2;
        String id = inputRow.getString(inputRow.fieldIndex("Id"));

        try {
            if(id== null || id.equals("")){
                return false;
            }
            else
                return true;

        }catch (Exception e){
            return false;
        }

    }
}
