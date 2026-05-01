package com.example.chessalarm.puzzle;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MateInOneSolverTest {

    private PuzzleSolver solver;
    private ChessPuzzle puzzle;

    @Before
    public void setUp() {
        solver = new MateInOneSolver();
        puzzle = new ChessPuzzle(
                "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2",
                "Qh4#",
                ChessPuzzle.Difficulty.EASY);
    }

    @Test
    public void correctMoveReturnsTrue() {
        assertTrue(solver.checkAnswer(puzzle, "Qh4#"));
    }

    @Test
    public void correctMoveWithoutMateMarkerReturnsTrue() {
        assertTrue(solver.checkAnswer(puzzle, "Qh4"));
    }

    @Test
    public void correctMoveIsCaseInsensitive() {
        assertTrue(solver.checkAnswer(puzzle, "qh4"));
    }

    @Test
    public void correctMoveWithSurroundingWhitespaceReturnsTrue() {
        assertTrue(solver.checkAnswer(puzzle, "  Qh4  "));
    }

    @Test
    public void wrongMoveReturnsFalse() {
        assertFalse(solver.checkAnswer(puzzle, "Qh5"));
    }

    @Test
    public void emptyMoveReturnsFalse() {
        assertFalse(solver.checkAnswer(puzzle, ""));
    }

    @Test
    public void nullMoveReturnsFalse() {
        assertFalse(solver.checkAnswer(puzzle, null));
    }

    @Test
    public void nullPuzzleReturnsFalse() {
        assertFalse(solver.checkAnswer(null, "Qh4"));
    }

    @Test
    public void acceptsUciAnswerForUciStoredPuzzle() {
        ChessPuzzle uciPuzzle = new ChessPuzzle(
                "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2",
                "d8h4",
                ChessPuzzle.Difficulty.EASY);
        assertTrue(solver.checkAnswer(uciPuzzle, "d8h4"));
        assertTrue(solver.checkAnswer(uciPuzzle, "D8H4"));
        assertFalse(solver.checkAnswer(uciPuzzle, "Qh4"));
    }

    @Test
    public void multipleAcceptedMovesEitherWorks() {
        ChessPuzzle multiAnswer = new ChessPuzzle(
                "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2",
                Arrays.asList("Qh4#", "d8h4"),
                ChessPuzzle.Difficulty.EASY);
        assertTrue(solver.checkAnswer(multiAnswer, "Qh4"));
        assertTrue(solver.checkAnswer(multiAnswer, "d8h4"));
        assertFalse(solver.checkAnswer(multiAnswer, "Nf6"));
    }
}
