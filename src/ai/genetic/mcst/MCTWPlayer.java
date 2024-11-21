package ai.genetic.mcst;

import ai.AaronFish;
import ai.genetic.AiAgent;
import ai.genetic.BenchmarkAiAgent;
import ai.genetic.Games;
import othello.Othello;
import szte.mi.Move;
import szte.mi.Player;
import java.util.Random;

public class MCTWPlayer implements BenchmarkAiAgent, Player {

    int maxTimeInMilliseconds;
    String name;
    int points;
    boolean playerOne;
    MonteCarloTreeSearch monteCarloTree;
    Random random;

    int gamesPlayed =0;
    Othello board;

    public MCTWPlayer(int maxTimeInMilliseconds) {
        this.name = "MCTS";
        this.maxTimeInMilliseconds = maxTimeInMilliseconds;
        this.board = new Othello();
        this.monteCarloTree = new MonteCarloTreeSearch();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public int getGamesPlayed() {
        return this.gamesPlayed;
    }

    @Override
    public void playAgainstNormalAgent(AiAgent agent, int gamesPerMatchUp) {
        Random rnd = new Random();
        int result = 0;
        for (int order = 0; order < 2; order++) {
            for (int i = 0; i < gamesPerMatchUp; i++) {
                this.init(order, 3, rnd);
                AaronFish aiAgent = agent.initAi((order + 1) % 2);
                if (order==0) {
                    result += Games.playSingleGameWithPlayerInterface(this, aiAgent);
                }else {
                    result += Games.playSingleGameWithPlayerInterface(aiAgent,this);
                }
            }
            if (result == 0) {
                this.points += 1;
                agent.addDraw();
            } else if (result > 0) {
                this.points += 3;
            } else {
                agent.addWin();
            }
            this.gamesPlayed++;
        }
    }

    @Override
    public void resetPoints() {
        this.points = 0;
        this.gamesPlayed =0;
    }

    @Override
    public void addPoints(int points) {
        this.points*=points;
    }

    @Override
    public void addGame() {
        this.gamesPlayed++;
    }

    @Override
    public void init(int order, long t, Random rnd) {
        if (order == 0) {
            this.playerOne = true;
        } else {
            this.playerOne = false;
        }
        this.random=rnd;
        this.board = new Othello();
        this.monteCarloTree=new MonteCarloTreeSearch();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        board.makeMove(prevMove, !this.playerOne);
        long legalMoves = board.getLegalMovesAsLong(playerOne);
        if (Long.bitCount(legalMoves) <= 1) {
            int index = -1;
            if (legalMoves != 0) {
                for (int i = 0; i < 64; i++) {
                    if (legalMoves == 1L << i) {
                        index = i;
                        break;
                    }
                }
            }
            this.board.makeMove(index,this.playerOne);
            return Othello.getMoveFromInt(index);
        }

        Othello newBoard = monteCarloTree.findNextMove(board, this.playerOne, maxTimeInMilliseconds,this.random);
        Move next = getLastMoveFromTwoBoards(this.board, newBoard);
        this.board = newBoard;
        return next;
    }

    public Move getLastMoveFromTwoBoards(Othello oldBoard, Othello newBoard) {
        long oldDiscs = oldBoard.blackPlayerDiscs | oldBoard.whitePLayerDiscs;
        long newDiscs = newBoard.blackPlayerDiscs | newBoard.whitePLayerDiscs;
        long newDiscMasc = oldDiscs ^ newDiscs;
        int index = -1;
        if (newDiscMasc != 0L) {
            for (int i = 0; i < 64; i++) {
                if (newDiscMasc == 1L << i) {
                    index = i;
                    break;
                }
            }
        }
        return Othello.getMoveFromInt(index);
    }


}
