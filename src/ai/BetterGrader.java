package ai;

import othello.Othello;
import othelloTrees.OthelloTree;

public class BetterGrader implements BoardGrader {
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

    int ePossibleMovesIndex = 18;
    int eFrontierDiscsIndex = 19;
    int eStableDiscsIndex = 20;
    int eBalancedEdgeIndex = 21;
    int eUnbalancedEdgeIndex = 22;
    int ePairIndex = 23;
    int eWedgeIndex = 24;
    int eUnevenEdgeGapIndex = 25;
    int eEvenEdgeGapIndex = 26;
    int eCenter4StonesIndex = 27;
    int eNextToCenterDiscsIndex = 28;
    int eNextToEdgeDiscsIndex = 29;
    int eDangerDiscsIndex = 30;
    int eEdgeDiscsIndex = 31;
    int eEdgeNextToCornerDiscsIndex = 32;
    int eCornerDiscsIndex = 33;
    int eDiscDifferenceWeightIndex = 34;
    final long leftBorderBitMask = 0b1111111011111110111111101111111011111110111111101111111011111110L;
    final long rightBorderBitMask = 0b111111101111111011111110111111101111111011111110111111101111111L;
    final long upperBorderBitMask = 0xffffffffffffff00L;
    final long downBorderBitMask = 0xffffffffffffffL;
    final long upRightBorderBitMask = 9187201950435737344L;
    final long upLeftBorderBitMask = 0xfefefefefefefe00L;
    final long downRightBorderBitMask = 0x7f7f7f7f7f7f7fL;
    final long downLeftBorderBitMask = 0xfefefefefefefeL;

    public int[] weights = {20, 10, -20, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 60, 1, 10, -5, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 50, 2};


    @Override
    public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
        Othello board = node.getBoard();
        int discsSet = Long.bitCount(board.blackPlayerDiscs & board.whitePLayerDiscs);
        boolean startWeights = discsSet < weights[sMovesEnd];

        int score = 0;

        score += getPossibleMovesScore(node, playerOne, startWeights);
        score += getFrontierDiscsScore(node, startWeights);


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

    private int getFrontierDiscsScore(OthelloTree.OthelloNode node, boolean startWeights) {
        long blackPlayerDiscs = node.getBoard().blackPlayerDiscs;
        long whitePlayerDiscs = node.getBoard().whitePLayerDiscs;
        long allDiscs = blackPlayerDiscs & whitePlayerDiscs;
        long empty = ~allDiscs;
        long frontierDiscs = allDiscs & (((empty >>> 8L) & downBorderBitMask) | ((empty << 8L) & upperBorderBitMask)
                | ((empty << 1L) & leftBorderBitMask) | ((empty >>> 1L) & rightBorderBitMask)
                | (empty >>> 9L) & downRightBorderBitMask | ((empty << 9L) & upLeftBorderBitMask)
                | ((empty << 7L) & upRightBorderBitMask) | ((empty >>> 7L) & downLeftBorderBitMask));

        int blackFrontierDiscs = Long.bitCount(frontierDiscs & blackPlayerDiscs);
        int whiteFrontierDiscs = Long.bitCount(frontierDiscs & whitePlayerDiscs);
        if (startWeights) {
            return weights[sFrontierDiscsIndex] * (blackFrontierDiscs - whiteFrontierDiscs);
        }
        return weights[eFrontierDiscsIndex] * (blackFrontierDiscs - whiteFrontierDiscs);

    }


}
