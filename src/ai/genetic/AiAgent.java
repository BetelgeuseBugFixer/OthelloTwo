package ai.genetic;

import ai.AaronFish;
import ai.BetterGrader;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AiAgent implements Comparable<AiAgent> {
    int[] weights;
    AtomicInteger points;

    public AiAgent(int weightSize) {
        this.points = new AtomicInteger();

        Random rnd = new Random();
        this.weights = new int[weightSize];
        for (int i = 0; i < weightSize; i++) {
            int newWeight = rnd.nextInt(200);
            weights[i] = newWeight - 100;
        }
    }

    public AiAgent(int[] weights) {
        this.weights = weights;
        this.points=new AtomicInteger();
    }

    public static AiAgent mutate(AiAgent agent, NormalDistribution distribution) {
        int[] newWeights = new int[agent.weights.length];
        for (int i = 0; i < newWeights.length; i++) {
            newWeights[i] = agent.weights[i] + (int) (distribution.sample());
        }
        return new AiAgent(newWeights);
    }

    public static AiAgent recombine(AiAgent mother, AiAgent father, int crossoverPercentage, NormalDistribution distribution) {
        int[] childWeights = new int[mother.weights.length];
        Random random = new Random();
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
                if (random.nextInt(100) < crossoverPercentage) {
                    childWeights[gene] = getRecombinedScaledWeight(notInherited[gene], getsToInherent[gene], notInheritSum, inheritSum);
                }else {
                    childWeights[gene]=getsToInherent[gene];
                }
                //mutate
                childWeights[gene]+=(int) (distribution.sample());
                gene++;
            }

        }
        return new AiAgent(childWeights);
    }

    public static int getRecombinedScaledWeight(int weightToScale, int weightToBeReplaced, int sumOfToScale, int sumOfToBeReplaced) {
        int absWeightToScale=Math.abs(weightToScale);
        if (sumOfToScale==absWeightToScale){
            return weightToScale;
        }
        int absWeightToBeReplaced=Math.abs(weightToBeReplaced);
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
        Random rnd = new Random();

        AaronFish aaronFish = new AaronFish();
        aaronFish.init(order, 8, rnd);
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
