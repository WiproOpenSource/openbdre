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

package com.wipro.ats.bdre.security;

import org.apache.log4j.Logger;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class AuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);
    private String credentialsRequestHeader;
    private String userServiceURL;

    /**
     * Read and returns the header named by <tt>principalRequestHeader</tt> from the request.
     *
     * @throws PreAuthenticatedCredentialsNotFoundException if the header is missing and <tt>exceptionIfHeaderMissing</tt>
     *                                                      is set to <tt>true</tt>.
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        String principal = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("bdre-auth-token")) {
                    cookie = c;
                    break;
                }
            }
        }
        if (cookie != null) {
            String scheme = "http";
            String serverName = "localhost";
            int portNumber = request.getLocalPort();
            String completeServiceURL = scheme + "://" + serverName + ":" + portNumber + userServiceURL;
            LOGGER.debug("completeServiceURL=" + completeServiceURL);
            String token = cookie.getValue();
            RestTemplate restTemplate = new RestTemplate();
            AuthResult authResult = restTemplate.getForObject(completeServiceURL + token, AuthResult.class);
            if (authResult != null && authResult.isAuthenticated()) {
                principal = authResult.getUser().getUsername();
                LOGGER.info("USER is authenticated for token " + token + "; principal=" + principal);
                //store the authResult in session for comparing it later in SessionCheck Filter
                request.getSession().setAttribute("CURRENT_LOGGED_IN_AUTH", authResult);
            } else {
                LOGGER.error(" authResult not found for not authenticated for token:" + token);
            }
        } else {
            LOGGER.error("bdre-auth-token cookie not found in request.");
        }


        return principal;
    }

    /**
     * Credentials aren't usually applicable, but if a <tt>credentialsRequestHeader</tt> is set, this
     * will be read and used as the credentials value. Otherwise a dummy value will be used.
     */
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        if (credentialsRequestHeader != null) {
            String credentials = request.getHeader(credentialsRequestHeader);

            return credentials;
        }

        return "N/A";
    }


    public void setCredentialsRequestHeader(String credentialsRequestHeader) {
        Assert.hasText(credentialsRequestHeader, "credentialsRequestHeader must not be empty or null");
        this.credentialsRequestHeader = credentialsRequestHeader;
    }

    public String getUserServiceURL() {
        return userServiceURL;
    }

    public void setUserServiceURL(String userServiceURL) {
        this.userServiceURL = userServiceURL;
    }


}