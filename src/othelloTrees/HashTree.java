package othelloTrees;

import Game.Othello;
import ai.BoardGrader;

import java.util.HashMap;

public class HashTree implements OthelloTree{
	HashMap<Othello,hashNode>[]states=new HashMap[60];
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

	 class hashNode extends OthelloNode{

		 @Override
		 public othello.Othello getBoard() {
			 return null;
		 }

		 @Override
		 protected void calculateChildren(boolean playerOne) {

		 }

	 }
}
