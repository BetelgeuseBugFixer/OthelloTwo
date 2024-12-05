package othelloTrees;

import othello.Othello;

public class OpeningLibraryWrapper {
	public static Integer getMoveFromOpeningLibrary(Othello othello, boolean playerOne) {
		HashmapStateAndMirror representativeStateAndMirror = getMirrorAndOthelloToLookUp(othello, playerOne);
		Integer move = OpeningLibraryMap.openingLibrary.get(representativeStateAndMirror.othelloState);
		if (move != null && move != -1) {
			move = representativeStateAndMirror.mirror.mirrorMove(move);
		}
		return move;
	}

	private static HashmapStateAndMirror getMirrorAndOthelloToLookUp(Othello othello, boolean playerOne) {
		Othello representativeBoard = new Othello(othello.blackPlayerDiscs, othello.whitePLayerDiscs);
		MirroredHashTree.Mirror moveMirror = new MirroredHashTree.NoMirror();
		Othello candidate = representativeBoard.mirrorVertical();
		if (!representativeBoard.isGreater(candidate)) {
			representativeBoard = candidate;
			moveMirror = new MirroredHashTree.VerticalMirror();
		}
		candidate = candidate.mirrorHorizontal();
		if (!representativeBoard.isGreater(candidate)) {
			representativeBoard = candidate;
			moveMirror = new MirroredHashTree.DiagonalMirror();
		}
		candidate = candidate.mirrorVertical();
		if (!representativeBoard.isGreater(candidate)) {
			representativeBoard = candidate;
			moveMirror = new MirroredHashTree.HorizontalMirror();
		}
		return new HashmapStateAndMirror(new HashTree.OthelloState(representativeBoard, playerOne), moveMirror);
	}

	static record HashmapStateAndMirror(HashTree.OthelloState othelloState, MirroredHashTree.Mirror mirror) {
	}
}
