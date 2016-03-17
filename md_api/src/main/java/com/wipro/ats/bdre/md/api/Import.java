package com.wipro.ats.bdre.md.api;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Import
{
    private static final Logger LOGGER = Logger.getLogger(Import.class);
    private static final String OUTPUT_FOLDER = "/home/cloudera/bdretest/";
    public static void main( String[] args )
    {
        UUID idOne = UUID.randomUUID();
        LOGGER.info("UUID is "+idOne);

        Import unZip = new Import();
        String jsonfile=unZip.unZipIt("/home/cloudera/bdre-wfd/export-38/38-de03d1e1-6f6a-42a5-8ad9-a2abcaa6c4a5.zip",OUTPUT_FOLDER+"/"+idOne);

        LOGGER.info("returned jsonfile is "+jsonfile);
    }

    /**
     * Unzip it
     * @param zipFile input zip file
     */
    public String unZipIt(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];
        String jsonfile="";
        try{

            //create output directory is not exists
            File folder = new File(OUTPUT_FOLDER);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                LOGGER.info("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                //Unix zip also adds directory entries
                //so if an entry is of type directory create that directory or else create a file
                if(ze.isDirectory()){
                    newFile.mkdirs();
                }else {
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }

                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            LOGGER.info("Done");

            String temp;
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(outputFolder+"/process.json"));
            while ((temp=br.readLine()) != null) {
                jsonfile=jsonfile+temp;
                LOGGER.info(jsonfile);
            }
          LOGGER.info("final string is"+jsonfile);

        }catch(IOException ex){
            LOGGER.info(ex);
        }

       return jsonfile;
    }
}