import ai.BetterGrader;
import ai.genetic.AiAgent;
import othello.Othello;

import static org.junit.jupiter.api.Assertions.*;

class AllTest {

    @org.junit.jupiter.api.Test
    void getEdgeScores() {
        BetterGrader grader = new BetterGrader();
        grader.setAllWeightsToOne();

        long test1 = 0x18080000080804aL;
        long test2 = 0x7e01010101010100L;
        assertEquals(17, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-17, grader.getEdgeScores(new Othello(test2, test1), true, true));

        test1 = 0x768080800000003cL;
        test2 = 0x10080818000L;
        assertEquals(10, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-10, grader.getEdgeScores(new Othello(test2, test1), true, true));


        test1 = 0xfa00008000000003L;
        test2 = 0x50101018181817cL;
        assertEquals(12, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-12, grader.getEdgeScores(new Othello(test2, test1), true, true));


        test1 = 0x7008001008000c3L;
        test2 = 0xb80000800101012cL;
        assertEquals(12, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-12, grader.getEdgeScores(new Othello(test2, test1), true, true));


        test1 = 4611968043168301073L;
        test2 = 2377900603268432170L;
        assertEquals(5, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-5, grader.getEdgeScores(new Othello(test2, test1), true, true));


        test1 = 0b1000000100000000100000000000000000000000000000000000000101100000L;
        test2 = 0b111111010000001000000010000000100000001000000010000000000011110L;
        assertEquals(-6, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(6, grader.getEdgeScores(new Othello(test2, test1), true, true));

        test1 = 0x50181818181002aL;
        test2 = 0x280000000008100L;
        assertEquals(12, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-12, grader.getEdgeScores(new Othello(test2, test1), true, true));

        test1 = 0x90181818181002aL;
        test2 = 0x680000000008100L;
        assertEquals(11, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-11, grader.getEdgeScores(new Othello(test2, test1), true, true));

        test1 = 0x7c0000018080007eL;
        test2 = 0x1008001010081L;
        assertEquals(10, grader.getEdgeScores(new Othello(test1, test2), false, true));
        assertEquals(-10, grader.getEdgeScores(new Othello(test2, test1), true, true));
    }

    @org.junit.jupiter.api.Test
    void getLegalMovesAsLong() {
        Othello[] exampleBoards = {new Othello(),
                new Othello(17181835264L, 240920821760L),
                new Othello(1966080L, 266690625536L),
                new Othello(17763717873664L, 9449064829960L),
                new Othello(136391250035716L, 2147215030328L),
                new Othello(1157427415334714372L, 3255537269534949560L)};
        for (Othello othello : exampleBoards) {
            long moves = othello.getLegalMovesAsLong(true);
            Othello.MoveWithResult[] expectedMoves = othello.getPossibleMoves(true);
            long legal = 0L;
            for (Othello.MoveWithResult expectedMove : expectedMoves) {
                if (expectedMove == null) {
                    break;
                }
                legal |= 1L << expectedMove.move;
            }
            assertEquals(legal, moves);
        }
    }

    @org.junit.jupiter.api.Test
    void getParityScore() {
        BetterGrader grader = new BetterGrader();
        grader.setAllWeightsToOne();

        Othello[] boards = {new Othello(),
                new Othello(0x21021428003000L, 0x7f9efdebd7ff0d7cL),
                new Othello(0x21031529013101L, 0x7f9efcead6fe0c7cL),
                new Othello(0x21031529010101L, 0x7f9efcead6fe7c7cL),
                new Othello(0x2103172b030301L, 0x7f9efce8d4fc7c7cL),
                new Othello(0x103172b030301L, 0x7ffefce8d4fc7c7cL),
                new Othello(0x804123172b030301L, 0x7fbedce8d4fc7c7cL),
                new Othello(0x3e302343474ff100L, 0xedcbcb8b00cfcL),
                new Othello(0x3e3021414549f100L, 0xedebebab60efcL),
                new Othello(0x3f3225495569f100L, 0xcdab6aa960efcL),
                new Othello(0x3f3025495569f100L, 0xfdab6aa960efcL),
                new Othello(0x3e000343474ff100L, 0x7efcbcb8b00cfcL),
                new Othello(0x3e000343474ff300L, 0x7efcbcb8b000fcL),
                new Othello(0x3e000343474bf100L, 0x7efcbcb8b402fdL),
                new Othello(0x3e000343474bff02L, 0x7efcbcb8b400fdL),
                new Othello(0x3e000242464afe02L, 0x7ffdbdb9b501fdL),
                new Othello(0x3f02064a566afe02L, 0x7df9b5a99501fdL),
                new Othello(0x3f02064a566afe02L, 0x7df9b5a99501fdL),
                new Othello(0x3e60c04040c83f1cL, 0x11f3fbfbf370002L),
                new Othello(0x3e60c04040c83f00L, 0x11f3fbfbf37003eL),
                new Othello(0x3e60c04040c83f00L, 0x11f3fbfbf37003eL)};
        int[] expectedScores = {-1, -2, -1, -1, -1, -2, -1, -1, -1, -1, -1, -2, -2, -2, -2, -2, 1, 1, -2, 2, 2};
        boolean[] playerOneDidLastMoveArray = {false, false, true, false, true, false, true, true, false, true, false,
                false, true, false, true, false, true, true, true, false, true};

        boolean[] startWeights = {true, false};
        for (boolean startWeight : startWeights) {
        for (int i = 0; i < boards.length; i++) {
            Othello board = boards[i];
            int expectedScore = expectedScores[i];
            boolean playerOneDidLastMove = playerOneDidLastMoveArray[i];
            long possibleMoves = board.getLegalMovesAsLong(!playerOneDidLastMove);

            int score = grader.getParityScore(board, playerOneDidLastMove, startWeight, possibleMoves);
            if (expectedScore != score) {
                System.out.println("Bug at example " + i + ("(normal direction)"));
                System.out.println(board);
                score = grader.getParityScore(board, playerOneDidLastMove, startWeight, possibleMoves);

            }
            assertEquals(expectedScore, score);

            //reverse
            Othello reverseBoard = new Othello(board.whitePLayerDiscs, board.blackPlayerDiscs);
            int reverseExpectedScore = expectedScore * -1;
            boolean reverseLastPlayer = !playerOneDidLastMove;
            long reversePossibleMoves = reverseBoard.getLegalMovesAsLong(playerOneDidLastMove);


            int reverseScore = grader.getParityScore(reverseBoard, reverseLastPlayer, startWeight, reversePossibleMoves);
            if (reverseExpectedScore != reverseScore) {
                System.out.println("Bug at example " + i + "(reverse)");
                System.out.println(reverseBoard);
                reverseScore = grader.getParityScore(reverseBoard, reverseLastPlayer, startWeight, reversePossibleMoves);

            }
            assertEquals(reverseExpectedScore, reverseScore);

        }
    }
    }

    @org.junit.jupiter.api.Test
    void getSplitArraySum(){
        int[] testToSum={1,35,2,5,6,7,7,6,2};
        int[] endpoints={3,6,testToSum.length};

        int[] result= AiAgent.getAbsolutSplitArraySum(endpoints,testToSum);
        int[] expected={38,18,15};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i],result[i]);
        }

    }
}
