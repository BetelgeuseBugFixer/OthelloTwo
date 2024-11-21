package ai.genetic;

import ai.BetterGrader;
import ai.genetic.aai.AaiWrapper;
import ai.genetic.mcst.MCTWPlayer;
import org.apache.commons.math3.distribution.NormalDistribution;
import progressbar.Progressbar;
import progressbar.Timer;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class GeneticAlgorithm {
	static final File weightFile = new File("geneticFiles/weights.tsv");
	static final File bestFile = new File("geneticFiles/best.txt");
	static final File allBenchmark = new File("geneticFiles/benchmarkAgainstAll.tsv");
	static final File bestBenchmark = new File("geneticFiles/benchmarkAgainstBest.tsv");
	static int generationsPerBenchmark = 5;
	static int gamesPlayedPerMatchUp = 2;
	static int numOfThreads = Runtime.getRuntime().availableProcessors();
	static int populationSize = 100;
	static int singleParentPercentage = 50;
	static int mutationSV = 5;
	static int crossoverPercentage = 15;
	public final File weightsInGenerations = new File("src/ai/genetic/weights.tsv");

	public final File generationFile = new File("src/ai/genetic/generation.tsv");

	public static void main(String[] args) throws InterruptedException, IOException {

		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		if (args.length == 1) {
			geneticAlgorithm.start(Integer.parseInt(args[0]));
		} else {
			int generationsToTrain = Integer.parseInt(args[1]);
			if (args[0].equalsIgnoreCase("c")) {
				geneticAlgorithm.continueTraining(generationsToTrain);
			} else {
				geneticAlgorithm.start(generationsToTrain);
			}
		}
	}

	public static int gaussSum(int n) {
		return n * (n + 1) / 2;
	}

	public static int getWeightsSize() {
		BetterGrader grader = new BetterGrader();
		return grader.weights.length;
	}

	public static void makeFilesEmptyOrCreate(File[] files) throws IOException {
		for (File file : files) {
			if (file.exists()) {
				new FileWriter(file, false).close(); // Empty the file
			} else {
				File parentDir = file.getParentFile();
				if (parentDir != null && !parentDir.exists()) {
					if (!parentDir.mkdirs()) {
						throw new RuntimeException("Could not create directories for file " + file.getName());
					}
				}
				if (!file.createNewFile()) {
					throw new RuntimeException("Could not create file " + file.getName());
				}
			}
		}
	}

	public static int readNumberFromFile(File numberFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(numberFile);
		if (scanner.hasNextInt()) {
			int number = scanner.nextInt();
			scanner.close();
			return number;
		} else {
			throw new RuntimeException("No valid number found in the file.");
		}
	}

	public void start(int generations) throws InterruptedException, IOException {
		File[] files = {weightFile, bestFile, weightsInGenerations, allBenchmark, bestBenchmark, generationFile};
		makeFilesEmptyOrCreate(files);
		// initialize population randomly
		int weightSize = getWeightsSize();
		AiAgent[] aiAgents = new AiAgent[populationSize];
		for (int i = 0; i < populationSize; i++) {
			aiAgents[i] = new AiAgent(weightSize);
		}

		train(aiAgents, 0, generations);

	}

	public void continueTraining(int generationsToTrain) throws IOException, InterruptedException {
		AiAgent[] agents = readAiAgents();
		populationSize = agents.length;
		int current = readNumberFromFile(generationFile) + 1;
		train(agents, current, generationsToTrain);
	}

	public void train(AiAgent[] currentAgents, int startGen, int generationsToTrain) throws IOException, InterruptedException {
		NormalDistribution distribution = new NormalDistribution(0, mutationSV);

		BenchmarkAiAgent[] benchmarks = {new AaiWrapper(80), new HandCraftedWeights(), new MCTWPlayer(20)};

		int generation = startGen;
		Progressbar bar = new Progressbar("generations", generationsToTrain);
		for (int i = 0; i < generationsToTrain; i++, generation++) {

			if (generation % generationsPerBenchmark == 0) {
				this.simulateAllGames(benchmarks, currentAgents);
				writeBenchMark(benchmarks, generation, allBenchmark);
			} else {
				this.simulateAllGames(currentAgents);
			}

			Arrays.sort(currentAgents);
			safeCurrentAgents(currentAgents);

			updateBest(currentAgents[currentAgents.length - 1], generation);

			currentAgents = getNextGeneration(currentAgents, distribution);

			FileWriter generationWriter = new FileWriter(generationFile);
			generationWriter.write(generation + "\n");
			generationWriter.flush();
			generationWriter.close();
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
		BufferedWriter writer = new BufferedWriter(new FileWriter(weightFile));
		for (AiAgent sortedAiAgent : sortedAiAgents) {
			writer.write(sortedAiAgent.toString());
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	public void updateBest(AiAgent contender, int generation) throws IOException, InterruptedException {
		BenchmarkAiAgent[] benchmarks = {new AaiWrapper(600), new HandCraftedWeights(), new MCTWPlayer(130)};
		AiAgent bestAgent = contender;
		contender.resetPoints();
		playAgainstBenchmarks(benchmarks, contender);
		if (generation > 0) {
			bestAgent = getBestAgent();
			int bestGameDifference = Games.playBestMatchUp(bestAgent, contender, gamesPlayedPerMatchUp);

			if (bestAgent.points.get() + bestGameDifference < contender.points.get()) {
				System.out.println("\rnew best found in generation " + generation);
				bestAgent = contender;
			}

		}
		writeBest(bestAgent);
		writeWeights(bestAgent, generation);
		writeBenchMark(benchmarks, generation, bestBenchmark);
	}

	public void playAgainstBenchmarks(BenchmarkAiAgent[] benchmarks, AiAgent agent) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

		for (BenchmarkAiAgent benchmark : benchmarks) {
			executorService.submit(() -> benchmark.playAgainstNormalAgent(agent, gamesPlayedPerMatchUp));
		}
		executorService.shutdown();
		if (!executorService.awaitTermination(100, TimeUnit.SECONDS)) {
			System.out.println("is still running");
		}
	}

	public void writeBest(AiAgent newBest) throws IOException {
		FileWriter writer = (new FileWriter(bestFile));
		writer.write(newBest.toString());
		writer.write("\n");
		writer.write(newBest.points.toString());
		writer.write("\n");

		writer.flush();
		writer.close();
	}

	public AiAgent getBestAgent() throws IOException {
		List<Integer> integerList = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(bestFile));

		String weights = br.readLine();
		for (String field : weights.split("\t")) {
			int intValue = Integer.parseInt(field);
			integerList.add(intValue);
		}

		int points = Integer.parseInt(br.readLine());


		int[] intArray = new int[integerList.size()];
		for (int i = 0; i < integerList.size(); i++) {
			intArray[i] = integerList.get(i);
		}
		AiAgent best = new AiAgent(intArray);
		best.points.set(points);


		return best;
	}

	public void simulateGamesWithMultiThreading(AiAgent[] aiAgents) throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

		for (int i = 0; i < populationSize - 1; i++) {
			for (int j = i + 1; j < populationSize; j++) {
				AiAgent agentOne = aiAgents[i];
				AiAgent agentTwo = aiAgents[j];

				executorService.submit(() -> Games.playFullMatchUp(agentOne, agentTwo, gamesPlayedPerMatchUp));
			}
		}
		executorService.shutdown();
		if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
			System.out.println("is still running");
		}

	}

	public void simulateAllGamesMeasuringTime(BenchmarkAiAgent[] benchmarks, AiAgent[] aiAgents) throws InterruptedException {

		Timer timer = new Timer();
		timer.startTimer();
		for (int i = 0; i < populationSize - 1; i++) {
			for (int j = i + 1; j < populationSize; j++) {
				AiAgent agentOne = aiAgents[i];
				AiAgent agentTwo = aiAgents[j];

				Games.playFullMatchUp(agentOne, agentTwo, gamesPlayedPerMatchUp);
			}
		}
		timer.stopTimer();
		System.out.println("all matches: " + timer.getCurrentTimeInSeconds());
		for (BenchmarkAiAgent benchmark : benchmarks) {
			timer = new Timer();
			timer.startTimer();
			benchmarkAgainstAll(benchmark, aiAgents);
			System.out.println(benchmark.getName() + ": " + timer.getCurrentTimeInSeconds());

		}
	}

	public void simulateAllGames(BenchmarkAiAgent[] benchmarks, AiAgent[] aiAgents) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

		for (BenchmarkAiAgent benchmark : benchmarks) {
			executorService.submit(() -> benchmarkAgainstAll(benchmark, aiAgents));
		}

		simulateAgents(aiAgents, executorService);
		executorService.shutdown();
		if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
			System.out.println("is still running");
		}
	}

	private void simulateAgents(AiAgent[] aiAgents, ExecutorService executorService) {
		for (int i = 0; i < populationSize - 1; i++) {
			for (int j = i + 1; j < populationSize; j++) {
				AiAgent agentOne = aiAgents[i];
				AiAgent agentTwo = aiAgents[j];

				executorService.submit(() -> Games.playFullMatchUp(agentOne, agentTwo, gamesPlayedPerMatchUp));
			}
		}
	}

	public void simulateAllGames(AiAgent[] aiAgents) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
		simulateAgents(aiAgents, executorService);
		executorService.shutdown();
		if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
			System.out.println("is still running");
		}
	}

	public void playAgainstBenchmarks(BenchmarkAiAgent[] benchmarks, AiAgent[] aiAgents) throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

		for (BenchmarkAiAgent benchmark : benchmarks) {
			executorService.submit(() -> benchmarkAgainstAll(benchmark, aiAgents));
		}

		executorService.shutdown();
		if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
			System.out.println("is still running");
		}
	}

	public void benchmarkAgainstAll(BenchmarkAiAgent benchmark, AiAgent[] agents) {
		for (AiAgent aiAgent : agents) {
			benchmark.playAgainstNormalAgent(aiAgent, gamesPlayedPerMatchUp);
		}
	}

	public void simulateGamesNormal(AiAgent[] aiAgents) {
		Progressbar normalTimer = new Progressbar("normal", populationSize - 1);
		for (int i = 0; i < populationSize - 1; i++) {
			for (int j = i + 1; j < populationSize; j++) {
				AiAgent agentOne = aiAgents[i];
				AiAgent agentTwo = aiAgents[j];
				Games.playFullMatchUp(agentOne, agentTwo, gamesPlayedPerMatchUp);
			}
			normalTimer.countUp();
		}
	}

	public void writeBenchMark(BenchmarkAiAgent[] benchmarks, int generation, File outputFile) throws IOException {
		FileWriter writer = new FileWriter(outputFile, true);
		for (BenchmarkAiAgent benchmark : benchmarks) {
			writer.write(BenchmarkAiAgent.getStatistic(benchmark, generation));
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public void writeWeights(AiAgent contender, int generation) throws IOException {
		FileWriter writer = new FileWriter(weightsInGenerations, true);
		writer.write(generation + "\t" + contender.toString());
		writer.flush();
		writer.close();
	}

	public AiAgent[] readAiAgents() throws IOException {
		ArrayList<int[]> allWeights = new ArrayList<>();
		BufferedReader agentFileReader = new BufferedReader(new FileReader(weightFile));
		String line = agentFileReader.readLine();
		while (line != null && !line.isEmpty()) {
			String[] stringWeights = line.split("\t");
			int[] weights = new int[stringWeights.length];
			for (int i = 0; i < stringWeights.length; i++) {
				weights[i] = Integer.parseInt(stringWeights[i]);
			}
			allWeights.add(weights);
			line = agentFileReader.readLine();
		}
		AiAgent[] agents = new AiAgent[allWeights.size()];

		for (int i = 0; i < agents.length; i++) {
			agents[i] = new AiAgent(allWeights.get(i));
		}
		return agents;
	}


}