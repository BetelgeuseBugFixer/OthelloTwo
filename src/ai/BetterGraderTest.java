package ai;
import othello.Othello;
import static org.junit.jupiter.api.Assertions.*;

class BetterGraderTest {

    @org.junit.jupiter.api.Test
    void getEdgeScores() {
        BetterGrader grader = new BetterGrader();
        grader.setAllWeightsToOne();

        long test1 = 0x18080000080804aL;
        long test2 = 0x7e01010101010100L;
        assertEquals(17,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-17,grader.getEdgeScores(new Othello(test2,test1),false,true));

        test1 = 0x768080800000003cL;
        test2 = 0x10080818000L;
        assertEquals(10,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-10,grader.getEdgeScores(new Othello(test2,test1),false,true));


        test1 = 0xfa00008000000003L;
        test2 = 0x50101018181817cL;
        assertEquals(12,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-12,grader.getEdgeScores(new Othello(test2,test1),false,true));


        test1 = 0x7008001008000c3L;
        test2 = 0xb80000800101012cL;
        assertEquals(12,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-12,grader.getEdgeScores(new Othello(test2,test1),false,true));


        test1 = 4611968043168301073L;
        test2 = 2377900603268432170L;
        assertEquals(5,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-5,grader.getEdgeScores(new Othello(test2,test1),false,true));


        test1 = 0b1000000100000000100000000000000000000000000000000000000101100000L;
        test2 = 0b111111010000001000000010000000100000001000000010000000000011110L;
        assertEquals(-6,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(6,grader.getEdgeScores(new Othello(test2,test1),false,true));

        test1 = 0x50181818181002aL;
        test2 = 0x280000000008100L;
        assertEquals(12,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-12,grader.getEdgeScores(new Othello(test2,test1),false,true));

        test1 = 0x90181818181002aL;
        test2 = 0x680000000008100L;
        assertEquals(11,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-11,grader.getEdgeScores(new Othello(test2,test1),false,true));

        test1 = 0x7c0000018080007eL;
        test2 = 0x1008001010081L;
        assertEquals(10,grader.getEdgeScores(new Othello(test1,test2),true,true));
        assertEquals(-10,grader.getEdgeScores(new Othello(test2,test1),false,true));
    }
}