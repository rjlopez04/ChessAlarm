package com.example.chessalarm.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.chessalarm.puzzle.ChessBoard;

/**
 * Renders a chess position on a square canvas. Pure Read-only display: parses a FEN
 * via {@link ChessBoard#fromFen(String)} and draws the squares + Unicode piece glyphs.
 * Auto-orients so the side-to-move is at the bottom.
 */
public class ChessBoardView extends View {

    private static final int LIGHT_SQUARE = 0xFFF0D9B5;
    private static final int DARK_SQUARE  = 0xFFB58863;
    private static final int LABEL_LIGHT  = 0xCCB58863; // file/rank letters on dark squares
    private static final int LABEL_DARK   = 0xCCF0D9B5; // file/rank letters on light squares

    private final Paint lightSquarePaint = new Paint();
    private final Paint darkSquarePaint = new Paint();
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pieceFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pieceStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect glyphBounds = new Rect();

    @Nullable private ChessBoard board;

    public ChessBoardView(Context context) {
        super(context);
        init();
    }

    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChessBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lightSquarePaint.setColor(LIGHT_SQUARE);
        darkSquarePaint.setColor(DARK_SQUARE);
        labelPaint.setTextAlign(Paint.Align.LEFT);
        pieceFillPaint.setStyle(Paint.Style.FILL);
        pieceFillPaint.setTextAlign(Paint.Align.CENTER);
        pieceStrokePaint.setStyle(Paint.Style.STROKE);
        pieceStrokePaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Sets the position to render. Pass null to clear. Throws if the FEN is malformed.
     */
    public void setPosition(@Nullable String fen) {
        this.board = (fen == null) ? null : ChessBoard.fromFen(fen);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (wMode == MeasureSpec.UNSPECIFIED && hMode == MeasureSpec.UNSPECIFIED) {
            size = 320; // arbitrary default
        } else if (wMode == MeasureSpec.UNSPECIFIED) {
            size = hSize;
        } else if (hMode == MeasureSpec.UNSPECIFIED) {
            size = wSize;
        } else {
            size = Math.min(wSize, hSize);
        }
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int side = Math.min(getWidth(), getHeight());
        if (side <= 0) return;
        float sq = side / 8f;
        boolean flipped = board != null && !board.isWhiteToMove();

        float pieceTextSize = sq * 0.75f;
        pieceFillPaint.setTextSize(pieceTextSize);
        pieceStrokePaint.setTextSize(pieceTextSize);
        pieceStrokePaint.setStrokeWidth(Math.max(1.5f, sq * 0.04f));
        labelPaint.setTextSize(Math.max(8f, sq * 0.18f));

        // Squares + pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                float left = col * sq;
                float top = row * sq;
                boolean isLight = (row + col) % 2 == 0;
                canvas.drawRect(left, top, left + sq, top + sq,
                        isLight ? lightSquarePaint : darkSquarePaint);

                if (board != null) {
                    int boardRank = flipped ? row : 7 - row;
                    int boardFile = flipped ? 7 - col : col;
                    ChessBoard.Piece p = board.pieceAt(boardRank, boardFile);
                    if (p != ChessBoard.Piece.EMPTY) {
                        drawPiece(canvas, p, left, top, sq);
                    }
                }
            }
        }

        if (board == null) return;

        // Coordinate labels — file letters across bottom row, rank numbers down left column
        float pad = Math.max(2f, sq * 0.06f);
        for (int col = 0; col < 8; col++) {
            int boardFile = flipped ? 7 - col : col;
            String fileChar = String.valueOf((char) ('a' + boardFile));
            // Bottom row is row=7
            boolean bottomIsLight = (7 + col) % 2 == 0;
            labelPaint.setColor(bottomIsLight ? LABEL_LIGHT : LABEL_DARK);
            float textWidth = labelPaint.measureText(fileChar);
            canvas.drawText(fileChar,
                    col * sq + sq - pad - textWidth,
                    side - pad,
                    labelPaint);
        }
        for (int row = 0; row < 8; row++) {
            int boardRank = flipped ? row : 7 - row;
            String rankStr = String.valueOf(boardRank + 1);
            // Leftmost col is col=0
            boolean leftIsLight = (row + 0) % 2 == 0;
            labelPaint.setColor(leftIsLight ? LABEL_LIGHT : LABEL_DARK);
            canvas.drawText(rankStr,
                    pad,
                    row * sq + pad + labelPaint.getTextSize(),
                    labelPaint);
        }
    }

    private void drawPiece(Canvas canvas, ChessBoard.Piece p, float left, float top, float sq) {
        String glyph = glyphFor(p);
        boolean isWhite = p.name().startsWith("WHITE");
        pieceFillPaint.setColor(isWhite ? Color.WHITE : Color.BLACK);
        pieceStrokePaint.setColor(isWhite ? Color.BLACK : Color.WHITE);

        pieceFillPaint.getTextBounds(glyph, 0, glyph.length(), glyphBounds);
        float cx = left + sq / 2f;
        float cy = top + sq / 2f - glyphBounds.exactCenterY();
        // Stroke first so the fill sits on top
        canvas.drawText(glyph, cx, cy, pieceStrokePaint);
        canvas.drawText(glyph, cx, cy, pieceFillPaint);
    }

    private static String glyphFor(ChessBoard.Piece p) {
        // Use the Unicode "black" (filled) chess glyphs for both colors —
        // they have more visual weight than the outline (white) set.
        switch (p) {
            case WHITE_KING:   case BLACK_KING:   return "♚";
            case WHITE_QUEEN:  case BLACK_QUEEN:  return "♛";
            case WHITE_ROOK:   case BLACK_ROOK:   return "♜";
            case WHITE_BISHOP: case BLACK_BISHOP: return "♝";
            case WHITE_KNIGHT: case BLACK_KNIGHT: return "♞";
            case WHITE_PAWN:   case BLACK_PAWN:   return "♟";
            default: return "";
        }
    }
}
