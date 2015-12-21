package com.wipro.ats.bdre.lineage.entiity;

import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * Created by jayabroto on 29-04-2015.
 */
public abstract class Node {
	private static final Logger LOGGER = Logger.getLogger(Node.class);

	private String nodeType;
	private String id;
	private String label;
	private String shape;
	private String displayName;

	public Node(String label, String nodeType, String shape, String displayName) {
//		id = ++counter;
		id = UUID.randomUUID().toString();
		setNodeType(nodeType);
		setLabel(label);
		setShape(shape);
		setDisplayName(displayName);
	}

	public abstract String toDotString();

	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getNodeType() {return nodeType;}
	public void setNodeType(String nodeType) {this.nodeType = nodeType;}
	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	public String getShape() {return shape;}
	public void setShape(String shape) {this.shape = shape;}
	public String getDisplayName() {return displayName;}
	public void setDisplayName(String displayName) {this.displayName = displayName;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		if (nodeType != null ? !nodeType.equals(node.nodeType) : node.nodeType != null) return false;
		if (id != null ? !id.equals(node.id) : node.id != null) return false;
		return !(label != null ? !label.equals(node.label) : node.label != null);
	}

	@Override
	public int hashCode() {
		int result = nodeType != null ? nodeType.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (label != null ? label.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Node{" +
				"nodeType='" + nodeType + '\'' +
				", id=" + id +
				", label='" + label + '\'' +
				", shape='" + shape + '\'' +
				", displayName='" + displayName + '\'' +
				'}';
	}
}