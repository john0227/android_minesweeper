package com.android.myproj.minesweeper.logic;

public enum Level {

    EASY(10, 10, 10, 75, 1),
    INTERMEDIATE(12, 14, 32, 50, 2),
    EXPERT(14, 22, 60, 20, 3);

    private final int col;
    private final int row;
    private final int mines;
    private final long animationDelay;
    private final int code;

    Level(final int col, final int row, final int mines, final int animation_delay, final int code) {
        this.col = col;
        this.row = row;
        this.mines = mines;
        this.animationDelay = animation_delay;
        this.code = code;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public int getMines() {
        return this.mines;
    }

    public long getAnimationDelay() {
        return this.animationDelay;
    }

    public int getCode() {
        return this.code;
    }

    public static Level getLevelFromCode(int code) {
        return switch (code) {
            case 1 -> Level.EASY;
            case 2 -> Level.INTERMEDIATE;
            case 3 -> Level.EXPERT;
            default -> throw new RuntimeException("Invalid code to get Level: " + code);
        };
    }

}
