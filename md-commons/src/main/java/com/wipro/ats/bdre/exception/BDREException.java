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

package com.wipro.ats.bdre.exception;

/**
 * Created by arijit on 12/27/14.
 */
public class BDREException extends RuntimeException {
    /**
     * Constructor for a new runtime exception. It has the detail message as {@code null}.
     */
    public BDREException() {

        super();
    }

    /**
     * Constructor for a new runtime exception. with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).
     *
     * @param exception The cause can be fetched later using {@link #getCause()}.
     *                  If the value is null, it means cause does not exist or is unknown
     * @since 1.4
     */
    public BDREException(Exception exception) {
        super(exception);
    }

    /**
     * Constructor for a new runtime exception. Detail message is taken from the parameter.
     *
     * @param exceptionMessage the detail message. The detail message can be fetched later using {@link #getMessage()}.
     */
    public BDREException(String exceptionMessage) {
        super(exceptionMessage);
    }


    /**
     * Constructor for a new runtime exception. Accepts both the detail message and the cause.
     *
     * @param exceptionMessage the detail message (can be fetched later using {@link #getMessage()} )
     * @param exception        the cause (can be fetched later using {{@link #getCause()} )
     *                         If the value is null, it means cause does not exist or is unknown
     * @since 1.4
     */
    public BDREException(String exceptionMessage, Exception exception) {
        super(exceptionMessage, exception);
    }

}
