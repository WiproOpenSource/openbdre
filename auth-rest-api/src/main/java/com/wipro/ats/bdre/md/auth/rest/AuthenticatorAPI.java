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

package com.wipro.ats.bdre.md.auth.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.auth.RestWrapper;
import com.wipro.ats.bdre.md.auth.SessionHolder;
import com.wipro.ats.bdre.md.beans.table.UserRoles;
import com.wipro.ats.bdre.md.beans.table.Users;
import com.wipro.ats.bdre.security.AuthResult;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by Arijit
 */
@Controller
@RequestMapping("/security")


public class AuthenticatorAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(AuthenticatorAPI.class);

    @RequestMapping(value = "/centrallogin", method = RequestMethod.GET)
    public String intermediateLogin(@RequestHeader(value = "referer", defaultValue = "/../mdui/pages/content.page") String referer, HttpServletResponse response, HttpServletRequest request) {

        LOGGER.debug("Refrer=" + referer);
        return "redirect:/bdre/security/bdrelogin?url=" + referer;
    }

    @RequestMapping(value = "/bdrelogin", method = RequestMethod.GET)
    public String bdreLogin(HttpSession session,
                            HttpServletResponse response,
                            @RequestParam(value = "url", defaultValue = "/../mdui/pages/content.page") String url) {
        AuthResult authResult = null;

        try {
            authResult = (AuthResult) session.getAttribute("CURRENT_SESSION_AUTH_RESULT");
            if (authResult == null) {
                Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
                authResult = new AuthResult();
                authResult.setAuthenticated(true);
                Users users = new Users();
                users.setEnabled((short) 1);
                users.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                authResult.setUser(users);
                //obtain all user roles
                List<UserRoles> rolesList = new ArrayList<UserRoles>();
                for (GrantedAuthority grantedAuthority : grantedAuthorities) {
                    UserRoles userRoles = new UserRoles();
                    userRoles.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                    userRoles.setRole(grantedAuthority.getAuthority());
                    rolesList.add(userRoles);
                }
                authResult.setUserRoles(rolesList);
                //Add a new token
                UUID uuid = UUID.randomUUID();
                authResult.setAuthToken(uuid.toString());

                //Set start ts
                authResult.setStartTime(new Timestamp(session.getCreationTime()));
                //If there is an authenticated session, reuse the same auth result
                session.setAttribute("CURRENT_SESSION_AUTH_RESULT", authResult);
                SessionHolder.addAuthResult(authResult);
            }
            //Set expiration
            //set session timeout
            session.setMaxInactiveInterval(SessionHolder.MAX_INACTIVE_SESSION_DURATION);
            Timestamp expTs = new Timestamp(0);
            expTs.setTime(session.getCreationTime() + session.getMaxInactiveInterval() * 1000);
            authResult.setExpiration(expTs);

            //Add the session object as well
            authResult.setHttpSession(session);

        } catch (Exception e) {
            LOGGER.debug("exception caught inside /bdrelogin");
            authResult = new AuthResult();
            authResult.setAuthenticated(false);
            authResult.setErrorMsg(e.getMessage());
        }
        Cookie authCookie = new Cookie("bdre-auth-token", authResult.getAuthToken());
        authCookie.setPath("/");
        response.addCookie(authCookie);
        LOGGER.info("referer='" + url + "'");
        //Redirect to the caller
        if (url != null && !url.isEmpty()) {
            LOGGER.debug("url is not null or empty, inside /bdrelogin url = " + url);
            return "redirect:" + url;
        }
        return null;

    }

    @RequestMapping(value = "/validatetoken/{authtoken}", method = RequestMethod.GET)
    public
    @ResponseBody
    AuthResult validateToken(@PathVariable("authtoken") String token) {
        AuthResult authResult = null;
        try {

            if (token != null) {
                //check for valid uuid
                UUID.fromString(token);
                authResult = (AuthResult) SessionHolder.getAuthResult(token);
                if (authResult == null) throw new LoginException("Authtoken not found");
                //reset session timeout
                HttpSession session = authResult.getHttpSession();
                SessionHolder.resetSessionTimeout(session);
                authResult.setExpiration(new Timestamp(session.getCreationTime() + session.getMaxInactiveInterval() * 1000));
                return authResult;

            } else {
                throw new LoginException("null token");
            }

        } catch (Exception e) {
            LOGGER.error(e);
            authResult = new AuthResult();
            authResult.setAuthenticated(false);
            authResult.setErrorMsg(e.getMessage());
        }
        return authResult;
    }

    @RequestMapping(value = "/admin/logout/{authtoken}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper logoutByToken(@PathVariable("authtoken") String token, HttpSession session) throws LoginException {
        RestWrapper restWrapper = null;
        if (token != null) {
            try {
                //check for valid uuid
                UUID.fromString(token);
            } catch (Exception e) {
                //bad uuid
                restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            }
            AuthResult authResult = SessionHolder.getAuthResult(token);
            if (authResult == null) {
                restWrapper = new RestWrapper(null, RestWrapper.OK);
                return restWrapper;
            }

            HttpSession otherUserSession = authResult.getHttpSession();
            otherUserSession.invalidate();

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            return restWrapper;
        } else {
            restWrapper = new RestWrapper("No token supplied", RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = "/{page}.page", method = RequestMethod.GET)
    public String gotoPage(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping(value = "/admin/sessions", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper list(HttpSession session) {
        //reset session timeout
        SessionHolder.resetSessionTimeout(session);
        RestWrapper restWrapper = new RestWrapper(SessionHolder.getAllAuthResults(), RestWrapper.OK);
        return restWrapper;
    }

    @RequestMapping(value = "/login.page", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }
        model.setViewName("login");
        return model;

    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}

