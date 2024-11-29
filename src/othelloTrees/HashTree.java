package othelloTrees;

import ai.BoardGrader;

public class HashTree implements OthelloTree{
	@Override
	public void setRoot(OthelloNode node) {

	}

	@Override
	public OthelloNode getRoot() {
		return null;
	}

	@Override
	public void move(int move, boolean playerOne) {

	}

	static class hashNode extends OthelloNode{

		@Override
		public LastHopeNode getWinChances(boolean nextPlayer) {
			return null;
		}

		@Override
		public OthelloNode getNextNode(int move, boolean playerOne) {
			return null;
		}

		@Override
		public boolean getIsTerminalNode(boolean playerOne) {
			return false;
		}

		@Override
		public int getScoreWithoutCalcCheck(BoardGrader grader, boolean playerOne) {
			return 0;
		}

		@Override
		public OthelloNode[] getNextNodes(boolean playerOne) {
			return new OthelloNode[0];
		}

		@Override
		public boolean getIsFullyCalculated() {
			return false;
		}

		@Override
		public void setToFullyCalculated() {

		}

		@Override
		public void setScore(int score) {

		}

		@Override
		public int getScoreWithoutCalcCheck() {
			return 0;
		}
	}
}
