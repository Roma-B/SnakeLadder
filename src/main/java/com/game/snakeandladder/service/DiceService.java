package com.game.snakeandladder.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DiceService {

    public static int rollDice() {
        return new Random().nextInt(6);
    }

    public static int rollMultipleDice(int noOfDice) {
        int result = 0;
        for (int i = 0; i < noOfDice; i++)
            result += new Random().nextInt(6);
        return result;
    }

}
