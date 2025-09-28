package com.anhtu.turbo88.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.anhtu.turbo88.R;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer player;
    private float volume = 0.5f;
    private boolean muted = false;

    private MusicManager() {}

    public static synchronized MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void init(Context ctx) {
        if (player == null) {
            player = MediaPlayer.create(ctx, R.raw.menu_music); // file mp3 đặt trong res/raw/
            player.setLooping(true);
        }
    }

    public void play() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void setVolume(float vol) {
        this.volume = vol;
        if (player != null) {
            float v = muted ? 0f : volume;
            player.setVolume(v, v);
        }
    }

    public void setMuted(boolean mute) {
        this.muted = mute;
        setVolume(volume);
    }

    public boolean isMuted() {
        return muted;
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
