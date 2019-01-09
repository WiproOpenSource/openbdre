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


import com.wipro.ats.bdre.exception.MetadataException;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by MI294210 on 10-02-2015.
 */

/**
 * This class is used to convert the date format from epoc to "yyyy/MM/dd hh:mm:ss" format
 * to reflect in the UI.And vise-versa to store in database.
 */
public class DateConverter {
    private static final Logger LOGGER = Logger.getLogger(DateConverter.class);

    Date date;

    /**
     * This method converts  Date object into String so that it does not reflect as epoc.
     *
     * @param date Date object
     * @return String with "yyyy/MM/dd hh:mm:ss" format date
     */
    public static String dateToString(Date date) {
        String dateString = new String();
        try {
            if (date == null) {
                return "";
            } else {
                dateString = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(date);//Date to String Object
            }
        } catch (Exception e) {
            LOGGER.error("dateConverter(): " + e.getMessage());
            throw new MetadataException(e);

        }
        return dateString;
    }

    /**
     * This method converts the String of date from UI to Date object so that it can be
     * inserted into the database.
     *
     * @param strDate String in "yyyy/MM/dd hh:mm:ss" format fetched from UI.
     * @return Date object.
     */
    public static Date stringToDate(String strDate) {

        try {
            if (strDate != null && !strDate.isEmpty()) {
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                return formatter.parse(strDate);   // String to Date object
            } else if (strDate != null && strDate.isEmpty()) {//check if the date is nullable
                return null;
            } else {
                return new Date();
            }
        } catch (Exception e) {
            LOGGER.error("dateConverter.convertReverse error: " + e.getMessage());
            throw new MetadataException(e);

        }


    }
}