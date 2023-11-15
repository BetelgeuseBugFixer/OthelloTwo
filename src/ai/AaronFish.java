package ai;

import othello.Othello;
import othelloTrees.OthelloTree;
import szte.mi.Move;
import othelloTrees.HashOthelloTree;


import java.util.Random;

public class AaronFish implements szte.mi.Player{
    Othello board;
    OthelloTree boardTree;
    @Override
    public void init(int order, long t, Random rnd) {
        this.board=new Othello();
        this.boardTree=new HashOthelloTree();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        if (prevMove!=null){
            
        }
        return null;
    }
}
