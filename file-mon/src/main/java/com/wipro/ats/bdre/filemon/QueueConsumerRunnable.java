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

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.exception.BDREException;
import org.apache.log4j.Logger;


/**
 * Created by vishnu on 1/11/15.
 */
public class QueueConsumerRunnable implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(QueueConsumerRunnable.class);

    @Override
    public void run() {
        try {
            while (true) {
                QueuedFileUploader.executeCopyProcess();
                Thread.sleep(FileMonRunnableMain.getSleepTime());
            }
        } catch (Exception err) {
            LOGGER.error("Error in Queue consumer ", err);
            throw new BDREException(err);
        }
    }


}