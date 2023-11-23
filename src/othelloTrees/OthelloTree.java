package othelloTrees;

import ai.BoardGrader;
import othello.Othello;
import szte.mi.Move;

public interface OthelloTree {

    public void setRoot(OthelloNode node);
    public OthelloNode getRoot();

    public void move(Move move);

    abstract class  OthelloNode {
        private final boolean parentNodePassed;
        private final Othello board;
        public OthelloNode(Othello board, boolean parentNodePassed) {
            this.board=board;
            this.parentNodePassed =parentNodePassed;
        }

        public abstract OthelloNode getNextNode(Move move);



        //if isTerminalNode==0 -> it has not been calculated yet
        private Boolean isTerminalNode;

        public abstract boolean getIsTerminalNode();

        public Othello getBoard(){
            return this.board;
        }

        public abstract int getScore(BoardGrader grader);

        public abstract Move[] getNextMoves();

        public abstract OthelloNode[] getNextNodes();

        public abstract Othello getNextBoard(Move move);
    }
}
