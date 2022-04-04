package com.android.myproj.minesweeper.game.logic;

import androidx.core.util.Consumer;
import androidx.core.util.Predicate;

import com.android.myproj.minesweeper.config.JSONKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

    private final int row;
    private final int col;
    private final int mines;
    private final Tile[] board;
    private int coveredTiles;

    protected Board(Level level) {
        this.row = level.getRow();
        this.col = level.getCol();
        this.mines = level.getMines();
        this.board = new Tile[this.row * this.col];
        this.coveredTiles = this.row * this.col - this.mines;
    }

    protected List<Tile> getVisibleTiles() {
        List<Tile> visibleTiles = new ArrayList<>();
        for (Tile t : this.board) {
            if (!t.isCovered()) {
                visibleTiles.add(t);
            }
        }
        return visibleTiles;
    }

    protected List<Tile> getFlaggedTiles() {
        List<Tile> flaggedTiles = new ArrayList<>();
        for (Tile t : this.board) {
            if (t.isFlagged()) {
                flaggedTiles.add(t);
            }
        }
        return flaggedTiles;
    }

    protected void generateTiles(int tileIndex) {
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i] == null) {
                this.board[i] = new Tile(i, TileValue.ZERO);  // initialize all tiles to zero
            }
        }

        Predicate<Integer> canPlaceAtIndex = tileIndex == -1
                ? i -> !this.board[i].isMine()
                : i -> i != tileIndex && !this.isAdjacent(tileIndex, i) && !this.board[i].isMine();

        Random random = new Random();
        int mineCount = 0;
        while (mineCount < this.mines) {
            int indexToPlace = random.nextInt(this.board.length);
            if (canPlaceAtIndex.test(indexToPlace)) {
                this.board[indexToPlace].setTileValue(TileValue.MINE);
                mineCount++;
            }
        }
        this.calcAdjacent();
    }

    // Checks if tiles at index1 and index2 are adjacent
    private boolean isAdjacent(int index1, int index2) {
        return this.countAdjIf(index1, i -> i == index2) == 1;
    }

    private void calcAdjacent() {
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].isMine()) {
                continue;
            }
            this.board[i].setTileValue(TileValue.getTile(
                    this.countAdjIf(i, j -> this.board[j].getTileValue() == TileValue.MINE)
            ));
        }
    }

    protected List<Tile> selectTile(int index) {
        Tile tileSel = this.board[index];
        List<Tile> selectedTiles = new ArrayList<>();

        if (tileSel.isFlagged()) {
            return selectedTiles;
        }
        if (tileSel.isMine()) {
            this.selectAndAdd(index, selectedTiles);
            return selectedTiles;
        }

        if (tileSel.isCovered()) {
            // If tile is not flagged, uncover the tile and return its value
            this._selectTile(index, selectedTiles);
        } else if (tileSel.getTileValue().isNonZeroNumberTile()) {
            // Uncover all adjacent covered Tiles if #_of_adjacent_flags == tileSel.number_value
            this.uncoverAdj(index, selectedTiles);
        }

        // Decrement the number of covered tiles
        this.coveredTiles -= selectedTiles.size();

        return selectedTiles;
    }

    // Uncovers all adjacent zero tiles
    private void _selectTile(int index, List<Tile> selectedTiles) {
        if (this.board[index].isFlagged() || !this.board[index].isCovered()) {
            return;
        }

        // Select the tile at index and add to list
        this.selectAndAdd(index, selectedTiles);

        if (this.board[index].getTileValue().isNonZeroNumberTile()) {
            return;
        }
        this.doForEachAdjTile(index, i -> this._selectTile(i, selectedTiles));
    }

    private void uncoverAdj(int index, List<Tile> selectedTiles) {
        // count if TileValue == FLAGGED and if TileValue == MINE
        int adjFlags = countAdjIf(
                index,
                i -> this.board[i].isFlagged() && this.board[i].getTileValue() == TileValue.MINE
        );

        if (adjFlags != this.board[index].getTileValue().getCode()) {
            return;
        }

        Consumer<Integer> selectIf = i -> {
            if (board[i].isUncoverable()) {
                this._selectTile(i, selectedTiles);
            }
        };
        this.doForEachAdjTile(index, selectIf);
    }

    private void selectAndAdd(int index, List<Tile> selectedTiles) {
        this.board[index].select();
        selectedTiles.add(this.board[index]);
    }

    private void doForEachAdjTile(int index, Consumer<Integer> action) {
        this.doForEachAdjTile(
                Tile.getRowFromIndex(index, this.col),
                Tile.getColFromIndex(index, this.col),
                action
        );
    }

    private void doForEachAdjTile(int row, int col, Consumer<Integer> action) {
        for (int rAdd = -1; rAdd <= 1; rAdd++) {
            for (int cAdd = -1; cAdd <=1; cAdd++) {
                if (rAdd == 0 && cAdd == 0) {
                    continue;
                }
                int newRow = row + rAdd;
                int newCol = col + cAdd;
                if (newRow >= 0 && newRow < this.row && newCol >= 0 && newCol < this.col) {
                    action.accept(Tile.getIndexFromCoord(newRow, newCol, this.col));
                }
            }
        }
    }

    private int countAdjIf(int index, Predicate<Integer> equals) {
        return this.countAdjIf(
                Tile.getRowFromIndex(index, this.col),
                Tile.getColFromIndex(index, this.col),
                equals
        );
    }

    // Predicate<Integer> receives index as its parameter
    private int countAdjIf(int row, int col, Predicate<Integer> equals) {
        int[] result = new int[]{0};
        Consumer<Integer> addIf = i -> {
            if (equals.test(i)) {
                result[0]++;
            }
        };
        this.doForEachAdjTile(row, col, addIf);
        return result[0];
    }

    protected List<Tile> flagTile(int index) {
        List<Tile> flaggedTiles = new ArrayList<>();
        try {
            if (!this.board[index].isCovered()) {
                this.flagAdjacent(index, flaggedTiles);
            } else {
                this.flagAndAdd(index, flaggedTiles);
            }
        } catch (NullPointerException npe) {
            this.board[index] = new Tile(index, true, true, TileValue.FLAGGED);
            flaggedTiles.add(this.board[index]);
        }
        return flaggedTiles;
    }

    private void flagAdjacent(int index, List<Tile> flaggedTiles) {
        // If #_of_nonFlagged_adjacent_covered_cells + #_of_adjacent_flags == tile.number_value
        // Then flag all adjacent covered cells
        // isCovered() returns true if tile is covered (regardless of whether it is flagged or not)
        int numCoveredTiles = this.countAdjIf(index, i -> this.board[i].isCovered());

        if (this.board[index].getTileValue().getCode() != numCoveredTiles) {
            return;
        }

        Consumer<Integer> selectIf = i -> {
            if (this.board[i].isCovered() && !this.board[i].isFlagged()) {
                this.flagAndAdd(i, flaggedTiles);
            }
        };
        this.doForEachAdjTile(index, selectIf);
    }

    private void flagAndAdd(int index, List<Tile> flaggedTiles) {
        this.board[index].flag();
        flaggedTiles.add(this.board[index]);
    }

    protected List<Integer> getMineIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i].getTileValue() == TileValue.MINE) {
                indices.add(i);
            }
        }
        return indices;
    }

    protected boolean hasUncoveredAll() {
        return coveredTiles == 0;
    }

    protected Tile showHint() {
        // Check if it is possible to reveal a cell
        int count = 0;
        for (Tile tile : this.board) {
            if (tile.isUncoverable() && tile.getTileValue().getCode() > 0) {
                count++;
            }
        }
        // If there is no uncover-able Tile, return null
        if (count == 0) {
            return null;
        }

        int randIndex;
        while (true) {
            Random random = new Random();
            randIndex = random.nextInt(this.board.length);
            if (this.board[randIndex].isUncoverable() && this.board[randIndex].getTileValue().getCode() > 0) {
                this.coveredTiles--;
                this.board[randIndex].select();
                return this.board[randIndex];
            }
        }
    }

    protected void save(JSONObject savedState) throws JSONException {
        int[] isCoveredArray = new int[this.board.length];
        int[] isFlaggedArray = new int[this.board.length];
        int[] tileValueArray = new int[this.board.length];
        for (int i = 0; i < this.board.length; i++) {
            Tile tile = this.board[i];
            isCoveredArray[i] = tile.isCovered() ? 1 : 0;
            isFlaggedArray[i] = tile.isFlagged() ? 1 : 0;
            tileValueArray[i] = tile.getTileValue().getCode();
        }

        savedState.put(JSONKey.KEY_EXISTS_SAVED_GAME, true);
        savedState.put(JSONKey.KEY_COVERED_TILES, this.coveredTiles);
        savedState.put(JSONKey.KEY_ARRAY_IS_COVERED, new JSONArray(isCoveredArray));
        savedState.put(JSONKey.KEY_ARRAY_IS_FLAGGED, new JSONArray(isFlaggedArray));
        savedState.put(JSONKey.KEY_ARRAY_TILE_VALUE, new JSONArray(tileValueArray));
    }

    public void restore(JSONObject savedState) throws JSONException {
        this.coveredTiles = savedState.getInt(JSONKey.KEY_COVERED_TILES);

        JSONArray isCoveredArray = (JSONArray) savedState.get(JSONKey.KEY_ARRAY_IS_COVERED);
        JSONArray isFlaggedArray = (JSONArray) savedState.get(JSONKey.KEY_ARRAY_IS_FLAGGED);
        JSONArray tileValueArray = (JSONArray) savedState.get(JSONKey.KEY_ARRAY_TILE_VALUE);

        for (int i = 0; i < this.board.length; i++) {
            this.board[i] = new Tile(
                    i,
                    isCoveredArray.getInt(i) == 1,
                    isFlaggedArray.getInt(i) == 1,
                    TileValue.getTile(tileValueArray.getInt(i))
            );
        }
    }

}
