package othelloTrees;

import othello.Othello;

import java.util.stream.Collectors;

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
		this.root = (ArrayNode) root.getNextNode(move,playerOne);
	}

	public static class ArrayNode extends OthelloNode {
		Othello board;

		public ArrayNode() {
			this.board = new Othello();
		}

		static ArrayNode getNodeFromBoard(Othello board){
			return new ArrayNode(board);
		}

		public ArrayNode(Othello board) {
			this.board = board;
		}

		@Override
		public Othello getBoard() {
			return this.board;
		}

		@Override
		protected void calculateChildren(boolean playerOne) {
			Othello.MoveAndResultingBoardList nextMovesAndBoards = this.board.getPossibleMovesLists(ArrayNode::getNodeFromBoard,playerOne);
			this.children = nextMovesAndBoards.nodes();
			this.nextMoves = nextMovesAndBoards.moves();
		}
	}
}

