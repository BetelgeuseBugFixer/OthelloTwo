package ai;

import othello.Othello;
import othelloTrees.OthelloTree;

import java.util.Arrays;

public class BetterGrader implements BoardGrader {
    static final long leftBorderBitMask = 0b1111111011111110111111101111111011111110111111101111111011111110L;
    static long rightBorderBitMask = 0b111111101111111011111110111111101111111011111110111111101111111L;
    static long upperBorderBitMask = 0xffffffffffffff00L;
    static long downBorderBitMask = 0xffffffffffffffL;
    static long upRightBorderBitMask = 9187201950435737344L;
    static long upLeftBorderBitMask = 0xfefefefefefefe00L;
    static long downRightBorderBitMask = 0x7f7f7f7f7f7f7fL;
    static long downLeftBorderBitMask = 0xfefefefefefefeL;
    static long upLeftCornerMask = 1L;
    static long upRightCornerMask = 0x80L;
    static long downLeftCornerMask = 0x100000000000000L;
    static long downRightCornerMask = 0x8000000000000000L;
    //All edges are represented as start and end edge, and the shift to get from start to end
    //startEdges[i], endEdges[i] and shift[i] is therefore the upper edge
    static long[] startCorner = {upLeftCornerMask, upLeftCornerMask, upRightCornerMask, downLeftCornerMask};
    static long[] endCorner = {upRightCornerMask, downLeftCornerMask, downRightCornerMask, downRightCornerMask};
    static Shifter[] shifts = {BetterGrader::shiftRight, BetterGrader::shiftDown, BetterGrader::shiftDown, BetterGrader::shiftRight};
    static long[] borderMasks = {~upperBorderBitMask, ~leftBorderBitMask, ~rightBorderBitMask, ~downBorderBitMask};

    public int[] weights = {20, 10, -20, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 60, 1, 20, 10, -5, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 50, 2, 30};
    //to include in metric:
    //-two unbalanced edges next to each other
    //-parity
    //-corner capture
    int sMovesEnd = 0;
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
    int ePossibleMovesIndex = 19;
    int eFrontierDiscsIndex = 20;
    int eStableDiscsIndex = 21;
    int eBalancedEdgeIndex = 22;
    int eUnbalancedEdgeIndex = 23;
    int ePairIndex = 24;
    int eWedgeIndex = 25;
    int eUnevenEdgeGapIndex = 26;
    int eEvenEdgeGapIndex = 27;
    int eCenter4StonesIndex = 28;
    int eNextToCenterDiscsIndex = 29;
    int eNextToEdgeDiscsIndex = 30;
    int eDangerDiscsIndex = 31;
    int eEdgeDiscsIndex = 32;
    int eEdgeNextToCornerDiscsIndex = 33;
    int eCornerDiscsIndex = 34;
    int eDiscDifferenceWeightIndex = 35;
    int eFlippableEdgeWeightIndex = 36;


    public void setWeights(int[] weights){
        this.weights=weights;
    }

    public void setAllWeightsToOne(){
        for (int i = 0; i < this.weights.length; i++) {
            weights[i]=1;
        }
    }


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

        //get all stable discs from initial corner

        //first shift
        long lastStableDiscs;
        long tempStableDisc = corner;
        do {
            lastStableDiscs = tempStableDisc;
            tempStableDisc |= verticalEdgeShift.shift(tempStableDisc) & playerDiscs;
        } while (lastStableDiscs != tempStableDisc);
        stableDiscs |= tempStableDisc;

        //second shift

        tempStableDisc = corner;
        do {
            lastStableDiscs = tempStableDisc;
            tempStableDisc |= horizontalEdgeShift.shift(tempStableDisc) & playerDiscs;
        } while (lastStableDiscs != tempStableDisc);
        stableDiscs |= tempStableDisc;

        //shift in 3 directions to find rest of stable discs
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
        //0: before border, 1: gap with length > 1, 2: single gap ending with black
        //3: single gap ending in white, 4: black discs, 5: white discs

        //beginning of board followed by empty squares is always a safe flip
        if (lastChain == 0 && currentChain == 0) {
            return 0;
        } else if (lastChain == 0 && ((currentChain == 1 && newDisc == 2) || (currentChain == 2 && newDisc == 1))) {
            //edge beginns with row of discs, that is interrupted by other discs -> we need to check for a safe flip
            return 7;
        } else if (lastChain == 1 && ((currentChain == 1 && newDisc == 2) || currentChain == 2 && newDisc == 1)) {
            //a gap >1 is followed by 2 lines od adversary discs. to the left there is a safe flip, but maybe also to the right
            return 2;
        } else if (((lastChain == 4 && newDisc == 1) || lastChain == 5 && newDisc == 2) && currentChain == 0) {
            //empty chain between to discs from same player, introduce gap and update lastChain
            return 3;
        } else if (((lastChain == 4 && newDisc == 2) || lastChain == 5 && newDisc == 1) && currentChain == 0) {
            //empty squares between discs from different players, update gap
            return 4;
        } else if (lastChain == 4 && currentChain == 2 && newDisc == 1) {
            //white wedge
            return 5;
        } else if (lastChain == 5 && currentChain == 1 && newDisc == 2) {
            //black wedge
            return 6;
        } else if (lastChain == 2 && currentChain == 1 && newDisc == 2) {
            //gap between two black chains, followed by white chain
            //white has no safe flip to the left, but black might have to the right
            return 1;
        } else if (lastChain == 3 && currentChain == 2 && newDisc == 1) {
            //same as above with other colours
            return 1;
        } else if (lastChain == 2 && currentChain == 2 && newDisc == 1) {
            //white discs + single gap + black disc+ white discs -> safe flip for white, but possibly also for black
            //both directions need to be checked
            return 2;

        } else if (lastChain == 3 && currentChain == 1 && newDisc == 0) {
            //same as above with reverse discs
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

    @Override
    public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
        Othello board = node.getBoard();
        int discsSet = Long.bitCount(board.blackPlayerDiscs & board.whitePLayerDiscs);
        boolean startWeights = discsSet < weights[sMovesEnd];

        int score = 0;

        score += getPossibleMovesScore(node, playerOne, startWeights);
        score += getFrontierDiscsScore(board, startWeights);
        score += getCornerAndStableDiscs(board, startWeights);
        score += getEdgeScores(board, playerOne, startWeights);


        return score;
    }

    private int getPossibleMovesScore(OthelloTree.OthelloNode node, boolean playerOne, boolean startWeights) {
        int score = 0;
        for (OthelloTree.OthelloNode nextMove : node.getNextNodes(!playerOne)) {
            if (nextMove == null) {
                break;
            }
            score++;
        }
        if (!playerOne) {
            score = score * -1;
        }
        if (startWeights) {
            return score * weights[sPossibleMovesIndex];
        }
        return score * weights[ePossibleMovesIndex];
    }

    private int getFrontierDiscsScore(Othello board, boolean startWeights) {
        long blackPlayerDiscs = board.blackPlayerDiscs;
        long whitePlayerDiscs = board.whitePLayerDiscs;
        long allDiscs = blackPlayerDiscs | whitePlayerDiscs;
        long empty = ~allDiscs;
        long frontierDiscs = allDiscs & (shiftDown(empty) | shiftUp(empty) | shiftLeft(empty) | shiftRight(empty) | shiftUpLeft(empty) | shiftUpRight(empty) | shiftDownLeft(empty) | shiftDownRight(empty));

        int blackFrontierDiscs = Long.bitCount(frontierDiscs & blackPlayerDiscs);
        int whiteFrontierDiscs = Long.bitCount(frontierDiscs & whitePlayerDiscs);
        if (startWeights) {
            return weights[sFrontierDiscsIndex] * (blackFrontierDiscs - whiteFrontierDiscs);
        }
        return weights[eFrontierDiscsIndex] * (blackFrontierDiscs - whiteFrontierDiscs);

    }

    public int getCornerAndStableDiscs(Othello board, boolean startWeights) {

        long blackPlayer = board.blackPlayerDiscs;
        long whitePlayer = board.whitePLayerDiscs;

        //check corners
        //get corner, stable discs, dangerous discs and discs next to corner on edge
        long blackStableDiscs = 0L;
        long whiteStableDiscs = 0L;

        int cornerSum = 0;
        int dangerousDiscsSum = 0;
        int discsNextToCornerOnEdgeSum = 0;
        //upper left corner
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

        //upper right corner
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

        //down Left corner
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

        //down right corner
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

        int stableDiscSum = Long.bitCount(blackStableDiscs) - Long.bitCount(whiteStableDiscs);

        if (startWeights) {
            return weights[sCornerDiscsIndex] * cornerSum + weights[sStableDiscsIndex] * stableDiscSum + weights[sDangerDiscsIndex] + dangerousDiscsSum + weights[sEdgeNextToCornerDiscsIndex] * discsNextToCornerOnEdgeSum;
        } else {
            return weights[eCornerDiscsIndex] * cornerSum + weights[eStableDiscsIndex] * stableDiscSum + weights[eDangerDiscsIndex] + dangerousDiscsSum + weights[eEdgeNextToCornerDiscsIndex] * discsNextToCornerOnEdgeSum;
        }
    }

    public int getEdgeScores(Othello board, boolean playerOne, boolean startWeight) {
        int evenGapSum = 0;
        int unEvenGapSum = 0;
        int wedgeSum = 0;
        int safelyFlippableDiscsSum = 0;
        int pairSum = 0;
        int balancedEdgeSum = 0;
        int unbalancedEdgeSum = 0;
        //16 is as rough estimate of the maximum number of contested flips
        int[] contestedFlips = new int[16];
        int contestedFlipsSize = 0;
        //All edges are represented as start and end edge, and the shift to get from start to end
        //startEdges[1], endEdges[1] and shift[1] is the upper edge
        //iterate over edges
        long blackPlayer = board.blackPlayerDiscs;
        long whitePlayer = board.whitePLayerDiscs;
        long occupancy = blackPlayer | whitePlayer;
        for (int i = 0; i < 4; i++) {
            //check if both corners are occupied
            boolean startCornerIsOcc = (startCorner[i] & occupancy) != 0L;
            boolean endCornerIsOcc = (endCorner[i] & occupancy) != 0L;
            boolean blackPlayerOnEdge = (borderMasks[i] & blackPlayer) != 0L;
            boolean whitePlayerOnEdge = (borderMasks[i] & whitePlayer) != 0L;
            boolean bothPlayerOnEdge = blackPlayerOnEdge && whitePlayerOnEdge;

            if (startCornerIsOcc || endCornerIsOcc || bothPlayerOnEdge) {
                //0: before border, 1: gap with length > 1, 2: single gap ending with black
                //3: single gap ending in white, 4: black discs, 5: white discs
                int lastChain = 0;
                //-1: no value, 0: gap, 1: black, 2: white
                int currentChain = -1;
                int currentChainLength = 0;

                long currentPosition = startCorner[i];
                for (int edgeIndex = 0; edgeIndex < 8; edgeIndex++) {
                    int newDisc = board.getDiscAtField(currentPosition);
                    //Lord forgive, for what I am about to do
                    //this spaghetti is my sin
                    if (newDisc == currentChain) {
                        currentChainLength++;
                    } else {
                        int breakCase = getChainBreakCase(lastChain, currentChain, newDisc);
                        //starting from an empty corner, we found first discs
                        if (breakCase == 0) {
                            //flip is safe, since there are no discs adjacent
                            lastChain = 1;
                            currentChain = newDisc;
                            currentChainLength = 1;
                        } else if (breakCase == 1) {
                            //gap between two black chains, followed by white chain
                            //white has no safe flip to the left, but black might have to the right
                            //white might have a safe flip, if the chain ends in a corner
                            //or reverse colours
                            int chainLength = getNextChainLengthAndCheckSafeFlip(currentChain, newDisc, edgeIndex, currentPosition, shifts[i], board);
                            if (chainLength > 0) {
                                if (currentChain == 1) {
                                    safelyFlippableDiscsSum += currentChainLength + chainLength;
                                } else {
                                    safelyFlippableDiscsSum -= currentChainLength + chainLength;
                                }
                            } else {
                                //check if the next chain goes to a corner, if so
                                //it is a safe flip for the new disc player
                                chainLength = Math.abs(chainLength);
                                if (edgeIndex + chainLength == 8) {
                                    if (newDisc == 1) {
                                        safelyFlippableDiscsSum += currentChainLength + chainLength;
                                    } else {
                                        safelyFlippableDiscsSum -= currentChainLength + chainLength;
                                    }
                                    //since the next chains end at a corner, we can break
                                    break;
                                }
                            }
                            //update variables
                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;

                        } else if (breakCase == 2) {
                            //last chain was a gap, so we have a safe skip for new disc, but if current may have a
                            //safe skip too
                            int safeSkipAlongEdge = getNextChainLengthAndCheckSafeFlip(currentChain, newDisc, edgeIndex, currentPosition, shifts[i], board);
                            if (safeSkipAlongEdge > 0) {
                                //flip in both directions is possible
                                int safeFlip = currentChainLength + safeSkipAlongEdge;
                                contestedFlips[contestedFlipsSize] = safeFlip;
                                contestedFlipsSize++;
                            } else {
                                //safeSkipAlongEdge now is the length of the next chain *-1, therefore we now change
                                // that and add those to safelyFlippable discs
                                safeSkipAlongEdge = Math.abs(safeSkipAlongEdge);
                                if (newDisc == 1) {
                                    safelyFlippableDiscsSum += currentChainLength + safeSkipAlongEdge;
                                } else {
                                    safelyFlippableDiscsSum -= currentChainLength + safeSkipAlongEdge;

                                }
                            }

                            //update variables
                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;


                        } else if (breakCase == 3) {
                            //we have a gap between to of the same disc, no we check how long it is
                            int gapScore = 1;
                            if (newDisc == 2) {
                                gapScore = -1;
                            }
                            if (currentChainLength % 2 == 0) {
                                evenGapSum += gapScore;
                            } else {
                                unEvenGapSum += gapScore;
                            }

                            //0: before border, 1: gap with length > 1, 2: single gap ending with black
                            //3: single gap ending in white
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
                            //gap between two different discs
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
                            //white wedge
                            wedgeSum -= currentChainLength;

                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;
                        } else if (breakCase == 6) {
                            //black wedge
                            wedgeSum += currentChainLength;

                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;
                        } else if (breakCase == 7) {
                            int flipScore = getSafeFlipScore(newDisc, edgeIndex, currentPosition, shifts[i], board);
                            if (flipScore > 0) {
                                if (currentChain == 1) {
                                    safelyFlippableDiscsSum += flipScore + currentChainLength;
                                } else {
                                    safelyFlippableDiscsSum -= flipScore + currentChainLength;
                                }
                            }
                            //update variables
                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;
                        } else {
                            lastChain = getLastChainFromCurrent(currentChain);
                            currentChain = newDisc;
                            currentChainLength = 1;
                        }

                    }

                    currentPosition = shifts[i].shift(currentPosition);
                }
                //check for pair, wedges, balanced and unbalanced edge
            } else {

                long playerOnEdge;
                if (blackPlayerOnEdge) {
                    playerOnEdge = blackPlayer;
                } else {
                    playerOnEdge = whitePlayer;
                }
                int[] gaps = new int[]{1,0,0,0};

                int gapsFound = 1;
                boolean currentlyOnGap = true;

                long currentPosition = shifts[i].shift(startCorner[i]);
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
                    currentPosition = shifts[i].shift(currentPosition);

                }

                //if we have 2 gaps, they must be at the beginning and the end -> unbalanced or balanced edge
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
                    for (int gapIndex = 1; gapIndex < gapsFound-1; gapIndex++) {
                        int gapLength = gaps[gapIndex];
                        if (gapLength % 2 == 0) {
                            tempEvenGapSum ++;
                        } else {
                            tempUnevenGapSum ++;
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
        //add contested flips
        contestedFlips = shortenAndSortArray(contestedFlips, contestedFlipsSize);
        int playerThatFlips = -1;
        if (playerOne) {
            playerThatFlips = 1;
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
                    + unbalancedEdgeSum * weights[sUnbalancedEdgeIndex];
        } else {
            return safelyFlippableDiscsSum * weights[eFlippableEdgeWeightIndex]
                    + evenGapSum * weights[eEvenEdgeGapIndex]
                    + unEvenGapSum * weights[eUnevenEdgeGapIndex]
                    + wedgeSum * weights[eWedgeIndex]
                    + pairSum * weights[ePairIndex]
                    + balancedEdgeSum * weights[eBalancedEdgeIndex]
                    + unbalancedEdgeSum * weights[eUnbalancedEdgeIndex];
        }
    }

    //returns the length of the next chain, if it is not a safe flip it returns the length *-1
    public int getNextChainLengthAndCheckSafeFlip(int currentChain, int newDisc, int edgeIndex, long currentPosition, Shifter shift, Othello board) {
        if (edgeIndex >= 7) {
            return -1;
        }
        //shift to see if we have a safe flip
        currentPosition = shift.shift(currentPosition);
        int lookAheadIndex = edgeIndex + 1;
        int lookAheadDisc = board.getDiscAtField(currentPosition);
        //shift ahead to next chain breaker
        while (lookAheadDisc == newDisc && lookAheadIndex < 7) {
            currentPosition = shift.shift(currentPosition);
            lookAheadIndex++;
            lookAheadDisc = board.getDiscAtField(currentPosition);
        }
        //initialize as negative and only make positive when there is a safe flip
        int chainSize = -(lookAheadIndex - edgeIndex);
        //check if corner

        if (lookAheadDisc == 0) {
            //we found empty spot, now if the next spot is empty, or it is occupied by the same as
            // chain, it is a safe flip
            //it also a safe flip, if we are at a corner
            if (lookAheadIndex == 7) {
                chainSize *= -1;
            } else {
                int newLookAheadDisc = board.getDiscAtField(shift.shift(currentPosition));
                if (newLookAheadDisc == 0 || newLookAheadDisc == currentChain) {
                    chainSize *= -1;
                }
            }
        } else {
            //if the last disc is not empty we might have not counted the corner in the chain length
            //we need to account for that
            if (lookAheadIndex == 7 && lookAheadDisc == newDisc) {
                chainSize--;
            }
        }

        return chainSize;
    }

    public int getSafeFlipScore(int newDisc, int edgeIndex, long currentPosition, Shifter shift, Othello board) {
        if (edgeIndex >= 7) {
            return 0;
        }
        //shift to see if we have a safe flip
        currentPosition = shift.shift(currentPosition);
        int lookAheadIndex = edgeIndex + 1;
        int lookAheadDisc = board.getDiscAtField(currentPosition);
        //shift ahead to next chain breaker
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
}
