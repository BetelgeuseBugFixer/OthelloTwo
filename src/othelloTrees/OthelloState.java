package othelloTrees;

import othello.Othello;

import java.util.Objects;

class OthelloState {
	private final Othello board;
	private final boolean playerOne;

	public OthelloState(Othello board, boolean playerOne) {
		this.board = board;
		this.playerOne = playerOne;
	}

	public Othello getBoard() {
		return board;
	}

	public boolean isPlayerOne() {
		return playerOne;
	}

	@Override
	public boolean equals(Object o) {
		//fuck checking here we want speed and just cast
		OthelloState that = (OthelloState) o;
		return playerOne == that.playerOne && board.equals(that.board);
	}

	@Override
	public int hashCode() {
		// Combine the two long values and the playerOne flag into a single hash.
		int result = Long.hashCode(board.blackPlayerDiscs);
		result = 31 * result + Long.hashCode(board.whitePLayerDiscs);
		result = 31 * result + (playerOne ? 1 : 0);
		return result;
	}
}