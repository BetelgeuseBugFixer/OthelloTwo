package othelloTrees;

import ai.BoardGrader;
import othello.Othello;
import szte.mi.Move;

import java.util.HashMap;

public class HashOthelloTree implements OthelloTree {

    private HashOthelloNode root;
    public HashOthelloTree(){

    }

    @Override
    public void setRoot(OthelloNode node) {

    }

    @Override
    public OthelloNode getRoot() {
        return null;
    }

    @Override
    public void move(Move move) {

    }

    public static class HashOthelloNode extends OthelloNode {

        public HashMap<String,Othello> nextMoves;

        public HashOthelloNode(Othello board, boolean parentNodePassed){
            super(board,parentNodePassed);
            this.nextMoves=new HashMap<>();
        }

        @Override
        public OthelloNode getNextNode(Move move) {
            return null;
        }


        @Override
        public boolean getIsTerminalNode() {
            return false;
        }


        @Override
        public int getScore(BoardGrader grader) {
            return 0;
        }

        @Override
        public Move[] getNextMoves() {

            return null;
        }

        @Override
        public OthelloNode[] getNextNodes() {
            return new OthelloNode[0];
        }

        @Override
        public Othello getNextBoard(Move move) {
            return null;
        }
    }
}
