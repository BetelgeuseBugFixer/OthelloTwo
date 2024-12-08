package ai;

import ai.genetic.mcst.MonteCarloBoardGrader;
import ai.grader.BetterGrader;
import ai.grader.BoardGrader;
import ai.grader.SimplestGrader;
import ai.grader.TestNNGrader;
import othello.Othello;
import othelloTrees.MirroredHashTree;
import othelloTrees.OpeningLibraryWrapper;
import othelloTrees.OthelloTree;
import othelloTrees.OthelloTree.OthelloNode;
import szte.mi.Move;

import java.util.Random;

public class AaronFish implements szte.mi.Player {
	int currentCallId;
	BoardGrader boardGrader;
	boolean playerOne;
	OthelloTree boardTree;
	int currentMove;
	DepthGoalCalculator depthGoalCalculator;
	boolean inOpeningLibrary;
	Othello openingBoard;
	boolean playedWithSameBoardTree;

	public AaronFish() {
		this.depthGoalCalculator = new changingDepth();
		this.boardGrader = new BetterGrader();
		this.currentCallId = 0;
		this.boardTree = new MirroredHashTree();
		this.playedWithSameBoardTree = false;
	}

	public void setRoot(OthelloNode newRoot) {
		this.boardTree.setRoot(newRoot);
	}

	public void setBoardGrader(BoardGrader boardGrader) {
		this.boardGrader = boardGrader;
	}

	@Override
	public void init(int order, long t, Random rnd) {
		this.currentCallId = 0;
		openingBoard = new Othello();
		if (order == 0) {
			this.playerOne = true;
			currentMove = -1;
		} else {
			this.playerOne = false;
			currentMove = 0;
		}
		inOpeningLibrary = true;
	}

	public void initWithRoot(OthelloNode root, boolean playerOne, DepthGoalCalculator depthGoalCalculator) {
		// NOTE: currentCall may never be init with 0
		this.currentCallId = 1;
		this.boardTree = new MirroredHashTree();
		this.boardTree.setRoot(root);
		this.playerOne = playerOne;
		this.currentMove = -1;
		this.setDepthGoalCalculator(depthGoalCalculator);
	}

	public void setToMonteCarloGrader() {
		this.setDepthGoalCalculator(new MonteCarloDepth());
		this.setBoardGrader(new MonteCarloBoardGrader());
	}

	public void setToMonteCarloGrader(int numberOfGames, boolean shallow) {
		if (shallow) {
			this.setDepthGoalCalculator(new MonteCarloDepth());
		} else {
			this.setDepthGoalCalculator(new ShallowMonteCarloDepth());
		}
		this.setBoardGrader(new MonteCarloBoardGrader(numberOfGames));
	}


	public void setDepthGoalCalculator(DepthGoalCalculator newDepthGoalCalculator) {
		this.depthGoalCalculator = newDepthGoalCalculator;
	}

	public void setDepthGoalCalculatorToRandom() {
		setDepthGoalCalculator(new RandomDepth());
	}

	@Override
	public Move nextMove(Move prevMove, long tOpponent, long t) {
		this.currentMove += 2;
		if (inOpeningLibrary) {
			if (currentMove != 1) {
				this.openingBoard.makeMove(prevMove, !this.playerOne);
			}
			Integer move = OpeningLibraryWrapper.getMoveFromOpeningLibrary(this.openingBoard, playerOne);
			if (move != null) {
				openingBoard.makeMove(move, playerOne);
				return Othello.getMoveFromInt(move);
			}
			inOpeningLibrary = false;
			// this.boardTree = new MirroredHashTree(openingBoard);
			this.boardTree.setRoot(openingBoard);
		} else {
			if (currentMove != 1) {
				this.boardTree.move(Othello.getIntFromMove(prevMove), !this.playerOne);
			}
		}
		int remainingSpaces = this.boardTree.getRoot().getBoard().getRemainingSpaces();
		int goalDepth = depthGoalCalculator.getGoalDepth(t, remainingSpaces);
		this.currentCallId = currentMove + goalDepth - 1;
		int bestMove = calculateNextMove(goalDepth);
		this.boardTree.move(bestMove, playerOne);
		return Othello.getMoveFromInt(bestMove);
	}

	public int calculateNextMove(int depth) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		OthelloNode root = this.boardTree.getRoot();

		if (root.getNextNodes(playerOne).length == 1) {
			return root.getMoveAt(0);
		}
		int bestMoveIndex = -1;
		if (playerOne) {
			int bestScore = Integer.MIN_VALUE;
			for (int i = 0; i < root.getNextNodes(true).length; i++) {
				OthelloNode node = root.getChildAt(i);
				int score = minValue(node, depth - 1, alpha, beta);
				if (score > bestScore) {
					bestScore = score;
					bestMoveIndex = i;
					if (bestScore == Integer.MAX_VALUE) {
						break;
					}
				}
				if (bestScore > alpha) {
					alpha = bestScore;
				}
			}
			if (bestScore == Integer.MIN_VALUE) {
				bestMoveIndex = getMoveIndexWithHighestWinProbability();
			}
		} else {
			int bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < root.getNextNodes(false).length; i++) {
				OthelloNode node = root.getChildAt(i);
				int score = maxValue(node, depth - 1, alpha, beta);
				if (score < bestScore) {
					bestScore = score;
					bestMoveIndex = i;
					if (bestScore == Integer.MIN_VALUE) {
						break;
					}
				}
				if (bestScore < beta) {
					beta = bestScore;
				}
			}
			if (beta == Integer.MAX_VALUE) {
				bestMoveIndex = getMoveIndexWithHighestWinProbability();
			}
		}
		return root.getMoveAt(bestMoveIndex);
	}

	public int gradeNodeAndReturnBestMove(int depth) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		OthelloNode root = this.boardTree.getRoot();

		int bestMove = -1;
		if (playerOne) {
			for (int i = 0; i < root.getNextNodes(true).length; i++) {
				OthelloNode node = root.getChildAt(i);
				int score = minValue(node, depth - 1, alpha, beta);
				if (score > alpha) {
					alpha = score;
					bestMove = root.getMoveAt(i);
					if (score == Integer.MAX_VALUE) {
						break;
					}
				}
			}
			root.setScore(alpha, currentCallId, true);
		} else {
			for (int i = 0; i < root.getNextNodes(false).length; i++) {
				OthelloNode node = root.getChildAt(i);
				int score = maxValue(node, depth - 1, alpha, beta);
				if (score < beta) {
					beta = score;
					bestMove = root.getMoveAt(i);
					if (score == Integer.MIN_VALUE) {
						break;
					}
				}
			}
			root.setScore(beta, this.currentCallId, false);
		}
		return bestMove;
	}

	public int maxValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
		if (node.getIsTerminalNode(true) || depth == 0 || node.noFurtherCalculationNeeded(this.currentCallId)) {
			return node.getScoreWithoutCalcCheck(this.boardGrader, true);
		}

		int bestScore = Integer.MIN_VALUE;
		for (OthelloNode nextNode : node.getNextNodes(true)) {
			int score = minValue(nextNode, depth - 1, alpha, beta);
			if (score > bestScore) {
				bestScore = score;
				alpha = Math.max(bestScore, alpha);
				if (beta <= alpha) {
					break;
				}
			}
		}
		if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
			node.setToFullyCalculated();
		}
		node.setScore(bestScore, currentCallId, true);
		return bestScore;
	}

	public int minValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
		if (node.getIsTerminalNode(false) || depth == 0 || node.noFurtherCalculationNeeded(this.currentCallId)) {
			return node.getScoreWithoutCalcCheck(this.boardGrader, false);
		}

		int bestScore = Integer.MAX_VALUE;
		for (OthelloNode nextNode : node.getNextNodes(false)) {
			int score = maxValue(nextNode, depth - 1, alpha, beta);
			if (score < bestScore) {
				bestScore = score;
				beta = Math.min(bestScore, beta);
				if (beta <= alpha) {
					break;
				}
			}
		}
		if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
			node.setToFullyCalculated();
		}
		node.setScore(bestScore, currentCallId, false);
		return bestScore;
	}

	public int getMoveIndexWithHighestWinProbability() {
		// OthelloNode root = this.boardTree.getRoot();
		OthelloNode[] children = this.boardTree.getRoot().getNextNodes(this.playerOne);

		int bestMoveIndex = 0;
		OthelloTree.LastHopeNode bestScore = children[0].getWinChances(!this.playerOne);
		if (playerOne) {
			for (int i = 1; i < children.length; i++) {
				OthelloNode currentChild = children[i];
				OthelloTree.LastHopeNode current = currentChild.getWinChances(!this.playerOne);
				if (current.isGreater(bestScore)) {
					bestMoveIndex = i;
					bestScore = current;
				}

			}
		} else {
			for (int i = 1; i < children.length; i++) {
				OthelloNode currentChild = children[i];
				OthelloTree.LastHopeNode current = currentChild.getWinChances(!this.playerOne);
				if (!bestScore.isGreater(current)) {
					bestMoveIndex = i;
					bestScore = current;
				}
			}
		}
		return bestMoveIndex;
	}

	public interface DepthGoalCalculator {
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces);
	}

	public static class ConstantDepth implements DepthGoalCalculator {
		int depth;

		public ConstantDepth() {
			int depth = 2;
		}

		public ConstantDepth(int depth) {
			this.depth = depth;
		}

		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			return depth;
		}
	}

	private static class RandomDepth implements DepthGoalCalculator {
		Random rnd = new Random();

		@Override
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			if (remainingEmptySpaces < 6) {
				return 10;
			} else {
				return rnd.nextInt(2, 4);
			}
		}
	}

	public static class changingDepth implements DepthGoalCalculator {
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			if (remainingEmptySpaces < 13) {
				return 25;
			} else {
				return 7;
			}
		}
	}

	public static class MonteCarloDepth implements DepthGoalCalculator {

		@Override
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			if (remainingEmptySpaces < 13) {
				return 25;
			} else {
				return 1;
			}
		}
	}

	public static class ShallowMonteCarloDepth implements DepthGoalCalculator {

		@Override
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			if (remainingEmptySpaces < 6) {
				return 25;
			} else {
				return 1;
			}
		}
	}


}
