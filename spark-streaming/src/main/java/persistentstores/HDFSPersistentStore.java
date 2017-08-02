package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.catalyst.plans.logical.Except;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by cloudera on 5/21/17.
 */
public class HDFSPersistentStore implements PersistentStore {

    @Override
    public void persist(DataFrame df, Integer pid, Integer prevPid) throws Exception {
        try {
            String hdfsPath = new String();
            System.out.println("Inside emitter hdfs, persisting pid = " + prevPid);
            GetProperties getProperties = new GetProperties();


            Properties hdfsProperties = getProperties.getProperties(String.valueOf(pid), "kafka");
            hdfsPath = hdfsProperties.getProperty("hdfs_path");
            if (hdfsPath == null || hdfsPath.isEmpty()) {
                hdfsPath = "/user/cloudera/spark-streaming-data/";
            }
            long date = new Date().getTime();
            if (df.rdd().isEmpty())
                System.out.println("dataframe is empty");
            else {
                System.out.println("Not empty - dataframe is non empty");
                df.show(100);
            }

            if (df != null && !df.rdd().isEmpty()) {
                System.out.println("showing dataframe df before writing to hdfs  ");
                df.show(100);
                System.out.println("df.rdd().count() = " + df.rdd().count());
                String inputPathName = hdfsPath + date + "_" + pid + "/";
                String finalOutputPathName = hdfsPath + date + "-" + pid + "/";
                df.rdd().saveAsTextFile(inputPathName);
                System.out.println("showing dataframe df after writing to hdfs  ");
                df.show(100);

                Path inputPath = new Path(inputPathName);
                Path finalOutputPath = new Path(finalOutputPathName);
                System.out.println("finalOutputPath = " + finalOutputPath);

                Configuration configuration = new Configuration();
                FileSystem fileSystem = inputPath.getFileSystem(configuration);
                boolean result = FileUtil.copyMerge(fileSystem, inputPath, fileSystem, finalOutputPath, true, configuration, null);
                System.out.println("merged result = " + result);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}