package othelloTrees;

import othello.Othello;

import java.util.HashMap;

public class MirroredHashTree implements OthelloTree {
	private final HashMap<MirroredOthelloState, MirrorNode>[] transpositionTable;
	Mirror moveMirror;
	private int stonesSet;
	private MirrorNode root;


	public MirroredHashTree() {
		this.stonesSet = 0;
		this.transpositionTable = new HashMap[61];
		for (int i = 0; i < transpositionTable.length; i++) {
			this.transpositionTable[i] = new HashMap<>();
		}
		this.root = new MirrorNode(new Othello(), 0);
		this.moveMirror = new NoMirror();
	}

	public MirroredHashTree(Othello othello) {
		this.stonesSet = 0;
		this.transpositionTable = new HashMap[othello.getRemainingSpaces() + 1];
		for (int i = 0; i < transpositionTable.length; i++) {
			this.transpositionTable[i] = new HashMap<>();
		}
		this.root = new MirrorNode(othello, 0);
		this.moveMirror = new NoMirror();
	}

	private static Mirror findNewMirror(MirrorNode root, MirrorNode nextRoot, int move, Mirror moveMirror, boolean playerOne) {
		Othello current = new Othello(root.getBoard().blackPlayerDiscs, root.getBoard().whitePLayerDiscs);
		current.makeMove(move, playerOne);
		// find the state where they are identical
		if (current.equals(nextRoot.board)) {
			return moveMirror;
		}
		current = current.mirrorVertical();
		if (current.equals(nextRoot.board)) {
			return moveMirror.addVerticalMirror();
		}
		current = current.mirrorHorizontal();
		if (current.equals(nextRoot.board)) {
			return moveMirror.addDiagonalMirror();
		}
		current = current.mirrorVertical();
		if (current.equals(nextRoot.board)) {
			return moveMirror.addHorizontalMirror();
		}
		throw new RuntimeException("could not match\n" + root.getBoard() + "\nto\n" + nextRoot.getBoard() + "\nwith move " + move);
	}

	@Override
	public OthelloNode getRoot() {
		return root;
	}

	@Override
	public void setRoot(OthelloNode node) {
		this.root = (MirrorNode) node;
		this.moveMirror = new NoMirror();
	}

	@Override
	public void setRoot(Othello openingBoard) {
		this.root = new MirrorNode(openingBoard, 64 - openingBoard.getRemainingSpaces() -4);
		this.moveMirror=new NoMirror();
	}

	@Override
	public void move(int move, boolean playerOne) {
		if (move != -1) {
			//transpositionTable[stonesSet] = null;
			stonesSet++;
			move = this.moveMirror.mirrorMove(move);
		}
		MirrorNode nextRoot = (MirrorNode) root.getNextNode(move, playerOne);
		this.moveMirror = findNewMirror(root, nextRoot, move, moveMirror, playerOne);
		this.root = nextRoot;
	}

	public static interface Mirror {
		public int mirrorMove(int moveToMirror);

		public Mirror addVerticalMirror();

		public Mirror addHorizontalMirror();

		public Mirror addDiagonalMirror();
	}

	static class MirroredOthelloState {
		Othello representativeBoard;

		boolean playerOne;

		public MirroredOthelloState(Othello board, boolean isPlayerOne) {
			this.playerOne = isPlayerOne;
			representativeBoard = board;
			Othello candidate = board.mirrorVertical();
			if (!representativeBoard.isGreater(candidate)) {
				representativeBoard = candidate;
			}
			candidate = candidate.mirrorHorizontal();
			if (!representativeBoard.isGreater(candidate)) {
				representativeBoard = candidate;
			}
			candidate = candidate.mirrorVertical();
			if (!representativeBoard.isGreater(candidate)) {
				representativeBoard = candidate;
			}
		}

		public Othello getBoard() {
			return representativeBoard;
		}

		public boolean isPlayerOne() {
			return playerOne;
		}

		@Override
		public boolean equals(Object o) {
			// fuck checking here we want speed and just cast
			MirroredOthelloState that = (MirroredOthelloState) o;
			return playerOne == that.isPlayerOne() && this.getBoard().equals(that.getBoard());
		}

		@Override
		public int hashCode() {
			int hash = Long.hashCode(representativeBoard.blackPlayerDiscs);
			hash = 31 * hash + Long.hashCode(representativeBoard.whitePLayerDiscs);
			return hash;
		}
	}

	public static class NoMirror implements Mirror {

		@Override
		public int mirrorMove(int moveToMirror) {
			return moveToMirror;
		}

		@Override
		public Mirror addVerticalMirror() {
			return new VerticalMirror();
		}

		@Override
		public Mirror addHorizontalMirror() {
			return new HorizontalMirror();
		}

		@Override
		public Mirror addDiagonalMirror() {
			return new DiagonalMirror();
		}
	}

	public static class VerticalMirror implements Mirror {
		@Override
		public int mirrorMove(int moveToMirror) {
			int row = moveToMirror / 8;
			int col = moveToMirror % 8;
			return row * 8 + (7 - col);
		}

		@Override
		public Mirror addVerticalMirror() {
			return new NoMirror();
		}

		@Override
		public Mirror addHorizontalMirror() {
			return new DiagonalMirror();
		}

		@Override
		public Mirror addDiagonalMirror() {
			return new HorizontalMirror();
		}
	}

	public static class HorizontalMirror implements Mirror {
		@Override
		public int mirrorMove(int moveToMirror) {
			int row = moveToMirror / 8;
			int col = moveToMirror % 8;
			return (7 - row) * 8 + col;
		}

		@Override
		public Mirror addVerticalMirror() {
			return new DiagonalMirror();
		}

		@Override
		public Mirror addHorizontalMirror() {
			return new NoMirror();
		}

		@Override
		public Mirror addDiagonalMirror() {
			return new VerticalMirror();
		}
	}

	public static class DiagonalMirror implements Mirror {
		@Override
		public int mirrorMove(int moveToMirror) {
			int row = moveToMirror / 8;
			int col = moveToMirror % 8;
			return (7 - row) * 8 + (7 - col);
		}

		@Override
		public Mirror addVerticalMirror() {
			return new HorizontalMirror();
		}

		@Override
		public Mirror addHorizontalMirror() {
			return new VerticalMirror();
		}

		@Override
		public Mirror addDiagonalMirror() {
			return new NoMirror();
		}
	}

	public class MirrorNode extends OthelloNode {
		private final Othello board;
		int stonesSet;

		public MirrorNode(Othello board, int stonesSet) {
			this.board = board;
			this.stonesSet = stonesSet;
		}

		@Override
		public int getMoveAt(int index) {
			int move = this.nextMoves[index];
			if (move != -1) {
				move = moveMirror.mirrorMove(move);
			}
			return move;
		}

		public int getMoveNoMirror(int index) {
			return this.nextMoves[index];
		}

		@Override
		public Othello getBoard() {
			return board;
		}

		@Override
		protected void calculateChildren(boolean playerOne) {
			Othello.MoveAndResultingBoardList<MirroredOthelloState> nextMovesAndBoards = this.board.getPossibleMovesAndStates(x -> new MirroredOthelloState(x, playerOne), playerOne);
			this.nextMoves = nextMovesAndBoards.moves();
			int n = nextMoves.length;
			this.children = new MirrorNode[n];
			Object[] states = nextMovesAndBoards.states();
			// if we pass the number of stones stay the same
			if (nextMoves[0] == -1) {
				this.children[0] = transpositionTable[this.stonesSet].computeIfAbsent((MirroredOthelloState) states[0], x -> new MirrorNode(x.getBoard(), this.stonesSet));
				return;
			}
			// Add children to transposition table
			int newStones = this.stonesSet + 1;
			for (int i = 0; i < n; i++) {
				MirroredOthelloState childState = (MirroredOthelloState) states[i];
				MirrorNode child = transpositionTable[newStones].get((MirroredOthelloState) states[i]);
				if (child == null) {
					child = new MirrorNode(childState.getBoard(), newStones);
					transpositionTable[newStones].put(childState, child);
				}
				this.children[i] = child;
			}
		}
	}

}

