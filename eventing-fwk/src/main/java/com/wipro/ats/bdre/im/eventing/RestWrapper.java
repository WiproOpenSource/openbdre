/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.eventing;
import org.codehaus.jackson.annotate.JsonProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by arijit on 1/10/15.
 */
public class RestWrapper {
    public static final String OK="OK";
    private String result;
    private Object records;


    public RestWrapper(Object objectToSerialize,String result){
        this.result=result;
        this.records=objectToSerialize;
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
    @JsonProperty("Record")
    public Object getRecord() {
        return records;
    }
    public void setRecords(Object records) {
        this.records = records;
    }
    @JsonProperty("TotalRecordCount")
    public int getTotalRecordCount() {
        if(records!=null){
            if(records instanceof Collection){
                Collection c = (Collection) records;
                for(Object obj: c){
                    try {
                        Method method = obj.getClass().getMethod("getCounter");
                        Integer count = (Integer) method.invoke(obj);
                        return count;
                    } catch (NoSuchMethodException e) {
                        return 0;
                    } catch (InvocationTargetException e) {
                        return 0;
                    } catch (IllegalAccessException e) {
                        return 0;
                    }
                }
            }
            else{
                return 1;
            }
        }
        return 0;
    }


}
