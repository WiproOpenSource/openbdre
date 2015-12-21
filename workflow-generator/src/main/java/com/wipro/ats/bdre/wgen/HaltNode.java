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
 * Methods getName() and getXML() for HaltNode
 * getXML() is formatting the string to be returned as XML format
 */

public class HaltNode extends OozieNode {

    public HaltNode() {

    }

    public String getName() {
        return "halt";
    }

    @Override
    public String getXML() {
        return "\n<end name='" + getName() + "'/>";
    }

    @Override
    public OozieNode getToNode() {
        throw new RuntimeException("Getting To node from end is not supported");
    }

    @Override
    public void setToNode(OozieNode node) {
        throw new RuntimeException("Setting To node to end is not supported");
    }


}