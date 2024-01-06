package ai.genetic.mcst;

import java.util.Random;

public class Node {
    State state;
    Node parent;
    Node[] childArray;


    public Node(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node[] getChildArray() {

        return childArray;
    }

    public void setChildArray(Node[] childArray) {
        this.childArray = childArray;
    }

    public Node getRandomChildNode(Random rnd) {
        return this.getChildArray()[rnd.nextInt(getChildArray().length)];
    }

    public double getWinRate() {
        return this.state.winScore / this.state.visitCount;
    }

    public Node getChildWithMaxScore() {
        Node bestNode = this.childArray[0];
        double bestScore = bestNode.getWinRate();
        for (int i = 1; i < this.childArray.length; i++) {
            double score = this.childArray[i].getWinRate();
            if (score > bestScore) {
                bestScore = score;
                bestNode = childArray[i];
            }
        }
        return bestNode;
    }

}
