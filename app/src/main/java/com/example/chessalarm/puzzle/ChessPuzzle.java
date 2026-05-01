package com.example.chessalarm.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChessPuzzle {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private final String fen;
    private final List<String> acceptedMoves;
    private final Difficulty difficulty;

    /**
     * Primary constructor for puzzles that accept multiple equivalent answers
     * (e.g. UCI {@code "d8h4"} and SAN {@code "Qh4#"}).
     */
    public ChessPuzzle(String fen, List<String> acceptedMoves, Difficulty difficulty) {
        if (fen == null || acceptedMoves == null || acceptedMoves.isEmpty() || difficulty == null) {
            throw new IllegalArgumentException(
                    "ChessPuzzle: fen, acceptedMoves (non-empty), difficulty must all be non-null");
        }
        this.fen = fen;
        this.acceptedMoves = Collections.unmodifiableList(new ArrayList<>(acceptedMoves));
        this.difficulty = difficulty;
    }

    /** Convenience constructor for puzzles with a single accepted move. */
    public ChessPuzzle(String fen, String singleMove, Difficulty difficulty) {
        this(fen, Collections.singletonList(singleMove), difficulty);
    }

    public String getFen() {
        return fen;
    }

    public List<String> getAcceptedMoves() {
        return acceptedMoves;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
