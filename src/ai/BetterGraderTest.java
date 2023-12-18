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
        assertEquals(grader.getEdgeScores(new Othello(test1,test2),true,true),17);

        test1 = 0x768080800000003cL;
        test2 = 0x10080818000L;
        assertEquals(grader.getEdgeScores(new Othello(test1,test2),true,true),10);

        test1 = 0xfa00008000000003L;
        test2 = 0x50101018181817cL;
        assertEquals(grader.getEdgeScores(new Othello(test1,test2),true,true),12);

        test1 = 0x7008001008000c3L;
        test2 = 0xb80000800101012cL;
        assertEquals(grader.getEdgeScores(new Othello(test1,test2),true,true),12);

        test1 = 4611968043168301073L;
        test2 = 2377900603268432170L;
        assertEquals(grader.getEdgeScores(new Othello(test1,test2),true,true),5);


    }
}