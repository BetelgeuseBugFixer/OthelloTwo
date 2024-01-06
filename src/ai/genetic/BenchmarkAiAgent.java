package ai.genetic;

public interface BenchmarkAiAgent {
    public String getName();
    public int getPoints();
    public void playAgainstNormalAgent(AiAgent agent,int gamesPerMatchUp);

    public void resetPoints();

}
