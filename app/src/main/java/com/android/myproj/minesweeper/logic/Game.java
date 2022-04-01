package com.android.myproj.minesweeper.logic;

import com.android.myproj.minesweeper.config.JSONKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Game {

    private final Board board;
    private int leftoverMine;

    public Game(Level level) {
        this.board = new Board(level);
        this.leftoverMine = level.getMines();
    }

    public int getLeftoverMine () {
        return this.leftoverMine;
    }

    public List<Tile> getVisibleTiles() {
        return this.board.getVisibleTiles();
    }

    public List<Tile> getFlaggedTiles() {
        return this.board.getFlaggedTiles();
    }

    public void start(int tileIndex) {
        this.board.generateTiles(tileIndex);
    }

    public int getTileIndex(Tile tile) {
        return tile.getIndex();
    }

    public boolean isTileCovered(Tile tile) {
        return tile.isCovered();
    }

    public boolean isTileFlagged(Tile tile) {
        return tile.isFlagged();
    }

    public TileValue getTileValue(Tile tile) {
        return tile.getTileValue();
    }

    // Returns a list of all selected Tiles
    public List<Tile> selectTile(int index) {
        return this.board.selectTile(index);
    }

    public List<Tile> flagTile(int index) {
        List<Tile> flaggedTiles = this.board.flagTile(index);
        for (Tile t : flaggedTiles) {
            if (t.isFlagged()) {
                this.leftoverMine--;
            } else if (t.isCovered()) {
                this.leftoverMine++;
            }
        }
        return flaggedTiles;
    }

    public List<Integer> getMineIndices() {
        return this.board.getMineIndices();
    }

    public boolean hasWon() {
        return this.board.hasUncoveredAll();
    }

    public Tile showHint() {
        return this.board.showHint();
    }

    public JSONObject save() throws JSONException {
        JSONObject savedState = this.board.save();
        savedState.put(JSONKey.KEY_LEFTOVER_MINES, this.leftoverMine);
        return savedState;
    }

    public static Game restore(JSONObject savedState) throws JSONException {
        Game game = new Game(Level.getLevelFromCode(savedState.getInt(JSONKey.KEY_LEVEL)));
        game.leftoverMine = savedState.getInt(JSONKey.KEY_LEFTOVER_MINES);
        game.board.restore(savedState);
        return game;
    }

}
