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
package com.wipro.ats.bdre.dq;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Satish Kumar
 *         <p/>
 *         JavaBean class that holds the data for Drool engine.
 */
public class DQDataModel {

    private static final Logger LOGGER = Logger.getLogger(DQDataModel.class);
    private String mRecord;
    private boolean isValidRecord;
    private Vector<String> mInvalidRecordMessage;
    private String mStructureId;
    private String mDelimiter;
    private List<String> mColumns = new ArrayList<String>();

    public DQDataModel(String record, String delimiter) {
        this.mRecord = record;
        this.mDelimiter = delimiter;
        if (mRecord != null) {
            String[] tokens = mRecord.split(delimiter);
            for (int i = 0; i < tokens.length; i++) {
                mColumns.add(tokens[i]);
            }
            LOGGER.trace("mColumns = " + mColumns.toString());
        }
        mInvalidRecordMessage = new Vector<String>();
    }
    public void setColumn(Column column){
        mColumns.set(column.getIndex(),column.getValue());
    }
    public Column getColumn(){
       return null;
    }
    public String getmRecord() {
        StringBuffer myRec=new StringBuffer();
        for(String cols: mColumns)
        {
            myRec=myRec.append(cols).append(mDelimiter);
        }
        if(myRec.length()>0)
        myRec.deleteCharAt(myRec.length()-1);
       return myRec.toString();
    }

    public void setmRecord(String mRecord) {
        this.mRecord = mRecord;
    }

    public boolean isValidRecord() {
        return isValidRecord;
    }

    public void setValidRecord(boolean isValidRecord) {
        this.isValidRecord = isValidRecord;
    }

    public String getmInvalidRecordMessage() {
        if (mInvalidRecordMessage.isEmpty()) {
            return null;
        }
        return mInvalidRecordMessage.toString();
    }

    public void addInvalidRecordMessage(String mInvalidRecordMessage) {
        this.mInvalidRecordMessage.add(mInvalidRecordMessage);
    }

    public String getmDelimter() {
        return mDelimiter;
    }

    public void setmDelimter(String mDelimiter) {
        this.mDelimiter = mDelimiter;
    }

    public List<String> getmColumns() {
        return mColumns;
    }

    public void setmColumns(List<String> mColumns) {
        this.mColumns = mColumns;
    }

    public String getColumn(int index) {
        return getmColumns().get(index);
    }

    public String getmStructureId() {
        return mStructureId;
    }

    public void setmStructureId(String mStructureId) {
        this.mStructureId = mStructureId;
    }

}
