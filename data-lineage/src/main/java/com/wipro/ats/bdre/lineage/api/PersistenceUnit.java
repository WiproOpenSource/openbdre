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

package com.wipro.ats.bdre.lineage.api;

import com.wipro.ats.bdre.lineage.entiity.*;
import com.wipro.ats.bdre.lineage.type.UniqueList;
import com.wipro.ats.bdre.md.api.Lineage;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.LineageNodeInfo;
import com.wipro.ats.bdre.md.beans.LineageNodeTypeEnumInfo;
import com.wipro.ats.bdre.md.beans.LineageRelationInfo;
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Created by jayabroto on 20-05-2015.
 */
public class PersistenceUnit extends MetadataAPIBase {
	private static final Logger LOGGER = Logger.getLogger(PersistenceUnit.class);

	private UniqueList<LineageNodeInfo> lineageTableNodes = new UniqueList<LineageNodeInfo>();
	private UniqueList<LineageNodeInfo> lineageColumnNodes = new UniqueList<LineageNodeInfo>();
	private List<LineageNodeInfo> lineageFunctionNodes = new UniqueList<LineageNodeInfo>();
	private UniqueList<LineageRelationInfo> lineageRelationInfos = new UniqueList<LineageRelationInfo>();
	private List<LineageNodeInfo> lineageConstantNodes = new UniqueList<LineageNodeInfo>();
	public UniqueList<LineageNodeInfo> getLineageTableNodes() {
		return lineageTableNodes;
	}
	public UniqueList<LineageNodeInfo> getLineageColumnNodes() {
		return lineageColumnNodes;
	}
	public List<LineageNodeInfo> getLineageFunctionNodes() {
		return lineageFunctionNodes;
	}
	public UniqueList<LineageRelationInfo> getLineageRelationInfos() {
		return lineageRelationInfos;
	}

	@Override
	public Object execute(String[] params) {

		return null;
	}

	public void persist() {
		Lineage lineage = new Lineage();
		//inserting to LQ

		for (LineageNodeInfo lineageTableNode : lineageTableNodes) {
			lineage.insertLineageNode(lineageTableNode);
		}
		for (LineageNodeInfo lineageColumnNode : lineageColumnNodes) {
			lineage.insertLineageNode(lineageColumnNode);
		}
		for (LineageNodeInfo lineageFunctionNode : lineageFunctionNodes) {
			lineage.insertLineageNode(lineageFunctionNode);
		}
		for (LineageNodeInfo lineageConstantNode : lineageConstantNodes) {
			lineage.insertLineageNode(lineageConstantNode);
		}
		for (LineageRelationInfo lineageRelationInfo : lineageRelationInfos) {
			lineage.insertLineageRelation(lineageRelationInfo);
		}
		LOGGER.info("All nodes and relations are persisted in MySql db");
	}

	/**
	 * Populate data beans from final node lists
	 */
	public void populateBeans(UniqueList<Table> finalInTableNodes, UniqueList<Table> finalOutTableNodes, List<Function> finalFunctions, UniqueList<Constant> finalConstants, List<Relation> finalRelations, LineageQuery lineageQuery) {
		lineageTableNodes = new UniqueList<LineageNodeInfo>();
		lineageColumnNodes = new UniqueList<LineageNodeInfo>();
		lineageFunctionNodes = new UniqueList<LineageNodeInfo>();
		lineageConstantNodes = new UniqueList<LineageNodeInfo>();
		lineageRelationInfos = new UniqueList<LineageRelationInfo>();

		for (Table table : finalInTableNodes) {
			LineageNodeInfo node = new LineageNodeInfo();
			node.setNodeId(table.getId());
			node.setContainerNodeId(null);
			node.setDisplayName(table.getDisplayName());
			node.setDotLabel(table.getLabel());
			node.setDotString(table.toDotString());
			if (table.isDataBaseTable())
				node.setNodeTypeId(LineageNodeTypeEnumInfo.TABLE.nodeTypeId);
			else
				node.setNodeTypeId(LineageNodeTypeEnumInfo.TEMPTABLE.nodeTypeId);
			lineageTableNodes.addToList(node);

			// add columns
			for (Column column : table.getColumns()) {
				node = new LineageNodeInfo();
				node.setNodeId(column.getId());
				node.setContainerNodeId(String.valueOf(column.getTable().getId()));
				node.setDisplayName(column.getDisplayName());
				node.setDotLabel(column.getLabel());
				node.setDotString(column.toDotString());
				node.setNodeOrder(column.getOrdinalPosition());
				if (column.isUsedInQuery())
					node.setNodeTypeId(LineageNodeTypeEnumInfo.COLUMN.nodeTypeId);
				else
					node.setNodeTypeId(LineageNodeTypeEnumInfo.IDLECOLUMN.nodeTypeId);
				lineageColumnNodes.addToList(node);
			}
		}
		for (Table table : finalOutTableNodes) {
			LineageNodeInfo node = new LineageNodeInfo();
			node.setNodeId(table.getId());
			node.setContainerNodeId(null);
			node.setDisplayName(table.getDisplayName());
			node.setDotLabel(table.getLabel());
			node.setDotString(table.toDotString());
			if (table.isDataBaseTable())
				node.setNodeTypeId(LineageNodeTypeEnumInfo.TABLE.nodeTypeId);
			else
				node.setNodeTypeId(LineageNodeTypeEnumInfo.TEMPTABLE.nodeTypeId);
			lineageTableNodes.addToList(node);

			// add columns
			for (Column column : table.getColumns()) {
				node = new LineageNodeInfo();
				node.setNodeId(column.getId());
				node.setContainerNodeId(String.valueOf(column.getTable().getId()));
				node.setDisplayName(column.getDisplayName());
				node.setDotLabel(column.getLabel());
				node.setDotString(column.toDotString());
				node.setNodeOrder(column.getOrdinalPosition());
				if (column.isUsedInQuery())
					node.setNodeTypeId(LineageNodeTypeEnumInfo.COLUMN.nodeTypeId);
				else
					node.setNodeTypeId(LineageNodeTypeEnumInfo.IDLECOLUMN.nodeTypeId);
				lineageColumnNodes.addToList(node);
			}
		}
		for (Function function : finalFunctions) {
			LineageNodeInfo node = new LineageNodeInfo();
			node.setNodeId(function.getId());
			node.setContainerNodeId(null);
			node.setDisplayName(function.getDisplayName());
			node.setDotLabel(function.getLabel());
			node.setDotString(function.toDotString());
			node.setNodeTypeId(LineageNodeTypeEnumInfo.FUNCTION.nodeTypeId);
			lineageFunctionNodes.add(node);
		}
		for (Constant constant : finalConstants) {
			LineageNodeInfo node = new LineageNodeInfo();
			node.setNodeId(constant.getId());
			node.setContainerNodeId(null);
			node.setDisplayName(constant.getDisplayName());
			node.setDotLabel(constant.getLabel());
			node.setDotString(constant.toDotString());
			node.setNodeTypeId(LineageNodeTypeEnumInfo.CONSTANT.nodeTypeId);
			lineageConstantNodes.add(node);
		}

		for (Relation relation : finalRelations) {
			if(relation.getSource() instanceof Column && relation.getDestination() instanceof Column){
				if (!(((Column) relation.getSource()).getTable().getTableName().equals(((Column) relation.getDestination()).getTable().getTableName()) && ((Column) relation.getSource()).getColumnName().equals(((Column) relation.getDestination()).getColumnName()))){
					LOGGER.info("source and destination are not same columns = " + relation);
					LineageRelationInfo lineageRelationInfo = new LineageRelationInfo();
					lineageRelationInfo.setRelationId(UUID.randomUUID().toString());
					lineageRelationInfo.setQueryId(lineageQuery.getQueryId());
					lineageRelationInfo.setSrcNodeId(relation.getSource().getId());
					lineageRelationInfo.setTargetNodeId(relation.getDestination().getId());
					lineageRelationInfo.setDotString(relation.toDotString());
					lineageRelationInfos.addToList(lineageRelationInfo);
				}}
				else {
					LineageRelationInfo lineageRelationInfo = new LineageRelationInfo();
					lineageRelationInfo.setRelationId(UUID.randomUUID().toString());
					lineageRelationInfo.setQueryId(lineageQuery.getQueryId());
					lineageRelationInfo.setSrcNodeId(relation.getSource().getId());
					lineageRelationInfo.setTargetNodeId(relation.getDestination().getId());
					lineageRelationInfo.setDotString(relation.toDotString());
					lineageRelationInfos.addToList(lineageRelationInfo);
				}
		}
	}

}