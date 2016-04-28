/*
 *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 */

package com.wipro.ats.bdre.clustermigration;

import com.wipro.ats.bdre.BaseStructure;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by cloudera on 4/7/16.
 */
public class DestTableLoad extends BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(DestTableLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"src", "source-path", " Source path"},
            {"dest", "dest-path", " Destination path"},
            {"destFs", "dest-fs", " Destination file system"}
    };

    public void execute(String[] params) throws IOException{

        CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
        String src = commandLine.getOptionValue("source-path");
        String dest = commandLine.getOptionValue("dest-path");
        String destFs=commandLine.getOptionValue("dest-fs");

        Configuration config = new Configuration();
        config.set("fs.defaultFS",destFs);
        FileSystem hdfs = FileSystem.get(config);
        Path srcPath = new Path(src);
        RemoteIterator<LocatedFileStatus> srcFiles = hdfs.listFiles(srcPath, true);
        while(srcFiles.hasNext()){
            String absolutePath=srcFiles.next().getPath().toUri().toString();
            if(absolutePath.endsWith("/"))
                absolutePath=absolutePath.substring(0,absolutePath.length()-1);
            LOGGER.info("absolutePath of source business partition= " + absolutePath);
            String relativePath=absolutePath.replace(src,"");
            if(relativePath.endsWith("/"))
                relativePath=relativePath.substring(0,relativePath.length()-1);
            LOGGER.info("relativePath of source business partition= = " + relativePath);
            if(!dest.endsWith("/"))
                dest=dest+"/";
            String destCheckPathString=dest+relativePath;
            Path destCheckPath=new Path(destCheckPathString);
            LOGGER.info("destCheckPath = " + destCheckPath);
            //find first index that contains a "/" from the end of the string, after first find the second such occurrence, finally trim the '/instanceexecid=number/part_0000' from the whole path, do this for both source and dest paths
            int destIndex=destCheckPathString.lastIndexOf("/");
            int secondLastDestIndex=destCheckPath.toString().lastIndexOf("/",destIndex-1);
            int srcIndex=absolutePath.lastIndexOf("/");
            int secondLastSrcIndex=absolutePath.substring(0,srcIndex).lastIndexOf("/",srcIndex-1);
            String truncatedSrcPath=absolutePath.substring(0,secondLastSrcIndex);
            LOGGER.info("truncated Src Path = " + truncatedSrcPath);
            String truncatedDestPath=destCheckPath.toString().substring(0,secondLastDestIndex);
            LOGGER.info("truncated Dest Path = " +truncatedDestPath) ;
            Path existsPathCheck=new Path(truncatedDestPath);
            Path srcPathToMove=new Path(truncatedSrcPath);
            //check if the business partition to be copied already exists inside the destination table, if it does, it has to be overwritten (in this case delete at dest and move from source to dest
            LOGGER.info("Does the business partition exist already inside the table? True/False? = " + hdfs.exists(existsPathCheck));
            if(hdfs.exists(existsPathCheck)){
                LOGGER.info("bus partitions to be copied already exist at the destination, hence deleting them at destination");
                hdfs.delete(existsPathCheck,true);
            }
            String destPartitionPath=truncatedDestPath.substring(0,truncatedDestPath.lastIndexOf("/"));
            Path partitionWisePath=new Path(destPartitionPath);
            hdfs.mkdirs(partitionWisePath);
            LOGGER.info("moving the business partitions to the destination table");
            LOGGER.info("moving " +srcPathToMove + " to " +partitionWisePath);
            hdfs.rename(srcPathToMove,partitionWisePath);
        }
        hdfs.delete(srcPath,true);
    }
}
