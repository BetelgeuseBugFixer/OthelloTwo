package ai.grader;

import othello.Othello;
import othelloTrees.OthelloTree;

import java.util.Arrays;

public class BetterGrader implements BoardGrader {
	static final long leftBorderBitMask = 0b1111111011111110111111101111111011111110111111101111111011111110L;
	static final long rightBorderBitMask = 0b111111101111111011111110111111101111111011111110111111101111111L;
	static final long upperBorderBitMask = 0xffffffffffffff00L;
	static final long downBorderBitMask = 0xffffffffffffffL;
	static final long upLeftCornerMask = 1L;
	static final long upRightCornerMask = 0x80L;
	static final long downLeftCornerMask = 0x100000000000000L;
	static final long downRightCornerMask = 0x8000000000000000L;
	static final long[] startCorner = {upLeftCornerMask, upLeftCornerMask, upRightCornerMask, downLeftCornerMask};
	static final long[] endCorner = {upRightCornerMask, downLeftCornerMask, downRightCornerMask, downRightCornerMask};
	static final Shifter[] shiftsAlongEdge = {BetterGrader::shiftRight, BetterGrader::shiftDown, BetterGrader::shiftDown, BetterGrader::shiftRight};
	static final long[] edgeMasks = {~upperBorderBitMask, ~leftBorderBitMask, ~rightBorderBitMask, ~downBorderBitMask};
	// All edges are represented as start and end edge, and the shift to get from start to end
	// startEdges[i], endEdges[i] and shift[i] is therefore the upper edge
	final Shifter[] allShifts = {BetterGrader::shiftUp, BetterGrader::shiftDown,
			BetterGrader::shiftLeft, BetterGrader::shiftRight,
			BetterGrader::shiftUpLeft, BetterGrader::shiftUpRight,
			BetterGrader::shiftDownLeft, BetterGrader::shiftDownRight};


	// third run
	public int[] third = {36, 417, -835, 2294, 1152, 756, 0, 164, 0, -722, 0, 303, -853, -4509, 0, -4264, 2198, 0, 0, -411, 1122, 318, -1164, 2396, 1032, 1392, -41, 1115, 0, -258, -257, 515, -1289, -5937, 350, 0, 2771, 0, 0, 0, 1165};    // second run that somehow did fantastic
	public int[] second = {34, 76, -133, 192, 116, 98, 0, 294, -28, 50, 0, -8, -164, -896, -30, -439, 1124, 23, 0, 162, 0, 131, -106, 242, 120, 122, 0, 273, 36, 0, -66, -18, -130, -844, 11, -405, 1146, 0, 0, 114, 69};
	// second run in between
	// public int[] secondB = {20, 49, -102, 209, 123, 104, 0, 303, -35, 32, -46, -16, -155, -885, -40, -436, 1117, 45, 0, 155, 0, 81, -100, 222, 133, 78, 0, 289, 25, 44, -63, -29, -123, -867, -9, -377, 1135, 14, 0, 130, 77};
	// first run
	public int[] first = {-130, -191, 22, -107, -20, 96, 30, -391, 169, 140, -43, 201, 35, -103, -74, -176, 156, -218, -79, 473, 268, 69, -210, 315, 189, 158, 126, 670, -220, 421, 126, 102, -117, -1422, 103, -701, 1680, -57, -19, -216, -29};
	// handcrafted
	public int[] handcrafted = {20, 10, -20, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 60, 1, 20, 70, -20, 10, -5, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 50, 2, 30, 90, -60};
	// combination
	public int[] combination = {34, 76, -133, 192, 116, 98, 40, 294, -28, 50, 0, -8, -164, -896, -30, -439, 1124, 23, 0, 162, 0, 0, 0, 2701, 2157, 1522, 202, 973, -271, -777, -507, 382, -1454, -6604, -998, 0, 0, 0, -150, -125, 1179};

	public int[] weights = first;
	int moveChange = 0;
	int sPossibleMovesIndex = 1;
	int sFrontierDiscsIndex = 2;
	int sStableDiscsIndex = 3;
	int sBalancedEdgeIndex = 4;
	int sUnbalancedEdgeIndex = 5;
	int sPairIndex = 6;
	int sWedgeIndex = 7;
	int sUnevenEdgeGapIndex = 8;
	int sEvenEdgeGapIndex = 9;
	int sCenter4StonesIndex = 10;
	int sNextToCenterDiscsIndex = 11;
	int sNextToEdgeDiscsIndex = 12;
	int sDangerDiscsIndex = 13;
	int sEdgeDiscsIndex = 14;
	int sEdgeNextToCornerDiscsIndex = 15;
	int sCornerDiscsIndex = 16;
	int sDiscDifferenceWeightIndex = 17;
	int sFlippableEdgeWeightIndex = 18;
	int sParityWeightIndex = 19;
	int sParityInDangerIndex = 20;
	int ePossibleMovesIndex = 21;
	int eFrontierDiscsIndex = 22;
	int eStableDiscsIndex = 23;
	int eBalancedEdgeIndex = 24;
	int eUnbalancedEdgeIndex = 25;
	int ePairIndex = 26;
	int eWedgeIndex = 27;
	int eUnevenEdgeGapIndex = 28;
	int eEvenEdgeGapIndex = 29;
	int eCenter4StonesIndex = 30;
	int eNextToCenterDiscsIndex = 31;
	int eNextToEdgeDiscsIndex = 32;
	int eDangerDiscsIndex = 33;
	int eEdgeDiscsIndex = 34;
	int eEdgeNextToCornerDiscsIndex = 35;
	int eCornerDiscsIndex = 36;
	int eDiscDifferenceWeightIndex = 37;
	int eFlippableEdgeWeightIndex = 38;
	int eParityWeightIndex = 39;
	int eParityInDangerIndex = 40;

	long center4StonesMask = 0x1818000000L;
	long nextToCenterStonesMask = 0x3c24243c0000L;
	long nextToEdgeStonesMask = 0x3c424242423c00L;
	long edgeDiscsMask = 0x3c0081818181003cL;

	private static long getStableDiscUpLeftCorner(long playerDiscs) {
		return getStableDiscFromCorner(playerDiscs, upLeftCornerMask, BetterGrader::shiftRight, BetterGrader::shiftDown, BetterGrader::shiftDownRight, BetterGrader::shiftUpRight, BetterGrader::shiftDownLeft);
	}

	private static long getStableDiscUpRightCorner(long playerDiscs) {
		return getStableDiscFromCorner(playerDiscs, upRightCornerMask, BetterGrader::shiftLeft, BetterGrader::shiftDown, BetterGrader::shiftDownLeft, BetterGrader::shiftUpLeft, BetterGrader::shiftDownRight);
	}

	private static long getStableDiscDownLeftCorner(long playerDiscs) {
		return getStableDiscFromCorner(playerDiscs, downLeftCornerMask, BetterGrader::shiftRight, BetterGrader::shiftUp, BetterGrader::shiftUpRight, BetterGrader::shiftUpLeft, BetterGrader::shiftDownRight);
	}

	private static long getStableDiscDownRightCorner(long playerDiscs) {
		return getStableDiscFromCorner(playerDiscs, downRightCornerMask, BetterGrader::shiftLeft, BetterGrader::shiftUp, BetterGrader::shiftUpLeft, BetterGrader::shiftUpRight, BetterGrader::shiftDownLeft);
	}

	public static long getStableDiscFromCorner(long playerDiscs, long corner, Shifter verticalEdgeShift, Shifter horizontalEdgeShift, Shifter obliqueShift, Shifter antiObliqueUpShift, Shifter antiObliqueDownShift) {
		long stableDiscs = 0L;

		// get all stable discs from initial corner

		// first shift
		long lastStableDiscs;
		long tempStableDisc = corner;
		do {
			lastStableDiscs = tempStableDisc;
			tempStableDisc |= verticalEdgeShift.shift(tempStableDisc) & playerDiscs;
		} while (lastStableDiscs != tempStableDisc);
		stableDiscs |= tempStableDisc;

		// second shift

		tempStableDisc = corner;
		do {
			lastStableDiscs = tempStableDisc;
			tempStableDisc |= horizontalEdgeShift.shift(tempStableDisc) & playerDiscs;
		} while (lastStableDiscs != tempStableDisc);
		stableDiscs |= tempStableDisc;

		// shift in 3 directions to find rest of stable discs
		do {
			lastStableDiscs = stableDiscs;
			long firstDirection = verticalEdgeShift.shift(stableDiscs);
			long secondDirection = horizontalEdgeShift.shift(stableDiscs);
			long thirdDirection = obliqueShift.shift(stableDiscs);
			long optionalDirections = antiObliqueUpShift.shift(stableDiscs) | antiObliqueDownShift.shift(stableDiscs);
			stableDiscs |= (firstDirection & secondDirection & thirdDirection & optionalDirections) & playerDiscs;
		} while (lastStableDiscs != stableDiscs);


		return stableDiscs;
	}

	public static int getLastChainFromCurrent(int currentChain) {
		if (currentChain == 1) {
			return 4;
		} else if (currentChain == 2) {
			return 5;
		} else {
			return 0;
		}
	}

	public static int getChainBreakCase(int lastChain, int currentChain, int newDisc) {
		// 0: before border, 1: gap with length > 1, 2: single gap ending with black
		// 3: single gap ending in white, 4: black discs, 5: white discs

		// beginning of board followed by empty squares is always a safe flip
		if (lastChain == 0 && currentChain == 0) {
			return 0;
		} else if (lastChain == 0 && ((currentChain == 1 && newDisc == 2) || (currentChain == 2 && newDisc == 1))) {
			// edge beginns with row of discs, that is interrupted by other discs -> we need to check for a safe flip
			return 7;
		} else if (lastChain == 1 && ((currentChain == 1 && newDisc == 2) || currentChain == 2 && newDisc == 1)) {
			// a gap >1 is followed by 2 lines od adversary discs. to the left there is a safe flip, but maybe also to the right
			return 2;
		} else if (((lastChain == 4 && newDisc == 1) || lastChain == 5 && newDisc == 2) && currentChain == 0) {
			// empty chain between to discs from same player, introduce gap and update lastChain
			return 3;
		} else if (((lastChain == 4 && newDisc == 2) || lastChain == 5 && newDisc == 1) && currentChain == 0) {
			// empty squares between discs from different players, update gap
			return 4;
		} else if (lastChain == 4 && currentChain == 2 && newDisc == 1) {
			// white wedge
			return 5;
		} else if (lastChain == 5 && currentChain == 1 && newDisc == 2) {
			// black wedge
			return 6;
		} else if (lastChain == 2 && currentChain == 1 && newDisc == 2) {
			// gap between two black chains, followed by white chain
			// white has no safe flip to the left, but black might have to the right
			return 1;
		} else if (lastChain == 3 && currentChain == 2 && newDisc == 1) {
			// same as above with other colours
			return 1;
		} else if (lastChain == 2 && currentChain == 2 && newDisc == 1) {
			// white discs + single gap + black disc+ white discs -> safe flip for white, but possibly also for black
			// both directions need to be checked
			return 2;

		} else if (lastChain == 3 && currentChain == 1 && newDisc == 2) {
			// same as above with reverse discs
			return 2;
		} else return 8;
	}

	public static int[] shortenAndSortArray(int[] original, int actualSize) {
		int[] newArray = Arrays.copyOf(original, actualSize);
		Arrays.sort(newArray);
		return newArray;
	}

	private static long shiftLeft(long x) {
		return (x >>> 1) & rightBorderBitMask;
	}

	private static long shiftRight(long x) {
		return (x << 1) & leftBorderBitMask;
	}

	private static long shiftUp(long x) {
		return (x >>> 8);
	}

	private static long shiftDown(long x) {
		return (x << 8);
	}

	private static long shiftUpRight(long x) {
		return (x >>> 7) & leftBorderBitMask;
	}

	private static long shiftUpLeft(long x) {
		return (x >>> 9) & rightBorderBitMask;
	}

	private static long shiftDownRight(long x) {
		return (x << 9) & leftBorderBitMask;
	}

	private static long shiftDownLeft(long x) {
		return (x << 7) & rightBorderBitMask;
	}

	protected static int getFrontiersDiscDifference(Othello board) {
		long blackPlayerDiscs = board.blackPlayerDiscs;
		long whitePlayerDiscs = board.whitePLayerDiscs;
		long allDiscs = blackPlayerDiscs | whitePlayerDiscs;
		long empty = ~allDiscs;
		long frontierDiscs = allDiscs & (shiftDown(empty) | shiftUp(empty) | shiftLeft(empty) | shiftRight(empty) | shiftUpLeft(empty) | shiftUpRight(empty) | shiftDownLeft(empty) | shiftDownRight(empty));

		return Long.bitCount(frontierDiscs & blackPlayerDiscs) - Long.bitCount(frontierDiscs & whitePlayerDiscs);
	}

	// to include in metric:
	//-two unbalanced edges next to each other
	//-corner capture
	@Override
	public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
		Othello board = node.getBoard();
		int discsSet = Long.bitCount(board.blackPlayerDiscs & board.whitePLayerDiscs);
		long possibleMoves = board.getLegalMovesAsLong(playerOne);
		boolean startWeights = discsSet < weights[moveChange];

		int score = 0;

		score += getPossibleMovesScore(possibleMoves, !playerOne, startWeights);
		score += getFrontierDiscsScore(board, startWeights);
		score += getCornerAndStableDiscs(board, startWeights);
		score += getEdgeScores(board, !playerOne, startWeights);
		score += getDiscDifferenceScore(board, startWeights);
		score += getDiscPositionScore(board, startWeights);
		score += getParityScore(board, playerOne, startWeights, possibleMoves);

		if (score == 0) {
			return score + 1;
		}
		return score;
	}

	public void setWeights(int[] weights) {
		this.weights = weights;
	}

	public void setAllWeightsToOne() {
		for (int i = 0; i < this.weights.length; i++) {
			weights[i] = 1;
		}
	}

	public int getParityScore(Othello board, boolean playerOneMadeLastMove, boolean startWeights, long possibleMoves) {
		int parity = -1;
		int parityInDanger = 0;
		long allDiscs = (board.blackPlayerDiscs | board.whitePLayerDiscs);
		boolean nextMoveIsPass = possibleMoves == 0;

		if (Long.bitCount(allDiscs) % 2 == 0) {
			// even number of discs -> player that places next disc has no parity
			if (playerOneMadeLastMove && (!nextMoveIsPass)) {
				// if black made the last move and the next move is no pass:
				// the next disc is placed by white and black has parity
				parity = 1;

			} else if ((!playerOneMadeLastMove) && nextMoveIsPass) {
				// if white made the last move but the next move is a pass:
				// the next disc is placed by white and black has parity
				parity = 1;
			}
			// parity in danger is awarded to the same player as the parity
			if (checkForRegionWithUnevenDiscs(allDiscs)) {
				parityInDanger = parity;
			}
		} else {
			// uneven number of discs -> player that places the next disc has parity
			if ((!playerOneMadeLastMove) && (!nextMoveIsPass)) {
				// if white made the last move and the next move is no pass:
				// black places the next disc and therefore has parity
				parity = 1;
			} else if (playerOneMadeLastMove && nextMoveIsPass) {
				// if black made the last move and the next move is a pass:
				// black places the next disc and therefore has parity
				parity = 1;
			}
			// have to update the moves to the next discs that can be placed
			if (nextMoveIsPass) {
				possibleMoves = board.getLegalMovesAsLong(playerOneMadeLastMove);
			}
			// if there is a Region, where the player with parity has no move,
			// or there are more than 1 uneven regions
			// parity is in danger
			long[] unevenRegions = getUnevenRegions(allDiscs);
			if (unevenRegions != null) {
				for (long unevenRegion : unevenRegions) {
					// if there is no move for the player with parity in a region with an uneven number of
					// squares, parity is in danger
					if (unevenRegion == 0) {
						break;
					}
					if ((possibleMoves & unevenRegion) == 0) {
						parityInDanger = parity;
						break;
					}
				}
			} else {
				parityInDanger = parity;
			}
		}
		if (startWeights) {
			return parity * weights[sParityWeightIndex] + parityInDanger * weights[sParityInDangerIndex];
		}
		return parity * weights[eParityWeightIndex] + parityInDanger * weights[eParityInDangerIndex];

	}

	public long[] getUnevenRegions(long allDiscs) {
		long alreadyCoveredRegions = allDiscs;
		long emptySquares = ~alreadyCoveredRegions;
		long[] regions = new long[2];
		int regionsSize = 0;

		for (int i = 0; i < 64; i++) {
			long region = 1L << i;
			if ((alreadyCoveredRegions & region) == 0) {
				// shift to cover all adjacent squares, that are empty
				long newRegions = getAllAdjacentSquares(region) & emptySquares;
				while (newRegions != region) {
					region = newRegions;
					newRegions = getAllAdjacentSquares(region) & emptySquares;
				}


				if ((Long.bitCount(region) % 2) == 1) {
					regions[regionsSize] = region;
					regionsSize++;
					if (regionsSize == 2) {
						return null;
					}
				}
				alreadyCoveredRegions |= region;
				if (alreadyCoveredRegions == -1) {
					break;
				}
			}
		}
		return regions;
	}

	public boolean checkForRegionWithUnevenDiscs(long allDiscs) {
		// safe every square, that is either occupied by a disc or where we already counted its region
		long alreadyCoveredRegions = allDiscs;
		long emptySquares = ~alreadyCoveredRegions;
		for (int i = 0; i < 64; i++) {
			long region = 1L << i;
			if ((alreadyCoveredRegions & region) == 0) {
				// shift to cover all adjacent squares, that are empty
				long newRegions = getAllAdjacentSquares(region) & emptySquares;
				while (newRegions != region) {
					region = newRegions;
					newRegions = getAllAdjacentSquares(region) & emptySquares;
				}
				if ((Long.bitCount(region) % 2) == 1) {
					return true;
				}
				alreadyCoveredRegions |= region;
				if (alreadyCoveredRegions == -1) {
					break;
				}
			}
		}
		return false;
	}

	public long getAllAdjacentSquares(long position) {
		long result = position;
		for (Shifter shifter : allShifts) {
			result |= shifter.shift(position);
		}
		return result;
	}

	private int getDiscPositionScore(Othello board, boolean startWeights) {


		int center4StonesSum = Long.bitCount(board.blackPlayerDiscs & center4StonesMask);
		center4StonesSum -= Long.bitCount(board.whitePLayerDiscs & center4StonesMask);

		int nextToCenterStonesSum = Long.bitCount(board.blackPlayerDiscs & nextToCenterStonesMask);
		nextToCenterStonesSum -= Long.bitCount(board.whitePLayerDiscs & nextToCenterStonesMask);

		int nextToEdgeStoneSum = Long.bitCount(board.blackPlayerDiscs & nextToEdgeStonesMask);
		nextToEdgeStoneSum -= Long.bitCount(board.whitePLayerDiscs & nextToEdgeStonesMask);

		int edgeDiscsSum = Long.bitCount(board.blackPlayerDiscs & edgeDiscsMask);
		edgeDiscsSum -= Long.bitCount(board.whitePLayerDiscs & edgeDiscsMask);

		if (startWeights) {
			return center4StonesSum * weights[sCenter4StonesIndex]
					+ nextToCenterStonesSum * weights[sNextToCenterDiscsIndex]
					+ nextToEdgeStoneSum * weights[sNextToEdgeDiscsIndex]
					+ edgeDiscsSum * weights[sEdgeDiscsIndex];
		}
		return center4StonesSum * weights[eCenter4StonesIndex]
				+ nextToCenterStonesSum * weights[eNextToCenterDiscsIndex]
				+ nextToEdgeStoneSum * weights[eNextToEdgeDiscsIndex]
				+ edgeDiscsSum * weights[eEdgeDiscsIndex];
	}

	private int getPossibleMovesScore(long possibleMoves, boolean playerOneMadeLastMove, boolean startWeights) {
		int score = Long.bitCount(possibleMoves);
		if (playerOneMadeLastMove) {
			score = score * -1;
		}
		if (startWeights) {
			return score * weights[sPossibleMovesIndex];
		}
		return score * weights[ePossibleMovesIndex];
	}

	private int getFrontierDiscsScore(Othello board, boolean startWeights) {
		int difference = getFrontiersDiscDifference(board);
		if (startWeights) {
			return weights[sFrontierDiscsIndex] * (difference);
		}
		return weights[eFrontierDiscsIndex] * (difference);

	}

	public int getCornerAndStableDiscs(Othello board, boolean startWeights) {

		long blackPlayer = board.blackPlayerDiscs;
		long whitePlayer = board.whitePLayerDiscs;

		// check corners
		// get corner, stable discs, dangerous discs and discs next to corner on edge
		long blackStableDiscs = 0L;
		long whiteStableDiscs = 0L;

		int cornerSum = 0;
		int dangerousDiscsSum = 0;
		int discsNextToCornerOnEdgeSum = 0;
		// upper left corner
		if ((blackPlayer & upLeftCornerMask) != 0L) {
			cornerSum++;
			blackStableDiscs = getStableDiscUpLeftCorner(blackPlayer);
		} else if ((whitePlayer & upLeftCornerMask) != 0L) {
			cornerSum--;
			whiteStableDiscs = getStableDiscUpLeftCorner(whitePlayer);
		} else {
			discsNextToCornerOnEdgeSum += Long.bitCount(0x102L & blackPlayer);
			discsNextToCornerOnEdgeSum -= Long.bitCount(0x102L & whitePlayer);

			dangerousDiscsSum += (512L & blackPlayer) != 0 ? 1 : 0;
			dangerousDiscsSum -= (512L & whitePlayer) != 0 ? 1 : 0;
		}

		// upper right corner
		if ((blackPlayer & upRightCornerMask) != 0L) {
			cornerSum++;
			blackStableDiscs |= getStableDiscUpRightCorner(blackPlayer);
		} else if ((whitePlayer & upRightCornerMask) != 0L) {
			cornerSum--;
			whiteStableDiscs |= getStableDiscUpRightCorner(whitePlayer);
		} else {
			discsNextToCornerOnEdgeSum += Long.bitCount(0x8040L & blackPlayer);
			discsNextToCornerOnEdgeSum -= Long.bitCount(0x8040L & whitePlayer);

			dangerousDiscsSum += (0x4000L & blackPlayer) != 0 ? 1 : 0;
			dangerousDiscsSum -= (0x4000L & whitePlayer) != 0 ? 1 : 0;
		}

		// down Left corner
		if ((blackPlayer & downLeftCornerMask) != 0L) {
			cornerSum++;
			blackStableDiscs |= getStableDiscDownLeftCorner(blackPlayer);
		} else if ((whitePlayer & downLeftCornerMask) != 0L) {
			cornerSum--;
			whiteStableDiscs |= getStableDiscDownLeftCorner(whitePlayer);
		} else {
			discsNextToCornerOnEdgeSum += Long.bitCount(0x201000000000000L & blackPlayer);
			discsNextToCornerOnEdgeSum -= Long.bitCount(0x201000000000000L & whitePlayer);

			dangerousDiscsSum += (0x2000000000000L & blackPlayer) != 0 ? 1 : 0;
			dangerousDiscsSum -= (0x2000000000000L & whitePlayer) != 0 ? 1 : 0;
		}

		// down right corner
		if ((blackPlayer & downRightCornerMask) != 0L) {
			cornerSum++;
			blackStableDiscs |= getStableDiscDownRightCorner(blackPlayer);
		} else if ((whitePlayer & downRightCornerMask) != 0L) {
			cornerSum--;
			whiteStableDiscs |= getStableDiscDownRightCorner(whitePlayer);
		} else {
			discsNextToCornerOnEdgeSum += Long.bitCount(0x4080000000000000L & blackPlayer);
			discsNextToCornerOnEdgeSum -= Long.bitCount(0x4080000000000000L & whitePlayer);

			dangerousDiscsSum += (0x40000000000000L & blackPlayer) != 0 ? 1 : 0;
			dangerousDiscsSum -= (0x40000000000000L & whitePlayer) != 0 ? 1 : 0;
		}

		// if edge is full, add it to stable disc
		long occupancy = blackPlayer | whitePlayer;
		for (long edgeMask : edgeMasks) {
			if ((edgeMask & occupancy) == edgeMask) {
				blackStableDiscs |= blackPlayer & edgeMask;
				whiteStableDiscs |= whitePlayer & edgeMask;
			}
		}

		// make sure the corners are not counted as stable discs
		int stableDiscSum = Long.bitCount(blackStableDiscs & 0x7effffffffffff7eL) - Long.bitCount(whiteStableDiscs & 0x7effffffffffff7eL);

		if (startWeights) {
			return weights[sCornerDiscsIndex] * cornerSum
					+ weights[sStableDiscsIndex] * stableDiscSum
					+ weights[sDangerDiscsIndex] * dangerousDiscsSum
					+ weights[sEdgeNextToCornerDiscsIndex] * discsNextToCornerOnEdgeSum;
		} else {
			return weights[eCornerDiscsIndex] * cornerSum
					+ weights[eStableDiscsIndex] * stableDiscSum
					+ weights[eDangerDiscsIndex] * dangerousDiscsSum
					+ weights[eEdgeNextToCornerDiscsIndex] * discsNextToCornerOnEdgeSum;
		}
	}

	public int getEdgeScores(Othello board, boolean playerOneMadeLastMove, boolean startWeight) {
		int evenGapSum = 0;
		int unEvenGapSum = 0;
		int wedgeSum = 0;
		int safelyFlippableDiscsSum = 0;
		int pairSum = 0;
		int balancedEdgeSum = 0;
		int unbalancedEdgeSum = 0;
		int stableDiscSum = 0;
		// 16 is as rough estimate of the maximum number of contested flips
		int[] contestedFlips = new int[16];
		int contestedFlipsSize = 0;
		// All edges are represented as start and end edge, and the shift to get from start to end
		// startEdges[1], endEdges[1] and shift[1] is the upper edge
		// iterate over edges
		long blackPlayer = board.blackPlayerDiscs;
		long whitePlayer = board.whitePLayerDiscs;
		long occupancy = blackPlayer | whitePlayer;
		for (int i = 0; i < 4; i++) {
			// check if both corners are occupied
			boolean startCornerIsOcc = (startCorner[i] & occupancy) != 0L;
			boolean endCornerIsOcc = (endCorner[i] & occupancy) != 0L;
			boolean blackPlayerOnEdge = (edgeMasks[i] & blackPlayer) != 0L;
			boolean whitePlayerOnEdge = (edgeMasks[i] & whitePlayer) != 0L;
			boolean bothPlayerOnEdge = blackPlayerOnEdge && whitePlayerOnEdge;
			boolean edgeIsFull = ((edgeMasks[i] & blackPlayer) | (edgeMasks[i] & whitePlayer)) == edgeMasks[i];

			if (edgeIsFull) {
				// simply count everything as stable Disc;

			} else if (startCornerIsOcc || endCornerIsOcc || bothPlayerOnEdge) {
				// 0: before border, 1: gap with length > 1, 2: single gap ending with black
				// 3: single gap ending in white, 4: black discs, 5: white discs
				int lastChain = 0;
				//-1: no value, 0: gap, 1: black, 2: white
				int currentChain = -1;
				int currentChainLength = 0;

				long currentPosition = startCorner[i];
				for (int edgeIndex = 0; edgeIndex < 8; edgeIndex++) {
					int newDisc = board.getDiscAtField(currentPosition);
					// Lord forgive, for what I am about to do
					// this spaghetti is my sin
					if (newDisc == currentChain) {
						currentChainLength++;
					} else {
						int breakCase = getChainBreakCase(lastChain, currentChain, newDisc);
						// starting from an empty corner, we found first discs
						if (breakCase == 0) {
							// flip is safe, since there are no discs adjacent
							lastChain = 1;
							currentChain = newDisc;
							currentChainLength = 1;
						} else if (breakCase == 1) {
							// gap between two black chains, followed by white chain
							// white has no safe flip to the left, but black might have to the right
							// white might have a safe flip, if the chain ends in a corner
							// or reverse colours
							LookAheadChain lookAheadChain = new LookAheadChain(currentChain, newDisc, edgeIndex, currentPosition, shiftsAlongEdge[i], board);
							if (lookAheadChain.safeFlipToRight) {
								if (currentChain == 1) {
									safelyFlippableDiscsSum += currentChainLength + lookAheadChain.chainLength;
								} else {
									safelyFlippableDiscsSum -= currentChainLength + lookAheadChain.chainLength;
								}
							} else {
								// check if the next chain goes to a corner, if so
								// it is a safe flip for the new disc player
								if (edgeIndex + lookAheadChain.chainLength == 8) {
									if (newDisc == 1) {
										safelyFlippableDiscsSum += currentChainLength + lookAheadChain.chainLength;
									} else {
										safelyFlippableDiscsSum -= currentChainLength + lookAheadChain.chainLength;
									}
									// since the next chains end at a corner, we can break
									break;
								}
							}
							// update variables
							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;

						} else if (breakCase == 2) {
							// last chain was a gap, so we have a safe skip for new disc, but if current may have a
							// safe skip too
							LookAheadChain lookAheadChain = new LookAheadChain(currentChain, newDisc, edgeIndex, currentPosition, shiftsAlongEdge[i], board);
							if (lookAheadChain.safeFlipToRight) {
								// flip in both directions is possible
								int safeFlip = currentChainLength + lookAheadChain.chainLength;
								contestedFlips[contestedFlipsSize] = safeFlip;
								contestedFlipsSize++;
							} else if (lookAheadChain.chainBreaker != currentChain) {
								// but it also could be a wedge if the chain is broken by current chain
								if (newDisc == 1) {
									safelyFlippableDiscsSum += currentChainLength + lookAheadChain.chainLength;
								} else {
									safelyFlippableDiscsSum -= currentChainLength + lookAheadChain.chainLength;

								}
							}

							// update variables
							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;


						} else if (breakCase == 3) {
							// we have a gap between to of the same disc, no we check how long it is
							int gapScore = 1;
							if (newDisc == 2) {
								gapScore = -1;
							}
							if (currentChainLength % 2 == 0) {
								evenGapSum += gapScore;
							} else {
								unEvenGapSum += gapScore;
							}

							// 0: before border, 1: gap with length > 1, 2: single gap ending with black
							// 3: single gap ending in white
							if (currentChainLength > 1) {
								lastChain = 1;
							} else {
								if (newDisc == 1) {
									lastChain = 2;
								} else {
									lastChain = 3;
								}
							}
							currentChain = newDisc;
							currentChainLength = 1;

						} else if (breakCase == 4) {
							// gap between two different discs
							if (currentChainLength > 1) {
								lastChain = 1;
							} else {
								if (lastChain == 4) {
									lastChain = 2;
								} else {
									lastChain = 3;
								}
							}
							currentChain = newDisc;
							currentChainLength = 1;
						} else if (breakCase == 5) {
							// white wedge
							wedgeSum -= currentChainLength;

							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;
						} else if (breakCase == 6) {
							// black wedge
							wedgeSum += currentChainLength;

							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;
						} else if (breakCase == 7) {
							int flipScore = getSafeFlipScore(newDisc, edgeIndex, currentPosition, shiftsAlongEdge[i], board);
							if (flipScore > 0) {
								if (currentChain == 1) {
									safelyFlippableDiscsSum += flipScore + currentChainLength;
								} else {
									safelyFlippableDiscsSum -= flipScore + currentChainLength;
								}
							}
							// update variables
							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;
						} else {
							lastChain = getLastChainFromCurrent(currentChain);
							currentChain = newDisc;
							currentChainLength = 1;
						}

					}

					currentPosition = shiftsAlongEdge[i].shift(currentPosition);
				}
				// check for pair, wedges, balanced and unbalanced edge
			} else {

				long playerOnEdge;
				if (blackPlayerOnEdge) {
					playerOnEdge = blackPlayer;
				} else {
					playerOnEdge = whitePlayer;
				}
				int[] gaps = new int[]{1, 0, 0, 0};

				int gapsFound = 1;
				boolean currentlyOnGap = true;

				long currentPosition = shiftsAlongEdge[i].shift(startCorner[i]);
				for (int edgeIndex = 1; edgeIndex < 8; edgeIndex++) {
					boolean isOnGap = (currentPosition & playerOnEdge) == 0L;
					if (currentlyOnGap) {
						if (isOnGap) {
							gaps[gapsFound - 1] = gaps[gapsFound - 1] + 1;
						} else {
							currentlyOnGap = false;
						}
					} else if (isOnGap) {
						gaps[gapsFound] = 1;
						gapsFound++;
						currentlyOnGap = true;
					}
					currentPosition = shiftsAlongEdge[i].shift(currentPosition);

				}

				// if we have 2 gaps, they must be at the beginning and the end -> unbalanced or balanced edge
				if (gapsFound == 2) {
					int edgeScore = 8 - (gaps[0] + gaps[1]);
					if (whitePlayerOnEdge) {
						edgeScore *= -1;
					}
					if (gaps[0] == gaps[1]) {
						balancedEdgeSum += edgeScore;
					} else {
						unbalancedEdgeSum += edgeScore;
					}
				} else if (gaps[0] == 2 && gaps[1] == 2 && gaps[2] == 2) {
					if (whitePlayerOnEdge) {
						pairSum--;
					} else {
						pairSum++;
					}
				} else {
					int tempEvenGapSum = 0;
					int tempUnevenGapSum = 0;
					for (int gapIndex = 1; gapIndex < gapsFound - 1; gapIndex++) {
						int gapLength = gaps[gapIndex];
						if (gapLength % 2 == 0) {
							tempEvenGapSum++;
						} else {
							tempUnevenGapSum++;
						}
					}
					if (whitePlayerOnEdge) {
						tempEvenGapSum *= -1;
						tempUnevenGapSum *= -1;
					}
					evenGapSum += tempEvenGapSum;
					unEvenGapSum += tempUnevenGapSum;


				}


			}


		}
		// add contested flips
		contestedFlips = shortenAndSortArray(contestedFlips, contestedFlipsSize);
		int playerThatFlips = 1;
		if (playerOneMadeLastMove) {
			playerThatFlips = -1;
		}

		for (int i = 0; i < contestedFlipsSize; i++) {
			safelyFlippableDiscsSum += contestedFlips[i] * playerThatFlips;
			playerThatFlips *= -1;
		}


		if (startWeight) {
			return safelyFlippableDiscsSum * weights[sFlippableEdgeWeightIndex]
					+ evenGapSum * weights[sEvenEdgeGapIndex]
					+ unEvenGapSum * weights[sUnevenEdgeGapIndex]
					+ wedgeSum * weights[sWedgeIndex]
					+ pairSum * weights[sPairIndex]
					+ balancedEdgeSum * weights[sBalancedEdgeIndex]
					+ unbalancedEdgeSum * weights[sUnbalancedEdgeIndex]
					+ stableDiscSum * weights[sStableDiscsIndex];
		} else {
			return safelyFlippableDiscsSum * weights[eFlippableEdgeWeightIndex]
					+ evenGapSum * weights[eEvenEdgeGapIndex]
					+ unEvenGapSum * weights[eUnevenEdgeGapIndex]
					+ wedgeSum * weights[eWedgeIndex]
					+ pairSum * weights[ePairIndex]
					+ balancedEdgeSum * weights[eBalancedEdgeIndex]
					+ unbalancedEdgeSum * weights[eUnbalancedEdgeIndex]
					+ stableDiscSum * weights[eStableDiscsIndex];
		}
	}


	private int getDiscDifferenceScore(Othello board, boolean startWeight) {
		int dif = board.getDiscDifference();
		if (startWeight) {
			return dif * weights[sDiscDifferenceWeightIndex];
		}
		return dif * weights[eDiscDifferenceWeightIndex];

	}

	public int getSafeFlipScore(int newDisc, int edgeIndex, long currentPosition, Shifter shift, Othello board) {
		if (edgeIndex >= 7) {
			return 0;
		}
		// shift to see if we have a safe flip
		currentPosition = shift.shift(currentPosition);
		int lookAheadIndex = edgeIndex + 1;
		int lookAheadDisc = board.getDiscAtField(currentPosition);
		// shift ahead to next chain breaker
		while (lookAheadDisc == newDisc && lookAheadIndex < 7) {
			currentPosition = shift.shift(currentPosition);
			lookAheadIndex++;
			lookAheadDisc = board.getDiscAtField(currentPosition);
		}
		if (lookAheadDisc != 0) {
			return 0;
		}
		return lookAheadIndex - edgeIndex;
	}

	public interface Shifter {
		long shift(long x);
	}

	public static class LookAheadChain {
		int chainBreaker;
		boolean safeFlipToRight;
		int chainLength;

		public LookAheadChain(int currentChain, int newDisc, int edgeIndex, long currentPosition, Shifter shift, Othello board) {
			if (edgeIndex >= 7) {
				this.safeFlipToRight = false;
				this.chainBreaker = -1;
				this.chainLength = 1;
				return;
			}
			// shift to see if we have a safe flip
			currentPosition = shift.shift(currentPosition);
			int lookAheadIndex = edgeIndex + 1;
			int lookAheadDisc = board.getDiscAtField(currentPosition);
			// shift ahead to next chain breaker
			while (lookAheadDisc == newDisc && lookAheadIndex < 7) {
				currentPosition = shift.shift(currentPosition);
				lookAheadIndex++;
				lookAheadDisc = board.getDiscAtField(currentPosition);
			}
			this.chainBreaker = lookAheadDisc;
			if (lookAheadDisc == newDisc) {
				this.chainBreaker = -1;
			}
			this.chainLength = lookAheadIndex - edgeIndex;
			// check if corner

			if (lookAheadDisc == 0) {
				// we found empty spot, now if the next spot is empty, or it is occupied by the same as
				// chain, it is a safe flip
				// it also a safe flip, if we are at a corner
				if (lookAheadIndex == 7) {
					this.safeFlipToRight = true;
				} else {
					int newLookAheadDisc = board.getDiscAtField(shift.shift(currentPosition));
					if (newLookAheadDisc == 0 || newLookAheadDisc == currentChain) {
						this.safeFlipToRight = true;
					}
				}
			} else {
				// if the last disc is not empty we might have not counted the corner in the chain length
				// we need to account for that
				if (lookAheadIndex == 7 && lookAheadDisc == newDisc) {
					this.chainLength++;
				}
			}


		}
	}
}
