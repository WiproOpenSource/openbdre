package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.ProcessType;
import com.wipro.ats.bdre.md.pm.beans.DataList;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;

/**
 * Created by cloudera on 6/2/16.
 */
public class MetadataActions {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void insertAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(ArrayList data : dataList.getData()){
                ProcessType processType1 = new ProcessType();
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId(Integer.parseInt((String) data.get(0)));
                processType.setProcessTypeName((String) data.get(1));
                try {
                    Integer temp = Integer.parseInt( data.get(2).toString());
                    processType.setParentProcessTypeId(temp);
                    LOGGER.info("subptype is "+data.get(0).toString());
                    processType1.insert(processType);
                } catch (NumberFormatException e) {
                    LOGGER.info("parent process");
                    LOGGER.info("pptype is "+data.get(0).toString());
                    if(processType1.get(Integer.parseInt((String) data.get(0))) ==  null) {
                        processType.setParentProcessTypeId(null);
                        processType1.insert(processType);
                    }
                    //processType.setParentProcessTypeId(null);
                }catch (ConstraintViolationException e) {
                    throw new BDREException("Sub process type already exists");
                }}

        }else if("GENERAL_CONFIG".equals(dataList.getTableName())){
            for(ArrayList data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.GeneralConfig generalConfig = new com.wipro.ats.bdre.md.beans.table.GeneralConfig();
                generalConfig.setConfigGroup((String) data.get(0));
                generalConfig.setKey((String) data.get(1));
                generalConfig.setValue((String) data.get(2));
                generalConfig.setDescription((String) data.get(3));
                generalConfig.setRequired(Integer.parseInt((String) data.get(4)));
                generalConfig.setDefaultVal((String) data.get(5));
                generalConfig.setType((String) data.get(6));

                if("1".equals( data.get(7)))
                {
                    generalConfig.setEnabled(true);
                }
                else
                {
                    generalConfig.setEnabled(false);
                }
                GetGeneralConfig getGeneralConfig = new GetGeneralConfig();
                getGeneralConfig.insert(generalConfig);
            }

        }
    }

    public void updateAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(ArrayList data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId((Integer) data.get(0));
                processType.setProcessTypeName((String) data.get(1));
                processType.setParentProcessTypeId((Integer) data.get(2));
                ProcessType processType1 = new ProcessType();
                processType1.update(processType);
            }

        }

    }

    public void deleteAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(ArrayList data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId((Integer) data.get(0));
                processType.setProcessTypeName((String) data.get(1));
                processType.setParentProcessTypeId((Integer) data.get(2));
                ProcessType processType1 = new ProcessType();
                processType1.delete(processType);
            }

        }
    }
}
