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

package com.wipro.ats.bdre.im.etl.api.exception;

import com.wipro.ats.bdre.exception.BDREException;

/**
 * Created by vishnu on 12/19/14.
 */
public class ETLException extends BDREException {
    public ETLException() {
        super();
    }

    public ETLException(String msg) {
        super(msg);
    }

    public ETLException(Exception e) {
        super(e);
    }

    public ETLException(String msg, Exception e) {
        super(msg, e);
    }
}
