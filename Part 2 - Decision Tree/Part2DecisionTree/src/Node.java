public class Node {
    private Node trueNode;
    private Node falseNode;
    private String attribute;
    private String className;
    private double probability;


    public Node getTrueNode() {
        return trueNode;
    }

    public void setTrueNode(Node trueNode) {
        this.trueNode = trueNode;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Node getFalseNode() {
        return falseNode;
    }

    public void setFalseNode(Node falseNode) {
        this.falseNode = falseNode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public Node(Node trueNode, Node falseNode, String attribute) {
        this.trueNode = trueNode;
        this.falseNode = falseNode;
        this.attribute = attribute;
    }

    public Node(String className, double probability){
        this.className=className;
        this.probability=probability;
    }

    public String toString(int level){
        String returnString = "";
        if(attribute != null){
            for(int i = 0; i<level; i++){
                returnString = returnString+ "       ";
            }
            returnString = returnString + "Separating Feature: " + this.attribute + "\n";
            for(int i = 0; i<level+1; i++){
                returnString = returnString+ "       ";
            }
            returnString = returnString + "true node: \n" + this.trueNode.toString(level+2) + "\n";
            for(int i = 0; i<level+1; i++){
                returnString = returnString+ "       ";
            }
            returnString = returnString + "false node: \n" + this.falseNode.toString(level+2) + "\n";
        }
        else{
            for(int i = 0; i<level; i++){
                returnString = returnString+ "       ";
            }
            returnString = returnString + "Most Likely Class: " + this.className + "\n";
            for(int i = 0; i<level; i++){
                returnString = returnString+ "       ";
            }
            returnString = returnString + "Probability: " + this.probability;
        }
        return returnString;
    }

}
