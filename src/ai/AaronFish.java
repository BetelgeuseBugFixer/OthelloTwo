package ai;

import othello.Othello;
import othelloTrees.ArrayTree;
import szte.mi.Move;
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
        this.boardTree = new ArrayTree();
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
            this.boardTree.move(Othello.getIntFromMove(prevMove),!this.playerOne);
        }
        int goalDepth = getGoalDepth(t);
        return calculateNextMove(goalDepth);
    }

    public Move calculateNextMove(int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int bestMove = -1;
        if (playerOne) {
            for (OthelloNode node : this.boardTree.getRoot().getNextNodes(true)) {
                if (node==null){
                    break;
                }
                int score = minValue(node, depth - 1, alpha, beta);
                if (score > alpha) {
                    alpha = score;
                    bestMove = node.getPreviousMove();
                }
            }
        } else {
            for (OthelloNode node : this.boardTree.getRoot().getNextNodes(false)) {
                if (node==null){
                    break;
                }
                int score = maxValue(node, depth - 1, alpha, beta);
                if (score < beta) {
                    beta = score;
                    bestMove = node.getPreviousMove();
                }
            }

        }
        this.boardTree.move(bestMove,this.playerOne);
        return Othello.getMoveFromInt(bestMove);
    }

    public int maxValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
        if (node.getIsTerminalNode(true) || depth == 0) {
            return node.getScore(this.boardGrader,true);
        }

        int bestScore = Integer.MIN_VALUE;
        for (OthelloNode nextNode : node.getNextNodes(true)) {
            if (nextNode==null){
                break;
            }
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
        if (node.getIsTerminalNode(false) || depth == 0) {
            return node.getScore(this.boardGrader,false);
        }

        int bestScore = Integer.MAX_VALUE;
        for (OthelloNode nextNode : node.getNextNodes(false)) {
            if (nextNode==null){
                break;
            }
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
