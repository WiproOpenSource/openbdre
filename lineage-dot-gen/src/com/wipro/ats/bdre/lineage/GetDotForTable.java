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
import com.wipro.ats.bdre.GetNodeIdForLineageRelation;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AshutoshRai on 1/20/16.
 */
public class GetDotForTable {
    protected static final Logger logger = LoggerFactory.getLogger(GetDotForTable.class);

    public static void main(String args[]){
        GetDotForTable getDotForTable = new GetDotForTable();
        GetNodeIdForLineageRelation getNodeIdForLineageRelation = new GetNodeIdForLineageRelation();
        //get elements of LR table
        List<LineageRelation> lineageRelationList;
        //get the nodes which have the same DISPLAY_NAME as that of the string provided
        List<LineageNode> lineageNodeList = getDotForTable.getLineageNodeNodeId(args);
        List<String> nodeIds = new ArrayList<>();
        List<String> nodeDotString = new ArrayList<>();
        //compare the list of Nodes got from LN table with LR table to get the required node & store its node id & dot string
        //for (LineageRelation lineageRelation:lineageRelationList) {
            for(LineageNode lineageNode:lineageNodeList) {
                System.out.println("outside if");
                /*System.out.println("LN: "+lineageNode.getNodeId());
                System.out.println("LR: "+lineageRelation.getDotString());
                System.out.println("LR: "+lineageRelation.getLineageNodeBySrcNodeId());
                if(lineageNode.getNodeId().equals(lineageRelation.getLineageNodeBySrcNodeId())) { */
                    System.out.println("inside if");
                    lineageRelationList = getNodeIdForLineageRelation.execute(lineageNode.getNodeId());
                if(lineageRelationList.size()!=0) {
                    nodeIds.add(lineageNode.getNodeId());                                       //add the matched Node's nodeId to list
                    nodeDotString.add(lineageRelationList.get(0).getDotString());                          //add the corresponding Dot String to list
                    logger.info(lineageRelationList.get(0).getDotString());
                    //break;
                }
                //}
            }
        //}
    }

    private List<LineageNode> getLineageNodeNodeId (String params[]) {
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();
        List<LineageNode> lineageNodeList = getLineageNodeByColName.execute(params);
        for(LineageNode lineageNode:lineageNodeList){
            logger.info("Column name: "+lineageNode.getDisplayName()+" Node id: "+lineageNode.getNodeId());
        }
        return lineageNodeList;
    }
}
