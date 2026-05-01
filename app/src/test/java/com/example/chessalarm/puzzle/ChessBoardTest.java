package com.example.chessalarm.puzzle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChessBoardTest {

    private static final String START_POSITION =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final String EMPTY_BOARD =
            "8/8/8/8/8/8/8/8 w - - 0 1";
    private static final String FOOLS_MATE =
            "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2";

    @Test
    public void parsesStartingPositionPieces() {
        ChessBoard board = ChessBoard.fromFen(START_POSITION);

        // White back rank (rank 0)
        assertEquals(ChessBoard.Piece.WHITE_ROOK, board.pieceAt(0, 0));
        assertEquals(ChessBoard.Piece.WHITE_KING, board.pieceAt(0, 4));
        assertEquals(ChessBoard.Piece.WHITE_ROOK, board.pieceAt(0, 7));
        // White pawns (rank 1)
        for (int f = 0; f < 8; f++) {
            assertEquals(ChessBoard.Piece.WHITE_PAWN, board.pieceAt(1, f));
        }
        // Empty middle (ranks 2-5)
        for (int r = 2; r <= 5; r++) {
            for (int f = 0; f < 8; f++) {
                assertEquals(ChessBoard.Piece.EMPTY, board.pieceAt(r, f));
            }
        }
        // Black pawns (rank 6) and back rank (rank 7)
        assertEquals(ChessBoard.Piece.BLACK_PAWN, board.pieceAt(6, 3));
        assertEquals(ChessBoard.Piece.BLACK_QUEEN, board.pieceAt(7, 3));
        assertEquals(ChessBoard.Piece.BLACK_KING, board.pieceAt(7, 4));
    }

    @Test
    public void parsesEmptyBoard() {
        ChessBoard board = ChessBoard.fromFen(EMPTY_BOARD);
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                assertEquals(ChessBoard.Piece.EMPTY, board.pieceAt(r, f));
            }
        }
    }

    @Test
    public void parsesMidGamePosition() {
        ChessBoard board = ChessBoard.fromFen(FOOLS_MATE);
        // White pawns moved: f3, g4
        assertEquals(ChessBoard.Piece.WHITE_PAWN, board.pieceAt(2, 5)); // f3
        assertEquals(ChessBoard.Piece.WHITE_PAWN, board.pieceAt(3, 6)); // g4
        // Original f2 and g2 squares are now empty
        assertEquals(ChessBoard.Piece.EMPTY, board.pieceAt(1, 5));
        assertEquals(ChessBoard.Piece.EMPTY, board.pieceAt(1, 6));
        // Black pawn moved to e5
        assertEquals(ChessBoard.Piece.BLACK_PAWN, board.pieceAt(4, 4));
    }

    @Test
    public void parsesSideToMove() {
        assertTrue(ChessBoard.fromFen(START_POSITION).isWhiteToMove());
        assertFalse(ChessBoard.fromFen(FOOLS_MATE).isWhiteToMove());
    }

    @Test
    public void defaultsToWhiteWhenSideMissing() {
        // Bare placement with no side-to-move field
        assertTrue(ChessBoard.fromFen("8/8/8/8/8/8/8/8").isWhiteToMove());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnNullFen() {
        ChessBoard.fromFen(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnEmptyFen() {
        ChessBoard.fromFen("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnTooFewRanks() {
        ChessBoard.fromFen("8/8/8/8/8/8/8 w - - 0 1");
    }

    @Test
    public void throwsOnIncompleteRank() {
        try {
            ChessBoard.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBN w KQkq - 0 1");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnknownPieceChar() {
        ChessBoard.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNX w KQkq - 0 1");
    }
}
