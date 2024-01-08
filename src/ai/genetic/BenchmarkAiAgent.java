package ai.genetic;

public interface BenchmarkAiAgent {
    public String getName();
    public int getPoints();

    public int getMatchesPlayed();
    public void playAgainstNormalAgent(AiAgent agent,int gamesPerMatchUp);

    public void resetPoints();

    public static String getStatistic(BenchmarkAiAgent benchmark, int generation){
        return benchmark.getName()+"\t"+generation+"\t"+ benchmark.getPoints()*1.0/ benchmark.getMatchesPlayed();
    }
}
