package ai;

import othello.Othello;
import szte.mi.Move;
import othelloTrees.HashOthelloTree;
import othelloTrees.OthelloTree.OthelloNode;
import othelloTrees.OthelloTree;


import java.util.Random;

public class AaronFish implements szte.mi.Player {

    BoardGrader boardGrader;
    boolean playerOne;
    Othello board;
    OthelloTree boardTree;
    int currentMove;

    @Override
    public void init(int order, long t, Random rnd) {
        this.boardGrader = new SimplestGrader();
        this.board = new Othello();
        this.boardTree = new HashOthelloTree();
        if (order == 0) {
            this.playerOne = true;
            currentMove = -1;
        } else {
            currentMove = 0;
        }
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        this.currentMove += 2;
        if (prevMove != null) {
            this.boardTree.move(prevMove);
        }
        int goalDepth = getGoalDepth(t);
        return calculateNextMove(goalDepth);
    }

    public Move calculateNextMove(int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Move bestMove = null;
        if (playerOne) {
            for (Move move : this.boardTree.getRoot().getNextMoves()) {
                OthelloNode nextNode = this.boardTree.getRoot().getNextNode(move);
                int score = minValue(nextNode, depth - 1, alpha, beta);
                if (score > alpha) {
                    alpha = score;
                    bestMove = move;
                }
            }
        } else {
            for (Move move : this.boardTree.getRoot().getNextMoves()) {
                OthelloNode nextNode = this.boardTree.getRoot().getNextNode(move);
                int score = maxValue(nextNode, depth - 1, alpha, beta);
                if (score < beta) {
                    beta = score;
                    bestMove = move;
                }
            }

        }

        return bestMove;
    }

    public int maxValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
        if (node.getIsTerminalNode() || depth == 0) {
            return node.getScore(this.boardGrader);
        }

        int bestScore = Integer.MIN_VALUE;
        for (OthelloNode nextNode : node.getNextNodes()) {
            int score = minValue(nextNode, depth - 1, alpha, beta);
            if (score > bestScore) {
                bestScore = score;
            }
            if (alpha < score) {
                alpha = score;
            }
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }

    public int minValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
        if (node.getIsTerminalNode() || depth == 0) {
            return node.getScore(this.boardGrader);
        }

        int bestScore = Integer.MAX_VALUE;
        for (OthelloNode nextNode : node.getNextNodes()) {
            int score = maxValue(nextNode, depth - 1, alpha, beta);
            if (score < bestScore) {
                bestScore = score;
            }
            if (beta < score) {
                beta = score;
            }
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }

    public int getGoalDepth(long remainingTime) {
        return 4;
    }
}
