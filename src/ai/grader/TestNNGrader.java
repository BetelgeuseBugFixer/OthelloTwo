package ai.grader;

import othello.Othello;
import othelloTrees.OthelloTree;

import java.util.Random;

public class TestNNGrader implements BoardGrader {
	public int[] hiddenLayerOne;
	public int[] layerOneBias;
	// public int[] hiddenLayerTwo;
	public int[] outputLayer;
	public int[] outputLayerBias;
	int hiddenLayerOneSize;
	int inputSize = 65;
	int maxValue = 1000;
	int biasMaxValue = 500;

	public TestNNGrader(int layerOneSize/*, int layerTwoSize*/) {
		// first layer
		hiddenLayerOneSize = layerOneSize;
		int weightLayerOneSize = inputSize * layerOneSize;
		this.hiddenLayerOne = getRandomArray(weightLayerOneSize, maxValue);
		layerOneBias = getRandomArray(layerOneSize, biasMaxValue);
		// second layer
		// int weightLayerTwoSize = layerOneSize * layerTwoSize;
		// this.hiddenLayerTwo=getRandomArray(weightLayerTwoSize,maxValue);
		// out layer
		this.outputLayer = getRandomArray(layerOneSize, maxValue);
	}

	public static int[] getRandomArray(int size, int maxValue) {
		int[] randomArray = new int[size];

		Random random = new Random();
		for (int i = 0; i < size; i++) {
			randomArray[i] = random.nextInt(1, maxValue);
		}
		return randomArray;
	}

	@Override
	public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
		int[] x = getInputVector(node.getBoard(), playerOne);
		x = multiplyVectorsWithBias(x, hiddenLayerOne, layerOneBias);
		int res = multiplyVectorsForOut(x, outputLayer);
		return res;
	}

	public static int[] multiplyVectorsWithBias(int[] n, int[] m, int[] bias) {
		int[] result = new int[bias.length];
		System.arraycopy(bias,0,result,0,bias.length);
		int secondIndex = 0;
		for (int firstIndex = 0; firstIndex < n.length; firstIndex++) {
			for (int i = 0; i < bias.length; i++,secondIndex++) {
				result[i] += Math.max(0,n[firstIndex] * m[secondIndex]);
			}
		}

		return result;
	}

	private int multiplyVectorsForOut(int[] n, int[] m) {
		int res = 0;
		int secondIndex = 0;
		for (int firstIndex = 0; firstIndex < n.length; firstIndex++, secondIndex++) {
			res += n[firstIndex] * m[secondIndex];
		}
		return res;
	}

	private int[] getInputVector(Othello board, boolean playerOne) {
		int[] input = new int[65];
		for (int i = 0; i < 64; i++) {
			input[i] = board.getDiscAtField(i);
		}
		if (!playerOne) {
			input[64] = 1;
		}
		return input;
	}

}
