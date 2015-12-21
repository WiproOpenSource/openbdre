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

package com.wipro.ats.bdre.md;

import com.wipro.ats.bdre.BaseStructure;
import org.junit.Ignore;
import org.junit.Test;

public class BaseStructureTest {

    private static final String[][] PARAMS_STRUCTURE = {
            {"x", "x-param", "This is X param"},
            {"y", "y-param", "This is Y param"},
            {"z", "z-param", "This is Z param"}

    };

    @Ignore
    @Test
    public void testGetCommandLine() throws Exception {
        BaseStructure baseStructure = new BaseStructure();
        baseStructure.getCommandLine(new String[]{"-x", "xval", "-y", "yval", "-z", "zval"}, PARAMS_STRUCTURE);

    }
}

