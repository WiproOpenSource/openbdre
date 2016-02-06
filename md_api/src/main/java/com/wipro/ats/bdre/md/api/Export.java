package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.rest.beans.ProcessExport;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * Created by cloudera on 1/28/16.
 */
public class Export {
    
    private static  String source_dir = "";
    List<String>  fileList = new ArrayList<String>();

    private static final Logger LOGGER = Logger.getLogger(Export.class);
    public  static void main(String[] args)
    {
        String processId=args[0];
        String homeDir = System.getProperty("user.home");
        String source_dir=homeDir+"/bdre-wfd/"+processId;


        ProcessExport processExport=new ProcessExport();

        List<Process> processList=new ArrayList<>();
        Process process=new Process();
        process.setAddTS(new Date());
        process.setBatchPattern("Batch");
        process.setBusDomainId(1234561);
        process.setCanRecover(true);
        process.setCounter(123);
        process.setDeleteFlag(false);
        process.setDescription("des");
        process.setEditTS(new Date());
        process.setEnqProcessId(1);

        processList.add(process);

        List<Properties> propertiesList=new ArrayList<>();
        Properties properties=new Properties();
        properties.setDescription("description");
        properties.setCounter(1234);
        properties.setConfigGroup("config");
        properties.setKey("23");
        properties.setProcessId(3232);

        propertiesList.add(properties);

        processExport.setProcessList(processList);
        processExport.setPropertiesList(propertiesList);


        String zippedFile = null;
        LOGGER.info("source directory is "+source_dir);
        if (Files.exists(Paths.get(source_dir)))
        {
            LOGGER.info(source_dir);
            LOGGER.info("path is "+Paths.get(source_dir));
            Export compression=new Export();
            zippedFile=compression.compress(processId,processExport);
        }

        LOGGER.info("zipped file is with location"+zippedFile);
        
        
    }


    public  String compress(String processId,ProcessExport processExport)
    {


        String homeDir = System.getProperty("user.home");

        source_dir=homeDir+"/bdre-wfd/"+processId;

        ObjectMapper mapper = new ObjectMapper();
        File creatingDir = new File(source_dir);
        if (!creatingDir.exists()) {
            creatingDir.mkdir();
        }

        if (creatingDir.exists()) {
            // convert user object to json string,
            try {
                mapper.writeValue(new File(homeDir + "/bdre-wfd/" + processId + "/" +"process.json"), processExport);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        UUID idOne = UUID.randomUUID();
        System.out.println("UUID is "+idOne);
        source_dir=homeDir+"/bdre-wfd/"+processId;
        Export export = new Export();
        export.generateFileList(new File(source_dir));
        String output_folder=homeDir+"/bdre-wfd/export-"+processId;
        File outdir=new File(output_folder);
        if (!outdir.exists())
            outdir.mkdir();
        System.out.println("output folder is "+output_folder);
        String zippedFile=output_folder+"/"+processId+"-"+idOne+".zip";
        export.zipIt(zippedFile);
        return zippedFile;
    }

    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public void zipIt(String zipFile){

        byte[] buffer = new byte[1024];

        try{

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);

            for(String file : this.fileList){

                System.out.println("File Added : " + file);
                ZipEntry ze= new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(source_dir + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     * @param node file or directory
     */
    public void generateFileList(File node){

        //add file only
        if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if(node.isDirectory()){
            String[] subNote = node.list();
            for(String filename : subNote){
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    public String generateZipEntry(String file){
        return file.substring(source_dir.length()+1, file.length());
    }
    
}
