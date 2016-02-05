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
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.event.spi.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by SH324337 on 12/18/2015.
 */
@Component
public class MetadataPersistenceTrigger implements PreUpdateEventListener, PreInsertEventListener, PostUpdateEventListener, PostInsertEventListener {

    private static final Logger LOGGER = Logger.getLogger(MetadataPersistenceTrigger.class);
    private static HashMap<Integer,Integer> processTypeMap=new HashMap<>();
    //TODO:We have to populate this from DB
    static {
        processTypeMap.put(1,0);
        processTypeMap.put(2,0);
        processTypeMap.put(3,0);
        processTypeMap.put(4,0);
        processTypeMap.put(5,0);
        processTypeMap.put(6,5);
        processTypeMap.put(7,5);
        processTypeMap.put(8,5);
        processTypeMap.put(9,2);
        processTypeMap.put(10,2);
        processTypeMap.put(11,2);
        processTypeMap.put(12,1);
        processTypeMap.put(13,4);
        processTypeMap.put(14,18);
        processTypeMap.put(15,0);
        processTypeMap.put(16,19);
        processTypeMap.put(17,3);
        processTypeMap.put(18,0);
        processTypeMap.put(19,0);
        processTypeMap.put(20,0);
        processTypeMap.put(21,20);
        processTypeMap.put(22,2);
        processTypeMap.put(23,1);
        processTypeMap.put(24,2);
        processTypeMap.put(25,2);
        processTypeMap.put(26,0);
        processTypeMap.put(27,26);
        processTypeMap.put(28,0);
        processTypeMap.put(29,28);
    }
    private void processTypeValidator(Object object) {
        Process process = (Process) object;
        LOGGER.info("Attempting to insert process " + process.getProcessName());
        Integer processTypeId = process.getProcessType().getProcessTypeId();
        LOGGER.info("processType id of the process is "+processTypeId);
        Process parentProcess = process.getProcess();
        LOGGER.info("parent process is "+process.getProcess());
        if (process.getProcess()!=null)
        LOGGER.info("parent processType id of the process is "+parentProcess.getProcessType().getProcessTypeId());
        if (parentProcess == null) {
            if (processTypeMap.get(processTypeId)!=0) {
                LOGGER.error("wrong process type");
                throw new MetadataException(processTypeId + " process type is not applicable for parent processes but " + process.getProcessId() + " seems to be parent process id");
            }
        } else {
            Integer parentProcessTypeId = parentProcess.getProcessType().getProcessTypeId();
            if (processTypeMap.get(processTypeId)==0) {
                LOGGER.error("wrong process type");
                throw new MetadataException(processTypeId + " process type is not applicable for sub processes. " + process.getProcessId() + " seems to be a subprocess with parent " + parentProcess.getProcessId());
            }else if(processTypeMap.get(processTypeId)!=parentProcessTypeId)
            {
                LOGGER.error("wrong process type");
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
        System.out.println("UUID is "+idOne);
        if (event.getEntity() instanceof Process) {
            if (((Process) event.getEntity()).getProcessCode()==null)
            ((Process) event.getEntity()).setProcessCode(idOne.toString());

            processTypeValidator(event.getEntity());
        }
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        //TODO: Update edit_ts property if its Process and Properties
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        //TODO: Update edit_ts property if its Process and Properties
    }
}


