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

package com.wipro.ats.bdre;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.LineageRelationDAO;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
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
            return lineageRelationDAO.getNodeIdForNode(nodeid);
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

    public List<LineageRelation> getWhenTargetNode(String nodeid) {

        try {
            return lineageRelationDAO.getNodeIdForNodeWhenTarget(nodeid);
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
