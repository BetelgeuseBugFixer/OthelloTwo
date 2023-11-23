package othello;

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

    public void makeMove(Move move, boolean playerOne) {
        if (move == null) {
            return;
        }
        int row = move.y;
        int col = move.x;
        long discsToFlip = getPiecesToFlip(row, col, playerOne);
        blackPlayerDiscs ^= discsToFlip;
        whitePLayerDiscs ^= discsToFlip;
        if (playerOne) {
            blackPlayerDiscs |= 1L << (row * 8 + col);
        } else {
            whitePLayerDiscs |= 1L << (row * 8 + col);
        }
    }

    public MoveWithResult[] getPossibleMoves(boolean playerOne) {
        //according to
        //https://jxiv.jst.go.jp/index.php/jxiv/preprint/view/480
        //33 is the maximum number of moves in reachable positions
        MoveWithResult[] possibleMoves = new MoveWithResult[33];
        int index = 0;
        //iterate over every field
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                long discsToFlip = getPiecesToFlip(row, col, playerOne);
                if (discsToFlip != 0) {
                    Move move = new Move(col, row);
                    long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
                    long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;
                    //add new disc
                    if (playerOne) {
                        newBlackDiscs |= 1L << (row * 8 + col);
                    } else {
                        newWhiteDiscs |= 1L << (row * 8 + col);
                    }
                    Othello newBoard = new Othello(newBlackDiscs, newWhiteDiscs);
                    possibleMoves[index] = new MoveWithResult(move, newBoard);
                    index++;
                }

            }
        }
        if (possibleMoves[0] == null) {
            Othello newBoard = new Othello(blackPlayerDiscs, whitePLayerDiscs);
            possibleMoves[0] = new MoveWithResult(null, newBoard);
        }
        return possibleMoves;
    }

    public long getPiecesToFlip(int row, int col, boolean playerOne) {
        //check if field is occupied
        long targetPosition = 1L << (row * 8 + col);
        if ((targetPosition&blackPlayerDiscs)!=0 || (targetPosition&whitePLayerDiscs)!=0){
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


                int curRow = row+rowDir;
                int curCol = col+colDir;

                while (curRow >= 0 && curRow <= 7 && curCol >= 0 && curCol <= 7) {

                    long currentPosition = 1L << (curRow * 8 + curCol);
                    if ((currentPosition & playerToBeFlipped) != 0) {
                        //position is added in to flip
                        currentFlips |= currentPosition;
                    } else {
                        if ((currentPosition & playerThatFlips) != 0) {
                            //row of opponents discs ends with own disc and the discs can be flipped
                            toFLip = toFLip | currentFlips;
                            break;
                        } else {
                            //row of opponents discs ends no disc and can't be flipped
                            break;
                        }
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
        public Move move;
        public Othello board;

        public MoveWithResult(Move move, Othello board) {
            this.move = move;
            this.board = board;
        }
    }
}
