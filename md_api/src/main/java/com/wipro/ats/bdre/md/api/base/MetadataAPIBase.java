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

package com.wipro.ats.bdre.md.api.base;

import com.wipro.ats.bdre.BaseStructure;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by arijit on 12/8/14.
 */
public abstract class MetadataAPIBase extends BaseStructure {

    private static AutowireCapableBeanFactory acbFactory = null;
    protected static AutowireCapableBeanFactory getAutowireCapableBeanFactory(){
        if(acbFactory==null) {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
            acbFactory = context.getAutowireCapableBeanFactory();
        }
        return acbFactory;
    }

    /**
     * This is an abstract method defining structure of execute method.
     *
     * @param params Sting array of Command line arguments.
     * @return Return type defines that this method can return any type of Object.
     */

    public abstract Object execute(String[] params);

}
