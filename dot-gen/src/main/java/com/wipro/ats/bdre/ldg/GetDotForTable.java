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
import java.util.List;

/**
 * Created by AshutoshRai on 1/20/16.
 */
public class GetDotForTable {
    protected static final Logger logger = LoggerFactory.getLogger(GetDotForTable.class);
    private String dotString;

    //fn() to get DOT through Column name & table name
    public String dotGeneratorWithCol(String args[]) {
        if(args.length !=2) {
            logger.info("Two arguments required: please Give both Column name and Table name");
            System.exit(-1);
        }
        GetDotForTable getDotForTable = new GetDotForTable();
        GetNodeIdForLineageRelation getNodeIdForLineageRelation = new GetNodeIdForLineageRelation();

        //get elements of LR table
        List<LineageRelation> lineageRelationList;

        //get the nodes which have the same DISPLAY_NAME as that of the string(COLUMN NAME) provided

        GetLineageNodeByColName getLineageNodeByColName;
        LineageMain lineageMain = new LineageMain();
        //src table name is provided by user
        String srcTableName = args[1];
        logger.info("Generating DOT for the given Column Name: " + args[0]);
        String targetTableName = null;
        String relationDot;
        getLineageNodeByColName = new GetLineageNodeByColName();
        LineageNode srcTableNode = getLineageNodeByColName.getTableDotFromTableName(srcTableName);
        List<LineageNode> lineageNodeList = getDotForTable.getLineageNodeNodeId(args[0], srcTableNode);

        //compare the list of Nodes got from LN table with LR table to get the required node
        for (LineageNode lineageNode : lineageNodeList) {
            lineageRelationList = getNodeIdForLineageRelation.execute(lineageNode.getNodeId());
            if (lineageRelationList.size() != 0) {
                relationDot = lineageRelationList.get(0).getDotString();                                            //search for target table name in the DOT string
                String targetTableNode = getLineageNodeByColName.getTableDotFromNodeId(lineageRelationList.get(0).getLineageNodeByTargetNodeId(), srcTableNode);
                if(targetTableNode.equals("same-nodes")) {
                    targetTableNode = getLineageNodeByColName.getTableDotFromNodeId(lineageRelationList.get(0).getLineageNodeBySrcNodeId(), srcTableNode);
                }
                logger.info("relation DOT: \n" + relationDot);
                dotString = lineageMain.generateLineageDot(relationDot, srcTableNode, targetTableNode);             //call lineage main to create the DOT
            }
        }
        return dotString;
    }

    //fn() to get DOT through Table name
    public String dotGeneratorWithTable(String args[]) {
        if(args.length !=1) {
            logger.info("One argument required: please Give Table name");
            System.exit(-1);
        }
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();;
        LineageMain lineageMain = new LineageMain();
        String srcTableName = args[0];
        logger.info("Generating DOT for the given Table Name: " + args[0]);
        String targetTableName = null;
        String relationDot = null;
        String targetTableNode = null;
        LineageNode srcTableNode = getLineageNodeByColName.getTableDotFromTableName(srcTableName);
        dotString = lineageMain.generateLineageDot(relationDot, srcTableNode, targetTableNode);             //call lineage main to create the DOT
        return dotString;
    }

    private List<LineageNode> getLineageNodeNodeId (String col, LineageNode tableNode) {
        GetLineageNodeByColName getLineageNodeByColName = new GetLineageNodeByColName();
        List<LineageNode> lineageNodeList = getLineageNodeByColName.execute(col, tableNode);
        for(LineageNode lineageNode:lineageNodeList){
            logger.info("Column name: "+lineageNode.getDisplayName()+" Node id: "+lineageNode.getNodeId());
        }
        return lineageNodeList;
    }
}
