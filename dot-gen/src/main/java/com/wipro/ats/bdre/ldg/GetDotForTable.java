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
package com.wipro.ats.bdre.ldg;

import com.wipro.ats.bdre.GetLineageNodeByColName;
import com.wipro.ats.bdre.GetNodeIdForLineageRelation;
import com.wipro.ats.bdre.lineage.LineageMain;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import com.wipro.ats.bdre.md.dao.jpa.LineageRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AshutoshRai on 1/20/16.
 */
public class GetDotForTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetDotForTable.class);
    private String dotString;

    //fn() to get DOT through Column name & table name
    public String dotGeneratorWithCol(String[] args) {
        if(args.length !=2) {
            LOGGER.info("Two arguments required: please Give both Column name and Table name");
            System.exit(-1);
        }
        GetDotForTable getDotForTable = new GetDotForTable();
        GetNodeIdForLineageRelation getNodeIdForLineageRelation = new GetNodeIdForLineageRelation();

        List<LineageRelation> lineageRelationList;
        List<LineageRelation> lineageRelationListForTargetNodes;

        //get the nodes which have the same DISPLAY_NAME as that of the string(COLUMN NAME) provided

        GetLineageNodeByColName getLineageNodeByColName;
        LineageMain lineageMain = new LineageMain();
        //src table name is provided by user
        String srcTableName = args[1];
        LOGGER.info("Generating DOT for the given Column Name: " + args[0]);
        StringBuffer relationDot = new StringBuffer("");
        getLineageNodeByColName = new GetLineageNodeByColName();
        //for each relation a new node is created in LN table, so get all those
        List<LineageNode> srcTableNodeList = getLineageNodeByColName.getTableDotFromTableName(srcTableName);
        List<LineageNode> lineageNodeList = new ArrayList<LineageNode>();
        int counter =0;
        //now get all those col nodes corresponding to the tables created for each relation
        for(LineageNode lnodes:srcTableNodeList) {
            LOGGER.info("----------------------------------src table" + lnodes.getDisplayName());
             lineageNodeList.add(getDotForTable.getLineageNodeNodeId(args[0], lnodes));
            LOGGER.info("----------------------------------col node" + lineageNodeList.get(counter++).getDisplayName());
        }
        StringBuffer targetTableNode = new StringBuffer("");

        //compare the list of Nodes got from LN table with LR table to get the required node
        for (LineageNode lineageNode : lineageNodeList) {
            //this only checks the case where the node is src
            LOGGER.info("------------------------inside");
            lineageRelationList = getNodeIdForLineageRelation.execute(lineageNode.getNodeId());
            for(LineageRelation lineageRelation:lineageRelationList) {
                LOGGER.info("------------------------inside src check");
                relationDot.append("\n" + lineageRelation.getDotString() + "\n");                                            //search for target table name in the DOT string
                LOGGER.info("------------------" + relationDot);
                targetTableNode.append("\n" + getLineageNodeByColName.getTableDotFromNodeId(lineageRelation.getLineageNodeByTargetNodeId(), srcTableNodeList.get(0)) + "\n");
                LOGGER.info("-------------------------" + targetTableNode);
                if("same-nodes".equals(targetTableNode)) {
                    targetTableNode.append("\n" + getLineageNodeByColName.getTableDotFromNodeId(lineageRelation.getLineageNodeBySrcNodeId(), srcTableNodeList.get(0)) + "\n");
                }
                LOGGER.info("------------------------------relation DOT: \n" + relationDot);
             }

            //this should check whether the nodes are target -- ??
            lineageRelationListForTargetNodes = getNodeIdForLineageRelation.getWhenTargetNode(lineageNode.getNodeId());
            for(LineageRelation lineageRelation:lineageRelationListForTargetNodes) {
                LOGGER.info("------------------------inside target check");
                relationDot.append("\n" + lineageRelation.getDotString() + "\n");                                            //search for target table name in the DOT string
                LOGGER.info("------------------" + relationDot);
                targetTableNode.append("\n" + getLineageNodeByColName.getTableDotFromNodeId(lineageRelation.getLineageNodeBySrcNodeId(), srcTableNodeList.get(0)) + "\n");
                LOGGER.info("-------------------------" + targetTableNode);
                if("same-nodes".equals(targetTableNode)) {
                    targetTableNode.append("\n" + getLineageNodeByColName.getTableDotFromNodeId(lineageRelation.getLineageNodeByTargetNodeId(), srcTableNodeList.get(0)) + "\n");
                }
                LOGGER.info("-------------------------------relation DOT: \n" + relationDot);
            }

        }
        dotString = lineageMain.generateLineageDot(relationDot.toString(), srcTableNodeList.get(0).getDotString(), targetTableNode.toString());             //call lineage main to create the DOT
        return dotString;
    }

    //fn() to get DOT through only Table name
    public String dotGeneratorWithTable(String[] args) {
        if(args.length !=1) {
            LOGGER.info("One argument required: please Give Table name");
            System.exit(-1);
        }
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();
        LineageMain lineageMain = new LineageMain();
        String srcTableName = args[0];
        LOGGER.info("Generating DOT for the given Table Name: " + args[0]);
        String relationDot = null;
        String targetTableNode = null;
        List<LineageNode> srcTableNodeList = getLineageNodeByColName.getTableDotFromTableName(srcTableName);
        dotString = lineageMain.generateLineageDot(relationDot, srcTableNodeList.get(0).getDotString(), targetTableNode);             //call lineage main to create the DOT
        return dotString;
    }

    private LineageNode getLineageNodeNodeId (String col, LineageNode tableNode) {
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();
        LineageNode lineageNode = getLineageNodeByColName.execute(col, tableNode);
            LOGGER.info("Column name: "+lineageNode.getDisplayName()+" Node id: "+lineageNode.getNodeId());
        return lineageNode;
    }
}
