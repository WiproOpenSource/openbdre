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

import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.ui.ModelMap;

import java.security.Principal;
import java.util.HashMap;

public class GeneralConfigAPITest extends TestCase {
    @Ignore
    public void testList() throws Exception {
        GeneralConfigAPI generalConfigAPI = new GeneralConfigAPI();
        ModelMap modelMap = null;
        Principal principal = null;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("source_a", "a");
        hashMap.put("sink_b", "b");
        hashMap.put("source_c", "c");
        hashMap.put("channel_encyption.keyProvider.keys.*.passwordFile", "d");
        hashMap.put("sink_e", "e");

    }
}