import othello.Othello;
import szte.mi.Move;
import progressbar.Timer;

import java.util.ArrayList;

public class Test {


    public static void main(String[] args) {
        Othello othello = new Othello();
        System.out.println(othello);
        Othello.MoveWithResult[] moves=othello.getPossibleMoves(true);
        for (Othello.MoveWithResult moveWithResult : moves) {
            System.out.println(moveWithResult.move.x+", "+moveWithResult.move.y);
            System.out.println(moveWithResult.board);

        }

    }
}
