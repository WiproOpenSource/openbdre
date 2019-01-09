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

package com.wipro.ats.bdre.md.rest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by arijit on 1/10/15.
 */

/**
 * This class is used to access the objects as JSON object.
 */
public class RestWrapper {
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    private static final Logger LOGGER = Logger.getLogger(RestWrapper.class);
    private String result;
    private Object records;
    private String message;
    public RestWrapper(Object objectToSerialize, String result) {
        this.result = result;
        if (OK.equals(result)) {
            this.records = objectToSerialize;
        } else if (objectToSerialize != null) {
            this.message = objectToSerialize.toString();
        }
    }
    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("Result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("Records")
    public Object getRecords() {
        return records;
    }

    public void setRecords(Object records) {
        this.records = records;
    }

    @JsonProperty("Record")
    public Object getRecord() {
        return records;
    }

    public void setRecord(Object record) {
        this.records = record;
    }

    @JsonProperty("TotalRecordCount")
    public int getTotalRecordCount() {
        if (records != null) {
            if (records instanceof Collection) {
                Collection c = (Collection) records;
                for (Object obj : c) {
                    try {
                        Method method = obj.getClass().getMethod("getCounter");
                        return (Integer) method.invoke(obj);
                    } catch (NoSuchMethodException e) {
                        LOGGER.error(e);
                        return 0;
                    } catch (InvocationTargetException e) {
                        LOGGER.error(e);
                        return 0;
                    } catch (IllegalAccessException e) {
                        LOGGER.error(e);
                        return 0;
                    }
                }
            } else {
                return 1;
            }
        }
        return 0;
    }


}
