package com.wipro.ats.bdre.md.api;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Shubham on 03/05/18.
 */

public class HiveSchemaEvolution extends MetadataAPIBase {
    @Autowired
    PropertiesDAO propertiesDAO;
    @Autowired
    ProcessDAO processDAO;
    private static final org.apache.log4j.Logger LOGGER= Logger.getLogger(HiveSchemaEvolution.class);
    public HiveSchemaEvolution() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    public void updateBaseTableProperties(Integer processId){
        System.out.println("inside update base table properties");
        GetProperties getProperties = new GetProperties();
        Properties appendedColumnProperties = getProperties.getProperties(processId.toString(), "appended-columns");
        Enumeration appendedColumnList = appendedColumnProperties.propertyNames();
        Properties deletedColumnProperties = getProperties.getProperties(processId.toString(), "deleted-columns");
        Enumeration deletedColumnList = deletedColumnProperties.propertyNames();
        List<String> deletedColumns= Collections.list(deletedColumnList);
        List<String> appendedColumns=Collections.list(appendedColumnList);
        System.out.println("no. of columns appended by user is " + appendedColumns.size());
        for(String column:appendedColumns){
            System.out.println("column being appended is " + column);
            PropertiesId propertiesId=new PropertiesId();
            propertiesId.setProcessId(processId);
            propertiesId.setPropKey(column);
            com.wipro.ats.bdre.md.dao.jpa.Properties properties=new com.wipro.ats.bdre.md.dao.jpa.Properties();
            properties.setId(propertiesId);
            properties.setConfigGroup("base-columns-and-types");
            properties.setDescription("Base Table columns and data types");
            properties.setPropValue((String) appendedColumnProperties.getProperty(column));
            properties.setProcess(processDAO.get(processId));
            propertiesDAO.update(properties);
        }
        for(String column:deletedColumns){
            System.out.println("column being deleted is " + column);
            PropertiesId propertiesId=new PropertiesId();
            propertiesId.setProcessId(processId);
            propertiesId.setPropKey(column);
            com.wipro.ats.bdre.md.dao.jpa.Properties properties=new com.wipro.ats.bdre.md.dao.jpa.Properties();
            properties.setId(propertiesId);
            propertiesDAO.delete(propertiesId);
        }
    }
    @Override
    public Object execute(String[] params) {
        return null;
    }
}
