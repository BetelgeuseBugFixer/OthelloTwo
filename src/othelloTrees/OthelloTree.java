package othelloTrees;

import othello.Othello;
import szte.mi.Move;

public interface OthelloTree {


    public OthelloNode getRoot();

    public void move(Move move);

    interface  OthelloNode {

        public void sortMoves();
        public Othello getBoard();

        public Othello getNextBoard(Move move);
    }
}
