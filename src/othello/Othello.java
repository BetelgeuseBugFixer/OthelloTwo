package othello;

import szte.mi.Move;

import java.util.HashMap;
import java.util.function.Function;

public class Othello {
	static final long leftBorderBitMask = 0b1111111011111110111111101111111011111110111111101111111011111110L;
	static final long rightBorderBitMask = 0b111111101111111011111110111111101111111011111110111111101111111L;
	public long whitePLayerDiscs;
	public long blackPlayerDiscs;

	public Othello() {
		this.whitePLayerDiscs = 0x0000001008000000L;
		this.blackPlayerDiscs = 0x0000000810000000L;
	}

	public Othello(long blackPlayerDiscs, long whitePLayerDiscs) {
		this.whitePLayerDiscs = whitePLayerDiscs;
		this.blackPlayerDiscs = blackPlayerDiscs;
	}

	public static int getIntFromMove(Move move) {
		if (move == null) {
			return -1;
		}
		return move.y * 8 + move.x;
	}

	public static Move getMoveFromInt(int move) {
		if (move == -1) {
			return null;
		}
		return new Move(move % 8, move / 8);
	}

	// helper method for debugging
	public static String singleLongToField(long board) {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				long curPos = 1L << (row * 8 + col);
				if ((curPos & board) != 0) {
					sb.append("|X");
				} else {
					sb.append("| ");
				}
			}
			sb.append("|\n");
		}
		return sb.toString();
	}

	public static boolean isValidMove(Othello board, Move move, boolean playerOne) {
		long legal = board.getLegalMovesAsLong(playerOne);
		if (move == null) {
			return legal == 0L;
		}
		int index = Othello.getIntFromMove(move);
		return (legal & (1L << index)) != 0;
	}

	public static long mirrorHorizontal(long bitboard) {
		bitboard = ((bitboard >>> 1) & 0x5555555555555555L) | ((bitboard & 0x5555555555555555L) << 1);
		bitboard = ((bitboard >>> 2) & 0x3333333333333333L) | ((bitboard & 0x3333333333333333L) << 2);
		bitboard = ((bitboard >>> 4) & 0x0F0F0F0F0F0F0F0FL) | ((bitboard & 0x0F0F0F0F0F0F0F0FL) << 4);
		return bitboard;
	}

	public static long mirrorVertical(long bitboard) {
		bitboard = ((bitboard >>> 8) & 0x00FF00FF00FF00FFL) | ((bitboard & 0x00FF00FF00FF00FFL) << 8);
		bitboard = ((bitboard >>> 16) & 0x0000FFFF0000FFFFL) | ((bitboard & 0x0000FFFF0000FFFFL) << 16);
		bitboard = ((bitboard >>> 32) & 0x00000000FFFFFFFFL) | ((bitboard & 0x00000000FFFFFFFFL) << 32);
		return bitboard;
	}

	public void makeMove(int move, boolean playerOne) {
		if (move == -1) {
			return;
		}

		long discsToFlip = getDiscsToFlip(move, playerOne);
		blackPlayerDiscs ^= discsToFlip;
		whitePLayerDiscs ^= discsToFlip;
		if (playerOne) {
			blackPlayerDiscs |= 1L << move;
		} else {
			whitePLayerDiscs |= 1L << move;
		}
	}

	public void makeMove(Move move, boolean playerOne) {
		if (move == null) {
			return;
		}
		makeMove(getIntFromMove(move), playerOne);
	}

	public MoveWithResult[] getPossibleMoves(boolean playerOne) {
		// according to
		// https://jxiv.jst.go.jp/index.php/jxiv/preprint/view/480
		// 33 is the maximum number of moves in reachable positions
		MoveWithResult[] possibleMoves = new MoveWithResult[33];
		int index = 0;
		// iterate over every field
		for (int i = 0; i < 64; i++) {
			long discsToFlip = getDiscsToFlip(i, playerOne);
			if (discsToFlip != 0) {
				long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
				long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;
				// add new disc
				if (playerOne) {
					newBlackDiscs |= 1L << i;
				} else {
					newWhiteDiscs |= 1L << i;
				}
				Othello newBoard = new Othello(newBlackDiscs, newWhiteDiscs);
				possibleMoves[index] = new MoveWithResult(i, newBoard);
				index++;
			}

		}

		if (possibleMoves[0] == null) {
			Othello newBoard = new Othello(blackPlayerDiscs, whitePLayerDiscs);
			possibleMoves[0] = new MoveWithResult(-1, newBoard);
		}
		return possibleMoves;
	}

	public int getRemainingSpaces() {
		return Long.bitCount(~(this.blackPlayerDiscs | whitePLayerDiscs));
	}

	public <T> MoveAndResultingBoardList<T> getPossibleMovesAndStates(Function<Othello, T> createState, boolean playerOne) {
		int[] moves = new int[34];
		T[] boards = (T[]) new Object[34]; // Generic array creation
		int index = 0;

		// Iterate over every field
		for (int i = 0; i < 64; i++) {
			long discsToFlip = getDiscsToFlip(i, playerOne);
			if (discsToFlip != 0) {
				long newWhiteDiscs = whitePLayerDiscs ^ discsToFlip;
				long newBlackDiscs = blackPlayerDiscs ^ discsToFlip;

				// Add new disc
				if (playerOne) {
					newBlackDiscs |= 1L << i;
				} else {
					newWhiteDiscs |= 1L << i;
				}

				Othello newBoard = new Othello(newBlackDiscs, newWhiteDiscs);
				moves[index] = i;
				boards[index] = createState.apply(newBoard);
				index++;
			}
		}

		if (index == 0) {
			Othello newBoard = new Othello(blackPlayerDiscs, whitePLayerDiscs);
			moves[0] = -1;
			boards[0] = createState.apply(newBoard);
			index = 1;
		}

		// Trim the arrays to their actual size
		int[] trimmedMoves = new int[index];
		T[] trimmedBoards = (T[]) new Object[index];
		System.arraycopy(moves, 0, trimmedMoves, 0, index);
		System.arraycopy(boards, 0, trimmedBoards, 0, index);

		return new MoveAndResultingBoardList(trimmedMoves, trimmedBoards);
	}

	public long getDiscsToFlip(int i, boolean playerOne) {
		// check if field is occupied
		long targetPosition = 1L << i;
		if ((targetPosition & blackPlayerDiscs) != 0 || (targetPosition & whitePLayerDiscs) != 0) {
			return 0;
		}

		long toFLip = 0L;

		long playerToBeFlipped = blackPlayerDiscs;
		long playerThatFlips = whitePLayerDiscs;
		if (playerOne) {
			playerToBeFlipped = whitePLayerDiscs;
			playerThatFlips = blackPlayerDiscs;
		}

		int[] directions = {-1, 0, 1};
		// iterate in all directions
		for (int rowDir : directions) {
			for (int colDir : directions) {
				// skip no direction
				if (rowDir == 0 && colDir == 0) {
					continue;
				}
				long currentFlips = 0L;


				int curRow = (i / 8) + rowDir;
				int curCol = (i % 8) + colDir;

				while (curRow >= 0 && curRow <= 7 && curCol >= 0 && curCol <= 7) {

					long currentPosition = 1L << (curRow * 8 + curCol);
					if ((currentPosition & playerToBeFlipped) != 0) {
						// position is added in to flip
						currentFlips |= currentPosition;
					} else {
						if ((currentPosition & playerThatFlips) != 0) {
							// row of opponents discs ends with own disc and the discs can be flipped
							toFLip = toFLip | currentFlips;
						}
						break;
					}
					curRow += rowDir;
					curCol += colDir;
				}

			}
		}
		return toFLip;
	}

	public int getDiscAtField(int row, int col) {
		int index = row * 8 + col;
		return getDiscAtField(index);
	}

	// returns 0 if there is no disc,1 if player Disc is black, 2 if player is white
	public int getDiscAtField(int index) {
		long position = 1L << index;
		if ((this.blackPlayerDiscs & position) != 0) {
			return 1;
		} else if ((this.whitePLayerDiscs & position) != 0) {
			return 2;
		} else {
			return 0;
		}
	}

	public int getDiscAtField(long bitmask) {
		if ((this.blackPlayerDiscs & bitmask) != 0L) {
			return 1;
		} else if ((this.whitePLayerDiscs & bitmask) != 0L) {
			return 2;
		} else {
			return 0;
		}
	}

	public String getCurrentGameResult() {
		return "black:white " + this.blackPlayerDiscs + ": " + this.whitePLayerDiscs;
	}

	@Override
	public String toString() {
		HashMap<Integer, String> intToDisc = new HashMap<>();
		intToDisc.put(0, " ");
		intToDisc.put(1, "X");
		intToDisc.put(2, "O");
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				sb.append("|");
				sb.append(intToDisc.get(getDiscAtField(row, col)));
			}
			sb.append("|\n");
		}
		return sb.toString();
	}

	public boolean boardIsFull() {
		return this.blackPlayerDiscs + this.whitePLayerDiscs == -1;
	}

	public int getDiscDifference() {
		return Long.bitCount(this.blackPlayerDiscs) - Long.bitCount(this.whitePLayerDiscs);
	}

	// shamelessly stolen from
	// https://stackoverflow.com/questions/5944230/optimization-of-moves-calculation-in-othello-bitboard
	public long getLegalMovesAsLong(Boolean playerOne) {
		long legal = 0L;
		long potentialMoves;
		long currentBoard;
		long opponentBoard;
		if (playerOne) {
			currentBoard = this.blackPlayerDiscs;
			opponentBoard = this.whitePLayerDiscs;
		} else {
			currentBoard = this.whitePLayerDiscs;
			opponentBoard = this.blackPlayerDiscs;
		}
		long emptyBoard = ~(this.blackPlayerDiscs | this.whitePLayerDiscs);
		// UP
		potentialMoves = (currentBoard >>> 8) & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves >>> 8);
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// DOWN
		potentialMoves = (currentBoard << 8) & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves << 8);
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// LEFT
		potentialMoves = (currentBoard >>> 1L) & rightBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves >>> 1L) & rightBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// RIGHT
		potentialMoves = (currentBoard << 1L) & leftBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves << 1L) & leftBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// UP LEFT
		potentialMoves = (currentBoard >>> 9) & rightBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves >>> 9) & rightBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// UP RIGHT
		potentialMoves = (currentBoard >>> 7) & leftBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves >>> 7) & leftBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// DOWN LEFT
		potentialMoves = (currentBoard << 7) & rightBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves << 7) & rightBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		// DOWN RIGHT
		potentialMoves = (currentBoard << 9) & leftBorderBitMask & opponentBoard;
		while (potentialMoves != 0L) {
			long tmp = (potentialMoves << 9) & leftBorderBitMask;
			legal |= tmp & emptyBoard;
			potentialMoves = tmp & opponentBoard;
		}
		return legal;
	}

	public int getResult() {
		int discDifference = this.getDiscDifference();
		return Integer.compare(discDifference, 0);
	}

	public boolean isOver() {
		return boardIsFull() || (getLegalMovesAsLong(true) == 0 && getLegalMovesAsLong(false) == 0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Othello that = (Othello) o;
		return this.equals(that);
	}

	public boolean equals(Othello other) {
		return this.blackPlayerDiscs == other.blackPlayerDiscs && this.whitePLayerDiscs == other.whitePLayerDiscs;
	}

	public Othello mirrorVertical() {
		long mirroredBlackPlayerDisc = mirrorHorizontal(this.blackPlayerDiscs);
		long mirroredWhitePlayerDiscs = mirrorHorizontal(this.whitePLayerDiscs);
		return new Othello(mirroredBlackPlayerDisc, mirroredWhitePlayerDiscs);
	}

	public Othello mirrorHorizontal() {
		long mirroredBlackPlayerDisc = mirrorVertical(this.blackPlayerDiscs);
		long mirroredWhitePlayerDiscs = mirrorVertical(this.whitePLayerDiscs);
		return new Othello(mirroredBlackPlayerDisc, mirroredWhitePlayerDiscs);
	}

	public boolean isGreater(Othello other) {
		// Compare black discs first
		if (this.blackPlayerDiscs > other.blackPlayerDiscs) {
			return true;
		} else if (this.blackPlayerDiscs < other.blackPlayerDiscs) {
			return false;
		}
		// compare white discs
		return this.whitePLayerDiscs >= other.whitePLayerDiscs;
	}

	public record MoveAndResultingBoardList<T>(int[] moves, T[] states) {
	}

	public static class MoveWithResult {
		public int move;
		public Othello board;

		public MoveWithResult(int move, Othello board) {
			this.move = move;
			this.board = board;
		}
	}
}
