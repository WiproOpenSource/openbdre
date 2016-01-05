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

package com.wipro.ats.bdre;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResolvePathTest {

    @Test
    public void testReplaceVars() throws Exception {
        String resolved = ResolvePath.replaceVars("t=%t a=%a A=%A b=%b B=%B c=%c d=%d e=%e D=%D H=%H I=%I j=%j k=%k m=%m n=%n M=%M p=%p s=%s S=%S y=%y Y=%Y z=%z");
        assert (!resolved.contains("Unknown"));
    }
}