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

import com.wipro.ats.bdre.md.beans.table.UserRoles;
import com.wipro.ats.bdre.md.beans.table.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AR288503 on 9/5/2015.
 */
public class UserRoleDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        UserRoleFetcher userRoleFetcher = new UserRoleFetcher();
        Users users = userRoleFetcher.getUser(username);
        List<UserRoles> userRoles = userRoleFetcher.getRoles(username);
        for (UserRoles role : userRoles) {
            grantedAuthorities.add(new GrantedAuthorityImpl(role.getRole()));
        }
        UserDetails user = new User(users.getUsername(), "na", true, true, true, true, grantedAuthorities);
        return user;
    }
}
