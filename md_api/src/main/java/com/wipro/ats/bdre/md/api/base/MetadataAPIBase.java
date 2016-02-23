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

/*
    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader = null;
    private static final Logger LOGGER = Logger.getLogger(MetadataAPIBase.class);

    static {
        try {


            String resource;
            resource = "mybatis-config.xml";
            reader = Resources.getResourceAsReader(resource);

        } catch (FileNotFoundException e) {
            throw new MetadataException("Config file not found", e);
        } catch (IOException e) {
            throw new MetadataException("Config file not found", e);
        }
    }

    */

    /**
     * This is an abstract method defining structure of execute method.
     *
     * @param params Sting array of Command line arguments.
     * @return Return type defines that this method can return any type of Object.
     */

    public abstract Object execute(String[] params);


/**
 * This method creates an instance of SqlSessionFactory and build a sql session.
 *
 * @param env Environment variable which defines connection setting for sql server.
 * @return The method returns instance of SqlSessionFactory.
 *//*


    public SqlSessionFactory getSqlSessionFactory(String env) {
        if (env == null) {
            env = getDefaultEnv();
            LOGGER.debug("env was null so adding env from default:env= " + env);
        }
        if (sqlSessionFactory == null) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, env);
        }
        return sqlSessionFactory;
    }
*/

}
