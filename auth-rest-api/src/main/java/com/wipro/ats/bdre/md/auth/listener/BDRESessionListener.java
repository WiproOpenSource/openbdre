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

package com.wipro.ats.bdre.md.auth.listener;

/**
 * Created by AR288503 on 9/6/2015.
 */

import com.wipro.ats.bdre.md.auth.SessionHolder;
import com.wipro.ats.bdre.security.AuthResult;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class BDRESessionListener implements HttpSessionListener {
    private static final Logger LOGGER = Logger.getLogger(BDRESessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.info("Session created");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession httpSession = event.getSession();
        AuthResult authResult = (AuthResult) httpSession.getAttribute("CURRENT_SESSION_AUTH_RESULT");
        if (authResult != null) {
            SessionHolder.removeAuthResult(authResult.getAuthToken());
            LOGGER.info("Session destroyed.Token=" + authResult.getAuthToken());
        } else {
            LOGGER.info("Session destroyed.Token not found.");
        }
    }
}