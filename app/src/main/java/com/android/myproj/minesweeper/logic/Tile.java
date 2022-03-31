package com.android.myproj.minesweeper.logic;

public class Tile {

    private int index;
    private boolean isCovered;
    private boolean isFlagged;
    private TileValue tileValue;

    protected Tile(int index, TileValue tileValue) {
        this(index, true, false, tileValue);
    }

    protected Tile(int index, boolean isCovered, boolean isFlagged, TileValue tileValue) {
        this.index = index;
        this.isCovered = isCovered;
        this.isFlagged = isFlagged;
        this.tileValue = tileValue;
    }

    protected int getIndex() {
        return this.index;
    }

    // Returns true if and only if this Tile is covered or flagged
    protected boolean isCovered() {
        return this.isCovered;
    }

    protected boolean isFlagged() {
        return this.isFlagged;
    }

    protected TileValue getTileValue() {
        return this.tileValue;
    }

    protected boolean isUncoverable() {
        return !this.isFlagged && this.isCovered && !this.isMine();
    }

    protected void select() {
        if (!this.isFlagged) {
            this.isCovered = false;
        }
    }

    protected void flag() {
        if (!this.isCovered) {
            return;
        }
        this.isFlagged = !this.isFlagged;
    }

    protected void setTileValue(TileValue tileValue) {
        this.tileValue = tileValue;
    }

    protected boolean isMine() {
        return this.tileValue == TileValue.MINE;
    }

    protected static int getIndexFromCoord(int row, int col, int board_col) {
        return row * board_col + col;
    }

    protected static int getRowFromIndex(int index, int board_col) {
        return (int) (index / board_col);
    }

    protected static int getColFromIndex(int index, int board_col) {
        return index % board_col;
    }

}
