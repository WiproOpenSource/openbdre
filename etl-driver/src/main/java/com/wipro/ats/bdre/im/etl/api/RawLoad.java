/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.im.etl.api;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.BatchDAO;
import com.wipro.ats.bdre.md.dao.FileDAO;
import com.wipro.ats.bdre.md.dao.ServersDAO;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import com.wipro.ats.bdre.md.dao.jpa.FileId;
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by vishnu on 12/14/14.
 * Modified by Arijit
 */
public class RawLoad extends ETLBase {

    @Autowired
    FileDAO fileDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    ServersDAO serversDAO;

    public RawLoad(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(RawLoad.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
            {"ied", "instance-exec-id", " instance exec id"},
            {"lof", "list-of-files", " List of files"},
            {"lob", "list-of-file-batchIds", "List of batch Ids corresponding to above files "}

    };

    public void execute(String[] params) throws IOException {

        //values of lof and lob parameters comes as null when filepath is choosen by user.
        //Null parameters are not handled by CommandLine class
        //So changing structure of params when there are null parameters
        int i=0;
        int j=0;
        String dupParams[]= new String[10];
        int index=0;
        for(String param:params){
            dupParams[index]=param;

            if(i==1) {
                if(param==null) dupParams[index]="null batch";
                i=0;
            }
            if(j==1) {
                if(param==null) dupParams[index]="null file";
                j=0;
            }
            if((param!=null) && (param.equals("-lob")||param.equals("--list-of-file-batchIds"))) i++;
            if((param!=null) && (param.equals("--list-of-files")||param.equals("-lof"))) j++;
            index++;
        }
        //Getting raw table information

        CommandLine commandLine = getCommandLine(dupParams, PARAMS_STRUCTURE);

        String processId = commandLine.getOptionValue("process-id");
        String instanceExecId = commandLine.getOptionValue("instance-exec-id");

        loadRawHiveTableInfo(processId);
        String rawTableName = rawTable;
        String rawDbName = rawDb;
        String filePathString = filePath;
        String listOfFiles = "";
        String listOfBatches = "";

        boolean userPathFlag = false;
        //If user selects enqueueId
        if( "null".equals(filePathString) || filePathString == null) {
            listOfFiles = commandLine.getOptionValue("list-of-files").replace("1234567890","");
            LOGGER.info("list of files is "+ listOfFiles);
            listOfBatches = commandLine.getOptionValue("list-of-file-batchIds").replace("1234567890","");
        }
        //If user select filepath or directoryPath
        else {
            userPathFlag = true;

            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
            FileSystem fs = null;
            try {
                fs = FileSystem.get(conf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path givenPath = new Path(filePathString);
            StringBuilder listOfFilesPath = new StringBuilder();

            try {
                if(!fs.isDirectory(givenPath)){
                    listOfFilesPath.append(filePathString);
                }
                else{
                    FileStatus[] fileStatus = fs.listStatus(givenPath);
                    for (FileStatus fileStat : fileStatus) {
                        listOfFilesPath.append(fileStat.getPath().toString()+",");
                    }
                    listOfFilesPath.deleteCharAt(listOfFilesPath.length()-1);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            listOfFiles = listOfFilesPath.toString();

            listOfBatches = "0";

            Batch batch = batchDAO.get(0L);
            Servers servers = serversDAO.get(123461);

            String[] files = listOfFiles.split(IMConstant.FILE_FIELD_SEPERATOR);
            for(String fileString: files){
                File file = new File();
                FileId fileId = new FileId();
                fileId.setBatchId(batch.getBatchId());
                fileId.setCreationTs(new Date());
                fileId.setFileSize(1L);
                fileId.setServerId(123461);
                fileId.setFileHash("null");
                if(fileString.endsWith(".xlsx"))
                    new RawLoad().readXLSXFile(fileString);
                fileId.setPath(fileString.replace("xlsx","csv"));
                file.setId(fileId);
                file.setBatch(batch);
                file.setServers(servers);
                fileDAO.insert(file);
                LOGGER.info("file "+fileString+" inserted successfully");

                listOfBatches = listOfBatches+",0";
            }

        }

        CreateRawBaseTables createRawBaseTables =new CreateRawBaseTables();
        String[] createTablesArgs={"-p",processId,"-instExecId",instanceExecId };
        createRawBaseTables.executeRawLoad(createTablesArgs);

        //Now load file to table
        loadRawLoadTable(rawDbName, rawTableName, listOfFiles.replaceAll("xlsx","csv"), listOfBatches, userPathFlag, processId);


    }

    private void loadRawLoadTable(String dbName, String tableName, String listOfFiles, String listOfBatches, Boolean userPathFlag, String processId) {
        try {
            LOGGER.debug("Reading Hive Connection details from Properties File");
            String[] files = listOfFiles.split(IMConstant.FILE_FIELD_SEPERATOR);
            String[] tempFiles = createTempCopies(files);
            String[] correspondingBatchIds = listOfBatches.split(IMConstant.FILE_FIELD_SEPERATOR);
            Connection con = getHiveJDBCConnection(dbName);
            Statement stmt = con.createStatement();

            LOGGER.debug("Inserting data into the table");

            if(userPathFlag){
                String deleteRawTableEntriesQuery = "TRUNCATE TABLE "+ tableName;
                GetGeneralConfig generalConfig = new GetGeneralConfig();
                String hdfsURI = generalConfig.byConigGroupAndKey("imconfig", "common.default-fs-name").getDefaultVal();
                String bdreLinuxUserName = generalConfig.byConigGroupAndKey("scripts_config", "bdreLinuxUserName").getDefaultVal();
                ProcessInfo process = new GetProcess().getProcess(Integer.parseInt(processId));

                String serdePath = hdfsURI+"/user/"+bdreLinuxUserName+"/wf/1/5/"+process.getParentProcessId()+"/lib/hive-hcatalog-core-0.13.1.jar";
                String addSerde = "add jar "+serdePath;
                stmt.execute(addSerde);
                stmt.executeUpdate(deleteRawTableEntriesQuery);
                for (int i = 0; i < tempFiles.length; i++) {
                    String query = "LOAD DATA INPATH '" + tempFiles[i] + "' INTO TABLE " + tableName
                            + " PARTITION (batchid='" + correspondingBatchIds[i] + "')";
                    LOGGER.info("Raw load query " + query);
                    stmt.executeUpdate(query);
                }
            }else {
                for (int i = 0; i < tempFiles.length; i++) {
                    String query = "LOAD DATA INPATH '" + tempFiles[i] + "'OVERWRITE INTO TABLE " + tableName
                            + " PARTITION (batchid='" + correspondingBatchIds[i] + "')";
                    LOGGER.info("Raw load query " + query);
                    stmt.executeUpdate(query);
                }
            }
            stmt.close();
            con.close();
            LOGGER.info("Raw load completed.");

        } catch (Exception e) {
            LOGGER.error("Error In RawLoad" + e);
            throw new ETLException(e);
        }

    }

    private String[] createTempCopies(String[] files){
        String[] outputFileList= new String[files.length];
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", IMConfig.getProperty("common.default-fs-name"));
            FileSystem fs = FileSystem.get(conf);
            for(int i=0;i<files.length;i++) {
                Path srcPath = new Path(files[i]);
                Path destPath = new Path(files[i]+"_tmp");
                FileUtil.copy(fs, srcPath, fs, destPath, false, conf);
                outputFileList[i]=files[i]+"_tmp";
            }

        } catch(Exception e){
            LOGGER.error("error occured ="+ e);
            throw new ETLException(e);
        }
        return outputFileList;
    }




    public void readXLSXFile(String fileName) throws IOException {
        InputStream XlsxFileToRead = null;
        XSSFWorkbook workbook = null;
        Configuration conf = new Configuration();
        FileSystem fs = null;
        String excelFileName=fileName.split("/")[fileName.split("/").length-1];
        String csvFileName=excelFileName.split("\\.")[0]+".csv";
        System.out.println("excelFileName is "+excelFileName);
        System.out.println("csvFileName is "+csvFileName);
        try {
            try {
                fs = FileSystem.get(new URI("hdfs://localhost:8020"),conf);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            Path dirPath = new Path(fileName);
            if (fs.exists(dirPath)) {
                // fs.delete(dirPath, true);
                System.out.println("file found");
            }else
                System.out.println("no file found");

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            // XlsxFileToRead = new FileInputStream(fileName);
            XlsxFileToRead=fs.open(new Path(fileName));
            //Getting the workbook instance for xlsx file
            workbook = new XSSFWorkbook(XlsxFileToRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //getting the first sheet from the workbook using sheet name.
        // We can also pass the index of the sheet which starts from '0'.
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        XSSFRow row;
        XSSFCell cell;

        //Iterating all the rows in the sheet
        Iterator rows = sheet.rowIterator();
        String outputPath=fileName.replace("xlsx","csv");
        System.out.println(outputPath);

        try {
            if (fs.exists(new Path(outputPath))) {
                // fs.delete(dirPath, true);
                System.out.println("output path found");
            }else{
                System.out.println("output path not found");
                //fs.mkdirs(new Path(outputPath));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        FSDataOutputStream fsDataOutputStream=null;
        //BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
        try {
            fsDataOutputStream=fs.create(new Path(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (rows.hasNext()) {
            row = (XSSFRow) rows.next();
            int n = row.getLastCellNum();
            //System.out.println("no of cells "+n);
            //Iterating all the cells of the current row
            Iterator cells = row.cellIterator();
            String tmpValue = null;
            while (cells.hasNext()) {
                cell = (XSSFCell) cells.next();
                int tmp=cell.getColumnIndex();
                DataFormatter fmt = new DataFormatter();
                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {

                    if (tmp==(n-1)){
                        if (cell.getStringCellValue().contains(",")==true)
                            tmpValue="\""+cell.getStringCellValue().replace("\n"," ")+"\"";
                        else
                            tmpValue=cell.getStringCellValue().replace("\n"," ");
                    }
                    else{
                        if (cell.getStringCellValue().contains(",")==true)
                            tmpValue="\""+cell.getStringCellValue().replace("\n"," ")+"\"" + ",";
                        else
                            tmpValue=cell.getStringCellValue().replace("\n"," ") + ",";
                    }
                    try {
                        fsDataOutputStream.write(tmpValue.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    if (tmp==(n-1))
                        tmpValue= String.valueOf(fmt.formatCellValue(cell));
                    else
                        tmpValue=fmt.formatCellValue(cell) + ",";
                    fsDataOutputStream.write(tmpValue.getBytes());
                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
                    if (tmp==(n-1))
                        tmpValue= String.valueOf(cell.getBooleanCellValue());
                    else
                        tmpValue=cell.getBooleanCellValue() + ",";
                    fsDataOutputStream.write(tmpValue.getBytes());

                } else { // //Here if require, we can also add below methods to
                    // read the cell content
                    // XSSFCell.CELL_TYPE_BLANK
                    // XSSFCell.CELL_TYPE_FORMULA
                    // XSSFCell.CELL_TYPE_ERROR
                }

                System.out.print(tmpValue);
            }
            System.out.println();
            try {
                XlsxFileToRead.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fsDataOutputStream.write("\n".getBytes());
        }
        fsDataOutputStream.close();
    }






}