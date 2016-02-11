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

package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.RegisterFileInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

public class RegisterFileTest {
    private static final Logger LOGGER = Logger.getLogger(RegisterFileTest.class);

    @Ignore
    @Test
    public void testExecute() throws Exception {
        String[] args = {"-cTS", "2014-12-11 11:56:17", "-fh", "3", "-sId", "1", "-fs", "3", "-p", "134", "--path", "/home/cool5", "--batch-id", "null"};
        RegisterFile bs = new RegisterFile();
        RegisterFileInfo info = bs.execute(args);
        LOGGER.debug(info);
    }
}







