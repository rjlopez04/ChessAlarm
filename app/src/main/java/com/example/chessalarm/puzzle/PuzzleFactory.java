package com.example.chessalarm.puzzle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PuzzleFactory {

    private static final List<ChessPuzzle> PUZZLES = Arrays.asList(
            // Fool's mate: position after 1.f3 e5 2.g4??, Black to move plays Qh4#
            new ChessPuzzle(
                    "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2",
                    "Qh4#",
                    ChessPuzzle.Difficulty.EASY),
            // Back-rank mate: White rook delivers from a1 to a8, Black king trapped by own pawns
            new ChessPuzzle(
                    "6k1/5ppp/8/8/8/8/8/R5K1 w - - 0 1",
                    "Ra8#",
                    ChessPuzzle.Difficulty.EASY)
            // TODO: add more verified mate-in-one puzzles (good source: lichess.org/training/mateIn1)
    );

    private static final Random RANDOM = new Random();

    public List<ChessPuzzle> getAll() {
        return Collections.unmodifiableList(PUZZLES);
    }

    public ChessPuzzle random() {
        return PUZZLES.get(RANDOM.nextInt(PUZZLES.size()));
    }
}
