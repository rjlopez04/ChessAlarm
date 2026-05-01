package com.example.chessalarm.puzzle;

public final class ChessBoard {

    public enum Piece {
        WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_PAWN,
        BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT, BLACK_PAWN,
        EMPTY
    }

    private final Piece[][] board;
    private final boolean whiteToMove;

    private ChessBoard(Piece[][] board, boolean whiteToMove) {
        this.board = board;
        this.whiteToMove = whiteToMove;
    }

    /**
     * Parses a FEN string. Only the piece-placement and side-to-move fields are used;
     * castling rights, en passant, and clocks are ignored.
     *
     * @throws IllegalArgumentException if the FEN is null, empty, or malformed.
     */
    public static ChessBoard fromFen(String fen) {
        if (fen == null || fen.trim().isEmpty()) {
            throw new IllegalArgumentException("FEN must not be null or empty");
        }
        String[] parts = fen.trim().split("\\s+");
        String placement = parts[0];
        boolean whiteToMove = parts.length < 2 || "w".equalsIgnoreCase(parts[1]);

        String[] fenRanks = placement.split("/");
        if (fenRanks.length != 8) {
            throw new IllegalArgumentException("FEN must have 8 ranks separated by '/'");
        }

        Piece[][] board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            // FEN lists ranks from 8 down to 1; our board[0] is rank 1.
            int boardRank = 7 - i;
            int file = 0;
            for (char c : fenRanks[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    int empty = c - '0';
                    if (empty < 1 || empty > 8 || file + empty > 8) {
                        throw new IllegalArgumentException("Invalid empty-square count in rank " + (boardRank + 1));
                    }
                    for (int j = 0; j < empty; j++) {
                        board[boardRank][file++] = Piece.EMPTY;
                    }
                } else {
                    Piece p = pieceFromChar(c);
                    if (p == null) {
                        throw new IllegalArgumentException("Unknown FEN piece char: '" + c + "'");
                    }
                    if (file >= 8) {
                        throw new IllegalArgumentException("Rank " + (boardRank + 1) + " has too many pieces");
                    }
                    board[boardRank][file++] = p;
                }
            }
            if (file != 8) {
                throw new IllegalArgumentException("Rank " + (boardRank + 1) + " is incomplete (got " + file + " files)");
            }
        }
        return new ChessBoard(board, whiteToMove);
    }

    /**
     * @param rank 0–7, where 0 is rank 1 (white's home) and 7 is rank 8 (black's home).
     * @param file 0–7, where 0 is file a and 7 is file h.
     */
    public Piece pieceAt(int rank, int file) {
        if (rank < 0 || rank > 7 || file < 0 || file > 7) {
            throw new IndexOutOfBoundsException("rank/file must be 0-7");
        }
        return board[rank][file];
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    private static Piece pieceFromChar(char c) {
        switch (c) {
            case 'K': return Piece.WHITE_KING;
            case 'Q': return Piece.WHITE_QUEEN;
            case 'R': return Piece.WHITE_ROOK;
            case 'B': return Piece.WHITE_BISHOP;
            case 'N': return Piece.WHITE_KNIGHT;
            case 'P': return Piece.WHITE_PAWN;
            case 'k': return Piece.BLACK_KING;
            case 'q': return Piece.BLACK_QUEEN;
            case 'r': return Piece.BLACK_ROOK;
            case 'b': return Piece.BLACK_BISHOP;
            case 'n': return Piece.BLACK_KNIGHT;
            case 'p': return Piece.BLACK_PAWN;
            default: return null;
        }
    }
}
