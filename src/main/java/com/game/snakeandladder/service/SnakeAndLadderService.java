package com.game.snakeandladder.service;

import com.game.snakeandladder.entities.Ladder;
import com.game.snakeandladder.entities.Player;
import com.game.snakeandladder.entities.Snake;
import com.game.snakeandladder.entities.SnakeAndLadderBoard;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SnakeAndLadderService {


    private static final int DEFAULT_BOARD_SIZE = 100; //The board will have 100 cells numbered from 1 to 100.
    private static final int DEFAULT_NO_OF_DICES = 1;
    SnakeAndLadderBoard snakeAndLadderBoard;
    private int initialNumberOfPlayers;
    private Queue<Player> players;
    // Comment: Keeping players in game service as they are specific to this game and not the board.
    // Keeping pieces in the board instead.
    private boolean isGameCompleted;
    private int noOfDices = 0; //Optional Rule 1
    private boolean shouldGameContinueTillLastPlayer; //Optional Rule 3
    private boolean shouldAllowMultipleDiceRollOnSix; //Optional Rule 4


    public SnakeAndLadderService() {
        this(SnakeAndLadderService.DEFAULT_BOARD_SIZE);
    }

    public SnakeAndLadderService(int boardSize) {
        this.snakeAndLadderBoard = new SnakeAndLadderBoard(boardSize);  //Optional Rule 2
        this.players = new LinkedList<Player>();
        this.noOfDices = SnakeAndLadderService.DEFAULT_NO_OF_DICES;
    }


    public void setNoOfDices(int noOfDices) {
        this.noOfDices = noOfDices;
    }

    public void setShouldGameContinueTillLastPlayer(boolean shouldGameContinueTillLastPlayer) {
        this.shouldGameContinueTillLastPlayer = shouldGameContinueTillLastPlayer;
    }

    public void setShouldAllowMultipleDiceRollOnSix(boolean shouldAllowMultipleDiceRollOnSix) {
        this.shouldAllowMultipleDiceRollOnSix = shouldAllowMultipleDiceRollOnSix;
    }


    public void setPlayers(List<Player> players) {
        this.players = new LinkedList<Player>();
        this.initialNumberOfPlayers = players.size();
        Map<String, Integer> playerPieces = new HashMap<String, Integer>();
        for (Player player : players) {
            this.players.add(player);
            playerPieces.put(player.getId(), 0); //Each player has a piece which is initially kept outside the board (i.e., at position 0).
        }
        snakeAndLadderBoard.setPlayerPieces(playerPieces); //  Add pieces to board
    }

    public void setSnakes(List<Snake> snakes) {
        snakeAndLadderBoard.setSnakes(snakes); // Add snakes to board
    }

    public void setLadders(List<Ladder> ladders) {
        snakeAndLadderBoard.setLadders(ladders); // Add ladders to board
    }

    /**
     * ==========Core business logic for the game==========
     */

    private int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition) {
        int previousPosition;

        do {
            previousPosition = newPosition;
            for (Snake snake : snakeAndLadderBoard.getSnakes()) {
                if (snake.getStart() == newPosition) {
                    newPosition = snake.getEnd(); // Whenever a piece ends up at a position with the head of the snake, the piece should go down to the position of the tail of that snake.
                }
            }

            for (Ladder ladder : snakeAndLadderBoard.getLadders()) {
                if (ladder.getStart() == newPosition) {
                    newPosition = ladder.getEnd(); // Whenever a piece ends up at a position with the start of the ladder, the piece should go up to the position of the end of that ladder.
                }
            }
        } while (newPosition != previousPosition); // There could be another snake/ladder at the tail of the snake or the end position of the ladder and the piece should go up/down accordingly.
        return newPosition;
    }

    private void movePlayer(Player player, int positions) {
        int oldPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int newPosition = oldPosition + positions; // Based on the dice value, the player moves their piece forward that number of cells.

        int boardSize = snakeAndLadderBoard.getSize();

        // Can modify this logic to handle side case when there are multiple dices (Optional requirements)
        if (newPosition > boardSize) {
            newPosition = oldPosition; // After the dice roll, if a piece is supposed to move outside position 100, it does not move.
        } else {
            newPosition = getNewPositionAfterGoingThroughSnakesAndLadders(newPosition);
        }

        snakeAndLadderBoard.getPlayerPieces().put(player.getId(), newPosition);

        System.out.println(player.getName() + " rolled a " + positions + " and moved from " + oldPosition + " to " + newPosition);
    }

    private int getTotalValueAfterDiceRolls() {
        int diceRollResult = 0;
        // Can use noOfDices and setShouldAllowMultipleDiceRollOnSix here to get total value (Optional requirements)
        if (noOfDices != 0)
            diceRollResult = DiceService.rollMultipleDice(noOfDices);
        else
            diceRollResult = DiceService.rollDice();

        return diceRollResult;
    }

    private boolean hasPlayerWon(Player player) {
        // Can change the logic a bit to handle special cases when there are more than one dice (Optional requirements)
        int playerPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int winningPosition = snakeAndLadderBoard.getSize();
        return playerPosition == winningPosition; // A player wins if it exactly reaches the position 100 and the game ends there.
    }

    private boolean isGameCompleted() {
        // Can use shouldGameContinueTillLastPlayer to change the logic of determining if game is completed (Optional requirements)
        int currentNumberOfPlayers = players.size();
        if (shouldGameContinueTillLastPlayer == false)
            return currentNumberOfPlayers < initialNumberOfPlayers;
        else return (currentNumberOfPlayers == 0);
    }

    public void startGame() {
        while (!isGameCompleted()) {
            int totalDiceValue = getTotalValueAfterDiceRolls(); // Each player rolls the dice when their turn comes.
            Player currentPlayer = players.poll();
            //optional
            if (shouldAllowMultipleDiceRollOnSix == true && totalDiceValue == 6) {
                int nextDiceVal = getTotalValueAfterDiceRolls();
                if (nextDiceVal == 6) {
                    int diceVal = getTotalValueAfterDiceRolls();
                    if (diceVal != 6) {
                        movePlayer(currentPlayer, 12 + diceVal);
                    } else
                        break;
                } else {
                    movePlayer(currentPlayer, 6 + nextDiceVal);
                }
            } else {
                movePlayer(currentPlayer, totalDiceValue);
            }
            if (hasPlayerWon(currentPlayer)) {
                System.out.println(currentPlayer.getName() + " wins the game");
                snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
            } else {
                players.add(currentPlayer);
            }
        }
    }

}
