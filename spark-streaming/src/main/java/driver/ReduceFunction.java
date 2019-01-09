package driver;

import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import scala.Tuple2;
import util.WrapperMessage;

import java.io.Serializable;

/**
 * Created by cloudera on 7/10/17.
 */
public class ReduceFunction implements Function2,Serializable {

    @Override
    public Object call(Object o1, Object o2) throws Exception {
        Tuple2<String,WrapperMessage> input1 = (Tuple2<String,WrapperMessage>) o1;
        Tuple2<String,WrapperMessage> input2 = (Tuple2<String,WrapperMessage>) o2;

        Row inputRow1 = input1._2.getRow();
        Row inputRow2 = input2._2.getRow();

        int noOfElements1 = inputRow1.size();
        String[] attributes1 = new String[noOfElements1];
        for(int i=0; i<noOfElements1; i++){
            attributes1[i] = inputRow1.getString(i);
            if(i == 0){
                attributes1[i] = inputRow1.getString(i) + inputRow2.getString(i);
            }
        }

        Row outputRow = RowFactory.create(attributes1);
        return new Tuple2<String,WrapperMessage>(null, new WrapperMessage(outputRow));
    }

}
