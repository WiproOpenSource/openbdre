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
import com.wipro.ats.bdre.md.dao.jpa.*;
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
 * Created by shubham on 04-30-2018.
 */
@Transactional
@Service

public class AccountLockoutDAO {
    @Autowired
    SessionFactory sessionFactory;
    private static final String userColumn="username";
    private static final Logger LOGGER=Logger.getLogger(AccountLockoutDAO.class);
    public List<AccountLockout> get(String userName){
        LOGGER.info("some changes made");
        Session session;
        List<AccountLockout> accountLockoutList=new ArrayList<>();
        try {
            session=sessionFactory.openSession();
            session.beginTransaction();
            LOGGER.info("inside get of AccountLockoutDAO");
            Criteria criteria = session.createCriteria(AccountLockout.class).add(Restrictions.eq(userColumn, userName));
            accountLockoutList = (List<AccountLockout>) criteria.list();
            session.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
            //session.getTransaction().rollback();
        }
        //session.close();
        return accountLockoutList;
    }
    public void insert(String userName){
        Session session=sessionFactory.openSession();

        try {
            session.beginTransaction();
            LOGGER.info("inside insert of AccountLockoutDAO");
            AccountLockout accountLockout=new AccountLockout();
            accountLockout.setAttempts(1);
            accountLockout.setLockStatus("Not Locked");
            accountLockout.setUsername(userName);
            session.save(accountLockout);
            session.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
    }
    public void update(AccountLockout accountLockout){
        Session session=sessionFactory.openSession();

        try {
            session.beginTransaction();
            LOGGER.info("inside update of AccountLockoutDAO");
            session.update(accountLockout);
            session.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
    }

    public void delete(AccountLockout accountLockout){
        Session session=sessionFactory.openSession();

        try {
            session.beginTransaction();
            LOGGER.info("inside delete of AccountLockoutDAO");
            session.delete(accountLockout);
            session.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();

    }
    public AccountLockout getAccount(String userName){
        Session session=sessionFactory.openSession();

        AccountLockout a=new AccountLockout();
        try {
            session.beginTransaction();
            LOGGER.info("inside getAccount of AccountLockoutDAO");
            a=(AccountLockout) session.get(AccountLockout.class,userName);
            session.getTransaction().commit();
        }
        catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return a;
    }
}
