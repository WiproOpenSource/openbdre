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

package com.wipro.ats.bdre.wgen;

/**
 * Created by arijit on 12/23/14.
 */

/**
 * Methods getName() and getXML() for InitJobNode
 * getXML() is formatting the string to be returned as XML format
 */

public class InitJobNode extends OozieNode {

    public InitJobNode() {

    }

    public String getName() {
        return "init-job";
    }

    @Override
    public String getXML() {
        String ret = "\n<action name=\"" + getName() + "\">\n" +
                "        <java>\n" +
                "            <job-tracker>${jobTracker}</job-tracker>\n" +
                "            <name-node>${nameNode}</name-node>\n" +
                "            <main-class>com.wipro.ats.bdre.md.api.oozie.OozieInitJob</main-class>\n" +
                "            <arg>--environment-id</arg>\n" +
                "            <arg>${env}</arg>\n" +
                "            <arg>--max-batch</arg>\n" +
                "            <arg>1</arg>\n" +
                "            <arg>--process-id</arg>\n" +
                "            <arg>" + getId() + "</arg>\n" +
                "            <capture-output />\n" +
                "        </java>\n" +
                "        <ok to=\"" + getToNode().getName() + "\"/>\n" +
                "        <error to=\"" + getTermNode().getName() + "\"/>\n" +
                "</action>";

        return ret;
    }

}