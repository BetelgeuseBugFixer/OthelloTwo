package ai.genetic.nico;
import ai.genetic.AiAgent;
import ai.genetic.BenchmarkAiAgent;
import games.othello.Agents.Agent;

public class NicoBenchmark implements BenchmarkAiAgent {

    String name;
    int points;
    int matchesPlayed;
    public NicoBenchmark(){
        this.name="Nico Benchmark";
        this.points=0;
        matchesPlayed=0;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public int getGamesPlayed() {
        return this.matchesPlayed;
    }

    @Override
    public void playAgainstNormalAgent(AiAgent agent, int gamesPerMatchUp) {

    }

    @Override
    public void resetPoints() {
        this.points=0;
    }

    @Override
    public void addPoints(int points) {
        this.points*=points;
    }

    @Override
    public void addGame() {
        this.matchesPlayed++;
    }
}
