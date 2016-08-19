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

package com.wipro.ats.bdre.md.api.airflow;


import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.RegisterFile;
import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import com.wipro.ats.bdre.util.AirflowUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by pushpak on 18/07/2016
 */
public class AirflowRegisterFile {
    private static final Logger LOGGER = Logger.getLogger(AirflowRegisterFile.class);

    /**
     * This method calls execute method and persist the output till runtime.
     *
     * @param args String array having environment and process-id with their command line notations.
     */
    public static void main(String[] args) {
        new AirflowRegisterFile().execute(args);
    }

    public void execute(String[] args){
        RegisterFileInfo fileInfo = new RegisterFile().execute(args);
        AirflowUtil airflowUtil = new AirflowUtil();
        try {
            airflowUtil.persistBeanData(fileInfo, false);
            try
            {
                String homeDir = System.getProperty("user.home");
                FileWriter fw = new FileWriter(homeDir+"/jobInfo.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);


                bw.write("fileInfo.getSubProcessId():"+fileInfo.getSubProcessId().toString()+"\n");
                bw.write("fileInfo.getServerId():"+fileInfo.getServerId().toString()+"\n");
                bw.write("fileInfo.getPath():"+fileInfo.getPath()+"\n");
                bw.write("fileInfo.getFileSize():"+fileInfo.getFileSize().toString()+"\n");
                if(fileInfo.getFileHash() != null)
                    bw.write("fileInfo.getFileHash():"+fileInfo.getFileHash().toString()+"\n");
                else
                    bw.write("fileInfo.getFileHash():null\n");
                if(fileInfo.getCreationTs() != null)
                    bw.write("fileInfo.getCreationTs():"+fileInfo.getCreationTs().toString()+"\n");
                else
                    bw.write("fileInfo.getCreationTs():null\n");
                bw.write("fileInfo.getBatchId():"+fileInfo.getBatchId().toString()+"\n");
                bw.write("fileInfo.getParentProcessId():"+fileInfo.getParentProcessId().toString()+"\n");
                bw.write("fileInfo.getBatchMarking():"+fileInfo.getBatchMarking()+"\n");

                bw.close();

            }catch(IOException i)
            {
                i.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }
}
