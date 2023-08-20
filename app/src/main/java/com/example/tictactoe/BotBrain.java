package com.example.tictactoe;

import android.widget.Button;

import java.util.Arrays;

public class BotBrain {

    private final GameMain INSTANCE;
    private final boolean isBot;
    private final int randomChance;

    public BotBrain(GameMain INSTANCE, boolean isBot, int randomChance) {
        this.INSTANCE = INSTANCE;
        this.isBot = isBot;
        this.randomChance = randomChance;
    }

    public void botMove() {
        Button[] buttonArray = INSTANCE.buttonArray;
        if (isBot && INSTANCE.turn.equals("O") && INSTANCE.winner == null) { // Get Bot Move if bot is chosen by difficulty
            int random = (int) (Math.random() * 100) + 1;
            if (random < randomChance) {
                boolean placed = false;
                while (!placed) {
                    int placeChance = (int) (Math.random() * 100) + 1;

                    for (int i = 0; i < 9; i++) {
                        if (!INSTANCE.isTaken(buttonArray[i])) {
                            if (placeChance < 15) {
                                INSTANCE.selectButton(i);
                                placed = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                bestMove();
            }
        }
    }

    public void bestMove() {

        if (!this.isBot) return;

        String[] board = INSTANCE.board;

        int move = -1, bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 9; i++) {
            if (Arrays.asList(board).contains(
                    String.valueOf(i + 1))) {
                board[i] = "O";
                int score = minimax(board, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                board[i] = String.valueOf(i + 1);
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }

        INSTANCE.selectButton(move);
    }

    public int minimax(String[] board, boolean isMaximizing, int alpha, int beta) {

        String result = INSTANCE.checkWinner();

        if (result != null) {
            if (result.equalsIgnoreCase("X")) {
                return -1;
            } else if (result.equalsIgnoreCase("O")) {
                return 1;
            } else if (result.equalsIgnoreCase("draw")) {
                return 0;
            }
        }

        int bestScore;
        if (isMaximizing) {
            bestScore = Integer.MIN_VALUE;

            for (int i = 0; i < 9; i++) {
                if (Arrays.asList(board).contains(
                        String.valueOf(i + 1))) {
                    board[i] = "O";
                    int score = minimax(board, false, alpha, beta);
                    board[i] = String.valueOf(i + 1);
                    bestScore = Math.max(score, bestScore);
                    if (score >= beta) {
                        break;
                    }
                    alpha = Math.max(alpha, bestScore);
                }
            }

        } else {
            bestScore = Integer.MAX_VALUE;

            for (int i = 0; i < 9; i++) {
                if (Arrays.asList(board).contains(
                        String.valueOf(i + 1))) {
                    board[i] = "X";
                    int score = minimax(board, true, alpha, beta);
                    board[i] = String.valueOf(i + 1);
                    bestScore = Math.min(score, bestScore);
                    if (score <= alpha)
                        break;
                    beta = Math.min(beta, bestScore);

                }
            }
        }
        return bestScore;

    }
}
