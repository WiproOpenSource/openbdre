package com.wipro.ats.bdre;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.LineageNodeDAO;
import com.wipro.ats.bdre.md.dao.LineageRelationDAO;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by AshutoshRai on 1/22/16.
 */
public class GetNodeIdForLineageRelation {
    public GetNodeIdForLineageRelation() {
        /* Hibernate Auto-Wire */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetNodeIdForLineageRelation.class);

    @Autowired
    private LineageRelationDAO lineageRelationDAO;

    public List<LineageRelation> execute(String nodeid) {

        try {
            List<LineageRelation> lineageRelationList = lineageRelationDAO.getNodeIdForNode(nodeid);
            return lineageRelationList;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
