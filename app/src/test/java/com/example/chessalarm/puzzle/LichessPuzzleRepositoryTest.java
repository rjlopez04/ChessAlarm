package com.example.chessalarm.puzzle;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LichessPuzzleRepositoryTest {

    private static List<ChessPuzzle> parse(String csv) throws Exception {
        return LichessPuzzleRepository.parseCsv(
                new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void parsesValidRows() throws Exception {
        String csv =
                "p1,8/8/8/8/8/8/8/k6K w - - 0 1,e2e4,1000,80,90,1000,mateIn1,,\n" +
                "p2,8/8/8/8/8/8/8/k6K b - - 0 1,d4d5,1100,80,90,1000,mateIn1,,\n";

        List<ChessPuzzle> puzzles = parse(csv);

        assertEquals(2, puzzles.size());
        assertEquals("8/8/8/8/8/8/8/k6K w - - 0 1", puzzles.get(0).getFen());
        assertEquals(1, puzzles.get(0).getAcceptedMoves().size());
        assertEquals("e2e4", puzzles.get(0).getAcceptedMoves().get(0));
    }

    @Test
    public void skipsBlankAndMalformedRows() throws Exception {
        String csv =
                "good,8/8/8/8/8/8/8/k6K w - - 0 1,a1a2,1000,,,,,,,\n" +
                "\n" +
                "broken,,,,\n" +
                "tooFewCols,foo\n";

        List<ChessPuzzle> puzzles = parse(csv);

        assertEquals(1, puzzles.size());
    }

    @Test
    public void takesLastUciTokenAsAnswer() throws Exception {
        // Simulates raw Lichess two-move format: setup move + solver move.
        String csv = "p1,8/8/8/8/8/8/8/k6K w - - 0 1,e7e5 d8h4,1000,,,,,,,\n";

        List<ChessPuzzle> puzzles = parse(csv);

        assertEquals(1, puzzles.size());
        assertEquals("d8h4", puzzles.get(0).getAcceptedMoves().get(0));
    }

    @Test
    public void mapsRatingToDifficultyBuckets() throws Exception {
        String csv =
                "easy,8/8/8/8/8/8/8/k6K w - - 0 1,a1a2,800,,,,,,,\n" +
                "med,8/8/8/8/8/8/8/k6K w - - 0 1,a1a2,1100,,,,,,,\n" +
                "hard,8/8/8/8/8/8/8/k6K w - - 0 1,a1a2,1500,,,,,,,\n";

        List<ChessPuzzle> puzzles = parse(csv);

        assertSame(ChessPuzzle.Difficulty.EASY, puzzles.get(0).getDifficulty());
        assertSame(ChessPuzzle.Difficulty.MEDIUM, puzzles.get(1).getDifficulty());
        assertSame(ChessPuzzle.Difficulty.HARD, puzzles.get(2).getDifficulty());
    }

    @Test
    public void ratingDefaultsTo1000WhenUnparseable() throws Exception {
        String csv = "p1,8/8/8/8/8/8/8/k6K w - - 0 1,a1a2,not-a-number,,,,,,,\n";

        List<ChessPuzzle> puzzles = parse(csv);

        // 1000 → MEDIUM bucket
        assertSame(ChessPuzzle.Difficulty.MEDIUM, puzzles.get(0).getDifficulty());
    }
}
