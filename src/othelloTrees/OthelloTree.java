package othelloTrees;

import ai.BoardGrader;
import othello.Othello;

public interface OthelloTree {

    public void setRoot(OthelloNode node);

    public OthelloNode getRoot();

    public void move(int move,boolean playerOne);

    abstract class OthelloNode {
        private final int previousMove;
        private final Othello board;

        public abstract LastHopeNode getWinChances(boolean nextPlayer);


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

        public abstract int getScoreWithoutCalcCheck(BoardGrader grader, boolean playerOne);

        public abstract OthelloNode[] getNextNodes(boolean playerOne);

        public abstract boolean getIsFullyCalculated();

        public abstract void setToFullyCalculated();

        public abstract void setScore(int score);

        public abstract int getScoreWithoutCalcCheck();
    }

    public class LastHopeNode{
        int wins;
        int draws;
        int uncalculated;
        int loses;

        public int sumOfGames(){
            return wins+draws+uncalculated+loses;
        }

        public boolean isGreater(LastHopeNode other) {
            double thisAllGames=this.sumOfGames();
            double otherAllGames=other.sumOfGames();

            if (this.wins/thisAllGames > other.wins/otherAllGames){
                return true;
            } else if (this.wins/thisAllGames<other.wins/otherAllGames){
                return false;
            }
            if (this.draws/thisAllGames > other.draws/otherAllGames){
                return true;
            } else if (this.draws/thisAllGames<other.draws/otherAllGames){
                return false;
            }
            return thisAllGames > otherAllGames;
        }
    }
}
