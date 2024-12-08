package ai.grader;

import othello.Othello;
import othelloTrees.OthelloTree;

import static ai.grader.BetterGrader.getFrontiersDiscDifference;

public class SimplerGrader implements BoardGrader {
	int frontiersDiscWeight = 2;

	@Override
	public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
		Othello board = node.getBoard();
		int score = getFrontiersDiscDifference(board) * frontiersDiscWeight;
		// parity
		score += 0;
		return score;
	}
}
