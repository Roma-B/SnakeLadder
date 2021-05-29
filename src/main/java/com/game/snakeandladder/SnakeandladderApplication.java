package com.game.snakeandladder;

import com.game.snakeandladder.entities.Ladder;
import com.game.snakeandladder.entities.Player;
import com.game.snakeandladder.entities.Snake;
import com.game.snakeandladder.service.SnakeAndLadderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
@SpringBootApplication
public class SnakeandladderApplication {

    StopWatch stopWatch = new StopWatch();

    @Autowired
    SnakeAndLadderService snakeAndLadderService;

    public static void main(String[] args) {
        //SpringApplication.run(SnakeandladderApplication.class, args);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter no. of snakes");
        int noOfSnakes = scanner.nextInt();
        List<Snake> snakes = new ArrayList<Snake>();
        for (int i = 0; i < noOfSnakes; i++) {
            System.out.println("Enter Starting and Ending point of the snake");
            int start=scanner.nextInt();
            int end=scanner.nextInt();
            if(end>start) {
                System.out.println("Please enter valid positions, end cannot be greater than start for snakes");
                noOfSnakes++;
            }
            snakes.add(new Snake(start,end));
        }

        System.out.println("Enter no. of ladders");
        int noOfLadders = scanner.nextInt();
        List<Ladder> ladders = new ArrayList<Ladder>();
        for (int i = 0; i < noOfLadders; i++) {
            System.out.println("Enter Starting and Ending point of the snake");
            int start=scanner.nextInt();
            int end=scanner.nextInt();
            if(start>end) {
                System.out.println("Please enter valid positions, start cannot be greater than end for ladders");
                noOfLadders++;
            }
            ladders.add(new Ladder(start, end));
        }

        System.out.println("Enter no. of Players");
        int noOfPlayers = scanner.nextInt();
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < noOfPlayers; i++) {
            System.out.println("Enter name of Player"+(i+1));
            players.add(new Player(scanner.next()));
        }

        SnakeAndLadderService snakeAndLadderService = new SnakeAndLadderService();
        snakeAndLadderService.setPlayers(players);
        snakeAndLadderService.setSnakes(snakes);
        snakeAndLadderService.setLadders(ladders);

        snakeAndLadderService.startGame();
    }
}

