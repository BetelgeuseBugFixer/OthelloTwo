package ai;

import othello.Othello;
import othelloTrees.OthelloTree;

public interface BoardGrader {

    public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne);
}
