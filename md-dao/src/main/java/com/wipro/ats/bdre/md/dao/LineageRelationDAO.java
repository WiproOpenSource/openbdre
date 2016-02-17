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
import com.wipro.ats.bdre.md.dao.jpa.LineageQueryType;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PR324290 on 10/28/2015.
 */
@Transactional
@Service
public class LineageRelationDAO {
    private static final Logger LOGGER = Logger.getLogger(LineageRelationDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<LineageRelation> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageRelation.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<LineageRelation> lineageRelations = criteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageRelations;
    }

    //getting those relations where the col is in src table
    public List<LineageRelation> getNodeIdForNode(String nodeid) {
        List<LineageRelation> lineageRelations = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        LineageNode lineageNode = new LineageNode();
        lineageNode.setNodeId(nodeid);
        Criteria criteria = session.createCriteria(LineageRelation.class).add(Restrictions.eq("lineageNodeBySrcNodeId", lineageNode));
        lineageRelations = criteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageRelations;
    }
    //getting those relations where the col is in target table
    public List<LineageRelation> getNodeIdForNodeWhenTarget(String nodeid) {
        List<LineageRelation> lineageRelations = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        LineageNode lineageNode = new LineageNode();
        lineageNode.setNodeId(nodeid);
        Criteria criteria = session.createCriteria(LineageRelation.class).add(Restrictions.eq("lineageNodeByTargetNodeId", lineageNode));
        lineageRelations = criteria.list();
        session.getTransaction().commit();
        session.close();
        return lineageRelations;
    }

    public Integer totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(LineageRelation.class);
        Integer size = criteria.list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }


    public LineageRelation get(String id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        LineageRelation lineageRelation = (LineageRelation) session.get(LineageRelation.class, id);
        session.getTransaction().commit();
        session.close();
        return lineageRelation;
    }


    public String insert(LineageRelation lineageRelation) {
        Session session = sessionFactory.openSession();
        String id = null;
        try {
            session.beginTransaction();
            LOGGER.info("LQ: " + lineageRelation.getLineageQuery().getQueryId());
            id = (String) session.save(lineageRelation);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return id;
    }


    public void update(LineageRelation lineageRelation) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(lineageRelation);
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
            LineageRelation lineageRelation = (LineageRelation) session.get(LineageRelation.class, id);
            session.delete(lineageRelation);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }
}
