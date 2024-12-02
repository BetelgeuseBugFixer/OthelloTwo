package othelloTrees;

import ai.BoardGrader;
import othello.Othello;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface OthelloTree {

	public OthelloNode getRoot();

	public void setRoot(OthelloNode node);

	public void move(int move, boolean playerOne);

	abstract class OthelloNode {
		protected List<OthelloNode> children;
		protected ArrayList<Integer> nextMoves;
		private boolean isTerminal;
		private boolean checkedIfTerminal = false;
		private boolean fullyCalculated;
		private boolean isGraded;
		private int score;

		public OthelloNode() {
		}

		public LastHopeNode getWinChances(boolean nextPlayer) {
			LastHopeNode lhn = new LastHopeNode();
			for (OthelloNode nextNode : this.getNextNodes(nextPlayer)) {
				if (nextNode == null) {
					break;
				}
				if (nextNode.getIsFullyCalculated()) {
					if (nextNode.getScoreWithoutCalcCheck() == Integer.MAX_VALUE) {
						lhn.wins += 1;
					} else if (nextNode.getScoreWithoutCalcCheck() == 0) {
						lhn.draws += 1;
					} else {
						lhn.loses += 1;
					}
				} else {
					lhn.uncalculated += 1;
				}
			}
			return lhn;
		}

		public int getMoveAt(int i) {
			return this.nextMoves.get(i);
		}

		public OthelloNode getChildAt(int i) {
			return this.children.get(i);
		}

		public abstract Othello getBoard();

		protected OthelloNode getNextNode(int move, boolean playerOne) {
			this.getNextNodes(playerOne);
			// non-optimal solution since data structure is optimized for other functions
			// does not matter, since it only called once per call with small data structure
			for (int i = 0; i < nextMoves.size(); i++) {
				if (move == nextMoves.get(i)) {
					return children.get(i);
				}
			}
			throw new RuntimeException("could not match " + move + " to any of the children in " + nextMoves);
		}

		public boolean getIsTerminalNode(boolean playerOne) {
			if (!this.checkedIfTerminal) {
				checkedIfTerminal = true;
				if (this.getBoard().boardIsFull()) {
					this.isTerminal = true;
				} else {
					this.isTerminal = this.hasToPass(playerOne) && nextHasToPass(!playerOne);
				}
			}
			return isTerminal;
		}

		private boolean hasToPass(boolean playerOne) {
			// make sure next nodes are calculated
			getNextNodes(playerOne);
			return this.nextMoves.get(0) == -1;
		}

		// only to be called after hasToPass returned true
		private boolean nextHasToPass(boolean nextIsPlayerOne) {
			return this.children.get(0).hasToPass(nextIsPlayerOne);
		}

		public int getScoreWithoutCalcCheck(BoardGrader grader, boolean playerOne) {
			if (!this.isGraded) {
				if (getIsTerminalNode(playerOne)) {
					this.fullyCalculated = true;
					int discDifference = this.getBoard().getDiscDifference();
					if (discDifference > 0) {
						this.score = Integer.MAX_VALUE;
					} else if (discDifference < 0) {
						this.score = Integer.MIN_VALUE;
					} else {
						this.score = 0;
					}
				} else {
					this.score = grader.gradeBoard(this, playerOne);
					this.isGraded = true;
				}

			}
			return this.score;
		}

		public ArrayList<OthelloNode> getNextNodes(boolean playerOne) {
			if (this.children == null) {
				this.calculateChildren(playerOne);
			}
			return (ArrayList<OthelloNode>) this.children;
		}

		protected abstract void calculateChildren(boolean playerOne);

		private void sortChildren() {

			List<Pair> pairs = new ArrayList<>();
			for (int i = 0; i < children.size(); i++) {
				pairs.add(new Pair(children.get(i), nextMoves.get(i)));
			}

			pairs.sort(Comparator.comparingInt(pair -> getHeuristicScore(pair.node())));

			for (int i = 0; i < pairs.size(); i++) {
				children.set(i, pairs.get(i).node());
				nextMoves.set(i, pairs.get(i).move());
			}
		}

		public boolean getIsFullyCalculated() {
			return this.fullyCalculated;
		}

		public void setToFullyCalculated() {
			this.fullyCalculated = true;
		}

		public void setScore(int score) {
			this.score = score;
			sortChildren();
		}

		public int getScoreWithoutCalcCheck() {
			return this.score;
		}

		private int getHeuristicScore(OthelloNode node) {
			return node.isGraded ? node.score : Integer.MIN_VALUE + 1;
		}

		record Pair(OthelloNode node, Integer move) {
		}

	}

	public class LastHopeNode {
		int wins;
		int draws;
		int uncalculated;
		int loses;

		public int sumOfGames() {
			return wins + draws + uncalculated + loses;
		}

		public boolean isGreater(LastHopeNode other) {
			double thisAllGames = this.sumOfGames();
			double otherAllGames = other.sumOfGames();
			if (this.wins / thisAllGames > other.wins / otherAllGames) {
				return true;
			} else if (this.wins / thisAllGames < other.wins / otherAllGames) {
				return false;
			}
			if (this.draws / thisAllGames > other.draws / otherAllGames) {
				return true;
			} else if (this.draws / thisAllGames < other.draws / otherAllGames) {
				return false;
			}
			return thisAllGames > otherAllGames;
		}
	}
}
