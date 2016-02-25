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

package com.wipro.ats.bdre.md.dao;


import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class LineageNodeDAO {

    private static final Logger LOGGER = Logger.getLogger(LineageNodeDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<LineageNode> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageNode.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<LineageNode> lineageNodes = criteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageNodes;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageNode.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public LineageNode get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        LineageNode lineageNode = (LineageNode) session.get(LineageNode.class, id);
        session.getTransaction().commit();
        session.close();
        return lineageNode;
    }

    //get all table node(s) by the name passed
    public List<LineageNode> getTableNode(String tableName) {
        List<LineageNode> lineageNodeList;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria getLastElementCriteria = session.createCriteria(LineageNode.class).add(Restrictions.eq("displayName", tableName));
        lineageNodeList = getLastElementCriteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageNodeList;
    }

    //get node by nodeid
    public String getContainerDot(String nodeid) {
        String dotString;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria getLastElementCriteria = session.createCriteria(LineageNode.class).add(Restrictions.eq("nodeId", nodeid));
        LineageNode lineageNode = (LineageNode) getLastElementCriteria.list().get(0);
        dotString = lineageNode.getLineageNode().getDotString();
        session.getTransaction().commit();
        session.close();
        return dotString;
    }


    public String insert(LineageNode lineageNode) {
        Session session = sessionFactory.openSession();
        String id = null;
        try {
            session.beginTransaction();
            id = (String) session.save(lineageNode);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }

    public LineageNode getColNodeId(String colName, LineageNode tableNode) throws Exception {
        LineageNode lineageNode;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria getLastElementCriteria = session.createCriteria(LineageNode.class).add(Restrictions.eq("lineageNode", tableNode)).add(Restrictions.eq("displayName", colName));
        lineageNode = (LineageNode) getLastElementCriteria.list().get(0);
        LOGGER.info("----------------- LN value when getting the col: "+ lineageNode.getDisplayName() + lineageNode.getNodeId());
        session.getTransaction().commit();
        session.close();
        return lineageNode;
    }

    public void update(LineageNode lineageNode) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(lineageNode);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


    public void delete(String id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            LineageNode lineageNode = (LineageNode) session.get(LineageNode.class, id);
            session.delete(lineageNode);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }


}
