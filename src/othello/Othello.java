package othello;

import szte.mi.Move;

import java.util.HashMap;

public class Othello {

    private long whitePLayerDiscs;
    private long blackPlayerDiscs;

    public Othello() {
        this.whitePLayerDiscs = 0x0000001008000000L;
        this.blackPlayerDiscs = 0x0000000810000000L;
    }

    public Othello(long blackPlayerDiscs,long whitePLayerDiscs){
        this.whitePLayerDiscs = blackPlayerDiscs;
        this.blackPlayerDiscs = whitePLayerDiscs;
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

    public MoveWithResult[] getPossibleMoves(boolean playerOne) {
        MoveWithResult[] possibleMoves = new MoveWithResult[60];
        int index=0;
        //iterate over every field
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                long discsToFlip = getPiecesToFlip(row, col, playerOne);
                if (discsToFlip != 0) {
                    Move move = new Move(col, row);
                    long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
                    long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;
                    Othello newBoard=new Othello(blackPlayerDiscs,whitePLayerDiscs);
                    possibleMoves[index]=new MoveWithResult(move,newBoard);
                    index++;
                }

            }
        }
        return possibleMoves;
    }

    public long getPiecesToFlip(int row, int col, boolean playerOne) {
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


                int curRow = row;
                int curCol = col;

                while (curRow > 0 && curRow < 7 && curCol > 0 && curCol < 7) {
                    curRow += rowDir;
                    curCol += colDir;
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

    public static class MoveWithResult {
        public Move move;
        public Othello board;

        public MoveWithResult(Move move, Othello board) {
            this.move = move;
            this.board=board;
        }
    }
}
