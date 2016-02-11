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

package com.wipro.ats.bdre.md.rest.util;

/**
 * Created by jayabroto on 06-04-2015.
 */

/**
 * This class is to access the Columns of the Tables used for import and dataload.
 */
public class Column {
    private String srcColumnName;
    private String destColumnName;
    private String srcDataType;
    private String destDataType;
    private String srcColumnIndex;

    public String getSrcColumnIndex() {
        return srcColumnIndex;
    }


    public void setSrcColumnIndex(String srcColumnIndex) {
        this.srcColumnIndex = srcColumnIndex;
    }

    public Column(String srcColumnName) {
        this.srcColumnName = srcColumnName;
    }

    public String getSrcColumnName() {
        return srcColumnName;
    }

    public void setSrcColumnName(String srcColumnName) {
        this.srcColumnName = srcColumnName;
    }

    public String getDestColumnName() {
        return destColumnName;
    }

    public void setDestColumnName(String destColumnName) {
        this.destColumnName = destColumnName;
    }

    public String getSrcDataType() {
        return srcDataType;
    }

    public void setSrcDataType(String srcDataType) {
        this.srcDataType = srcDataType;
    }

    public String getDestDataType() {
        return destDataType;
    }

    public void setDestDataType(String destDataType) {
        this.destDataType = destDataType;
    }

}
