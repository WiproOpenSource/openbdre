package com.wipro.ats.bdre.md.triggers;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by SH324337 on 12/18/2015.
 */

public class ProcessValidateInsert {

    private static final Logger LOGGER = Logger.getLogger(ProcessValidateInsert.class);
    public boolean ProcessTypeValidator(Process process)
    {

        try {
            Integer processTypeId=process.getProcessType().getProcessTypeId();
            LOGGER.info("reached 1");
            Integer parentProcessId=null;
            if(process.getProcess()!=null) {
                parentProcessId = process.getProcess().getProcessId();
            }
            Integer processId = process.getProcessId();
            LOGGER.info("reached 2");
            Integer processCheck = null;

            if ((processId > 0 && processTypeId < 6) && parentProcessId != null) {
                throw new MetadataException("1,2,3,4,5 process types are applicable for parent processes only");
            } else if ((processTypeId > 5 && processTypeId < 13) && parentProcessId != null) {
                throw new MetadataException("6,7,8,9,10,11,12 process types are applicable for sub processes only");
            } else if ((processTypeId > 5 && processTypeId < 9) && processCheck != 5) {
                throw new MetadataException("6,7,8 process types should have etl process type(5) parent");
            } else if ((processTypeId > 8 && processTypeId < 12) && processCheck != 2) {
                throw new MetadataException("9,10,11 process types should have semantic process type(2) parent");
            } else if ((processTypeId == 12) && processCheck != 1) {
                throw new MetadataException("12 process types should have semantic process type(2) parent");
            }
            return true;

        }
        catch (MetadataException e)
        {
            LOGGER.info("Error occured "+e);
            return false;
        }

    }



}


