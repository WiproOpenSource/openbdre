package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.api.ProcessType;
import com.wipro.ats.bdre.md.pm.beans.Data;
import com.wipro.ats.bdre.md.pm.beans.DataList;

import java.util.List;

/**
 * Created by cloudera on 6/2/16.
 */
public class MetadataActions {
    public void insertAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(Data data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId((Integer) data.getValues().get(0));
                processType.setProcessTypeName((String) data.getValues().get(1));
                processType.setParentProcessTypeId((Integer) data.getValues().get(2));
                ProcessType processType1 = new ProcessType();
                processType1.insert(processType);
            }

        }
    }

    public void updateAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(Data data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId((Integer) data.getValues().get(0));
                processType.setProcessTypeName((String) data.getValues().get(1));
                processType.setParentProcessTypeId((Integer) data.getValues().get(2));
                ProcessType processType1 = new ProcessType();
                processType1.update(processType);
            }

        }

    }

    public void deleteAction(DataList dataList){
        if("PROCESS_TYPE".equals(dataList.getTableName())){
            for(Data data : dataList.getData()){
                com.wipro.ats.bdre.md.beans.table.ProcessType processType = new com.wipro.ats.bdre.md.beans.table.ProcessType();
                processType.setProcessTypeId((Integer) data.getValues().get(0));
                processType.setProcessTypeName((String) data.getValues().get(1));
                processType.setParentProcessTypeId((Integer) data.getValues().get(2));
                ProcessType processType1 = new ProcessType();
                processType1.delete(processType);
            }

        }
    }
}
