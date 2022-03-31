package com.android.myproj.minesweeper.logic;

import androidx.core.util.Predicate;

import com.android.myproj.minesweeper.util.JSONKey;

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

        Random random = new Random();
        int mineCount = 0;
        while (mineCount < this.mines) {
            int indexToPlace = random.nextInt(this.row * this.col);
            if (indexToPlace != tileIndex && !this.board[indexToPlace].isMine()) {
                this.board[indexToPlace].setTileValue(TileValue.MINE);
                mineCount++;
            }
        }
        this.calcAdjacent();
    }

    private void calcAdjacent() {
        int index;
        for (int r = 0; r < this.row; r++) {
            for (int c = 0; c < this.col; c++) {
                index = Tile.getIndexFromCoord(r, c, this.col);
                if (this.board[index].isMine()) {
                    continue;
                }
                this.board[index].setTileValue(TileValue.getTile(
                        this.countAdjEquals(r, c, i -> this.board[i].getTileValue() == TileValue.MINE)
                ));
            }
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

        boolean validLeft = Tile.getColFromIndex(index, this.col) != 0;
        boolean validRight = Tile.getColFromIndex(index, this.col) != this.col - 1;
        boolean validTop = Tile.getRowFromIndex(index, this.col) != 0;
        boolean validBottom = Tile.getRowFromIndex(index, this.col) != this.row - 1;

        if (validLeft) {
            this._selectTile(index - 1, selectedTiles);
            if (validTop) {
                this._selectTile(index - this.col - 1, selectedTiles);
            }
            if (validBottom) {
                this._selectTile(index + this.col - 1, selectedTiles);
            }
        }
        if (validRight) {
            this._selectTile(index + 1, selectedTiles);
            if (validTop) {
                this._selectTile(index - this.col + 1, selectedTiles);
            }
            if (validBottom) {
                this._selectTile(index + this.col + 1, selectedTiles);
            }
        }
        if (validTop) {
            this._selectTile(index - this.col, selectedTiles);
        }
        if (validBottom) {
            this._selectTile(index + this.col, selectedTiles);
        }
    }

    private void uncoverAdj(int index, List<Tile> selectedTiles) {
        // count if TileValue == FLAGGED and if TileValue == MINE

        int adjFlags = countAdjEquals(
                index,
                i -> this.board[i].isFlagged() && this.board[i].getTileValue() == TileValue.MINE
        );
        if (adjFlags != this.board[index].getTileValue().getCode()) {
            return;
        }

        boolean validLeft = Tile.getColFromIndex(index, this.col) != 0;
        boolean validRight = Tile.getColFromIndex(index, this.col) != this.col - 1;
        boolean validTop = Tile.getRowFromIndex(index, this.col) != 0;
        boolean validBottom = Tile.getRowFromIndex(index, this.col) != this.row - 1;

        if (validLeft) {
            if (this.board[index - 1].isUncoverable()) {
                this._selectTile(index - 1, selectedTiles);
            }
            if (validTop && this.board[index - this.col - 1].isUncoverable()) {
                this._selectTile(index - this.col - 1, selectedTiles);
            }
            if (validBottom && this.board[index + this.col - 1].isUncoverable()) {
                this._selectTile(index + this.col - 1, selectedTiles);
            }
        }
        if (validRight) {
            if (this.board[index + 1].isUncoverable()) {
                this._selectTile(index + 1, selectedTiles);
            }
            if (validTop && this.board[index - this.col + 1].isUncoverable()) {
                this._selectTile(index - this.col + 1, selectedTiles);
            }
            if (validBottom && this.board[index + this.col + 1].isUncoverable()) {
                this._selectTile(index + this.col + 1, selectedTiles);
            }
        }
        if (validTop && this.board[index - this.col].isUncoverable()) {
            this._selectTile(index - this.col, selectedTiles);
        }
        if (validBottom && this.board[index + this.col].isUncoverable()) {
            this._selectTile(index + this.col, selectedTiles);
        }
    }
    
    private void selectAndAdd(int index, List<Tile> selectedTiles) {
        this.board[index].select();
        selectedTiles.add(this.board[index]);
    }

    private int countAdjEquals(int index, Predicate<Integer> equals) {
        return this.countAdjEquals(
                Tile.getRowFromIndex(index, this.col),
                Tile.getColFromIndex(index, this.col),
                equals
        );
    }

    // Predicate<Integer> receives index as its parameter
    private int countAdjEquals(int row, int col, Predicate<Integer> equals) {
        int index = Tile.getIndexFromCoord(row, col, this.col);
        int count = 0;
        
        boolean validLeft = col != 0;
        boolean validRight = col != this.col - 1;
        boolean validTop = row != 0;
        boolean validBottom = row != this.row - 1;

        // Calculate three tiles on the left (top left, left, bottom left)
        if (validLeft) {
            if (equals.test(index - 1)) {
                count++;
            }
            if (validTop && equals.test(index - this.col - 1)) {
                count++;
            }
            if (validBottom && equals.test(index + this.col - 1)) {
                count++;
            }
        }
        // Calculate three tiles on the right (top right, right, bottom right)
        if (validRight) {
            if (equals.test(index + 1)) {
                count++;
            }
            if (validTop && equals.test(index - this.col + 1)) {
                count++;
            }
            if (validBottom && equals.test(index + this.col + 1)) {
                count++;
            }
        }
        // Calculate one tile directly above
        if (validTop && equals.test(index - this.col)) {
            count++;
        }
        // Calculate one tile directly below
        if (validBottom && equals.test(index + this.col)) {
            count++;
        }
        
        return count;
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

    protected void flagAdjacent(int index, List<Tile> flaggedTiles) {
        // If #_of_nonFlagged_adjacent_covered_cells + #_of_adjacent_flags == tile.number_value
        // Then flag all adjacent covered cells
        // isCovered() returns true if tile is covered (regardless of whether it is flagged or not)
        int numCoveredTiles = this.countAdjEquals(index, i -> this.board[i].isCovered());
        
        if (this.board[index].getTileValue().getCode() != numCoveredTiles) {
            return;
        }

        Predicate<Integer> coveredAndNotFlagged = i -> this.board[i].isCovered() && !this.board[i].isFlagged();
        boolean validLeft = Tile.getColFromIndex(index, this.col) != 0;
        boolean validRight = Tile.getColFromIndex(index, this.col) != this.col - 1;
        boolean validTop = Tile.getRowFromIndex(index, this.col) != 0;
        boolean validBottom = Tile.getRowFromIndex(index, this.col) != this.row - 1;
        
        if (validLeft) {
            if (coveredAndNotFlagged.test(index - 1)) {
                this.flagAndAdd(index - 1, flaggedTiles);
            }
            if (validTop && coveredAndNotFlagged.test(index - this.col - 1)) {
                this.flagAndAdd(index - this.col - 1, flaggedTiles);
            }
            if (validBottom && coveredAndNotFlagged.test(index + this.col - 1)) {
                this.flagAndAdd(index + this.col - 1, flaggedTiles);
            }
        }
        if (validRight) {
            if (coveredAndNotFlagged.test(index + 1)) {
                this.flagAndAdd(index + 1, flaggedTiles);
            }
            if (validTop && coveredAndNotFlagged.test(index - this.col + 1)) {
                this.flagAndAdd(index - this.col + 1, flaggedTiles);
            }
            if (validBottom && coveredAndNotFlagged.test(index + this.col + 1)) {
                this.flagAndAdd(index + this.col + 1, flaggedTiles);
            }
        }
        if (validTop && coveredAndNotFlagged.test(index - this.col)) {
            this.flagAndAdd(index - this.col, flaggedTiles);
        }
        if (validBottom && coveredAndNotFlagged.test(index + this.col)) {
            this.flagAndAdd(index + this.col, flaggedTiles);
        }
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

    protected JSONObject save() throws JSONException {
        JSONObject savedState = new JSONObject();

        int[] isCoveredArray = new int[this.board.length];
        int[] isFlaggedArray = new int[this.board.length];
        int[] tileValueArray = new int[this.board.length];
        for (int i = 0; i < this.board.length; i++) {
            Tile tile = this.board[i];
            isCoveredArray[i] = tile.isCovered() ? 1 : 0;
            isFlaggedArray[i] = tile.isFlagged() ? 1 : 0;
            tileValueArray[i] = tile.getTileValue().getCode();
        }

        savedState.put(JSONKey.KEY_EXISTS_SAVED_DATA, true);
        savedState.put(JSONKey.KEY_COVERED_TILES, this.coveredTiles);
        savedState.put(JSONKey.KEY_ARRAY_IS_COVERED, new JSONArray(isCoveredArray));
        savedState.put(JSONKey.KEY_ARRAY_IS_FLAGGED, new JSONArray(isFlaggedArray));
        savedState.put(JSONKey.KEY_ARRAY_TILE_VALUE, new JSONArray(tileValueArray));

        return savedState;
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