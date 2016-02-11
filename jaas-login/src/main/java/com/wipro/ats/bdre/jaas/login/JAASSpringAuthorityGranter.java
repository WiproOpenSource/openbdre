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
import com.wipro.ats.bdre.security.UserRoleFetcher;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by arijit on 3/8/15.
 */
public class JAASSpringAuthorityGranter implements org.springframework.security.authentication.jaas.AuthorityGranter {
    @Override
    public Set<String> grant(Principal principal) {
        Set<String> roleSet = getRoles(principal.getName());
        return roleSet;
    }

    /**
     * Returns list of roles assigned to authenticated user.
     *
     * @return roleSet
     */
    private Set<String> getRoles(String username) {
        HashSet<String> roleSet = new HashSet<String>();
        UserRoleFetcher userRoleFetcher = new UserRoleFetcher();
        for (UserRoles role : userRoleFetcher.getRoles(username)) {
            roleSet.add(role.getRole());
        }
        return roleSet;
    }
}
