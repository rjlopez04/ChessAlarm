package com.example.chessalarm.puzzle;

public class MateInOneSolver implements PuzzleSolver {

    @Override
    public boolean checkAnswer(ChessPuzzle puzzle, String userMove) {
        if (puzzle == null || userMove == null) {
            return false;
        }
        String input = normalize(userMove);
        for (String accepted : puzzle.getAcceptedMoves()) {
            if (accepted != null && normalize(accepted).equals(input)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String move) {
        return move.trim().toLowerCase().replace("+", "").replace("#", "");
    }
}
