/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI294210 on 11/26/2015.
 */

@Transactional
@Service
public class GetDotStringDAO {
    private static final Logger LOGGER = Logger.getLogger(GetDotStringDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<String> getDotString(Integer processId) {
        List<String> dotStringList = new ArrayList<String>();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            //List of source and target nodeIds
            List<String> srcTargetNodeIdList = new ArrayList<String>();

            //List of sub processes of parent process passed
            Criteria fetchListOfSubProcess = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("process.processId", processId)).setProjection(Projections.property("processId"));
            LOGGER.info("Number of sub processes:" + fetchListOfSubProcess.list().size());
            List<Integer> subProcessList = fetchListOfSubProcess.list();

            // select max(instance_exec_id) from lineage_query group by process_id having process_id in
            if (fetchListOfSubProcess.list().size() != 0) {
                Criteria fetchMaxIeId = session.createCriteria(LineageQuery.class).setProjection(Projections.groupProperty("processId")).add(Restrictions.in("processId", subProcessList)).setProjection(Projections.max("instanceExecId"));
                List<Long> maxIeIdList = fetchMaxIeId.list();
                LOGGER.info("max instance exec Id list size:" + fetchMaxIeId.list().size());
                //select query_id from lineage_query where instance_exec_id in

                if (fetchMaxIeId.list().size() != 0) {
                    Criteria fetchQueryId = session.createCriteria(LineageQuery.class).add(Restrictions.in("instanceExecId", maxIeIdList)).setProjection(Projections.property("queryId"));
                    LOGGER.info("Number of query ids fetched:" + fetchQueryId.list().size());
                    List<String> queryIdList = fetchQueryId.list();

                    if (fetchQueryId.list().size() != 0) {
                        //select src_node_id from lineage_relation where query_id in
                        Criteria fetchSrcNodeId = session.createCriteria(LineageRelation.class).add(Restrictions.in("lineageQuery.queryId", queryIdList)).setProjection(Projections.property("lineageNodeBySrcNodeId.nodeId"));
                        LOGGER.info("Source node id list size:" + fetchSrcNodeId.list().size());
                        List<String> srcNodeIdList = fetchSrcNodeId.list();

                        //select target_node_id from lineage_relation where query_id in
                        Criteria fetchTargetNodeId = session.createCriteria(LineageRelation.class).add(Restrictions.in("lineageQuery.queryId", queryIdList)).setProjection(Projections.property("lineageNodeByTargetNodeId.nodeId"));
                        LOGGER.info("Target node id list size:" + fetchTargetNodeId.list().size());
                        List<String> targetNodeIdList = fetchTargetNodeId.list();

                        //adding to List of source and target nodeIds
                        for (String srcNodeId : srcNodeIdList) {
                            srcTargetNodeIdList.add(srcNodeId);
                        }
                        for (String targetNodeId : targetNodeIdList) {
                            srcTargetNodeIdList.add(targetNodeId);
                        }


                        if (srcTargetNodeIdList.size() != 0) {
                            //select dot_string as dotString from lineage_node where node_type_id in (3,4,5,6) and node_id in
                            Criteria fetchSpecificNodeTypeIdDotString = session.createCriteria(LineageNode.class).add(Restrictions.in("lineageNodeType.nodeTypeId", new Integer[]{3, 4, 5, 6})).add(Restrictions.in("nodeId", srcTargetNodeIdList)).setProjection(Projections.property("dotString"));
                            List<String> specificNodeTypeIdDotString = fetchSpecificNodeTypeIdDotString.list();
                            //adding the list to final returning dot string list
                            for (String dotString : specificNodeTypeIdDotString) {
                                dotStringList.add(dotString);
                            }

                            //(select container_node_id from lineage_node where node_type_id =2 and node_id in
                            Criteria fetchContainerNodeId = session.createCriteria(LineageNode.class).add(Restrictions.eq("lineageNodeType.nodeTypeId", 2)).add(Restrictions.in("nodeId", srcTargetNodeIdList)).setProjection(Projections.property("lineageNode.nodeId"));
                            List<String> containerNodeIdList = fetchContainerNodeId.list();
                            if (fetchContainerNodeId.list().size() != 0) {

                                //select dot_string as dotString from lineage_node where node_id in (select container_node_id from lineage_node where node_type_id =2 and node_id in
                                Criteria fetchContainerNodeIdDotString = session.createCriteria(LineageNode.class).add(Restrictions.in("nodeId", containerNodeIdList)).setProjection(Projections.property("dotString"));
                                List<String> containerNodeDotStringList = fetchContainerNodeIdDotString.list();

                                //adding the list to final returning dot string list
                                for (String dotString : containerNodeDotStringList) {
                                    dotStringList.add(dotString);
                                }
                            }
                        }

                        //select dot_string as dotString from lineage_relation where query_id in
                        Criteria fetchLineageRelationDotString = session.createCriteria(LineageRelation.class).add(Restrictions.in("lineageQuery.queryId", queryIdList)).setProjection(Projections.property("dotString"));
                        List<String> lineageRelationDotStringList = fetchLineageRelationDotString.list();
                        //adding the list to final returning dot string list
                        for (String dotString : lineageRelationDotStringList) {
                            dotStringList.add(dotString);
                        }

                    }
                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
            throw new MetadataException(e);
        } finally {
            session.close();
        }
        return dotStringList;
    }
}
