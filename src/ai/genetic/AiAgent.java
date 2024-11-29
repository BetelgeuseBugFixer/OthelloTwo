package ai.genetic;

import ai.AaronFish;
import ai.BetterGrader;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static ai.genetic.GeneticAlgorithm.percentageCheck;

public class AiAgent implements Comparable<AiAgent> {
	int[] weights;
	AtomicInteger points;

	public AiAgent(int weightSize, int deletionPercentage, Random random) {
		this.points = new AtomicInteger();

		this.weights = new int[weightSize];
		// init move change to be in between 20 and 50
		weights[0] = random.nextInt(30) + 20;
		for (int i = 1; i < weightSize; i++) {
			if (percentageCheck(random, deletionPercentage)) {
				weights[i] = 0;
			} else {
				int newWeight = random.nextInt(200);
				weights[i] = newWeight - 100;
			}
		}
	}

	public AiAgent(int[] weights) {
		this.weights = weights;
		this.points = new AtomicInteger();
	}

	public static AiAgent mutate(AiAgent agent, int deletionPercentage, int reactivationPercentage, int chromosomeCopyPercentage, NormalDistribution distribution, Random random) {
		int[] newWeights = new int[agent.weights.length];
		for (int i = 0; i < newWeights.length; i++) {
			newWeights[i] = mutateSingleWeight(agent.weights[i], reactivationPercentage, deletionPercentage, random, distribution);
		}
		// adjust move change to be in between 20 and 50
		adjustMoveChange(newWeights, random);
		copyChromosome(newWeights,chromosomeCopyPercentage,random);
		return new AiAgent(newWeights);
	}

	private static void copyChromosome(int[] weights, int chromosomeCopyPercentage, Random random) {
		int[] copyWeights=new int[weights.length];
		System.arraycopy(weights,0,copyWeights,0,weights.length);
		int[] chromosomEndpoints = {1, ((weights.length - 1) / 2) + 1, weights.length};
		int chromosomeLength = chromosomEndpoints[1] - chromosomEndpoints[0];
		for (int i = 0; i < 2; i++) {
			if (percentageCheck(random, chromosomeCopyPercentage)) {
				// mod 2 to make it the start of the other chromosome
				System.arraycopy(copyWeights, chromosomEndpoints[(i + 1) % 2], weights, chromosomEndpoints[i], chromosomeLength);
			}
		}
	}

	private static int mutateSingleWeight(int oldValue, int reactivationPercentage, int deletionPercentage, Random random, NormalDistribution distribution) {
		int newValue = 0;
		if (oldValue == 0) {
			if (percentageCheck(random, reactivationPercentage)) {
				// reactivate Gene
				newValue = random.nextInt(200) - 100;
			}
		} else {
			if (!percentageCheck(random, deletionPercentage)) {
				// weight is set to 0 if the condition is not met
				newValue = oldValue + (int) (distribution.sample());
			}
		}
		return newValue;
	}

	public static AiAgent recombine(AiAgent mother, AiAgent father, int crossoverPercentage, int reactivationPercentage, int deletionPercentage, int chromosomeCopyPercentage, NormalDistribution distribution, Random random) {
		int[] childWeights = new int[mother.weights.length];
		int[] chromosomEndpoints = {1, ((mother.weights.length - 1) / 2) + 1, mother.weights.length};
		int[] chromosomeSumsMother = getAbsolutSplitArraySum(chromosomEndpoints, mother.weights);
		int[] chromosomeSumsFather = getAbsolutSplitArraySum(chromosomEndpoints, father.weights);

		int gene = 0;
		for (int currentChromosom = 0; currentChromosom < chromosomEndpoints.length; currentChromosom++) {
			int chromosomEnd = chromosomEndpoints[currentChromosom];

			int[] getsToInherent = mother.weights;
			int[] notInherited = father.weights;

			int inheritSum = chromosomeSumsMother[currentChromosom];
			int notInheritSum = chromosomeSumsFather[currentChromosom];
			if (random.nextBoolean()) {
				getsToInherent = father.weights;
				notInherited = mother.weights;
				notInheritSum = chromosomeSumsMother[currentChromosom];
				inheritSum = chromosomeSumsFather[currentChromosom];
			}
			while (gene < chromosomEnd) {
				if (percentageCheck(random, crossoverPercentage)) {
					childWeights[gene] = getRecombinedScaledWeight(notInherited[gene], getsToInherent[gene], notInheritSum, inheritSum);
				} else {
					childWeights[gene] = getsToInherent[gene];
				}
				// mutate
				childWeights[gene] = mutateSingleWeight(childWeights[gene], reactivationPercentage, deletionPercentage, random, distribution);
				gene++;
			}

		}
		// adjust move change to be in between 20 and 50
		adjustMoveChange(childWeights, random);
		copyChromosome(childWeights,chromosomeCopyPercentage,random);
		return new AiAgent(childWeights);
	}

	private static void adjustMoveChange(int[] weights, Random random) {
		if (weights[0] > 50 || weights[0] < 30) {
			weights[0] = random.nextInt(30) + 20;
		}
	}

	public static int getRecombinedScaledWeight(int weightToScale, int weightToBeReplaced, int sumOfToScale, int sumOfToBeReplaced) {
		int absWeightToScale = Math.abs(weightToScale);
		if (sumOfToScale == absWeightToScale) {
			return weightToScale;
		}
		int absWeightToBeReplaced = Math.abs(weightToBeReplaced);
		return (int) (weightToScale * (sumOfToBeReplaced - absWeightToBeReplaced) / (1.0 * sumOfToScale - absWeightToScale));
	}

	public static int[] getAbsolutSplitArraySum(int[] endpointsArray, int[] toSum) {
		int[] sums = new int[endpointsArray.length];
		int endPointIndex = 0;
		for (int i = 0; i < toSum.length; i++) {
			while (endpointsArray[endPointIndex] <= i) {
				endPointIndex++;
			}
			sums[endPointIndex] += Math.abs(toSum[i]);
		}
		return sums;
	}

	public AiAgent copyWeightsToNewAgent() {
		int[] newWeights = new int[this.weights.length];
		System.arraycopy(this.weights, 0, newWeights, 0, this.weights.length);
		return new AiAgent(newWeights);
	}

	public void resetPoints() {
		this.points.set(0);
	}

	public void addWin() {
		this.points.getAndAdd(3);
	}

	public void addDraw() {
		this.points.getAndAdd(1);
	}

	public AaronFish initAi(int order) {
		Random random = new Random();

		AaronFish aaronFish = new AaronFish();
		aaronFish.init(order, 8, random);
		aaronFish.setDepthGoalCalculatorToRandom();

		BetterGrader grader = new BetterGrader();
		grader.weights = this.weights;
		aaronFish.setBoardGrader(grader);

		return aaronFish;
	}

	@Override
	public int compareTo(AiAgent o) {
		return Integer.compare(this.points.get(), o.points.get());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int weight : weights) {
			sb.append(weight);
			sb.append("\t");
		}
		return sb.substring(0, sb.length() - 1);
	}
}
