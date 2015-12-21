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

package com.wipro.ats.bdre.md.beans;

/**
 * Created by jayabroto on 14-05-2015.
 */

/**
 * This class is used to assign enum used in Data Lineage.
 */
public enum LineageNodeTypeEnumInfo {

    TABLE(1, "TABLE"),
    COLUMN(2, "COLUMN"),
    FUNCTION(3, "FUNCTION"),
    TEMPTABLE(4, "TEMP_TABLE"),
    IDLECOLUMN(5, "IDLE_COLUMN"),
    CONSTANT(6, "CONSTANT");

    public final int nodeTypeId;
    public final String nodeTypeName;


    LineageNodeTypeEnumInfo(int nodeTypeId, String nodeTypeName) {
        this.nodeTypeId = nodeTypeId;
        this.nodeTypeName = nodeTypeName;
    }

    private static int populateFromDb() {
        return 0;
    }

    @Override
    public String toString() {
        return " nodeTypeId:" + nodeTypeId + " nodeTypeName:" + nodeTypeName;
    }
}
