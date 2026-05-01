package com.example.chessalarm.puzzle;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Loads chess puzzles from a CSV asset bundled with the app, modelled on the
 * <a href="https://database.lichess.org/#puzzles">Lichess Puzzle Database</a> dump format.
 *
 * <p>Expected CSV columns (no header row):
 * {@code PuzzleId, FEN, Moves, Rating, RatingDeviation, Popularity, NbPlays, Themes, GameUrl, OpeningTags}.
 *
 * <p>Two practical notes:
 * <ul>
 *   <li><b>FEN must be the position the user solves from.</b> Raw Lichess rows store the FEN
 *       <em>before</em> the opponent's setup move and put the setup move first in the Moves field;
 *       to use those directly you would need to apply the setup move (requires a UCI move applier
 *       not bundled in this branch). The shipped {@code puzzles.csv} is hand-curated so the FEN
 *       already represents the solving position.</li>
 *   <li><b>The last whitespace-separated UCI token in the Moves field is treated as the answer.</b>
 *       Works for both single-move curated rows and the standard Lichess two-move (setup + solve)
 *       form.</li>
 * </ul>
 */
public class LichessPuzzleRepository implements PuzzleSource {

    private static final String ASSET_FILE = "puzzles.csv";

    private final Context appContext;
    private final Random random = new Random();
    private List<ChessPuzzle> cache;

    public LichessPuzzleRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public synchronized List<ChessPuzzle> getAll() {
        if (cache == null) {
            try (InputStream in = appContext.getAssets().open(ASSET_FILE)) {
                cache = parseCsv(in);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + ASSET_FILE, e);
            }
        }
        return cache;
    }

    @Override
    public ChessPuzzle random() {
        List<ChessPuzzle> all = getAll();
        if (all.isEmpty()) {
            throw new IllegalStateException("No puzzles loaded from " + ASSET_FILE);
        }
        return all.get(random.nextInt(all.size()));
    }

    /**
     * Package-private for unit testing — lets tests feed in a {@code ByteArrayInputStream}
     * without needing an Android {@code Context} or asset manager.
     */
    static List<ChessPuzzle> parseCsv(InputStream in) throws IOException {
        List<ChessPuzzle> puzzles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ChessPuzzle p = parseRow(line);
                if (p != null) puzzles.add(p);
            }
        }
        return Collections.unmodifiableList(puzzles);
    }

    private static ChessPuzzle parseRow(String row) {
        if (row == null || row.trim().isEmpty()) return null;
        String[] cols = row.split(",");
        if (cols.length < 4) return null;
        String fen = cols[1].trim();
        String moves = cols[2].trim();
        if (fen.isEmpty() || moves.isEmpty()) return null;
        String[] tokens = moves.split("\\s+");
        String solverMove = tokens[tokens.length - 1];
        if (solverMove.isEmpty()) return null;
        int rating = parseIntOr(cols[3].trim(), 1000);
        try {
            return new ChessPuzzle(fen, solverMove, ratingToDifficulty(rating));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static int parseIntOr(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static ChessPuzzle.Difficulty ratingToDifficulty(int rating) {
        if (rating < 1000) return ChessPuzzle.Difficulty.EASY;
        if (rating < 1300) return ChessPuzzle.Difficulty.MEDIUM;
        return ChessPuzzle.Difficulty.HARD;
    }
}
