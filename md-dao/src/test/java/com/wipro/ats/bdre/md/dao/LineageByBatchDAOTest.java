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

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.beans.GetLineageByBatchInfo;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


/**
 * Created by PR324290 on 11/5/2015.
 * Edited by SU324335 on 17-Nov-15.
 */
public class LineageByBatchDAOTest {
    private static final Logger LOGGER = Logger.getLogger(LineageByBatchDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    LineageByBatchDAO lineageByBatchDAO;

    /*  @Test
      public void testLineaseByBatch()
      {
          GetLineageByBatchInfo getLineageByBatchInfo = new GetLineageByBatchInfo();
          getLineageByBatchInfo.setTargetBatchId(0);
          List<GetLineageByBatchInfo> outputlist= lineageByBatchDAO.lineageByBatch(getLineageByBatchInfo);
          LOGGER.info("total objects returned is "+outputlist);
      }*/
    @Test
    public void testLineageByBatch() {
        GetLineageByBatchInfo getLineageByBatchInfo = new GetLineageByBatchInfo();
        getLineageByBatchInfo.setTargetBatchId(0);
        List<GetLineageByBatchInfo> outputList = lineageByBatchDAO.lineageByBatch(getLineageByBatchInfo);
        LOGGER.info("total objects returned is " + outputList.size());

        for (GetLineageByBatchInfo output : outputList) {
            LOGGER.info("TargetBatch Id is " + output.getTargetBatchId() + " source Batch Id is " + output.getSourceBatchId());
        }
    }
}
