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

package com.wipro.ats.bdre.md.triggers;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.ProcessTypeDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.event.spi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by SH324337 on 12/18/2015.
 */
@Component
public class MetadataPersistenceTrigger implements PreUpdateEventListener, PreInsertEventListener, PostUpdateEventListener, PostInsertEventListener {

    private static final Logger LOGGER = Logger.getLogger(MetadataPersistenceTrigger.class);
    private static Map<Integer,Integer> processTypeMap=new HashMap<>();
    @Autowired ProcessTypeDAO processTypeDAO;

    private void populateProcessTypeMap(){
        LOGGER.info("inserting into Map");
        if(processTypeMap.size()==0) {
            List<com.wipro.ats.bdre.md.dao.jpa.ProcessType> processTypeInfos = processTypeDAO.listFull(0, Integer.MAX_VALUE);
            for (com.wipro.ats.bdre.md.dao.jpa.ProcessType processType : processTypeInfos) {
                processTypeMap.put(processType.getProcessTypeId(), processType.getParentProcessTypeId() == null ? 0 : processType.getParentProcessTypeId());
                LOGGER.info("map contains " + processType.getProcessTypeId() + ", " + processTypeMap.get(processType.getProcessTypeId()));
            }
        }
    }
    private void processTypeValidator(Object object) {
        populateProcessTypeMap();
        Process process = (Process) object;
        LOGGER.info("Attempting to insert process " + process.getProcessName());
        Integer processTypeId = process.getProcessType().getProcessTypeId();
        Process parentProcess = process.getProcess();
        if (parentProcess == null) {
            if (processTypeMap.get(processTypeId)!=0) {
                throw new MetadataException(processTypeId + " process type is not applicable for parent processes but " + process.getProcessId() + " seems to be parent process id");
            }
        } else {
            Integer parentProcessTypeId = parentProcess.getProcessType().getProcessTypeId();
            if (processTypeMap.get(processTypeId)==0) {
                throw new MetadataException(processTypeId + " process type is not applicable for sub processes. " + process.getProcessId() + " seems to be a subprocess with parent " + parentProcess.getProcessId());
            }else if(processTypeMap.get(processTypeId)!=parentProcessTypeId)
            {
                throw new MetadataException(processTypeId + " should have a parent with process type=" + processTypeMap.get(processTypeId) + " but it set parent proces type="+parentProcessTypeId);
            }
        }
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (event.getEntity() instanceof Process) {
            processTypeValidator(event.getEntity());
        }
        return false;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        UUID idOne = UUID.randomUUID();
        if (event.getEntity() instanceof Process) {
            if (((Process) event.getEntity()).getProcessCode()==null)
            ((Process) event.getEntity()).setProcessCode(idOne.toString());

            processTypeValidator(event.getEntity());
        }
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        //tod o: Update edit_ts property if its Process and Properties
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        //TOD O: Update edit_ts property if its Process and Properties
    }
}


