package ai;

import ai.genetic.mcst.MonteCarloBoardGrader;
import othello.Othello;
import othelloTrees.ArrayTree;
import othelloTrees.HashTree;
import othelloTrees.MirroredHashTree;
import othelloTrees.OthelloTree;
import othelloTrees.OthelloTree.OthelloNode;
import szte.mi.Move;

import java.util.Random;

public class AaronFish implements szte.mi.Player {
	int currentCallId;
	BoardGrader boardGrader;
	boolean playerOne;
	Othello board;
	OthelloTree boardTree;
	int currentMove;
	DepthGoalCalculator depthGoalCalculator;

	public AaronFish() {
		this.depthGoalCalculator = new changingDepth();
		this.boardGrader = new BetterGrader();
		this.currentCallId = 0;
	}

	public void setBoardGrader(BoardGrader boardGrader) {
		this.boardGrader = boardGrader;
	}

	@Override
	public void init(int order, long t, Random rnd) {
		this.currentCallId = 0;
		this.board = new Othello();
		this.boardTree = new MirroredHashTree();
		if (order == 0) {
			this.playerOne = true;
			currentMove = -1;
		} else {
			this.playerOne = false;
			currentMove = 0;
		}
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
		currentCallId += 1;
		this.currentMove += 2;
		if (currentMove != 1) {
			this.boardTree.move(Othello.getIntFromMove(prevMove), !this.playerOne);
		}
		int remainingSpaces = this.boardTree.getRoot().getBoard().getRemainingSpaces();
		int goalDepth = depthGoalCalculator.getGoalDepth(t, remainingSpaces);
		return calculateNextMove(goalDepth);
	}

	public Move calculateNextMove(int depth) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		OthelloNode root = this.boardTree.getRoot();

		if (root.getNextNodes(playerOne).length == 1) {
			int move = root.getMoveAt(0);
			this.boardTree.move(move, this.playerOne);
			return Othello.getMoveFromInt(move);
		}
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
			if (alpha == Integer.MIN_VALUE) {
				bestMove = getMoveWithHighestWinProbability();
			}
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
			if (beta == Integer.MAX_VALUE) {
				bestMove = getMoveWithHighestWinProbability();
			}
		}
		this.boardTree.move(bestMove, this.playerOne);
		return Othello.getMoveFromInt(bestMove);
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
			}
			if (alpha < score) {
				alpha = score;
			}
			if (beta <= alpha) {
				break;
			}
		}
		if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
			node.setToFullyCalculated();
			node.setScore(bestScore, currentCallId);
		}
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
			}
			if (beta < score) {
				beta = score;
			}
			if (beta <= alpha) {
				break;
			}
		}
		if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
			node.setToFullyCalculated();
			node.setScore(bestScore, currentCallId);
		}
		return bestScore;
	}

	public int getMoveWithHighestWinProbability() {
		// OthelloNode root = this.boardTree.getRoot();
		OthelloNode[] children = this.boardTree.getRoot().getNextNodes(this.playerOne);

		int bestMove = this.boardTree.getRoot().getMoveAt(0);
		OthelloTree.LastHopeNode bestScore = children[0].getWinChances(!this.playerOne);
		if (playerOne) {
			for (int i = 1; i < children.length; i++) {
				OthelloNode currentChild = children[i];
				OthelloTree.LastHopeNode current = currentChild.getWinChances(!this.playerOne);
				if (current.isGreater(bestScore)) {
					bestMove = this.boardTree.getRoot().getMoveAt(i);
					bestScore = current;
				}

			}
		} else {
			for (int i = 1; i < children.length; i++) {
				OthelloNode currentChild = children[i];
				OthelloTree.LastHopeNode current = currentChild.getWinChances(!this.playerOne);
				if (!bestScore.isGreater(current)) {
					bestMove = this.boardTree.getRoot().getMoveAt(i);
					bestScore = current;
				}
			}
		}
		return bestMove;
	}

	public interface DepthGoalCalculator {
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces);
	}

	public static class ConstantDepth implements DepthGoalCalculator {
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			return 2;
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
		@Override
		public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
			if (remainingEmptySpaces < 13) {
				return 25;
			} else {
				return 5;
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
