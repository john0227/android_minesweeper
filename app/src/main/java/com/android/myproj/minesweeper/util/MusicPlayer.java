package com.android.myproj.minesweeper.util;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;

    public void playMusic(Context context, int resid, boolean playMusic) {
        if (!playMusic) {
            return;
        }

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, resid);
        mediaPlayer.start();
    }

    public void destroyPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

}
