package ai.genetic;

import ai.AaronFish;
import othello.Othello;
import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class Games {

    public static void playFullMatchUp(AiAgent agentOne, AiAgent agentTwo, int gamesPlayedPerMatchUp) {
        playSingleMatchUp(agentOne, agentTwo, gamesPlayedPerMatchUp);
        playSingleMatchUp(agentTwo, agentOne, gamesPlayedPerMatchUp);
    }

    public static void playSingleMatchUp(AiAgent agentOne, AiAgent agentTwo, int gamesPlayedPerMatchUp) {
        int result = 0;
        for (int i = 0; i < gamesPlayedPerMatchUp; i++) {
            result += playSingleGame(agentOne, agentTwo);
        }
        if (result == 0) {
            agentOne.addDraw();
            agentTwo.addDraw();
        } else if (result > 0) {
            agentOne.addWin();
        } else {
            agentTwo.addWin();
        }
    }

    public static int playBestMatchUp(AiAgent best, AiAgent contender, int gamesPlayedPerMatchUp) {
        int[] gameResults = {0, 0};
        for (int i = 0; i < gamesPlayedPerMatchUp; i++) {
            gameResults[0] += playSingleGame(best, contender);
            gameResults[1] += playSingleGame(contender, best);
        }
        int endResult = 0;
        for (int result : gameResults) {
            if (result > 0) {
                endResult += 3;
            } else if (result != 0) {
                endResult -= 3;
            }
        }
        return endResult;
    }

    public static int playSingleGame(AiAgent agentOne, AiAgent agentTwo) {
        AaronFish black = agentOne.initAi(0);
        AaronFish white = agentTwo.initAi(1);

        return playSingleGameWithPlayerInterface(black, white);
    }

    public static int playSingleGameWithPlayerInterface(Player black, Player white) {
        return playSingleGameWithPlayerInterface(black, white, 8);

    }

    public static int playSingleGameWithPlayerInterface(Player black, Player white, int time) {
        Player[] players = {black, white};
        AiOthelloGame aiOthelloGame = new AiOthelloGame();

        Move prevMove = null;
        int player = 0;
        while (aiOthelloGame.gameIsStillRunning) {
            prevMove = players[player].nextMove(prevMove, time, time);
            if (Othello.isValidMove(aiOthelloGame.game, prevMove, player == 0)) {
                aiOthelloGame.makeMove(prevMove, player == 0);
                player = (player + 1) % 2;
            } else {
                System.out.println(Othello.isValidMove(aiOthelloGame.game, prevMove, player == 0));
                System.out.println(aiOthelloGame.game);
                System.out.println(prevMove.x + ", " + prevMove.y);
            }
        }
        return aiOthelloGame.getResult();

    }


    private static class AiOthelloGame {
        Othello game;
        int roundsPassed;
        boolean gameIsStillRunning;

        public AiOthelloGame() {
            this.game = new Othello();
            this.roundsPassed = 0;
            this.gameIsStillRunning = true;
        }

        public void makeMove(Move move, boolean playerOne) {
            if (move == null) {
                this.roundsPassed++;
            } else {
                this.roundsPassed = 0;
                this.game.makeMove(move, playerOne);
            }
            if (this.roundsPassed == 2 || this.game.boardIsFull()) {
                this.gameIsStillRunning = false;
            }
        }

        public int getResult() {
            int blackScore = Long.bitCount(this.game.blackPlayerDiscs);
            int whiteScore = Long.bitCount(this.game.whitePLayerDiscs);
            if (blackScore > whiteScore) {
                return 1;
            } else if (whiteScore > blackScore) {
                return -1;
            }
            return 0;
        }
    }
}
