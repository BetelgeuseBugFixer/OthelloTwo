package gui;

import ai.AaronFish;
import ai.BetterGrader;
import ai.genetic.mcst.MCTWPlayer;
import ai.genetic.mcst.MonteCarloBoardGrader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import szte.mi.Move;
import szte.mi.Player;
import gui.oldthello.Oldthello;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Runner extends Application implements EventHandler<MouseEvent> {

    public static HashMap<Integer, OthelloSquare> board = new HashMap<>();

    boolean end = false;
    javafx.scene.control.Label gameStatus = new Label("Welcome to Othello");
    Oldthello game = new Oldthello();

    Player aiPlayer;
    Player aiPlayer2;
    Button pass = new Button("pass");
    //0=Ai vs player; 1=Ai vs Ai; 2=player vs player
    int gameMode = 0;
    Move previous;
    long uc = 0;
    BetterGrader grader = new BetterGrader();


    long t;

    GridPane gridPane = new GridPane();
    BorderPane layout = new BorderPane();
    GridPane buttons = new GridPane();
    Button resetToAiVsPlayer = new Button("reset to ai vs player");
    Button resetToPlayerVsPlayer = new Button("reset to player vs player");

    //BoardGrader1 grade=new BoardGrader1();


    public static class OthelloSquare extends Button {

        private final int square;
        Circle k;


        public int getSquare() {
            return square;
        }

        public int getX() {
            return this.getSquare() % 8;
        }

        public int getY() {
            return this.getSquare() / 8;
        }


        public void reset() {
            if (this.k != null) {
                this.getChildren().remove(k);
                k = null;
            }
        }

        public OthelloSquare(int square) {
            super();
            this.square = square;
        }

        public void paint(int player) {
            if (k == null) {
                k = new Circle(this.getWidth() / 2, this.getHeight() / 2, 20);
                if (player == 2) {
                    k.setFill(Color.WHITE);
                } else {
                    k.setFill(Color.BLACK);
                }
                this.getChildren().add(k);
            }
        }

        public void flip(int player) {
            if (player == 2) {
                k.setFill(Color.WHITE);
            } else {
                k.setFill(Color.BLACK);
            }
        }
    }

    public void handle(MouseEvent e) {
        if (gameMode == 0 || gameMode == 2) {
            if (!end) {
                boolean sucess = false;

                OthelloSquare activated = (OthelloSquare) e.getSource();
                ArrayList<Integer> toFlip = game.checkFlips(activated.getX(), activated.getY(), game.getPlayer());
                if (toFlip.size() > 0) {
                    activated.paint(game.getPlayer());
                    for (Integer i : toFlip) {
                        board.get(i).flip(game.getPlayer());
                    }
                    Move next = new Move(activated.getX(), activated.getY());
                    game.move(next);
                    previous = next;
                    sucess = true;

                }

                this.gameStatus.setText(game.gameStatus);
                this.end = game.isOver();


                //if the player succesfully made a move, it's the ais turn
                if (sucess) {
                    //double grade2=grader.gradeBoard2(game,1);
                    //System.out.println(grade2);
                    //System.out.println("------------------------------");
                    if (gameMode == 0) {
                        long prevT = System.currentTimeMillis();
                        Move next = aiPlayer.nextMove(previous, 2, t);
                        t = t - (System.currentTimeMillis() - prevT);
                        //uc += aiPlayer.getMovesCalculated();
                        //System.out.println("pm: "+game.allMoves(game.getPlayer()).size());
                        //System.out.println("moves calculated: "+uc);
                        //System.out.println("uc: "+aiPlayer.getUtilCounter());
                        if (next != null) {
                            System.out.println(next.x+", "+next.y);
                            toFlip = game.checkFlips(next.x, next.y, game.getPlayer());
                            if (toFlip.size() > 0) {
                                board.get(game.xyToindex(next.x, next.y)).paint(game.getPlayer());
                                for (Integer i : toFlip) {
                                    board.get(i).flip(game.getPlayer());
                                }
                                game.move(next);
                                previous = next;
                                //System.out.println(next.x+", "+ next.y);
                            }
                        } else {
                            game.move(next);
                            previous = next;
                            //System.out.println("pass");
                        }
                        this.gameStatus.setText(game.gameStatus);
                        this.end = game.isOver();
                        //double grade = grader.gradeBoard2(this.game,game.otherPlayer());
                        //System.out.println("grade: " + grade);
                        System.out.println("remaining Time: "+t);
                        //System.out.println("------------------------------------------");
                    }
                }

            }
        }
    }

    public void start(Stage window) throws Exception {
        t = 8000;

        //generate all Buttons
        for (int i = 0; i < 64; i++) {
            board.put(i, new OthelloSquare(i));
            board.get(i).setPrefSize(50, 50);
            board.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, this);
            gridPane.add(board.get(i), i % 8, i / 8);
        }

        aiPlayer = new AaronFish();
        aiPlayer2 = new AaronFish();
        previous = null;

        long timeForInnit = System.currentTimeMillis();
        aiPlayer.init(0, 8000, new Random(42));
        timeForInnit=System.currentTimeMillis()-timeForInnit;
        //System.out.println(timeForInnit);

        pass.setOnAction((ActionEvent e) -> {
            if (gameMode == 0 || gameMode == 2) {
                if (!end) {
                    boolean sucess = false;

                    //OthelloSquare activated = (OthelloSquare) e.getSource();
                    //ArrayList<Integer> toFlip = game.checkFlips(activated.getX(), activated.getY(), game.getPlayer());
                    //if (toFlip.size() > 0) {
                    //   activated.paint(game.getPlayer());
                    //   for (Integer i : toFlip) {
                    //       board.get(i).flip(game.getPlayer());
                    //   }
                    Move next = null;
                    if (game.move(next)) {
                        previous = next;
                        sucess = true;
                    }

                    //}

                    this.gameStatus.setText(game.gameStatus);
                    this.end = game.isOver();


                    //if the player succesfully made a move, it's the ais turn
                    if (sucess) {
                        //double grade2=grader.gradeBoard2(game,1);
                        //System.out.println(grade2);
                        //System.out.println("------------------------------");
                        if (gameMode == 0) {
                            long prevT = System.currentTimeMillis();
                            next = aiPlayer.nextMove(previous, 2, t);
                            t = t - (System.currentTimeMillis() - prevT);
                            //uc += aiPlayer.getMovesCalculated();
                            //System.out.println("pm: "+game.allMoves(game.getPlayer()).size());
                            //System.out.println("moves calculated: "+uc);
                            //System.out.println("uc: "+aiPlayer.getUtilCounter());
                            if (next != null) {
                                ArrayList<Integer>toFlip = game.checkFlips(next.x, next.y, game.getPlayer());
                                if (toFlip.size() > 0) {
                                    board.get(game.xyToindex(next.x, next.y)).paint(game.getPlayer());
                                    for (Integer i : toFlip) {
                                        board.get(i).flip(game.getPlayer());
                                    }
                                    game.move(next);
                                    previous = next;
                                    //System.out.println(next.x+", "+ next.y);
                                }
                            } else {
                                game.move(next);
                                previous = next;
                                //System.out.println("pass");
                            }
                            this.gameStatus.setText(game.gameStatus);
                            this.end = game.isOver();
                            //System.out.println("grade: " + grade);
                            //System.out.println("remaining Time: "+t);
                            //System.out.println("------------------------------------------");
                        }
                    }

                }
            }
        });

        resetToAiVsPlayer.setOnAction((ActionEvent e) -> {
            end = false;
            this.game = new Oldthello();
            aiPlayer = new AaronFish();
            aiPlayer.init(1, 2, new Random());
            for (int i = 0; i < 64; i++) {
                board.get(i).reset();
            }
            gameMode = 0;
            board.get(27).paint(2);
            board.get(36).paint(2);
            board.get(28).paint(1);
            board.get(35).paint(1);

            gameMode = 0;
        });
        resetToPlayerVsPlayer.setOnAction((ActionEvent e) -> {
            end = false;
            this.game = new Oldthello();
            for (int i = 0; i < 64; i++) {
                board.get(i).reset();
            }
            board.get(27).paint(2);
            board.get(36).paint(2);
            board.get(28).paint(1);
            board.get(35).paint(1);

            gameMode = 2;
        });

        buttons.add(pass, 1, 0);

        buttons.add(resetToAiVsPlayer, 1, 1);
        buttons.add(resetToPlayerVsPlayer, 2, 1);

        layout.setTop(gameStatus);
        layout.setCenter(gridPane);
        layout.setBottom(buttons);
        layout.setStyle("-fx-background-color: green;");

        window.setTitle("Othello");
        window.setScene(new Scene(layout, 500, 400));
        window.show();

        board.get(27).paint(2);
        board.get(36).paint(2);
        board.get(28).paint(1);
        board.get(35).paint(1);


        if (gameMode == 0) {
            long prevT = System.currentTimeMillis();
            Move next = aiPlayer.nextMove(null, 2, t);
            t = t - (System.currentTimeMillis() - prevT);
            if (next != null) {
                ArrayList<Integer> toFlip = game.checkFlips(next.x, next.y, game.getPlayer());
                if (toFlip.size() > 0) {
                    board.get(game.xyToindex(next.x, next.y)).paint(game.getPlayer());
                    for (Integer i : toFlip) {
                        board.get(i).flip(game.getPlayer());
                    }
                    game.move(next);
                    previous = next;
                    //System.out.println(next.x+", "+ next.y);
                }
            } else {
                game.move(next);
                previous = next;
                //System.out.println("pass");
            }
            this.gameStatus.setText(game.gameStatus);
            this.end = game.isOver();
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}