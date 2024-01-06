package ai.genetic.mcst;

import othello.Othello;

public class Tree {


    public Tree(Othello board, boolean playerOne){
        this.root=new Node(new State(board,playerOne));
    }
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    Node root;
}
