package com.example.chessalarm.puzzle;

public class MateInOneSolver implements PuzzleSolver {

    @Override
    public boolean checkAnswer(ChessPuzzle puzzle, String userMove) {
        if (puzzle == null || userMove == null) {
            return false;
        }
        return normalize(puzzle.getCorrectMove()).equals(normalize(userMove));
    }

    private String normalize(String move) {
        return move.trim().toLowerCase().replace("+", "").replace("#", "");
    }
}
