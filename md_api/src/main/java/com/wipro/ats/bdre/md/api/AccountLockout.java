package com.wipro.ats.bdre.md.api;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.AccountLockoutDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.*;

/**
 * Created by Shubham on 03/05/18.
 */
public class AccountLockout extends MetadataAPIBase{
    @Autowired
    AccountLockoutDAO accountLockoutDAO;
    public AccountLockout() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    private static final Logger LOGGER=Logger.getLogger(AccountLockout.class);
    public void insert(String userName){
        try {
            LOGGER.info("inside md-api insert method , inserting " + userName + " in the account lockout table");
            accountLockoutDAO.insert(userName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void delete(String userName){
        try {
            LOGGER.info("inside md-api delete method , deleting " + userName + " in the account lockout table");
            com.wipro.ats.bdre.md.dao.jpa.AccountLockout accountLockout = new com.wipro.ats.bdre.md.dao.jpa.AccountLockout();
            accountLockout.setUsername(userName);
            accountLockoutDAO.delete(accountLockout);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void update(String userName){
        try {
            LOGGER.info("inside md-api update method , updating " + userName + " in the account lockout table");
            com.wipro.ats.bdre.md.dao.jpa.AccountLockout accountLockout=new com.wipro.ats.bdre.md.dao.jpa.AccountLockout();
            accountLockout.setUsername(userName);
            int attempts=0;
            com.wipro.ats.bdre.md.dao.jpa.AccountLockout accountLockout1=accountLockoutDAO.getAccount(userName);
            attempts=accountLockout1.getAttempts();
            int newAttempts=attempts+1;
            if(newAttempts>=3)
                accountLockout.setLockStatus("Locked");
            else
                accountLockout.setLockStatus("Not Locked");
            accountLockout.setAttempts(newAttempts);
            accountLockoutDAO.update(accountLockout);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<com.wipro.ats.bdre.md.dao.jpa.AccountLockout> get(String userName){
        LOGGER.info("inside api get method");
        LOGGER.info("username is " + userName);
        List<com.wipro.ats.bdre.md.dao.jpa.AccountLockout> accountLockoutList=new ArrayList<>();
        accountLockoutList=accountLockoutDAO.get(userName);
        return accountLockoutList;
    }
    @Override
    public Object execute(String[] params) {
        return null;
    }
}
