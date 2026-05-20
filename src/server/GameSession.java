package server;

import java.util.HashSet;
import java.util.Set;

public class GameSession {

    public String player1;
    public String player2;

    public String secretWord;

    public String currentWord;

    public int attemptsLeft;

    public String currentTurn;

    public Set<Character> guessedLetters;

    public boolean gameOver;

    public GameSession(String player1,
                       String player2,
                       String secretWord) {

        this.player1 = player1;
        this.player2 = player2;

        this.secretWord = secretWord.toUpperCase();

        this.currentWord =
                "_ ".repeat(secretWord.length());

        this.attemptsLeft = 6;

        this.currentTurn = player1;

        this.guessedLetters = new HashSet<>();

        this.gameOver = false;
    }
}