package othelloTrees;

import ai.AaronFish;
import games.wthor.Header;
import othello.Othello;
import progressbar.Timer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OpeningLibraryCreator {
	static int nodesGraded = 0;
	final static String HEADER= """
			package othelloTrees;

			import othello.Othello;

			import java.util.Collections;
			import java.util.HashMap;
			import java.util.Map;

			public class OpeningLibraryMap {
			\tprotected static final Map<HashTree.OthelloState, Integer> openingLibrary;

			\tstatic {
			\t\tHashMap<HashTree.OthelloState, Integer> stateHashtable = new HashMap<>();""";
	final static String TAIL= """
			\t\topeningLibrary = Collections.unmodifiableMap(stateHashtable);
			\t}
			}""";

	public static void main(String[] args) throws IOException {
		Timer timer = new Timer();
		timer.startTimer();
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/othelloTrees/OpeningLibraryMap.java"));
		writer.write(HEADER);
		writer.write("\n");
		MirroredHashTree tree = new MirroredHashTree();
		int goalDepth = 5;
		int ratingDepth = 7;
		goDeeper((MirroredHashTree.MirrorNode) tree.getRoot(), 0, goalDepth, ratingDepth, true, writer);
		writer.write(TAIL);
		writer.close();
		timer.stopTimer();
		System.out.println();
		System.out.println(timer.getCurrentTimeInSeconds());
	}

	private static int goDeeper(MirroredHashTree.MirrorNode current, int currentDepth, int targetDepth, int ratingDepth, boolean playerOne, BufferedWriter writer) throws IOException {
		if (targetDepth == currentDepth) {
			// grade board
			AaronFish grader = new AaronFish();
			grader.initWithRoot(current, playerOne, new AaronFish.ConstantDepth(ratingDepth));
			int move = grader.gradeNodeAndReturnBestMove(ratingDepth);
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
					bestMove = current.getMoveNoMirror(i);
				}
			}
		} else {
			bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < current.getNextNodes(false).length; i++) {
				MirroredHashTree.MirrorNode nextNode = (MirroredHashTree.MirrorNode) current.getChildAt(i);
				int score = goDeeper(nextNode, currentDepth + 1, targetDepth, ratingDepth, true, writer);
				if (score < bestScore) {
					bestScore = score;
					bestMove = current.getMoveNoMirror(i);
				}
			}
		}
		writeToFile(writer, current.getBoard(), playerOne, bestMove, bestScore);
		return bestScore;
	}

	private static void writeToFile(BufferedWriter writer, Othello board, boolean playerOne, int move, int score) throws IOException {
		nodesGraded++;
		System.out.print("\rNodes Graded: " + nodesGraded);
		//here we make sure, that the first Node is also in the represent board, like all the other boards
		if (board.equals(new Othello())) {
			board = board.mirrorVertical();
			move=new MirroredHashTree.VerticalMirror().mirrorMove(move);
		}
		writer.write("\t\tstateHashtable.put(new HashTree.OthelloState(new Othello(" + board.blackPlayerDiscs + "L, " + board.whitePLayerDiscs + "L), " + playerOne + "), " + move + ");\n");
		// writer.write(board.blackPlayerDiscs + "," + board.whitePLayerDiscs + " " + playerOne + "->" + move + " " + score+"\n");
	}
}
