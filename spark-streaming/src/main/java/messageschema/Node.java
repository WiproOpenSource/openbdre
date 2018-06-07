package messageschema;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 7/18/17.
 */
public class Node {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Node.class);
    private String column;
    private String dataType;
    private List<Node> childNodes;
    private Node parent;

    public Node(){
        this.column = null;
        this.childNodes = new ArrayList<>();
        this.parent = null;
    }
    public Node(String column){
        this.column = column;
        this.childNodes = new ArrayList<Node>();
        this.parent = null;
    }

    public static Node addChild(Node parent, String column){
        Node node = new Node();
        node.setColumn(column);
        node.setParent(parent);
        parent.getChildNodes().add(node);
        return node;
    }

    public static void printTree(Node node) {

        for (Node each : node.getChildNodes()) {
            printTree(each);
        }
        if(!node.getColumn().equals("")){
            LOGGER.info("node.column = " + node.column);
        }
    }

    public boolean isLeaf() {
        if(this.childNodes.size() == 0)
            return true;
        else
            return false;
    }


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }


    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Node> childNodes) {
        this.childNodes = childNodes;
    }


}
