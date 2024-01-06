package ai.genetic.mcst;
import ai.BoardGrader;
import othello.Othello;
import othelloTrees.OthelloTree;
import java.util.Random;

public class MonteCarloBoardGrader implements BoardGrader {
    int numberOfGames=200;
    @Override
    public int gradeBoard(OthelloTree.OthelloNode node, boolean playerOne) {
        Random random=new Random();

        int result=0;
        for (int i = 0; i < numberOfGames; i++) {
            Othello game=new Othello(node.getBoard().blackPlayerDiscs,node.getBoard().whitePLayerDiscs);
            while (!game.isOver()) {
                int move=MonteCarloTreeSearch.chooseRandomMove(game,playerOne,random);
                game.makeMove(move,playerOne);
                playerOne=!playerOne;

            }
            result += game.getResult();
        }
        return result;
    }
}
