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

package com.wipro.ats.bdre.jaas.login;

import com.wipro.ats.bdre.md.beans.table.UserRoles;
import com.wipro.ats.bdre.md.beans.table.Users;
import com.wipro.ats.bdre.security.UserRoleFetcher;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import com.wipro.ats.bdre.md.api.*;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author arijit
 */
public class JAASLoginModule implements LoginModule {

    private static final Logger LOGGER = Logger.getLogger(JAASLoginModule.class);

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    // configurable option

    static String env;
    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    //user credentials
    private String username = null;
    private char[] password = null;

    //user principle
    private JAASUserPrincipal userPrincipal = null;
    private JAASPasswordPrincipal passwordPrincipal = null;

    public JAASLoginModule() {
        super();
        LOGGER.debug("Login module constructor call");

    }


    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        env = (String) options.get("env");

        LOGGER.debug("Login module constructor call. env=" + env);
    }

    @Override
    public boolean login() throws LoginException {

        if (callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available " +
                    "to garner authentication information from the user");
        }
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password: ", false);

        try {

            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            //password = DigestUtils.sha1Hex(new String(((PasswordCallback) callbacks[1]).getPassword())).toCharArray();
            password = ((PasswordCallback) callbacks[1]).getPassword();


            if (username == null || password == null) {
                LOGGER.error("Callback handler does not return login data properly");
                throw new LoginException("Callback handler does not return login data properly user=" + username);
            }

            if (isValidUser()) { //validate user.
                succeeded = true;
                return true;
            }

        } catch (IOException e) {
            LOGGER.error("Callback handler does not return login data properly. " + e.getMessage());
            LOGGER.info(e);
        } catch (UnsupportedCallbackException e) {
            LOGGER.error("Callback handler does not return login data properly. " + e.getMessage());
            LOGGER.info(e);
        }

        return false;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!succeeded) {
            return false;
        } else {
            userPrincipal = new JAASUserPrincipal(username);
            if (!subject.getPrincipals().contains(userPrincipal)) {
                subject.getPrincipals().add(userPrincipal);
                LOGGER.debug("User principal added:" + userPrincipal);
            }
            passwordPrincipal = new JAASPasswordPrincipal(new String(password));
            if (!subject.getPrincipals().contains(passwordPrincipal)) {
                subject.getPrincipals().add(passwordPrincipal);
                LOGGER.debug("Password principal added: " + passwordPrincipal);
            }

            //populate subject with roles.
            List<String> roles = getRoles();
            for (String role : roles) {
                JAASRolePrincipal rolePrincipal = new JAASRolePrincipal(role);
                if (!subject.getPrincipals().contains(rolePrincipal)) {
                    subject.getPrincipals().add(rolePrincipal);
                    LOGGER.debug("Role principal added: " + rolePrincipal);
                }
            }

            commitSucceeded = true;

            LOGGER.info("Login subject were successfully populated with principals and roles");

            return true;
        }
    }

    @Override
    public boolean abort() throws LoginException {
        if (!succeeded) {
            return false;
        } else if (succeeded && !commitSucceeded) {
            succeeded = false;
            username = null;
            if (password != null) {
                password = null;
            }
            userPrincipal = null;
        } else {
            logout();
        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        succeeded = false;
        succeeded = commitSucceeded;
        username = null;
        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = ' ';
                password = null;
            }
        }
        userPrincipal = null;
        return true;
    }

    private boolean isValidUser() throws LoginException {
        LOGGER.debug("Checking user validity");
        LOGGER.info("checking if login is possible");
        if (password == null || new String(password).trim().isEmpty()) {
            LOGGER.error("Cannot authenticate because of empty password");
            return false;

        }
        UserRoleFetcher userRoleFetcher = new UserRoleFetcher();
        Users user = userRoleFetcher.getUser(username);
        try {
            AccountLockout accountLockout = new AccountLockout();
            List<com.wipro.ats.bdre.md.dao.jpa.AccountLockout> accountLockoutList = accountLockout.get(username);

            LOGGER.info("list obtained has size of " + accountLockoutList.size());
            if (user != null) {
                LOGGER.info("valid username entered");
                if(accountLockoutList.isEmpty()){
                    LOGGER.info(username + " not present in account lockout table");
                    String passwordStr = new String(password);
                    if (user.getPassword().equals(passwordStr)) {
                        LOGGER.info("Authentication success for " + username);
                        return true;
                    }
                    else{
                        //create an entry for the username with 1 attempt and status as not locked
                        LOGGER.info("wrong password entered for "+ username);
                        accountLockout.insert(username);
                    }
                }
                else{
                    LOGGER.info(username + " already present in account lockout table");
                    com.wipro.ats.bdre.md.dao.jpa.AccountLockout a=accountLockoutList.get(0);
                    String status=a.getLockStatus();
                    if(status.equalsIgnoreCase("Locked")){
                        //increase no. of attempts by 1
                        LOGGER.info(username + " is already locked... cannot login");
                        accountLockout.update(username);
                    }
                    else{
                        //check if password is correct.
                        // if it is correct, delete the entry from the table for the user
                        String passwordStr = new String(password);
                        if (user.getPassword().equals(passwordStr)) {
                            LOGGER.info("Authentication success for " + username);
                            accountLockout.delete(username);
                            return true;
                        }
                        // if it is not correct, increase the no. of attempts by 1. check if attemts are equal to k.
                        // if attempts have become k, change status to locked
                        else {
                            LOGGER.info("wrong pasword entered");
                            accountLockout.update(username);
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        LOGGER.error("Authentication failed for " + username);
        return false;
    }

    /**
     * Returns list of roles assigned to authenticated user.
     *
     * @return
     */
    private List<String> getRoles() {


        List<String> roleList = new ArrayList<String>();
        UserRoleFetcher userRoleFetcher = new UserRoleFetcher();
        for (UserRoles role : userRoleFetcher.getRoles(username)) {
            roleList.add(role.getRole());
        }
        return roleList;
    }


}