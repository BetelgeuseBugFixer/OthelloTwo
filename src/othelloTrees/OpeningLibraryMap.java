package othelloTrees;

import othello.Othello;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OpeningLibraryMap {
			protected static final Map<HashTree.OthelloState, Integer> openingLibrary;

			static {
			HashMap<HashTree.OthelloState, Integer> stateHashtable = new HashMap<>();
						openingLibrary = Collections.unmodifiableMap(stateHashtable);
			}
}