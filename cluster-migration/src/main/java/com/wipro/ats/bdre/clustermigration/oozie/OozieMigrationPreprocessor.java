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

package com.wipro.ats.bdre.clustermigration.oozie;

import com.wipro.ats.bdre.clustermigration.MigrationPreprocessor;
import com.wipro.ats.bdre.clustermigration.beans.MigrationPreprocessorInfo;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.util.OozieUtil;

/**
 * Created by cloudera on 3/31/16.
 */
public class OozieMigrationPreprocessor {

    private OozieMigrationPreprocessor(){
    }

    public static void main(String[] args) {
        try {
            MigrationPreprocessor migrationPreprocessor = new MigrationPreprocessor();
            MigrationPreprocessorInfo migrationPreprocessorInfo=migrationPreprocessor.execute(args);
            OozieUtil oozieUtil = new OozieUtil();
            oozieUtil.persistBeanData(migrationPreprocessorInfo, false);
        }
        catch(Exception e){
            throw new BDREException(e);
        }
    }
}
