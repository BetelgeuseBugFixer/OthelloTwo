package othelloTrees;

import othello.Othello;

import java.util.Arrays;
import java.util.HashMap;

public class HashTree implements OthelloTree {
	private final HashMap<OthelloState, HashTreeNode>[] transpositionTable;
	int stonesSet;
	private HashTreeNode root;



	public HashTree() {
		this.stonesSet = 0;
		this.transpositionTable = new HashMap[61];
		for (int i = 0; i < transpositionTable.length; i++) {
			this.transpositionTable[i] = new HashMap<>();
		}
		this.root = new HashTreeNode(new Othello(), 0);
	}

	@Override
	public OthelloNode getRoot() {
		return root;
	}

	@Override
	public void setRoot(OthelloNode node) {
		this.root = (HashTreeNode) node;
	}

	@Override
	public void move(int move, boolean playerOne) {
		if (move != -1) {
			transpositionTable[stonesSet] = null;
			stonesSet++;
		}
		this.root = (HashTreeNode) root.getNextNode(move, playerOne);
	}

	class HashTreeNode extends OthelloNode {
		private final Othello board;
		int stonesSet;


		public HashTreeNode(Othello board, int stonesSet) {
			this.board = board;
			this.stonesSet = stonesSet;
		}

		@Override
		public Othello getBoard() {
			return board;
		}

		@Override
		protected void calculateChildren(boolean playerOne) {
			Othello.MoveAndResultingBoardList<OthelloState> nextMovesAndBoards = this.board.getPossibleMovesAndStates(x -> new OthelloState(x, playerOne), playerOne);

			// this.children = nextMovesAndBoards.nodes();
			this.nextMoves = nextMovesAndBoards.moves();

			int n = nextMoves.length;
			this.children = new HashTreeNode[n];
			Object[] states = nextMovesAndBoards.states();
			if (nextMoves[0] == -1) {
				this.children[0] = transpositionTable[this.stonesSet].computeIfAbsent((OthelloState) states[0], x -> new HashTreeNode(x.getBoard(), this.stonesSet));
				return;
			}
			// Add children to transposition table
			int newStones = this.stonesSet + 1;
			for (int i = 0; i < n; i++) {
				OthelloState childState = (OthelloState) states[i];
				HashTreeNode child = transpositionTable[newStones].get((OthelloState) states[i]);
				if (child == null) {
					child = new HashTreeNode(childState.getBoard(), newStones);
					transpositionTable[newStones].put(childState, child);
				}
				this.children[i] = child;
			}
		}
	}
}
