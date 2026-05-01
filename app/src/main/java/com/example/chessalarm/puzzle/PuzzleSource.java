package com.example.chessalarm.puzzle;

import java.util.List;

/**
 * A source of chess puzzles. Implementations may be hardcoded, asset-backed,
 * or networked — callers should not need to care which.
 */
public interface PuzzleSource {
    List<ChessPuzzle> getAll();
    ChessPuzzle random();
}
