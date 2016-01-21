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

package com.wipro.ats.bdre.lineage;

import com.wipro.ats.bdre.GetLineageNodeByColName;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by AshutoshRai on 1/20/16.
 */
public class GetDotForTable {
    protected static final Logger logger = LoggerFactory.getLogger(GetDotForTable.class);

    public static void main(String args[]){
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();
        List<LineageNode> lineageNodeList = getLineageNodeByColName.execute(args);
        for(LineageNode lineageNode:lineageNodeList){
            System.out.println("Column name: "+lineageNode.getDisplayName()+" Node id: "+lineageNode.getNodeId());
        }
    }
}
