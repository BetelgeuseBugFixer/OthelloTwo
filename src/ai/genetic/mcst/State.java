package ai.genetic.mcst;

import othello.Othello;

import java.util.Random;

//https://www.baeldung.com/java-monte-carlo-tree-search
public class State {
    Othello board;
    boolean playerOne;
    int visitCount;

    public Othello getBoard() {
        return board;
    }

    public void setBoard(Othello board) {
        this.board = board;
    }

    public boolean isPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(boolean playerOne) {
        this.playerOne = playerOne;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public double getWinScore() {
        return winScore;
    }

    public boolean getOpponent(){
        return !this.playerOne;
    }
    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    double winScore;

    public State(Othello board, boolean playerOne) {
        this.board = board;
        this.playerOne = playerOne;
    }

    static int getSize(Othello.MoveWithResult[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                return i;
            }
        }
        return array.length;
    }

    public State[] getAllPossibleStates() {
        Othello.MoveWithResult[] nextMoves = this.board.getPossibleMoves(!this.playerOne);
        State[] nextStates = new State[getSize(nextMoves)];
        for (int i = 0; i < nextStates.length; i++) {
            nextStates[i] = new State(nextMoves[i].board, this.playerOne);
        }

        return nextStates;
    }
    public void incrementVisit(){
        this.visitCount+=1;
    }
    public void addScore(int score){
        this.winScore+=score;
    }

    public State randomPlay(Random rnd) {
        State[] nextStates=getAllPossibleStates();
        return nextStates[rnd.nextInt(nextStates.length)];
    }
}
