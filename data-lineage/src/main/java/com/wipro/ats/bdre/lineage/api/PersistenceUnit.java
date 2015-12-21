package com.wipro.ats.bdre.lineage.api;

import com.wipro.ats.bdre.lineage.entiity.*;
import com.wipro.ats.bdre.lineage.type.UniqueList;
import com.wipro.ats.bdre.md.api.Lineage;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.LineageNodeInfo;
import com.wipro.ats.bdre.md.beans.LineageNodeTypeEnumInfo;
import com.wipro.ats.bdre.md.beans.LineageQueryInfo;
import com.wipro.ats.bdre.md.beans.LineageRelationInfo;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Created by jayabroto on 20-05-2015.
 */
public class PersistenceUnit extends MetadataAPIBase {
	private static final Logger LOGGER = Logger.getLogger(PersistenceUnit.class);

//	private List<LineageNodeInfo> LineageNodeInfos = new UniqueList<LineageNodeInfo>();
	private UniqueList<LineageNodeInfo> lineageTableNodes = new UniqueList<LineageNodeInfo>();
	private UniqueList<LineageNodeInfo> lineageColumnNodes = new UniqueList<LineageNodeInfo>();
	private List<LineageNodeInfo> lineageFunctionNodes = new UniqueList<LineageNodeInfo>();
	private LineageQueryInfo lineageQueryInfo = null;
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
	public LineageQueryInfo getLineageQueryInfo() {
		return lineageQueryInfo;
	}
	public UniqueList<LineageRelationInfo> getLineageRelationInfos() {
		return lineageRelationInfos;
	}

//	public static void main(String[] args) {
//		System.out.println("*****Start*****");
//		PersistenceUnit persist = new PersistenceUnit();
//		persist.persist();
//	}

	@Override
	public Object execute(String[] params) {

		return null;
	}

	public void persist() {
		Lineage lineage = new Lineage();

//		LineageQueryInfo LineageQueryInfo = new LineageQueryInfo();
//		LineageQueryInfo.setProcessId(122);
//		LineageQueryInfo.setQueryId(UUID.randomUUID().toString());
//		LineageQueryInfo.setQueryTypeId(1);
//		LineageQueryInfo.setQueryString("select * from me");
//		LineageQueryInfo.setInstanceExecId((long)1);
//		LineageQueryInfo.setCreateTs(new Date());
//		s.insert("call_procedures.InsertLineageQuery", lineageQueryInfo);
		lineage.insertLineageQuery(lineageQueryInfo);


		for (LineageNodeInfo lineageTableNode : lineageTableNodes) {
//			s.insert("call_procedures.InsertLineageNode", lineageTableNode);
			lineage.insertLineageNode(lineageTableNode);
		}
		for (LineageNodeInfo lineageColumnNode : lineageColumnNodes) {
//			s.insert("call_procedures.InsertLineageNode", lineageColumnNode);
			lineage.insertLineageNode(lineageColumnNode);
		}
		for (LineageNodeInfo lineageFunctionNode : lineageFunctionNodes) {
//			s.insert("call_procedures.InsertLineageNode", lineageFunctionNode);
			lineage.insertLineageNode(lineageFunctionNode);
		}
		for (LineageNodeInfo lineageConstantNode : lineageConstantNodes) {
//			s.insert("call_procedures.InsertLineageNode", lineageConstantNode);
			lineage.insertLineageNode(lineageConstantNode);
		}
		for (LineageRelationInfo LineageRelationInfo : lineageRelationInfos) {
//			s.insert("call_procedures.InsertLineageRelation", LineageRelationInfo);
			lineage.insertLineageRelation(LineageRelationInfo);

		}

		System.out.println("All nodes and relations are persisted in MySql db");
	}

	/**
	 * Populate data beans from final node lists
	 */
	public void populateBeans(UniqueList<Table> finalInTableNodes, UniqueList<Table> finalOutTableNodes, List<Function> finalFunctions,UniqueList<Constant> finalConstants, List<Relation> finalRelations, String query, int processId,long instanceId) {
		lineageTableNodes = new UniqueList<LineageNodeInfo>();
		lineageColumnNodes = new UniqueList<LineageNodeInfo>();
		lineageFunctionNodes = new UniqueList<LineageNodeInfo>();
		lineageConstantNodes = new UniqueList<LineageNodeInfo>();
		lineageQueryInfo = null;
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
		// one query
		lineageQueryInfo = new LineageQueryInfo();
		lineageQueryInfo.setQueryId(UUID.randomUUID().toString());
		lineageQueryInfo.setQueryString(query);
		lineageQueryInfo.setQueryTypeId(1);
		lineageQueryInfo.setProcessId(processId);
		lineageQueryInfo.setInstanceExecId(instanceId);

		for (Relation relation : finalRelations) {
			if(relation.getSource() instanceof Column && relation.getDestination() instanceof Column){
				if (!(((Column) relation.getSource()).getTable().getTableName().equals(((Column) relation.getDestination()).getTable().getTableName()) && ((Column) relation.getSource()).getColumnName().equals(((Column) relation.getDestination()).getColumnName()))){
					System.out.println("source and destination are not same columns = " + relation);
					LineageRelationInfo lineageRelationInfo = new LineageRelationInfo();
					lineageRelationInfo.setRelationId(UUID.randomUUID().toString());
					lineageRelationInfo.setQueryId(lineageQueryInfo.getQueryId());
					lineageRelationInfo.setSrcNodeId(relation.getSource().getId());
					lineageRelationInfo.setTargetNodeId(relation.getDestination().getId());
					lineageRelationInfo.setDotString(relation.toDotString());
					lineageRelationInfos.addToList(lineageRelationInfo);
				}}
				else {
					LineageRelationInfo lineageRelationInfo = new LineageRelationInfo();
					lineageRelationInfo.setRelationId(UUID.randomUUID().toString());
					lineageRelationInfo.setQueryId(lineageQueryInfo.getQueryId());
					lineageRelationInfo.setSrcNodeId(relation.getSource().getId());
					lineageRelationInfo.setTargetNodeId(relation.getDestination().getId());
					lineageRelationInfo.setDotString(relation.toDotString());
					lineageRelationInfos.addToList(lineageRelationInfo);
				}
		}
	}


	// populate lineage beans from data stored in DB
//	public void populateBeansFromDB(String queryId) {
//		LineageQueryInfo = null;
//		LineageRelationInfos = new UniqueList<LineageRelationInfo>();
//		lineageTableNodes = new UniqueList<LineageNodeInfo>();
//		lineageColumnNodes = new UniqueList<LineageNodeInfo>();
//		lineageFunctionNodes = new UniqueList<LineageNodeInfo>();
//
//		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory(null);
//		SqlSession s = sqlSessionFactory.openSession();
//
//		LineageQueryInfo = s.selectOne("call_procedures.GetLineageQueryInfo", queryId);
//
//		LineageRelationInfos = s.selectList("call_procedures.ListLineageRelationInfoByQueryId", queryId);
//
//
//	}
}