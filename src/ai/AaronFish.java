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
    DepthGoalCalculator depthGoalCalculator;

    public AaronFish() {
        this.depthGoalCalculator = new changingDepth();
        this.boardGrader = new BetterGrader();
    }

    public void setBoardGrader(BoardGrader boardGrader) {
        this.boardGrader = boardGrader;
    }

    @Override
    public void init(int order, long t, Random rnd) {
        this.board = new Othello();
        this.boardTree = new ArrayTree();
        if (order == 0) {
            this.playerOne = true;
            currentMove = -1;
        } else {
            currentMove = 0;
        }
    }


    public void setDepthGoalCalculator(DepthGoalCalculator newDepthGoalCalculator) {
        this.depthGoalCalculator = newDepthGoalCalculator;
    }

    public void setDepthGoalCalculatorToRandom() {
        setDepthGoalCalculator(new RandomDepth());
    }


    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        this.currentMove += 2;
        if (currentMove != 1) {
            this.boardTree.move(Othello.getIntFromMove(prevMove), !this.playerOne);
        }
        int remainingSpaces = this.boardTree.getRoot().getBoard().getRemainingSpaces();
        int goalDepth = depthGoalCalculator.getGoalDepth(t, remainingSpaces);
        return calculateNextMove(goalDepth);
    }

    public Move calculateNextMove(int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        OthelloNode root = this.boardTree.getRoot();

        int bestMove = -1;
        if (playerOne) {
            //check if the next move is a pass
            if (root.getNextNodes(true)[0].getPreviousMove() == -1) {
                return null;
            }
            for (OthelloNode node : root.getNextNodes(true)) {
                if (node == null) {
                    break;
                }
                int score = minValue(node, depth - 1, alpha, beta);
                if (score > alpha) {
                    alpha = score;
                    bestMove = node.getPreviousMove();
                    if (score == Integer.MAX_VALUE) {
                        break;
                    }
                }
            }
            System.out.println("current position: " + alpha);
        } else {
            //check if the next move is a pass
            if (root.getNextNodes(false)[0].getPreviousMove() == -1) {
                return null;
            }
            for (OthelloNode node : root.getNextNodes(false)) {
                if (node == null) {
                    break;
                }
                int score = maxValue(node, depth - 1, alpha, beta);
                if (score < beta) {
                    beta = score;
                    bestMove = node.getPreviousMove();
                    if (score == Integer.MIN_VALUE) {
                        break;
                    }
                }
            }
            System.out.println("current position: " + beta);
        }
        //TODO Ai passes if optimal Opponent play leads to loss
        this.boardTree.move(bestMove, this.playerOne);
        return Othello.getMoveFromInt(bestMove);
    }

    public int maxValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
        if (node.getIsTerminalNode(true) || depth == 0 || node.getIsFullyCalculated()) {
            return node.getScore(this.boardGrader, true);
        }

        int bestScore = Integer.MIN_VALUE;
        for (OthelloNode nextNode : node.getNextNodes(true)) {
            if (nextNode == null) {
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
        if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
            node.setToFullyCalculated();
            node.setScore(bestScore);
        }
        return bestScore;
    }

    public int minValue(OthelloTree.OthelloNode node, int depth, int alpha, int beta) {
        if (node.getIsTerminalNode(false) || depth == 0 || node.getIsFullyCalculated()) {
            return node.getScore(this.boardGrader, false);
        }

        int bestScore = Integer.MAX_VALUE;
        for (OthelloNode nextNode : node.getNextNodes(false)) {
            if (nextNode == null) {
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
        if (bestScore == Integer.MIN_VALUE || bestScore == Integer.MAX_VALUE || bestScore == 0) {
            node.setToFullyCalculated();
            node.setScore(bestScore);
        }
        return bestScore;
    }

    public interface DepthGoalCalculator {
        public int getGoalDepth(long remainingTime, int remainingEmptySpaces);
    }

    public static class ConstantDepth implements DepthGoalCalculator {
        public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
            return 2;
        }
    }

    private static class RandomDepth implements DepthGoalCalculator {
        Random rnd = new Random();

        @Override
        public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
            return rnd.nextInt(2, 4);
        }
    }

    public static class changingDepth implements DepthGoalCalculator {
        @Override
        public int getGoalDepth(long remainingTime, int remainingEmptySpaces) {
            if (remainingEmptySpaces < 13) {
                return 25;
            } else {
                return 2;
            }
        }
    }


}
