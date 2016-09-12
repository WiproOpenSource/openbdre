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

package com.wipro.ats.bdre.wgen.dag;

/**
 * Created by arijit on 1/4/15.
 */

/**
 * This class contains all the setter and getter methods for Workflow variables .
 */
public class DAG {
    private StringBuilder dag;
    private StringBuilder dot;

    public StringBuilder getDAG() {
        return dag;
    }

    public void setDAG(StringBuilder dag) {
        this.dag = dag;
    }

    public StringBuilder getDot() {
        return dot;
    }

    public void setDot(StringBuilder dot) {
        this.dot = dot;
    }
}
