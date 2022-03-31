package com.android.myproj.minesweeper.util;

import android.app.Activity;
import android.media.MediaPlayer;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;

    public void playMusic(Activity activity, int resid, boolean playMusic) {
        if (!playMusic) {
            return;
        }

        this.destroyPlayer();
        mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), resid);
        mediaPlayer.start();
    }

    public void destroyPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

}
