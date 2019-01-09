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

package com.wipro.ats.bdre.wgen;

/**
 * Created by arijit on 12/23/14.
 */

/**
 * Methods getName() and getXML() for StartNode
 * getXML() is formatting the string to be returned as XML format
 */

public class StartNode extends OozieNode {

    public StartNode() {

    }

    public String getName() {
        return "start";
    }

    @Override
    public String getXML() {
        return "\n<start to='" + getToNode().getName() + "'></start>";
    }

}