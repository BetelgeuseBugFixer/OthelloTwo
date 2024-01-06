package ai.genetic.mcst;

import ai.AaronFish;
import ai.genetic.AiAgent;
import ai.genetic.BenchmarkAiAgent;

public class MCSTBoardGraderPlayer implements BenchmarkAiAgent {

    String name;
    int points;
    AaronFish ai;

    public MCSTBoardGraderPlayer(){
    this.name="Monte-Carlo Board Grader";
    this.points=0;
    this.ai=new AaronFish();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public void playAgainstNormalAgent(AiAgent agent, int gamesPerMatchUp) {

    }

    @Override
    public void resetPoints() {
        this.points=0;
    }
}
