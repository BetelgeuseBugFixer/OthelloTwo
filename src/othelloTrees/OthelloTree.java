package othelloTrees;

import ai.BoardGrader;
import othello.Othello;
import szte.mi.Move;

public interface OthelloTree {

    public void setRoot(OthelloNode node);

    public OthelloNode getRoot();

    public void move(int move,boolean playerOne);

    abstract class OthelloNode {
        private final int previousMove;
        private final Othello board;
        private boolean isTerminalNode;

        public OthelloNode(Othello board, int previousMove) {
            this.board = board;
            this.previousMove = previousMove;
        }

        public OthelloNode() {
            this.board = new Othello();
            this.previousMove = -1;
        }

        public Othello getBoard() {
            return this.board;
        }

        public int getPreviousMove() {
            return this.previousMove;
        }

        public abstract OthelloNode getNextNode(int move,boolean playerOne);

        public abstract boolean getIsTerminalNode(boolean playerOne);

        public abstract int getScore(BoardGrader grader, boolean playerOne);

        public abstract OthelloNode[] getNextNodes(boolean playerOne);
    }
}
