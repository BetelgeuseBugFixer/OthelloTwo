package ai.genetic.server;

import ai.AaronFish;
import ai.genetic.AiAgent;
import ai.genetic.BenchmarkAiAgent;
import ai.genetic.Games;
import szte.mi.Player;

import java.io.PrintStream;
import java.util.Random;

public class ServerPlayer implements BenchmarkAiAgent {
	Player ai;
	String name;
	int points;
	int gamesPlayed = 0;
	int timeForMoveInMilliseconds;

	public ServerPlayer(int timeForMoveInMilliseconds,Player ai, String name) {
		this.ai = ai;
		this.name = name;
		this.points = 0;
		this.timeForMoveInMilliseconds = timeForMoveInMilliseconds;
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
				if (order == 0) {
					result += Games.playSingleGameWithPlayerInterface(this.ai, aiAgent, this.timeForMoveInMilliseconds);
				} else {
					result += Games.playSingleGameWithPlayerInterface(aiAgent, this.ai, this.timeForMoveInMilliseconds);
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
		this.gamesPlayed = 0;
	}

	@Override
	public void addPoints(int points) {
		this.points+=points;
	}

	@Override
	public void addGame() {
		this.gamesPlayed++;
	}
}
