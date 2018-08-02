package driver;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import scala.Tuple2;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by cloudera on 7/13/17.
 */
public class FlatMapFunctionEg implements FlatMapFunction,Serializable {
    @Override
    public Iterator<Tuple2> call(Object inputRecord) throws Exception {
        Tuple2<String,WrapperMessage> input = (Tuple2<String,WrapperMessage>) inputRecord;
        ArrayList<WrapperMessage> wrapperMessageArrayList = new ArrayList<>();

        Row inputRow = input._2.getRow();
        int noOfElements = inputRow.size();
        String[] attributes = new String[noOfElements];
        for(int i=0; i<noOfElements; i++){
            attributes[i] = inputRow.getString(i);
            if(i == 0){
                attributes[i] = inputRow.getString(i)+"madhav";
            }

        }
        Row outputRow = RowFactory.create(attributes);
        WrapperMessage wrapperMessage = new WrapperMessage(outputRow);
        wrapperMessageArrayList.add(wrapperMessage);

        return (Iterator<Tuple2>) wrapperMessageArrayList;

    }
}
