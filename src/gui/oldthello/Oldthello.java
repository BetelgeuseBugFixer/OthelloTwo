package gui.oldthello;

import szte.mi.Move;

import java.util.ArrayList;
import java.util.HashMap;

  public class Oldthello {

      public Oldthello clone(){
       Oldthello newGame=new Oldthello();
       newGame.setPlayer(this.getPlayer());
       newGame.setGameStatus(this.gameStatus);
       newGame.setOver(this.isOver());
       newGame.roundsPassedInARow=this.roundsPassedInARow;
       newGame.field.putAll(this.field);
       newGame.roundsPassed=this.roundsPassed;
       newGame.player1Stones=this.player1Stones;
       newGame.player2Stones=this.player2Stones;
       newGame.movesmade=this.movesmade;
       return newGame;
      }

      public HashMap<Integer, Integer> field = new HashMap<>();

      public int player1Stones;
      public int player2Stones;
      private int player;
      private boolean over;
      public String gameStatus;
      public int roundsPassedInARow;
      public int roundsPassed;
      public int movesmade;

      public boolean isOver() {
          return over;
      }

      public int allStones(){
          return player2Stones+player1Stones;
      }

      public void setOver(boolean over) {
          this.over = over;
      }

      public int countStones(){
          int sum=0;
          for (int i = 0; i < 64; i++) {
              if (this.getField().get(i)==1){
                  sum++;
              } else if (this.getField().get(i)==2){
                  sum--;
              }
          }
          return sum;
      }

      public HashMap<Integer, Integer> getField() {
          return field;
      }

      public int xyToindex(int x, int y) {
          return y * 8 + x;
      }

      public int otherPlayer(int player) {
          if (player == 1) {
              return 2;
          } else return 1;
      }

      public int otherPlayer() {
          if (this.getPlayer() == 1) {
              return 2;
          } else return 1;
      }

      public boolean move(Move move) {
          if(!this.isOver()) {
              if (move!=null) {
                  ArrayList<Integer> flips = checkFlips(move.x, move.y, this.getPlayer());
                  int numOfFlips=flips.size();
                  if (numOfFlips > 0) {
                      if (player==1){
                          player1Stones+=1+ numOfFlips;
                          player2Stones-= numOfFlips;
                      }else {
                          player2Stones+=1+ numOfFlips;
                          player1Stones-= numOfFlips;
                      }
                      movesmade++;
                      this.set(move.x, move.y, this.getPlayer());
                      for (int flip : flips) {
                          this.set(flip, this.player);
                      }
                      roundsPassedInARow=0;
                      switchPlayer();
                      return true;
                  }
              } else if(allMoves(this.getPlayer()).size()==0){
                  roundsPassedInARow++;
                  roundsPassed++;
                  if (roundsPassedInARow>1){
                      if (player1Stones>player2Stones){
                          setGameStatus("Player 1 won the game");
                      } else if(player2Stones>player1Stones){
                          setGameStatus("Player 2 won the game");
                      }
                      else {
                          setGameStatus("It's a tie");
                      }
                      this.setOver(true);
                  } else {
                      switchPlayer();
                      return true;
                  }
              }
          }
          return false;
      }

      public String toString() {
          String s = "";
          StringBuilder strBuilder = new StringBuilder(s);
          for (int i = 0; i < 64; i++) {
              if (i % 8 == 0) {
                  strBuilder.append("\n");
              }
              if (this.getField().get(i) == 0) {
                  strBuilder.append('-');
              } else {
                  strBuilder.append(getField().get(i));
              }
          }
          return strBuilder.toString();
      }

      public ArrayList<Integer> checkFlips(int x, int y, int player) {
          ArrayList<Integer> squaresToFlip = new ArrayList<>();
          if (field.get(xyToindex(x, y)) == 0) {

              //all squares downwards are checked
              if (y < 6) {
                  for (int canidate = y + 1; canidate < 8; canidate++) {
                      if (field.get(xyToindex(x, canidate)) == player) {
                          for (int i = y + 1; i < canidate; i++) {
                              squaresToFlip.add(xyToindex(x, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(x, canidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards are checked
              if (y > 1) {
                  for (int canidate = y - 1; canidate >= 0; canidate--) {
                      if (field.get(xyToindex(x, canidate)) == player) {
                          for (int i = y - 1; i > canidate; i--) {
                              squaresToFlip.add(xyToindex(x, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(x, canidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares to the right
              if (x < 6) {
                  for (int canidate = x + 1; canidate < 8; canidate++) {
                      if (field.get(xyToindex(canidate, y)) == player) {
                          for (int i = x + 1; i < canidate; i++) {
                              squaresToFlip.add(xyToindex(i, y));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(canidate, y)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares to the left
              if (x > 1) {
                  for (int canidate = x - 1; canidate >= 0; canidate--) {
                      if (field.get(xyToindex(canidate, y)) == player) {
                          for (int i = x - 1; i > canidate; i--) {
                              squaresToFlip.add(xyToindex(i, y));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(canidate, y)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares downward to the right
              if (x < 6 && y < 6) {
                  int xCanidate = x + 1;
                  for (int yCanidate = y + 1; yCanidate < 8 && xCanidate < 8; yCanidate++, xCanidate++) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          int j = x + 1;
                          for (int i = y + 1; i < yCanidate; i++, j++) {
                              squaresToFlip.add(xyToindex(j, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares downward to the left
              if (x > 1 && y < 6) {
                  int xCanidate = x - 1;
                  for (int yCanidate = y + 1; yCanidate < 8 && xCanidate >= 0; yCanidate++, xCanidate--) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          int j = x - 1;
                          for (int i = y + 1; i < yCanidate; i++, j--) {
                              squaresToFlip.add(xyToindex(j, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards to the right
              if (x < 6 && y > 1) {
                  int xCanidate = x + 1;
                  for (int yCanidate = y - 1; yCanidate >= 0 && xCanidate < 8; yCanidate--, xCanidate++) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          int j = x + 1;
                          for (int i = y - 1; i > yCanidate; i--, j++) {
                              squaresToFlip.add(xyToindex(j, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards to the left
              if (x > 1 && y > 1) {
                  int xCanidate = x - 1;
                  //for (int yCanidate = y - 1; yCanidate >= 0 && xCanidate < 8; yCanidate--, xCanidate--) {
                      for (int yCanidate = y - 1; yCanidate >= 0 && xCanidate >= 0; yCanidate--, xCanidate--) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          int j = x - 1;
                          for (int i = y - 1; i > yCanidate; i--, j--) {
                              squaresToFlip.add(xyToindex(j, i));
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }
          }
          return squaresToFlip;
      }

      public boolean checkIfFlips(int x, int y, int player) {
          if (field.get(xyToindex(x, y)) == 0) {

              //all squares downwards are checked
              if (y < 6) {
                  for (int canidate = y + 1; canidate < 8; canidate++) {
                      if (field.get(xyToindex(x, canidate)) == player) {
                          // for (int i = y + 1; i < canidate; i++) {
                          if (canidate != y + 1) {
                              return true;
                          }
                          // }
                          break;
                      } else {
                          if (field.get(xyToindex(x, canidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards are checked
              if (y > 1) {
                  for (int canidate = y - 1; canidate >= 0; canidate--) {
                      if (field.get(xyToindex(x, canidate)) == player) {
                          if (canidate != y - 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(x, canidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares to the right
              if (x < 6) {
                  for (int canidate = x + 1; canidate < 8; canidate++) {
                      if (field.get(xyToindex(canidate, y)) == player) {
                          if (canidate != x + 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(canidate, y)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares to the left
              if (x > 1) {
                  for (int canidate = x - 1; canidate >= 0; canidate--) {
                      if (field.get(xyToindex(canidate, y)) == player) {
                          if (canidate != x - 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(canidate, y)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares downward to the right
              if (x < 6 && y < 6) {
                  int xCanidate = x + 1;
                  for (int yCanidate = y + 1; yCanidate < 8 && xCanidate < 8; yCanidate++, xCanidate++) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          if (yCanidate != y + 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares downward to the right
              if (x > 1 && y < 6) {
                  int xCanidate = x - 1;
                  for (int yCanidate = y + 1; yCanidate < 8 && xCanidate >= 0; yCanidate++, xCanidate--) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          if (yCanidate != y + 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards to the right
              if (x < 6 && y > 1) {
                  int xCanidate = x + 1;
                  for (int yCanidate = y - 1; yCanidate >= 0 && xCanidate < 8; yCanidate--, xCanidate++) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          if (yCanidate != y - 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }

              //all squares upwards to the left
              if (x > 1 && y > 1) {
                  int xCanidate = x - 1;
                  for (int yCanidate = y - 1; yCanidate >= 0 && xCanidate >= 0; yCanidate--, xCanidate--) {
                      if (field.get(xyToindex(xCanidate, yCanidate)) == player) {
                          if (yCanidate != y - 1) {
                              return true;
                          }
                          break;
                      } else {
                          if (field.get(xyToindex(xCanidate, yCanidate)) == 0) {
                              break;
                          }
                      }
                  }
              }
          }
          return false;
      }

      public ArrayList<Integer> allMoves(int player) {
          ArrayList<Integer> allMoves = new ArrayList<>();
          for (int x = 0; x < 8; x++) {
              for (int y = 0; y < 8; y++) {
                  if (checkIfFlips(x, y, player)) {
                      allMoves.add(xyToindex(x, y));
                  }
              }
          }
          return allMoves;
      }

      public ArrayList<Move> allMovesMoves(int player) {
          ArrayList<Move> allMoves = new ArrayList<>();
          for (int x = 0; x < 8; x++) {
              for (int y = 0; y < 8; y++) {
                  if (checkIfFlips(x, y, player)) {
                      allMoves.add(new Move(x,y));
                  }
              }
          }
          return allMoves;
      }

      public int getPlayer() {
          return player;
      }

      public void setPlayer(int player) {
          this.player = player;
      }

      public void switchPlayer(){
          if (this.getPlayer()==1){
              this.setPlayer(2);
              this.setGameStatus("player 2, your turn");
          }
          else {
              this.setPlayer(1);
              this.setGameStatus("player 1, your turn");
          }
      }

      public void setGameStatus(String gameStatus) {
          this.gameStatus = gameStatus;
      }

      public Oldthello() {

          roundsPassedInARow=0;
          roundsPassed=0;
          player2Stones=2;
          player1Stones=2;
          movesmade=0;

          for (int i = 0; i < 64; i++) {
              field.put(i, 0);
          }

          field.put(xyToindex(3, 3), 2);
          field.put(xyToindex(4, 4), 2);
          field.put(xyToindex(3, 4), 1);
          field.put(xyToindex(4, 3), 1);

          this.setPlayer(1);
          this.setOver(false);
          this.setGameStatus("player 1, your turn");
      }

      public void set(int x, int y, int player) {
          field.put(xyToindex(x, y), player);
      }

      public void set(int i, int player) {
          field.put(i, player);
      }
  }
