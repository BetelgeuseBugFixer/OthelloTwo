package othelloTrees;

import ai.AaronFish;
import othello.Othello;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class OpeningLibraryCreator {

	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/othelloTrees/Lib.txt"));
		MirroredHashTree tree = new MirroredHashTree();
		int goalDepth = 2;
		int ratingDepth = 2;
		goDeeper((MirroredHashTree.MirrorNode) tree.getRoot(), 0, goalDepth, ratingDepth, true, writer);
		writer.close();
	}

	private static int goDeeper(MirroredHashTree.MirrorNode current, int currentDepth, int targetDepth, int ratingDepth, boolean playerOne, BufferedWriter writer) throws IOException {
		if (targetDepth == currentDepth) {
			// grade board
			AaronFish grader = new AaronFish();
			grader.initWithRoot(current, playerOne, new AaronFish.ConstantDepth(ratingDepth));
			int move = Othello.getIntFromMove(grader.nextMove(null, 0, 0));
			int score = current.getScoreWithoutCalcCheck();
			writeToFile(writer, current.getBoard(), playerOne, move, score);
			return score;
		}
		int bestScore;
		int bestMove = -1;
		if (playerOne) {
			bestScore = Integer.MIN_VALUE;
			for (int i = 0; i < current.getNextNodes(true).length; i++) {
				MirroredHashTree.MirrorNode nextNode = (MirroredHashTree.MirrorNode) current.getChildAt(i);
				int score = goDeeper(nextNode, currentDepth + 1, targetDepth, ratingDepth, false, writer);
				if (score > bestScore) {
					bestScore = score;
					bestMove = nextNode.getMoveNoMirror(i);
				}
			}

		} else {
			bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < current.getNextNodes(false).length; i++) {
				MirroredHashTree.MirrorNode nextNode = (MirroredHashTree.MirrorNode) current.getChildAt(i);
				int score = goDeeper(nextNode, currentDepth + 1, targetDepth, ratingDepth, true, writer);
				if (score < bestScore) {
					bestScore = score;
					bestMove = nextNode.getMoveNoMirror(i);
				}
			}
		}
		writeToFile(writer, current.getBoard(), playerOne, bestMove, bestScore);
		return bestScore;
	}

	private static void writeToFile(BufferedWriter writer, Othello board, boolean playerOne, int move, int score) throws IOException {
		writer.write(board.blackPlayerDiscs + "," + board.whitePLayerDiscs + " " + playerOne + "->" + move + " " + score+"\n");
	}
}
