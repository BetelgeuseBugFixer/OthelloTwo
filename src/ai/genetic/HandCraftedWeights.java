package ai.genetic;

public class HandCraftedWeights implements BenchmarkAiAgent{

    AiAgent agent;
    public HandCraftedWeights(){
        int[] weights={20, 10, -20, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 60, 1, 20, 70, -20, 10, -5, 30, 40, 30, 30, 20, -20, 10, 15, 10, -5, -50, 20, -10, 50, 2, 30, 90, -60};
        this.agent=new AiAgent(weights);
    }
    @Override
    public String getName() {
        return "Handcrafted Weights";
    }

    @Override
    public int getPoints() {
        return this.agent.points.get();
    }

    @Override
    public void playAgainstNormalAgent(AiAgent agent, int gamesPerMatchUp) {
        GeneticAlgorithm.playFullMatchUp(this.agent,agent);
    }

    @Override
    public void resetPoints() {
        this.agent.resetPoints();
    }
}
