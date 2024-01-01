package ai.genetic;

public interface BenchmarkAiAgent {
    public int getPoints();
    public int playAgainstNormalAgent(AiAgent agent);
}
