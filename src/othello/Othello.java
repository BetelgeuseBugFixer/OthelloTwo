package othello;

import othelloTrees.ArrayTree;
import szte.mi.Move;

import java.util.HashMap;

public class Othello {

    public long whitePLayerDiscs;
    public long blackPlayerDiscs;

    public Othello() {
        this.whitePLayerDiscs = 0x0000001008000000L;
        this.blackPlayerDiscs = 0x0000000810000000L;
    }

    public Othello(long blackPlayerDiscs, long whitePLayerDiscs) {
        this.whitePLayerDiscs = whitePLayerDiscs;
        this.blackPlayerDiscs = blackPlayerDiscs;
    }

    public void makeMove(int move, boolean playerOne) {
        if (move == -1) {
            return;
        }

        long discsToFlip = getDiscsToFlip(move, playerOne);
        blackPlayerDiscs ^= discsToFlip;
        whitePLayerDiscs ^= discsToFlip;
        if (playerOne) {
            blackPlayerDiscs |= 1L << move;
        } else {
            whitePLayerDiscs |= 1L << move;
        }
    }

    public void makeMove(Move move, boolean playerOne) {
        if (move == null) {
            return;
        }
        makeMove(getIntFromMove(move), playerOne);
    }

    public static int getIntFromMove(Move move) {
        if (move==null){
            return -1;
        }
        return move.y * 8 + move.x;
    }

    public static Move getMoveFromInt(int move) {
        return new Move(move % 8, move / 8);
    }

    public MoveWithResult[] getPossibleMoves(boolean playerOne) {
        //according to
        //https://jxiv.jst.go.jp/index.php/jxiv/preprint/view/480
        //33 is the maximum number of moves in reachable positions
        MoveWithResult[] possibleMoves = new MoveWithResult[33];
        int index = 0;
        //iterate over every field
        for (int i = 0; i < 64; i++) {
            long discsToFlip = getDiscsToFlip(i, playerOne);
            if (discsToFlip != 0) {
                long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
                long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;
                //add new disc
                if (playerOne) {
                    newBlackDiscs |= 1L << i;
                } else {
                    newWhiteDiscs |= 1L << i;
                }
                Othello newBoard = new Othello(newBlackDiscs, newWhiteDiscs);
                possibleMoves[index] = new MoveWithResult(i, newBoard);
                index++;
            }

        }

        if (possibleMoves[0] == null) {
            Othello newBoard = new Othello(blackPlayerDiscs, whitePLayerDiscs);
            possibleMoves[0] = new MoveWithResult(-1, newBoard);
        }
        return possibleMoves;
    }

    public ArrayTree.ArrayNode[] getPossibleMovesAsNodes(boolean playerOne) {
        //TODO Alternative version here:
        // https://stackoverflow.com/questions/5944230/optimization-of-moves-calculation-in-othello-bitboard
        //according to
        //https://jxiv.jst.go.jp/index.php/jxiv/preprint/view/480
        //33 is the maximum number of moves in reachable positions
        ArrayTree.ArrayNode[] possibleMoves = new ArrayTree.ArrayNode[33];
        int index = 0;
        //iterate over every field
        for (int i = 0; i < 64; i++) {
            long discsToFlip = getDiscsToFlip(i, playerOne);
            if (discsToFlip != 0) {
                long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
                long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;
                //add new disc
                if (playerOne) {
                    newBlackDiscs |= 1L << i;
                } else {
                    newWhiteDiscs |= 1L << i;
                }
                Othello newBoard = new Othello(newBlackDiscs, newWhiteDiscs);
                possibleMoves[index] = new ArrayTree.ArrayNode(newBoard,i);
                index++;
            }

        }

        if (possibleMoves[0] == null) {
            Othello newBoard = new Othello(blackPlayerDiscs, whitePLayerDiscs);
            possibleMoves[0] = new ArrayTree.ArrayNode(newBoard,-1);
        }
        return possibleMoves;
    }

    public long getDiscsToFlip(int i, boolean playerOne) {
        //check if field is occupied
        long targetPosition = 1L << i;
        if ((targetPosition & blackPlayerDiscs) != 0 || (targetPosition & whitePLayerDiscs) != 0) {
            return 0;
        }

        long toFLip = 0L;

        long playerToBeFlipped = blackPlayerDiscs;
        long playerThatFlips = whitePLayerDiscs;
        if (playerOne) {
            playerToBeFlipped = whitePLayerDiscs;
            playerThatFlips = blackPlayerDiscs;
        }

        int[] directions = {-1, 0, 1};
        //iterate in all directions
        for (int rowDir : directions) {
            for (int colDir : directions) {
                //skip no direction
                if (rowDir == 0 && colDir == 0) {
                    continue;
                }
                long currentFlips = 0L;


                int curRow = (i / 8) + rowDir;
                int curCol = (i % 8) + colDir;

                while (curRow >= 0 && curRow <= 7 && curCol >= 0 && curCol <= 7) {

                    long currentPosition = 1L << (curRow * 8 + curCol);
                    if ((currentPosition & playerToBeFlipped) != 0) {
                        //position is added in to flip
                        currentFlips |= currentPosition;
                    } else {
                        if ((currentPosition & playerThatFlips) != 0) {
                            //row of opponents discs ends with own disc and the discs can be flipped
                            toFLip = toFLip | currentFlips;
                        }
                        break;
                    }
                    curRow += rowDir;
                    curCol += colDir;
                }

            }
        }
        return toFLip;
    }

    public int getDiscAtField(int row, int col) {
        int index = row * 8 + col;
        return getDiscAtField(index);
    }

    //returns 0 if there is no disc,1 if player Disc is black, 2 if player is white
    public int getDiscAtField(int index) {
        long position = 1L << index;
        if ((this.blackPlayerDiscs & position) != 0) {
            return 1;
        } else if ((this.whitePLayerDiscs & position) != 0) {
            return 2;
        } else {
            return 0;
        }
    }

    public int getDiscAtField(long bitmask){
        if ((this.blackPlayerDiscs&bitmask)!=0L){
            return 1;
        }else if ((this.whitePLayerDiscs&bitmask)!=0L){
            return 2;
        }else {
            return 0;
        }
    }

    public String getCurrentGameResult(){
        return "black:white "+this.blackPlayerDiscs+": "+this.whitePLayerDiscs;
    }

    @Override
    public String toString() {
        HashMap<Integer, String> intToDisc = new HashMap<>();
        intToDisc.put(0, " ");
        intToDisc.put(1, "X");
        intToDisc.put(2, "O");
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                sb.append("|");
                sb.append(intToDisc.get(getDiscAtField(row, col)));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    //helper method for debugging
    public static String singleLongToField(long board) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                long curPos = 1L << (row * 8 + col);
                if ((curPos & board) != 0) {
                    sb.append("|X");
                } else {
                    sb.append("| ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public static class MoveWithResult {
        public int move;
        public Othello board;

        public MoveWithResult(int move, Othello board) {
            this.move = move;
            this.board = board;
        }
    }

    public boolean boardIsFull(){
        return this.blackPlayerDiscs+this.whitePLayerDiscs==-1;
    }
}
