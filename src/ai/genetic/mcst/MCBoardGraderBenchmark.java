package ai.genetic.mcst;

import ai.AaronFish;
import ai.genetic.AiAgent;
import ai.genetic.BenchmarkAiAgent;
import ai.genetic.Games;

import java.util.Random;

public class MCBoardGraderBenchmark implements BenchmarkAiAgent {

    String name;
    int points;
    AaronFish ai;
    int gamesPlayed;

    public MCBoardGraderBenchmark(int numberOfRandomGames) {
        this.name = "Monte-Carlo Board Grader";
        this.points = 0;
        this.ai = new AaronFish();
        ai.setToMonteCarloGrader(numberOfRandomGames);
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
    public int getMatchesPlayed() {
        return this.gamesPlayed;
    }

    @Override
    public void playAgainstNormalAgent(AiAgent agent, int gamesPerMatchUp) {
        Random rnd = new Random();
        int result = 0;
        for (int order = 0; order < 2; order++) {
            for (int i = 0; i < gamesPerMatchUp; i++) {
                this.ai.init(order, 3, rnd);
                AaronFish aiAgent = agent.initAi((order + 1) % 2);
                if (order == 1) {
                    result += Games.playSingleGameWithPlayerInterface(this.ai, aiAgent);
                } else {
                    result += Games.playSingleGameWithPlayerInterface(aiAgent, this.ai);
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
        this.gamesPlayed=0;
    }
}
