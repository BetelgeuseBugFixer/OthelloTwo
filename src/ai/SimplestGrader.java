package ai;

import othello.Othello;

public class SimplestGrader implements BoardGrader{
    @Override
    public int gradeBoard(Othello board) {
        return Long.bitCount(board.blackPlayerDiscs)-Long.bitCount(board.whitePLayerDiscs);
    }
}
