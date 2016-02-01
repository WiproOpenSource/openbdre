package com.wipro.ats.bdre;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.LineageNodeDAO;
import com.wipro.ats.bdre.md.dao.LineageQueryDAO;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by AshutoshRai on 1/21/16.
 */
public class GetLineageNodeByColName {
    public GetLineageNodeByColName() {
        /* Hibernate Auto-Wire */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetLineageNodeByColName.class);

    /*private static final String[][] PARAMS_STRUCTURE = {
            {"col", "column-name", " Name of column to get Node id for"},
    };*/


    @Autowired
    private LineageNodeDAO lineageNodeDAO;

    public List<LineageNode> execute(String col) {

        try {
            //CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            //String col = commandLine.getOptionValue("column-name");
            LOGGER.debug("Column name is " + col);
            List<LineageNode> lineageNodeList = lineageNodeDAO.getColNodeId(col);

            return lineageNodeList;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

    public LineageNode getTableDotFromTableName (String tableName) {
        LOGGER.debug("Table name is: " + tableName);
        LineageNode lineageNode = lineageNodeDAO.getTableNode(tableName);
        return lineageNode;
    }

    //returns the container node
    public String getTableDotFromNodeId (LineageNode lineageNode) {
        return lineageNodeDAO.getContainerDot(lineageNode.getNodeId());
    }
}
