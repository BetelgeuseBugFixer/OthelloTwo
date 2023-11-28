package ai;

import othello.Othello;
import othelloTrees.OthelloTree;

public class SimplestGrader implements BoardGrader{
    @Override
    public int gradeBoard(OthelloTree.OthelloNode node,boolean playerOne) {
        Othello board=node.getBoard();
        return Long.bitCount(board.blackPlayerDiscs)-Long.bitCount(board.whitePLayerDiscs);
    }
}
