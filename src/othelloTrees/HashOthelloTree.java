package othelloTrees;

import othello.Othello;
import szte.mi.Move;

import java.util.HashMap;

public class HashOthelloTree implements OthelloTree {

    private HashOthelloNode root;
    public HashOthelloTree(){

    }
    @Override
    public OthelloNode getRoot() {
        return null;
    }

    @Override
    public void move(Move move) {

    }

    public static class HashOthelloNode implements OthelloNode {

        public HashMap<String,Othello> nextMoves;
        final private Othello board;

        public HashOthelloNode(Othello board){
            this.board=board;
            this.nextMoves=new HashMap<>();
        }

        @Override
        public void sortMoves() {

        }

        @Override
        public Othello getBoard() {
            return this.board;
        }

        @Override
        public Othello getNextBoard(Move move) {
            return null;
        }
    }
}
