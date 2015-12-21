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

package com.wipro.ats.bdre.security;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import static org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

/**
 * Created by AR288503 on 9/7/2015.
 */

public class SessionCheckPerRequestFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(SessionCheckPerRequestFilter.class);
    private String userServiceURL;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (userServiceURL == null) throw new ServletException("userServiceURL must be passed.");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.info("IN SessionCheckPerRequestFilter");
        Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
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
            int portNumber = servletRequest.getLocalPort();
            String completeServiceURL = scheme + "://" + serverName + ":" + portNumber + userServiceURL;
            LOGGER.debug("completeServiceURL=" + completeServiceURL);
            String token = cookie.getValue();
            RestTemplate restTemplate = new RestTemplate();
            //Handle HTTPS
            if (servletRequest.isSecure()) {
                try {
                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                    DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();
                    TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] certificate, String authType) {
                            return true;
                        }
                    };
                    SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy, ALLOW_ALL_HOSTNAME_VERIFIER);
                    httpClient.getConnectionManager().getSchemeRegistry().unregister("https");
                    httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", portNumber, sf));
                    restTemplate = new RestTemplate(requestFactory);
                } catch (NoSuchAlgorithmException e) {
                    LOGGER.error(e);
                } catch (KeyManagementException e) {
                    LOGGER.error(e);
                } catch (KeyStoreException e) {
                    LOGGER.error(e);
                } catch (UnrecoverableKeyException e) {
                    LOGGER.error(e);
                }
            }

            AuthResult authResult = null;
            try {
                authResult = restTemplate.getForObject(completeServiceURL + token, AuthResult.class);
            } catch (Exception e) {
                LOGGER.error(e);
                throw new RuntimeException("Error Occurred. Service URL:" + completeServiceURL + token + "; servletRequest.isSecure()=" + servletRequest.isSecure(), e);
            }

            if (authResult != null && authResult.isAuthenticated()) {
                principal = authResult.getUser().getUsername();
                LOGGER.info("USER appears to be still authenticated for token " + token + "; principal=" + principal);
                AuthResult authResultInSession = (AuthResult) ((HttpServletRequest) servletRequest).getSession().getAttribute("CURRENT_LOGGED_IN_AUTH");
                if (authResultInSession != null && !authResultInSession.getAuthToken().equals(authResult.getAuthToken())) {
                    LOGGER.error("Token in session(" + authResultInSession.getAuthToken() + ") and token from cookie(" + authResult.getAuthToken() + ") don't match. Purging current session.");
                    ((HttpServletRequest) servletRequest).getSession().invalidate();
                }
            } else {
                LOGGER.error(" authResult not found for not authenticated for token:" + token);
                ((HttpServletRequest) servletRequest).getSession().invalidate();
            }
        } else {
            LOGGER.error("bdre-auth-token cookie not found in request.");
            ((HttpServletRequest) servletRequest).getSession().invalidate();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public void setUserServiceURL(String userServiceURL) {
        this.userServiceURL = userServiceURL;
    }
}