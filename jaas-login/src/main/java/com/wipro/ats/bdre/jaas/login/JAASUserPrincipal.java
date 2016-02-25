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

import java.io.Serializable;
import java.security.Principal;

/**
 * @author arijit
 */
public class JAASUserPrincipal implements Principal, Serializable {

    private String name;

    /**
     * This method is used to access the principal username of JAAS.
     *
     * @param name
     */
    public JAASUserPrincipal(String name) {

        if (name == null) {
            throw new NullPointerException("NULL user name");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserPrincipal [name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        JAASUserPrincipal other = (JAASUserPrincipal) obj;
        if (name == null && other.name != null)
            return false;
         else if (!name.equals(other.name))
            return false;

        return true;
    }
}