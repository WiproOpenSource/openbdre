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

package com.wipro.ats.bdre.md.auth;

import com.wipro.ats.bdre.security.AuthResult;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AR288503 on 9/6/2015.
 */
public class SessionHolder {

    public static final int MAX_INACTIVE_SESSION_DURATION = 600;
    private static Map<String, AuthResult> authResultMap = new HashMap<String, AuthResult>();

    private SessionHolder(){}

    public static void resetSessionTimeout(HttpSession session) {
        int timeElapsed = (int) (new Date().getTime() - session.getCreationTime()) / 1000;
        session.setMaxInactiveInterval(SessionHolder.MAX_INACTIVE_SESSION_DURATION + timeElapsed);
    }

    public static Map<String, AuthResult> getAuthResultMap() {
        return authResultMap;
    }

    public static void addAuthResult(AuthResult authResult) {
        authResultMap.put(authResult.getAuthToken(), authResult);
    }

    public static void removeAuthResult(String token) {
        authResultMap.remove(token);
    }

    public static AuthResult getAuthResult(String token) {
        return authResultMap.get(token);
    }

    public static Collection<AuthResult> getAllAuthResults() {
        return authResultMap.values();
    }
}
