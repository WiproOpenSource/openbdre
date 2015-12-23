package com.wipro.ats.bdre.md.triggers;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by SH324337 on 12/18/2015.
 */

public class ProcessValidateInsert {

    private static final Logger LOGGER = Logger.getLogger(ProcessValidateInsert.class);

    public boolean ProcessTypeValidator(Process process, Process parentProcess)
    {

        try {
            Integer processTypeId=process.getProcessType().getProcessTypeId();
            Integer parentProcessTypeId = 0;
            if (parentProcess != null) {
                LOGGER.info("parent Process Type ID is " + parentProcess.getProcessType().getProcessTypeId());
                parentProcessTypeId = parentProcess.getProcessType().getProcessTypeId();
            }
            if ((processTypeId < 6 || processTypeId == 15 || processTypeId == 18 || processTypeId == 19 || processTypeId ==20) && parentProcess != null) {
                throw new MetadataException("1,2,3,4,5 process types are applicable for parent processes only");
            }  else if ((processTypeId > 5 && processTypeId < 9) && parentProcessTypeId != 5) {
                throw new MetadataException("6,7,8 process types should have etl process type(5) parent");
            } else if (( processTypeId > 8 && processTypeId < 12) && parentProcessTypeId != 2) {
                throw new MetadataException("9,10,11 process types should have semantic process type(2) parent");
            } else if ((processTypeId == 12) && parentProcessTypeId != 1) {
                throw new MetadataException("12 process types should have semantic process type(1) parent");
            } else if ((processTypeId == 13) && parentProcessTypeId != 4) {
                throw new MetadataException("13 process types should have semantic process type(4) parent");
            }else if ((processTypeId == 14) && parentProcessTypeId != 18) {
                throw new MetadataException("14 process types should have semantic process type(18) parent");
            }else if ((processTypeId == 16) && parentProcessTypeId != 19) {
                throw new MetadataException("16 process types should have semantic process type(19) parent");
            }else if ((processTypeId == 17) && parentProcessTypeId != 3) {
                throw new MetadataException("17 process types should have semantic process type(3) parent");
            }else if ((processTypeId == 21) && parentProcessTypeId != 20) {
                throw new MetadataException("21 process types should have semantic process type(20) parent");
            }else if ((processTypeId == 22) && parentProcessTypeId != 2) {
                throw new MetadataException("22 process types should have semantic process type(2) parent");
            }else if ((processTypeId == 23) && parentProcessTypeId != 1) {
                throw new MetadataException("23 process types should have semantic process type(1) parent");
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


