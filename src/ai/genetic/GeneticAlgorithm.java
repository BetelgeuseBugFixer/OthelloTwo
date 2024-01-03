package ai.genetic;

import ai.AaronFish;
import ai.BetterGrader;
import org.apache.commons.math3.distribution.NormalDistribution;
import othello.Othello;
import progressbar.Progressbar;
import szte.mi.Move;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class GeneticAlgorithm {
    static final int gamesPlayedPerMatchUp = 2;
    static final int numOfThreads = 4;
    static final int populationSize = 50;
    static final int singleParentPercentage = 50;
    static final int mutationSV = 5;
    static final int crossoverPercentage = 15;
    static final File outputFile = new File("src/ai/genetic/weights.tsv");
    static final File bestFile = new File("src/ai/genetic/best.txt");


    public static void main(String[] args) throws InterruptedException, IOException {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.start(100);

    }

    static public AaronFish initAi(AiAgent agent, int order) {
        Random rnd = new Random();

        AaronFish aaronFish = new AaronFish();
        aaronFish.init(order, 8, rnd);
        aaronFish.setDepthGoalCalculatorToRandom();

        BetterGrader grader = new BetterGrader();
        grader.weights = agent.weights;
        aaronFish.setBoardGrader(grader);

        return aaronFish;
    }

    public static int gaussSum(int n) {
        return n * (n + 1) / 2;
    }

    public static int getWeightsSize() {
        BetterGrader grader = new BetterGrader();
        return grader.weights.length;
    }

    public void start(int generations) throws InterruptedException, IOException {
        //initialize population randomly
        int weightSize = getWeightsSize();
        AiAgent[] aiAgents = new AiAgent[populationSize];
        for (int i = 0; i < populationSize; i++) {
            aiAgents[i] = new AiAgent(weightSize);
        }

        train(aiAgents, generations);

    }

    public void train(AiAgent[] currentAgents, int generations) throws IOException, InterruptedException {
        NormalDistribution distribution = new NormalDistribution(0, mutationSV);

        Progressbar bar = new Progressbar("generations", generations);
        for (int generation = 0; generation < generations; generation++) {


            simulateGamesWithMultiThreading(currentAgents);
            //simulateGamesNormal(aiAgents);

            //TODO add games against Benchmarks and plot their result


            Arrays.sort(currentAgents);
            safeCurrentAgents(currentAgents);
            updateBest(currentAgents[currentAgents.length - 1],generation);
            currentAgents = getNextGeneration(currentAgents, distribution);
            bar.countUp();

        }
    }

    public AiAgent[] getNextGeneration(AiAgent[] previousGenration, NormalDistribution distribution) {
        AiAgent[] nextGeneration = new AiAgent[populationSize];
        int[] rankArray = getProportionalRankArray();
        Random random = new Random();

        for (int i = 0; i < populationSize; i++) {
            boolean isSingleParent = singleParentPercentage < random.nextInt(100);
            if (isSingleParent) {
                AiAgent parent = previousGenration[rankArray[random.nextInt(rankArray.length)]];
                nextGeneration[i] = AiAgent.mutate(parent, distribution);
            } else {
                AiAgent mother = previousGenration[rankArray[random.nextInt(rankArray.length)]];
                AiAgent father = previousGenration[rankArray[random.nextInt(rankArray.length)]];

                nextGeneration[i] = AiAgent.recombine(mother, father, crossoverPercentage, distribution);
            }

        }

        return nextGeneration;
    }

    public int[] getProportionalRankArray() {
        int size = gaussSum(populationSize);
        int[] ranks = new int[size];

        int currentIndex = 0;
        for (int rank = 1; rank <= populationSize; rank++) {
            for (int i = 0; i < rank; i++) {
                ranks[currentIndex] = rank - 1;
                currentIndex++;
            }

        }

        return ranks;
    }

    public void safeCurrentAgents(AiAgent[] sortedAiAgents) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (AiAgent sortedAiAgent : sortedAiAgents) {
            writer.write(sortedAiAgent.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void updateBest(AiAgent contender, int generation) throws IOException {
        AiAgent bestAgent = getBestAgent();
        //TODO include other agents as metric
        contender.resetPoints();

        playFullMatchUp(bestAgent, contender);

        if (bestAgent.points.get() < contender.points.get()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(bestFile));
            System.out.println("\rnew best found in generation "+generation);

            writer.write(contender.toString());

            writer.flush();
            writer.close();
        }

    }

    public AiAgent getBestAgent() throws IOException {
        List<Integer> integerList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(bestFile));
        String line;
        while ((line = br.readLine()) != null) {
            for (String field : line.split("\t")) {
                int intValue = Integer.parseInt(field);
                integerList.add(intValue);
            }
        }

        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }

        return new AiAgent(intArray);
    }

    public void simulateGamesWithMultiThreading(AiAgent[] aiAgents) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

        for (int i = 0; i < populationSize - 1; i++) {
            for (int j = i + 1; j < populationSize; j++) {
                AiAgent agentOne = aiAgents[i];
                AiAgent agentTwo = aiAgents[j];

                executorService.submit(() -> playFullMatchUp(agentOne, agentTwo));
            }
        }
        executorService.shutdown();
        if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
            System.out.println("is still running");
        }

    }

    public void simulateGamesNormal(AiAgent[] aiAgents) {
        Progressbar normalTimer = new Progressbar("normal", populationSize - 1);
        for (int i = 0; i < populationSize - 1; i++) {
            for (int j = i + 1; j < populationSize; j++) {
                AiAgent agentOne = aiAgents[i];
                AiAgent agentTwo = aiAgents[j];
                playFullMatchUp(agentOne, agentTwo);
            }
            normalTimer.countUp();
        }
    }

    public void playFullMatchUp(AiAgent agentOne, AiAgent agentTwo) {
        playSingleMatchUp(agentOne, agentTwo);
        playSingleMatchUp(agentTwo, agentOne);
    }

    public void playSingleMatchUp(AiAgent agentOne, AiAgent agentTwo) {
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

    public int playSingleGame(AiAgent agentOne, AiAgent agentTwo) {
        AiOthelloGame aiOthelloGame = new AiOthelloGame();

        AaronFish[] ais = new AaronFish[2];
        ais[0] = initAi(agentOne, 0);
        ais[1] = initAi(agentTwo, 1);

        Move prevMove = null;
        int player = 0;
        while (aiOthelloGame.gameIsStillRunning) {
            prevMove = ais[player].nextMove(prevMove, 8, 8);
            aiOthelloGame.makeMove(prevMove, player == 0);
            player = (player + 1) % 2;
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