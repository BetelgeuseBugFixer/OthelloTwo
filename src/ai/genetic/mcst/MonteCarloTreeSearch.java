package ai.genetic.mcst;

import othello.Othello;
import progressbar.Timer;
import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

// https://www.baeldung.com/java-monte-carlo-tree-search
public class MonteCarloTreeSearch {
    static final int WIN_SCORE = 10;
    int level;
    boolean opponent;

    public Othello findNextMove(Othello board, boolean playerOne, int maxTimeMilliseconds,Random random) {

        Timer timer = new Timer();
        opponent = !playerOne;
        Tree tree = new Tree(board, opponent);
        Node rootNode = tree.getRoot();

        timer.startTimer();

            while (timer.getCurrentTimeInMilliSeconds() < maxTimeMilliseconds) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (!promisingNode.getState().getBoard().isOver()) {
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray() != null) {
                nodeToExplore = promisingNode.getRandomChildNode(random);
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore,random);

            backPropagation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getBoard();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray()!=null) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        State[] possibleStates = node.getState().getAllPossibleStates();
        Node[] childArray = new Node[possibleStates.length];
        for (int i = 0; i < possibleStates.length; i++) {
            State state = possibleStates[i];
            Node newNode = new Node(state);
            newNode.setParent(node);
            newNode.getState().setPlayerOne(node.getState().getOpponent());
            childArray[i] = newNode;
        }
        node.setChildArray(childArray);
    }


    private void backPropagation(Node nodeToExplore, int result) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (playerOneIsWinner(tempNode.getState().isPlayerOne(),result)) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }

    public boolean playerOneIsWinner(boolean playerOne, int result){
        if (playerOne){
            return result==1;
        }else {
            return result==-1;
        }
    }


    private int simulateRandomPlayout(Node node,Random random) {
        Othello game=new Othello(node.getState().board.blackPlayerDiscs,node.getState().board.whitePLayerDiscs);
        boolean playerOne=node.state.playerOne;
        while (!game.isOver()) {
            int move=chooseRandomMove(game,playerOne,random);
            game.makeMove(move,playerOne);
            playerOne=!playerOne;

        }
        return game.getResult();
    }

    public static int chooseRandomMove(Othello board, boolean playerOne, Random random){
        long allMoves = board.getLegalMovesAsLong(playerOne);
        int numberOfMoves=Long.bitCount(allMoves);
        if (allMoves!=0) {
            int randomIndex=random.nextInt(numberOfMoves);
            for (int i = 0; i < 64; i++) {
                long bitMask = 1L << i;
                if ((allMoves & bitMask) != 0) {
                    randomIndex--;
                    if (randomIndex == -1) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }



    public class UCT {
        public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinScore / (double) nodeVisit)
                    + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }

        public static Node findBestNodeWithUCT(Node node) {
            int parentVisit = node.getState().getVisitCount();

            Node bestNode = node.childArray[0];
            double bestScore = uctValue(parentVisit, bestNode.getState().getWinScore(), bestNode.getState().getVisitCount());
            for (int i = 1; i < node.getChildArray().length; i++) {
                Node currentNode=node.getChildArray()[i];
                double score=uctValue(parentVisit, currentNode.getState().getWinScore(), currentNode.getState().getVisitCount());
                if (score>bestScore){
                    bestNode=currentNode;
                    bestScore=score;
                }
            }
            return bestNode;
        }
    }
}
