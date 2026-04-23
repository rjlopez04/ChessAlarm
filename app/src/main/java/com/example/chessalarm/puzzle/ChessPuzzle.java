package com.example.chessalarm.puzzle;

public final class ChessPuzzle {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private final String fen;
    private final String correctMove;
    private final Difficulty difficulty;

    public ChessPuzzle(String fen, String correctMove, Difficulty difficulty) {
        if (fen == null || correctMove == null || difficulty == null) {
            throw new IllegalArgumentException("ChessPuzzle fields must not be null");
        }
        this.fen = fen;
        this.correctMove = correctMove;
        this.difficulty = difficulty;
    }

    public String getFen() {
        return fen;
    }

    public String getCorrectMove() {
        return correctMove;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
