package com.android.myproj.minesweeper.game.logic;

public enum TileValue {

    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4),
    FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
    MINE(9), MINE_EXPLODE(10), COVERED(11), FLAGGED(12);

    private final int code;
    TileValue(final int code) {
        this.code = code;
    }

    static TileValue getTile(int val) {
         return switch (val) {
             case 0 -> ZERO;
             case 1 -> ONE;
             case 2 -> TWO;
             case 3 -> THREE;
             case 4 -> FOUR;
             case 5 -> FIVE;
             case 6 -> SIX;
             case 7 -> SEVEN;
             case 8 -> EIGHT;
             case 9 -> MINE;
             case 10 -> MINE_EXPLODE;
             case 11 -> COVERED;
             case 12 -> FLAGGED;
             default -> throw new RuntimeException("Invalid int value to get tile: " + val);
        };
    }

    int getCode() {
        return this.code;
    }

    boolean isNonZeroNumberTile() {
        return this != ZERO && this != MINE && this != MINE_EXPLODE && this != COVERED && this != FLAGGED;
    }

}
