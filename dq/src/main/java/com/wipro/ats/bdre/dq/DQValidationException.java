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

package com.wipro.ats.bdre.dq;

/**
 * Created by arijit on 3/6/15.
 */
public class DQValidationException extends Exception {

private DQStats dqStats;
    public DQValidationException(DQStats dqStats){
        this.dqStats=dqStats;
    }

    @Override
    public String getMessage() {
        return "Data quality check did not pass the defined threshold. " +
                "Bad records="+dqStats.getNumBad()+"; Good records="+dqStats.getNumGood()+"; " +
                "%Passed="+dqStats.getGoodPercent()+"; %Minimum="+dqStats.getThreshold();
    }

   /* public static class CommonDQFunctionsTest extends TestCase {

        @Ignore
        public void testCheckString() throws Exception {
            String s;
            CommonDQFunctions commonDQFunctions = new CommonDQFunctions();
            s = commonDQFunctions.checkString("6","10","0","p","023");
            System.out.println(s);
        }
        @Ignore
        public void testCheckInteger() throws Exception {
            String s;
            CommonDQFunctions commonDQFunctions = new CommonDQFunctions();
            s = commonDQFunctions.checkInteger("2093","1000","10000");
            System.out.println(s);
        }
        @Ignore
        public void testCheckLong() throws Exception {
            String s;
            CommonDQFunctions commonDQFunctions = new CommonDQFunctions();
            s = commonDQFunctions.checkLong("12345678910", "100000000", "100000000000000");
            System.out.println(s);
        }
        @Ignore
        public void testCheckDouble() throws Exception {
            String s;
            CommonDQFunctions commonDQFunctions = new CommonDQFunctions();
            s = commonDQFunctions.checkDouble("2093d", "1000d", "10000d");
            System.out.println(s);
        }
        @Ignore
        public void testCheckFloat() throws Exception {
            String s;
            CommonDQFunctions commonDQFunctions = new CommonDQFunctions();
            s = commonDQFunctions.checkFloat("2093f","1000f","10000f");
            System.out.println(s);
        }
    }*/
}
