package othelloTrees;

import ai.BoardGrader;
import othello.Othello;

public class ArrayTree implements OthelloTree {
    public ArrayTree() {
        this.root = new ArrayNode();
    }

    ArrayNode root;

    @Override
    public void setRoot(OthelloNode node) {
        this.root = (ArrayNode) node;
    }

    @Override
    public OthelloNode getRoot() {
        return this.root;
    }

    @Override
    public void move(int move,boolean playerOne) {
        this.root = (ArrayNode) root.getNextNode(move,playerOne);
    }

    public static class ArrayNode extends OthelloNode {
        private ArrayNode[] nextNodes;
        private boolean isGraded;

        private int score;

        public ArrayNode() {
            super();
            isGraded = false;
        }

        public ArrayNode(Othello board, int previousMove) {
            super(board, previousMove);
            isGraded = false;
        }


        @Override
        public OthelloNode getNextNode(int move,boolean playerOne) {
            //non-optimal solution since data structure is optimized for other functions
            for (OthelloNode nextNode : this.getNextNodes(playerOne)) {
                if (nextNode==null){
                    break;
                }
                if (move==nextNode.getPreviousMove()){
                    return nextNode;
                }
            }
            return null;
        }

        @Override
        public boolean getIsTerminalNode(boolean playerOne) {
            if (this.getBoard().boardIsFull()) {
                return true;
            }
            return this.getPreviousMove() == -1 && this.hasToPass(playerOne);
        }

        private boolean hasToPass(boolean playerOne) {
            return this.getNextNodes(playerOne)[0].getPreviousMove() == -1;
        }

        @Override
        public int getScore(BoardGrader grader) {
            if (!this.isGraded) {
                this.score = grader.gradeBoard(this.getBoard());
                this.isGraded=true;
            }
            return this.score;
        }


        @Override
        public OthelloNode[] getNextNodes(boolean playerOne) {
            if (this.nextNodes == null) {
                this.nextNodes = this.getBoard().getPossibleMovesAsNodes(playerOne);
            }
            return this.nextNodes;
        }
    }
}
