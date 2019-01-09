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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by kapil on 09-03-2015.
 */

/**
 * This class is used to access the objects as JSON object.
 */
public class RestWrapperOptions {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    private String result;
    private Object options;
    private String message;

    public RestWrapperOptions(Object objectToSerialize, String result) {
        this.result = result;
        if (OK.equals(result)) {
            this.options = objectToSerialize;
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


    @JsonProperty("Options")
    public Object getOptions() {
        return options;
    }

    public void setOptions(Object options) {
        this.options = options;
    }

    //InnerClass
    public static class Option {
        private String displayText;
        private Object value;
        public Option(String displayText, Object value) {
            this.displayText = displayText;
            this.value = value;
        }

        @JsonProperty("DisplayText")
        public String getDisplayText() {
            return displayText;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }

        @JsonProperty("Value")
        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }


    }
}
