import oldOthello.OldOthello;
import othello.Othello;
import progressbar.Progressbar;
import szte.mi.Move;

import java.util.ArrayList;
import java.util.HashSet;

public class Test {

    private static long[] testGameWithPlus() {
        Othello othello = new Othello();
        int roundsPassed = 0;
        long current = 0;
        long[] decoy = new long[2];

        boolean playerOne = false;
        for (int i = 0; i <= 70; i++) {
            playerOne = !playerOne;
            Othello.MoveWithResult next = othello.getPossibleMoves(playerOne)[0];
            if (next.move == null) {
                //System.out.println("passed");
                roundsPassed++;
                if (roundsPassed == 2) {
                    //System.out.println("game is over before all Stones are set");
                    break;
                }
            } else {
                othello = next.board;
                current = othello.blackPlayerDiscs + othello.whitePLayerDiscs;
                roundsPassed = 0;

                decoy[i % 2] = current;

                //System.out.println(othello);
                //System.out.println(othello.blackPlayerDiscs+othello.whitePLayerDiscs);

            }
        }
        //System.out.println("End of test");
        //System.out.println("disc set:" +discsSet);
        //System.out.println(othello.blackPlayerDiscs+", "+othello.whitePLayerDiscs);
        //System.out.println(othello);
        return decoy;
    }

    private static long[] testGameWitBit() {
        Othello othello = new Othello();
        int roundsPassed = 0;
        long current = 0;
        long[] decoy = new long[2];

        boolean playerOne = false;
        for (int i = 0; i <= 70; i++) {
            playerOne = !playerOne;
            Othello.MoveWithResult next = othello.getPossibleMoves(playerOne)[0];
            if (next.move == null) {
                //System.out.println("passed");
                roundsPassed++;
                if (roundsPassed == 2) {
                    //System.out.println("game is over before all Stones are set");
                    break;
                }
            } else {
                othello = next.board;
                current = othello.blackPlayerDiscs | othello.whitePLayerDiscs;
                roundsPassed = 0;

                decoy[i % 2] = current;

                //System.out.println(othello);
                //System.out.println(othello.blackPlayerDiscs+othello.whitePLayerDiscs);

            }
        }
        //System.out.println("End of test");
        //System.out.println("disc set:" +discsSet);
        //System.out.println(othello.blackPlayerDiscs+", "+othello.whitePLayerDiscs);
        //System.out.println(othello);
        return decoy;
    }

    public static void playOldOthelloGame() {
        OldOthello game = new OldOthello();
        int player = 1;
        for (int i = 0; i < 60; i++) {
            ArrayList<Move> curMoves = game.allMovesMoves(player);
            Move move = null;
            if (curMoves.size() > 0) {
                move = curMoves.get(0);

            }
            game.move(move);
            player = game.otherPlayer(player);
        }

    }

    public static void newOthelloGame() {
        Othello othello = new Othello();
        boolean playerOne = true;
        for (int i = 0; i < 60; i++) {
            Othello.MoveWithResult[] nextPos = othello.getPossibleMoves(playerOne);
            Othello.MoveWithResult next = nextPos[0];
            othello = next.board;
            playerOne = !playerOne;
        }
    }


    public static void main(String[] args) {
        int[] orderedList=getOrderedFields();
        int n = 100_000;

        Progressbar test=new Progressbar("testing iterations",n);
        for (int i = 0; i < n; i++) {
            for (int field:orderedList) {
                int j=field*2;
            }
            test.countUp();
        }


    }

    public static boolean compareMoves(Move a, Move b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.x == b.x && a.y == b.y;
    }

    public static int[] getOrderedFields(){
        //corners
        int[] orderedFields=new int[60];
        int index=0;
        //add corners
        int[] corners={0,7,56,63};
        for (; index < corners.length; index++) {
            orderedFields[index]=corners[index];
        }
        //add edge center discs
        int[] edgeCenter={3,4,24,32,31,39,59,60};
        for (int j : edgeCenter) {
            orderedFields[index] = j;
            index++;
        }
        //add outer edge discs
        int[] edgeOuter={2,5,16,23,40,47,58,61};
        for (int j : edgeOuter) {
            orderedFields[index] = j;
            index++;
        }
        //add center discs
        for (int row = 2; row < 6; row++) {
            for (int col = 2; col < 6; col++) {
                int field=row*8+col;
                //ignore the center 4
                if (field==27||field==28||field==35||field==36){
                    continue;
                }
                orderedFields[index]=field;
                index++;
            }
        }

        //ad discs next to edge
        int[] discsNextToEdge={10,11,12,13,17,22,25,30,33,38,41,46,50,51,52,53};
        for (int j : discsNextToEdge) {
            orderedFields[index] = j;
            index++;
        }
        //ad discs next to corner on edge
        int[] discsNextToCornerOnEdge={1,6,8,15,48,55,57,62};
        for (int j : discsNextToCornerOnEdge) {
            orderedFields[index] = j;
            index++;
        }
        //ad danger discs
        int[] dangerDiscs={9,14,49,54};
        for (int j : dangerDiscs) {
            orderedFields[index] = j;
            index++;
        }

        //test
        /*
        for (int i = 0; i < 64; i++) {
            boolean found=false;

            for (int orderedField : orderedFields) {
                if (orderedField==i){
                    found=true;
                    break;
                }
            }
            if (!found){
                System.out.println("missing number: "+i);
            }
        }

        HashSet<Integer> test=new HashSet<>();
        for (int orderedField : orderedFields) {
            test.add(orderedField);
        }

        if (test.size()!= orderedFields.length){
            System.out.println("wrong length");
        }
         */

        return orderedFields;
    }
}
