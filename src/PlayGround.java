import ai.AaronFish;
import ai.genetic.mcst.MonteCarloTreeSearch;
import ai.grader.BetterGrader;
import ai.grader.BoardGrader;
import ai.grader.SimplestGrader;
import ai.grader.TestNNGrader;
import othello.Othello;
import othelloTrees.ArrayTree;
import othelloTrees.HashTree;
import progressbar.Progressbar;
import progressbar.Timer;
import szte.mi.Move;

import java.util.Random;

public class PlayGround {

	private static long[] testGameWithPlus() {
		Othello othello = new Othello();
		int roundsPassed = 0;
		long current;
		long[] decoy = new long[2];

		boolean playerOne = false;
		for (int i = 0; i <= 70; i++) {
			playerOne = !playerOne;
			Othello.MoveWithResult next = othello.getPossibleMoves(playerOne)[0];
			if (next.move == -1) {
				// System.out.println("passed");
				roundsPassed++;
				if (roundsPassed == 2) {
					// System.out.println("game is over before all Stones are set");
					break;
				}
			} else {
				othello = next.board;
				current = othello.blackPlayerDiscs + othello.whitePLayerDiscs;
				roundsPassed = 0;

				decoy[i % 2] = current;

				// System.out.println(othello);
				// System.out.println(othello.blackPlayerDiscs+othello.whitePLayerDiscs);

			}
		}
		// System.out.println("End of test");
		// System.out.println("disc set:" +discsSet);
		// System.out.println(othello.blackPlayerDiscs+", "+othello.whitePLayerDiscs);
		// System.out.println(othello);
		return decoy;
	}

	private static long[] testGameWithBit() {
		Othello othello = new Othello();
		int roundsPassed = 0;
		long current;
		long[] decoy = new long[2];

		boolean playerOne = false;
		for (int i = 0; i <= 70; i++) {
			playerOne = !playerOne;
			Othello.MoveWithResult next = othello.getPossibleMoves(playerOne)[0];
			if (next.move == -1) {
				// System.out.println("passed");
				roundsPassed++;
				if (roundsPassed == 2) {
					// System.out.println("game is over before all Stones are set");
					break;
				}
			} else {
				othello = next.board;
				current = othello.blackPlayerDiscs | othello.whitePLayerDiscs;
				roundsPassed = 0;

				decoy[i % 2] = current;

				// System.out.println(othello);
				// System.out.println(othello.blackPlayerDiscs+othello.whitePLayerDiscs);

			}
		}
		// System.out.println("End of test");
		// System.out.println("disc set:" +discsSet);
		// System.out.println(othello.blackPlayerDiscs+", "+othello.whitePLayerDiscs);
		// System.out.println(othello);
		return decoy;
	}


	public static void newOthelloGame() {
		Othello othello = new Othello();
		boolean playerOne = true;
		for (int i = 0; i < 60; i++) {
			Othello.MoveWithResult[] nextPos = othello.getPossibleMoves(playerOne);
			Othello.MoveWithResult next = nextPos[0];
			othello = next.board;
			playerOne = !playerOne;
		}
	}

	public static void simulateGame() {
		Othello game = new Othello();

		Random r = new Random(42);

		AaronFish aiOne = new AaronFish();
		aiOne.init(0, 8, r);

		AaronFish aiTwo = new AaronFish();
		aiTwo.init(1, 8, r);


		boolean gameIsRunning = true;
		Move prevMove = null;
		int roundsPassed = 0;
		// Timer test = new Timer();
		// test.startTimer();
		while (gameIsRunning) {
			prevMove = aiOne.nextMove(prevMove, 8, 8);
			if (prevMove == null) {
				roundsPassed++;
			} else {
				roundsPassed = 0;
				game.makeMove(prevMove, true);
			}
			if (roundsPassed == 2 || game.boardIsFull()) {
				gameIsRunning = false;
			}
            /*System.out.println("player one: " + moveToString(prevMove));
            System.out.println(game);
            System.out.println();

             */

			prevMove = aiTwo.nextMove(prevMove, 8, 8);
			if (prevMove == null) {
				roundsPassed++;
			} else {
				roundsPassed = 0;
				game.makeMove(prevMove, false);
			}
			if (roundsPassed == 2 || game.boardIsFull()) {
				gameIsRunning = false;
			}
            /*System.out.println("player two: " + moveToString(prevMove));
            System.out.println(game);
            System.out.println();
            */
		}
		// test.stopTimer();
/*
        System.out.println(game.blackPlayerDiscs + game.whitePLayerDiscs);
        System.out.println("black discs: " + Long.bitCount(game.blackPlayerDiscs));
        System.out.println("white discs: " + Long.bitCount(game.whitePLayerDiscs));
        System.out.println("game took " + test.getCurrentTimeInMilliSeconds() + "ms");

             */
	}

	public static void randomOthelloGame(Random random, boolean printResult) {
		int[] fields = getOrderedFields();

		Othello game = new Othello();
		int roundsPassed = 0;
		boolean playerOne = true;

		while ((!game.boardIsFull()) && roundsPassed < 2) {
			long discsToFlip = 0;
			int lastMove = -1;
			for (int field : fields) {
				discsToFlip = game.getDiscsToFlip(field, playerOne);
				if (discsToFlip != 0) {
					lastMove = field;
					if (random.nextBoolean()) {
						break;
					}
				}
			}
			if (lastMove == -1) {
				roundsPassed++;
			} else {
				roundsPassed = 0;
				long newWhiteDiscs = game.whitePLayerDiscs ^ discsToFlip;
				long newBlackDiscs = game.blackPlayerDiscs ^ discsToFlip;
				// add new disc
				if (playerOne) {
					newBlackDiscs |= 1L << lastMove;
				} else {
					newWhiteDiscs |= 1L << lastMove;
				}
				game = new Othello(newBlackDiscs, newWhiteDiscs);
				playerOne = !playerOne;
			}

		}
		if (printResult) {
			System.out.println(game.getCurrentGameResult());
		}
	}

	public static void measureGraderTime() {
		BoardGrader[] graders = new BoardGrader[]{new BetterGrader(), new SimplestGrader(), new TestNNGrader(120)};
		Timer[] graderTimer = new Timer[graders.length];
		for (int i = 0; i < graderTimer.length; i++) {
			graderTimer[i] = new Timer();
		}

		Random random = new Random(42);
		int testSize = 300_000;
		HashTree.OthelloState[] toGrade = getRandomOthelloArray(testSize, random);

		Progressbar progressbar = new Progressbar("Measuring Grader Performance", testSize);
		for (HashTree.OthelloState othello : toGrade) {
			ArrayTree.ArrayNode node = new ArrayTree.ArrayNode(othello.getBoard());
			for (int i = 0; i < graders.length; i++) {
				BoardGrader grader = graders[i];
				Timer timer = graderTimer[i];
				timer.startTimer();
				grader.gradeBoard(node, othello.isPlayerOne());
				timer.stopTimer();
			}
			progressbar.countUp();
		}
		System.out.println();
		for (Timer timer : graderTimer) {
			System.out.println(timer.getCurrentTimeInMilliSeconds());
		}
	}
	private void testNextMoveSpeed(){
		Random random = new Random(42);
		int testSize = 200;
		HashTree.OthelloState[] position = getRandomOthelloArray(testSize, random);
		AaronFish ai = new AaronFish();
		int depth = 7;
		AaronFish.DepthGoalCalculator depthGoalCalculator = new AaronFish.ConstantDepth(6);
		Timer timer = new Timer();
		timer.startTimer();
		for (HashTree.OthelloState othelloState : position) {
			ai.initWithRoot(othelloState.getBoard(), othelloState.isPlayerOne(), depthGoalCalculator);
			ai.calculateNextMove(depth);
		}
		timer.stopTimer();
		System.out.println(timer.getCurrentTimeInMilliSeconds());
	}

	public static void main(String[] args) {


	}

	static HashTree.OthelloState getRandomOthelloPosition(Random random) {
		Othello game = new Othello();
		boolean playerOne = true;
		int moves = random.nextInt(55);
		for (int i = 0; i < moves && !game.isOver(); i++) {
			int move = MonteCarloTreeSearch.chooseRandomMove(game, playerOne, random);
			game.makeMove(move, playerOne);
			playerOne = !playerOne;
		}
		return new HashTree.OthelloState(game, playerOne);
	}

	static HashTree.OthelloState[] getRandomOthelloArray(int size, Random random) {
		HashTree.OthelloState[] positions = new HashTree.OthelloState[size];
		for (int i = 0; i < size; i++) {
			positions[i] = getRandomOthelloPosition(random);
		}
		return positions;
	}

	public static long rightShift(long x) {
		return x << 1;
	}

	public static void performShiftsWithInterface(long test, int n, TestShifter shifter) {
		Progressbar testBar = new Progressbar("shifts", n);
		for (int i = 0; i < n; i++) {
			long temp = shifter.shift(test);
			testBar.countUp();
		}
	}

	public static void performShifts(Long test, int n) {
		Progressbar testBar = new Progressbar("shifts", n);

		for (int i = 0; i < n; i++) {
			long temp = test << 1;
			testBar.countUp();
		}
	}

	public static String moveToString(Move move) {
		return move == null ? "pass" : move.x + ", " + move.y;
	}

	public static boolean compareMoves(Move a, Move b) {
		if (a == null || b == null) {
			return a == null && b == null;
		}
		return a.x == b.x && a.y == b.y;
	}

	public static int[] getOrderedFields() {
		// corners
		int[] orderedFields = new int[60];
		int index = 0;
		// add corners
		int[] corners = {0, 7, 56, 63};
		for (; index < corners.length; index++) {
			orderedFields[index] = corners[index];
		}
		// add edge center discs
		int[] edgeCenter = {3, 4, 24, 32, 31, 39, 59, 60};
		for (int j : edgeCenter) {
			orderedFields[index] = j;
			index++;
		}
		// add outer edge discs
		int[] edgeOuter = {2, 5, 16, 23, 40, 47, 58, 61};
		for (int j : edgeOuter) {
			orderedFields[index] = j;
			index++;
		}
		// add center discs
		for (int row = 2; row < 6; row++) {
			for (int col = 2; col < 6; col++) {
				int field = row * 8 + col;
				// ignore the center 4
				if (field == 27 || field == 28 || field == 35 || field == 36) {
					continue;
				}
				orderedFields[index] = field;
				index++;
			}
		}

		// ad discs next to edge
		int[] discsNextToEdge = {10, 11, 12, 13, 17, 22, 25, 30, 33, 38, 41, 46, 50, 51, 52, 53};
		for (int j : discsNextToEdge) {
			orderedFields[index] = j;
			index++;
		}
		// ad discs next to corner on edge
		int[] discsNextToCornerOnEdge = {1, 6, 8, 15, 48, 55, 57, 62};
		for (int j : discsNextToCornerOnEdge) {
			orderedFields[index] = j;
			index++;
		}
		// ad danger discs
		int[] dangerDiscs = {9, 14, 49, 54};
		for (int j : dangerDiscs) {
			orderedFields[index] = j;
			index++;
		}

		// test
        /*
        for (int i = 0; i < 64; i++) {
            boolean found=false;

            for (int orderedField : orderedFields) {
                if (orderedField==i){
                    found=true;
                    break;
                }
            }
            if (!found){
                System.out.println("missing number: "+i);
            }
        }

        HashSet<Integer> test=new HashSet<>();
        for (int orderedField : orderedFields) {
            test.add(orderedField);
        }

        if (test.size()!= orderedFields.length){
            System.out.println("wrong length");
        }
         */

		return orderedFields;
	}

	private void timeGettingNextMoves() {
		Random random = new Random(42);
		int testSize = 500_000;
		HashTree.OthelloState[] position = getRandomOthelloArray(testSize, random);
		Timer timer = new Timer();
		timer.startTimer();
		for (HashTree.OthelloState othelloState : position) {
			Othello.MoveAndResultingBoardList<ArrayTree.ArrayNode> nextMovesAndBoards = othelloState.getBoard().getPossibleMovesAndStates(ArrayTree.ArrayNode::getNodeFromBoard, othelloState.isPlayerOne());
		}
		timer.stopTimer();
		System.out.println(timer.getCurrentTimeInMilliSeconds());
	}

	public interface TestShifter {
		long shift(long x);
	}
}
