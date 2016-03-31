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

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AR288503 on 10/4/2015.
 */
@Controller
@RequestMapping("/filehandler")
public class CodeUploaderAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(CodeUploaderAPI.class);
    private static final String UPLOADBASEDIRECTORY = "upload.base-directory";

    //Multipart does not support put
    @RequestMapping(value = "/upload/{parentProcessId}/{subDir}", method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper handleFileUpload(@PathVariable("parentProcessId") Integer parentProcessId,
                                 //This is lib, hql etc according to file to be uploaded
                                 @PathVariable("subDir") String subDir,
                                 @RequestParam("file") MultipartFile file, Principal principal) {

        if (!file.isEmpty()) {
            try {

                String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY);
                String name = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                String uploadLocation = uploadedFilesDirectory + "/" + parentProcessId + "/" + subDir;
                LOGGER.debug("Upload location: " + uploadLocation);
                File fileDir = new File(uploadLocation);
                fileDir.mkdirs();
                File fileToBeSaved = new File(uploadLocation + "/" + name);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(fileToBeSaved));
                stream.write(bytes);
                stream.close();
                LOGGER.debug("Uploaded file: " + fileToBeSaved);

                //Populating Uploaded file bean to return in RestWrapper
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setParentProcessId(parentProcessId);
                uploadedFile.setSubDir(subDir);
                uploadedFile.setFileName(name);
                uploadedFile.setFileSize(fileToBeSaved.length());
                LOGGER.debug("The UploadedFile bean:" + uploadedFile);
                LOGGER.info("File uploaded : " + uploadedFile + " uploaded by User:" + principal.getName());

                return new RestWrapper(uploadedFile, RestWrapper.OK);
            } catch (Exception e) {
                LOGGER.error( e);
                return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            }
        } else {
            return new RestWrapper("You failed to upload because the file was empty.", RestWrapper.ERROR);

        }
    }

    @RequestMapping(value = "/upload/{parentProcessId}/{subDir}/{fileName:.+}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(@PathVariable("parentProcessId") Integer parentProcessId,
                       //This is lib, hql etc
                       @PathVariable("subDir") String subDir,
                       @PathVariable("fileName") String fileName, Principal principal) {
        UploadedFile uploadedFile = new UploadedFile();

        try {
            String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY);
            String deleteFile = uploadedFilesDirectory + "/" + parentProcessId + "/" + subDir + "/" + fileName;
            File file = new File(deleteFile);
            Path fp = file.toPath();

            //delete the  file
            Files.delete(fp);
            LOGGER.info("File: " + deleteFile + " deleted by User:" + principal.getName());
            return new RestWrapper(uploadedFile, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error("Error occurred while deleting file", e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
    }

    @RequestMapping(value = "/upload/{parentProcessId}/{subDir}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper listJars(@PathVariable("parentProcessId") Integer parentProcessId,
                         //This is lib, hql etc
                         @PathVariable("subDir") String subDir, Principal principal) {


        List<String> fileList = new ArrayList<String>();
        try {
            String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY) + "/" + parentProcessId + "/" + subDir;
            File[] files = new File(uploadedFilesDirectory).listFiles();
            //If this pathname does not denote a directory, then listFiles() returns null.
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file.getName());
                }
            }
            LOGGER.info("Uploaded jar list : " + fileList + " fetched by User:" + principal.getName());
            return new RestWrapper(fileList, RestWrapper.OK);
        } catch (Exception e) {
            LOGGER.error("Error occurred while downloading file", e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
    }

    @RequestMapping(value = "/uploadzip/{subDir}", method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper zipUpload(   //This is lib, hql ,zip etc according to file to be uploaded
                                 @PathVariable("subDir") String subDir,
                                 @RequestParam("file") MultipartFile file, Principal principal) {
        if (!file.isEmpty()) {
            try {
                String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY);
                String name = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                String uploadLocation = uploadedFilesDirectory + "/" + subDir;
                LOGGER.debug("Upload location of zip file: " + uploadLocation);
                File fileDir = new File(uploadLocation);
                fileDir.mkdirs();
                File f = new File(uploadLocation+"/"+name);
                if(f.exists()) {
                   f.delete();
                }
                File fileToBeSaved = new File(uploadLocation + "/" + name);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(fileToBeSaved));
                stream.write(bytes);
                stream.close();
                //Populating Uploaded file bean to return in RestWrapper
                LOGGER.info("name of the file is + "+name);
                if (CodeUploaderAPI.isNumeric(subDir))
                {
                    File oldName = new File(uploadLocation+"/"+name);
                    File newName = new File(uploadLocation+"/"+"analytic.png");
                    if(oldName.renameTo(newName)) {
                        System.out.println("renamed");
                    } else {
                        System.out.println("Error");
                    }
                }
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setParentProcessId(null);
                uploadedFile.setSubDir(subDir);
                uploadedFile.setFileName(name);
                uploadedFile.setFileSize(fileToBeSaved.length());
                LOGGER.debug("The UploadedFile bean:" + uploadedFile);
                LOGGER.info("File uploaded : " + uploadedFile + " uploaded by User:" + principal.getName());
                return new RestWrapper(uploadedFile, RestWrapper.OK);
            } catch (Exception e) {
                LOGGER.error("error occurred while uploading file", e);
                return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            }
        } else {
            return new RestWrapper("You failed to upload because the file was empty.", RestWrapper.ERROR);

        }
    }

    @RequestMapping(value = "/upload/{parentProcessId}/{subDir}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody public
    void download(@PathVariable("parentProcessId") Integer parentProcessId,
                  //This is lib, hql etc
                  @PathVariable("subDir") String subDir,
                  @PathVariable("fileName") String fileName, HttpServletResponse response, Principal principal) {
        try {
            String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY);
            String downloadFile = uploadedFilesDirectory + "/" + parentProcessId + "/" + subDir + "/" + fileName;
            File file = new File(downloadFile);
            InputStream in = new BufferedInputStream(new FileInputStream(file));

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
            response.flushBuffer();

            LOGGER.info("File downloaded : " + downloadFile + " downloaded by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error("Error occurred while downloading file", e);
        }
    }

    public class UploadedFile {
        private Integer parentProcessId;
        private String subDir;
        private String fileName;
        private long fileSize;
        private boolean fileExists;
        @Override
        public String toString() {
            return "parentProcessId=" + parentProcessId +
                    " subDir=" + subDir +
                    " fileName=" + fileName +
                    " fileSize=" + fileSize;
        }

        public String getSubDir() {
            return subDir;
        }

        public void setSubDir(String subDir) {
            this.subDir = subDir;
        }

        public Integer getParentProcessId() {
            return parentProcessId;
        }

        public void setParentProcessId(Integer parentProcessId) {
            this.parentProcessId = parentProcessId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public boolean isFileExists() {
            return fileExists;
        }

        public void setFileExists(boolean fileExists) {
            this.fileExists = fileExists;
        }
    }

    @RequestMapping(value = "/check/{parentProcessId}/{subDir}", method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper checkFileExists(@PathVariable("parentProcessId") Integer parentProcessId,
                                 //This is lib, hql etc according to file to be uploaded
                                 @PathVariable("subDir") String subDir,
                                 @RequestParam("file") String file, Principal principal) {

        boolean fileExists=false;
            try {

                String uploadedFilesDirectory = MDConfig.getProperty(UPLOADBASEDIRECTORY);
                String fileName = file;
                String fileLocation = uploadedFilesDirectory + "/" + parentProcessId + "/" + subDir;
                LOGGER.debug("Upload location of file: " + fileLocation);
                File fileToBeChecked = new File(fileLocation + "/" + fileName);
                LOGGER.debug("Checking if file: " + fileToBeChecked+ " exists");
                fileExists=fileToBeChecked.exists() && !fileToBeChecked.isDirectory();
                UploadedFile uploadedFile = new UploadedFile();
                uploadedFile.setFileExists(fileExists);
                LOGGER.info("file upload checked by " + principal.getName());
                return new RestWrapper(uploadedFile, RestWrapper.OK);
            } catch (Exception e) {
                LOGGER.error("error occurred while checking if file exists", e);
                return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
                            }
    }
    @Override
    public Object execute(String[] params) {
        return null;
    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
