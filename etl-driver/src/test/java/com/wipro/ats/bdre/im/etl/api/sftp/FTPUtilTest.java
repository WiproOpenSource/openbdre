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

package com.wipro.ats.bdre.im.etl.api.sftp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FTPUtilTest {

    @Test
    public void testExtractFootPrint() throws Exception {
        String message="The authenticity of host '192.168.56.101' can't be established.\n" +
                "RSA key fingerprint is 2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.\n" +
                "Are you sure you want to continue connecting?";
        assertEquals(FTPUtil.extractFootPrint(message),"2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.");
    }
}