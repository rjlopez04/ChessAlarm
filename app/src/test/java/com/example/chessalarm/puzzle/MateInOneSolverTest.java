package com.example.chessalarm.puzzle;

import org.junit.Before;
import org.junit.Test;

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
}
