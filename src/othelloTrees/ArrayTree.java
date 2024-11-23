package othelloTrees;

import ai.BoardGrader;
import othello.Othello;

public class ArrayTree implements OthelloTree {
	ArrayNode root;

	public ArrayTree() {
		this.root = new ArrayNode();
	}

	@Override
	public OthelloNode getRoot() {
		return this.root;
	}

	@Override
	public void setRoot(OthelloNode node) {
		this.root = (ArrayNode) node;
	}

	@Override
	public void move(int move, boolean playerOne) {
		this.root = (ArrayNode) root.getNextNode(move, playerOne);
	}

	public static class ArrayNode extends OthelloNode {
		boolean fullyCalculated;
		private ArrayNode[] nextNodes;
		private boolean isGraded;
		private int score;

		public ArrayNode() {
			super();
			isGraded = false;
		}


		public ArrayNode(Othello board, int previousMove) {
			super(board, previousMove);
			isGraded = false;
		}

		@Override
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

		public int getScoreWithoutCalcCheck() {
			return this.score;
		}

		@Override
		public OthelloNode getNextNode(int move, boolean playerOne) {
			// non-optimal solution since data structure is optimized for other functions
			for (OthelloNode nextNode : this.getNextNodes(playerOne)) {
				if (nextNode == null) {
					break;
				}
				if (move == nextNode.getPreviousMove()) {
					return nextNode;
				}
			}
			// Throw proper error message when we get here
			StringBuilder allowedMoves = new StringBuilder();
			for (OthelloNode nextNode : this.getNextNodes(playerOne)) {
				if (nextNode == null) {
					break;
				}
				allowedMoves.append(nextNode.getPreviousMove()).append(", ");
			}
			if (!allowedMoves.isEmpty()) {
				allowedMoves.setLength(allowedMoves.length() - 2);
			}
			throw new RuntimeException("could not match move:" + move + " to any of the allowed moves: " + allowedMoves);
		}

		@Override
		public boolean getIsTerminalNode(boolean playerOne) {
			if (this.getBoard().boardIsFull()) {
				return true;
			}
			return this.getPreviousMove() == -1 && this.hasToPass(playerOne);
		}

		private boolean hasToPass(boolean playerOne) {
			return this.getNextNodes(playerOne)[0].getPreviousMove() == -1;
		}

		@Override
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


		@Override
		public OthelloNode[] getNextNodes(boolean playerOne) {
			if (this.nextNodes == null) {
				this.nextNodes = this.getBoard().getPossibleMovesAsNodes(playerOne);
			}
			return this.nextNodes;
		}

		@Override
		public boolean getIsFullyCalculated() {
			return this.fullyCalculated;
		}

		@Override
		public void setToFullyCalculated() {
			this.fullyCalculated = true;
			this.isGraded = true;
		}

		@Override
		public void setScore(int score) {
			this.score = score;
		}
	}
}
