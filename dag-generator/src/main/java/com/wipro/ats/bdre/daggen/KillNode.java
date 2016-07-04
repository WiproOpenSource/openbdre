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

package com.wipro.ats.bdre.daggen;

import com.wipro.ats.bdre.exception.BDREException;

/**
 * Created by arijit on 12/23/14.
 */
public class KillNode extends OozieNode {

    public KillNode() {

    }

    public String getName() {
        return "kill";
    }

    @Override
    public String getXML() {
        return "\n<kill name='" + getName() + "'>" +
                "\n\t<message>Action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]</message>" +
                "\n</kill>";
    }

    @Override
    public OozieNode getToNode() {
        throw new BDREException("Getting To node from kill is not supported");
    }

    @Override
    public void setToNode(OozieNode node) {
        throw new BDREException("Setting To node to kill is not supported");
    }


}
