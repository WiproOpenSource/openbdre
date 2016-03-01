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
package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.LineageNodeInfo;
import com.wipro.ats.bdre.md.beans.LineageQueryInfo;
import com.wipro.ats.bdre.md.beans.LineageRelationInfo;
import com.wipro.ats.bdre.md.dao.LineageNodeDAO;
import com.wipro.ats.bdre.md.dao.LineageQueryDAO;
import com.wipro.ats.bdre.md.dao.LineageRelationDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by KA294215 on 17-12-2015.
 */
public class Lineage extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(Lineage.class);
    public Lineage() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    @Autowired
    LineageQueryDAO lineageQueryDAO;
    @Autowired
    LineageNodeDAO lineageNodeDAO;
    @Autowired
    LineageRelationDAO lineageRelationDAO;

    public Lineage execute(String[] params) {
        return null;
    }

    public void insertLineageQuery(LineageQueryInfo lineageQueryInfo){
        LineageQuery lineageQuery = new LineageQuery();
        lineageQuery.setQueryId(lineageQueryInfo.getQueryId());
        if (lineageQueryInfo.getQueryString() != null){
            lineageQuery.setQueryString(lineageQueryInfo.getQueryString());
        }
        LineageQueryType lineageQueryType = new LineageQueryType();
        lineageQueryType.setQueryTypeId(lineageQueryInfo.getQueryTypeId());
        lineageQuery.setLineageQueryType(lineageQueryType);
        lineageQuery.setCreateTs(new Date());
        if (lineageQueryInfo.getProcessId() != null) {
            lineageQuery.setProcessId(lineageQueryInfo.getProcessId());
        }
        if (lineageQueryInfo.getInstanceExecId() != null){
            lineageQuery.setInstanceExecId(lineageQueryInfo.getInstanceExecId());
        }
        lineageQueryDAO.insert(lineageQuery);
    }

    public void insertLineageNode(LineageNodeInfo lineageNodeInfo){
        LineageNode lineageNode = new LineageNode();
        lineageNode.setNodeId(lineageNodeInfo.getNodeId());
        LineageNodeType lineageNodeType = new LineageNodeType();
        lineageNodeType.setNodeTypeId(lineageNodeInfo.getNodeTypeId());
        lineageNode.setLineageNodeType(lineageNodeType);
        if (lineageNodeInfo.getContainerNodeId() != null){
            LineageNode lineageContainerNode = new LineageNode();
            lineageContainerNode.setNodeId(lineageNodeInfo.getContainerNodeId());
            lineageNode.setLineageNode(lineageContainerNode);
        }
        if (lineageNodeInfo.getNodeOrder() != null){
            lineageNode.setNodeOrder(lineageNodeInfo.getNodeOrder());
            }
        lineageNode.setInsertTs(new Date());
        if (lineageNodeInfo.getUpdateTs() != null){
            lineageNode.setUpdateTs(lineageNodeInfo.getUpdateTs());
        }
        if (lineageNodeInfo.getDotString() != null){
            lineageNode.setDotString(lineageNodeInfo.getDotString());
            }
        if(lineageNodeInfo.getDotLabel() != null){
            lineageNode.setDotString(lineageNodeInfo.getDotString());
        }
        if (lineageNodeInfo.getDisplayName() != null){
            lineageNode.setDisplayName(lineageNodeInfo.getDisplayName());
        }
        lineageNodeDAO.insert(lineageNode);
    }

    public void insertLineageRelation (LineageRelationInfo lineageRelationInfo){
        LineageRelation lineageRelation = new LineageRelation();
        lineageRelation.setRelationId(lineageRelationInfo.getRelationId());

        LineageNode srcLineageNode = new LineageNode();
        srcLineageNode.setNodeId(lineageRelationInfo.getSrcNodeId());
        lineageRelation.setLineageNodeBySrcNodeId(srcLineageNode);

        LineageNode tagertLineageNode = new LineageNode();
        tagertLineageNode.setNodeId(lineageRelationInfo.getTargetNodeId());
        lineageRelation.setLineageNodeByTargetNodeId(tagertLineageNode);

        LineageQuery lineageQuery = new LineageQuery();
        lineageQuery.setQueryId(lineageRelationInfo.getQueryId());
        lineageRelation.setLineageQuery(lineageQuery);
        lineageRelation.setDotString(lineageRelationInfo.getDotString());

        lineageRelationDAO.insert(lineageRelation);

    }
}
