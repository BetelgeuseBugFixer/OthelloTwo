package othelloTrees;

import othello.Othello;

class OthelloState {
	private final Othello board;
	private final boolean playerOne;

	boolean alreadyHashed;
	int hash;

	public OthelloState(Othello board, boolean playerOne) {
		this.board = board;
		this.playerOne = playerOne;
		alreadyHashed = false;
	}

	public Othello getBoard() {
		return board;
	}

	public boolean isPlayerOne() {
		return playerOne;
	}

	@Override
	public boolean equals(Object o) {
		// fuck checking here we want speed and just cast
		OthelloState that = (OthelloState) o;
		return playerOne == that.playerOne && board.equals(that.board);
	}

	@Override
	public int hashCode() {
		//if (!alreadyHashed) {
			// Combine the two long values and the playerOne flag into a single hash.
			this.hash = Long.hashCode(board.blackPlayerDiscs);
			this.hash = 31 * this.hash + Long.hashCode(board.whitePLayerDiscs);
			//this.hash = 31 * this.hash + (playerOne ? 1 : 0);
			//alreadyHashed = true;
		//}
		return hash;
	}
}