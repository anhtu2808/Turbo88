package com.anhtu.turbo88.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.anhtu.turbo88.R;

public class SoundManager {
    private static SoundManager instance;
    private final SharedPreferences prefs;

    private MediaPlayer bgmPlayer;
    private SoundPool soundPool;
    private int soundClickId;

    private boolean isMuted;
    private float musicVolume;
    private float sfxVolume;

    private SoundManager(Context context) {
        prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // init volume từ prefs
        loadSettings();

        // Init SoundPool cho SFX
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attrs)
                .build();

        // Ví dụ load 1 hiệu ứng click
//        soundClickId = soundPool.load(context, R.raw.click, 1);

        // Init MediaPlayer nhạc nền
        bgmPlayer = MediaPlayer.create(context, R.raw.menu_music);
        bgmPlayer.setLooping(true);
        applyVolumes();
    }

    public static SoundManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new SoundManager(ctx.getApplicationContext());
        }
        return instance;
    }

    // === Nhạc nền ===
    public void playBgm() {
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public void stopBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    // === Hiệu ứng ===
    public void playClick() {
        if (soundPool != null) {
            soundPool.play(soundClickId, sfxVolume, sfxVolume, 1, 0, 1f);
        }
    }

    // === Cập nhật cài đặt âm lượng ===
    public void loadSettings() {
        int musicLevel = prefs.getInt("music_level", 50);
        int sfxLevel = prefs.getInt("sfx_level", 50);
        isMuted = prefs.getBoolean("mute", false);

        musicVolume = isMuted ? 0f : musicLevel / 100f;
        sfxVolume = isMuted ? 0f : sfxLevel / 100f;

        applyVolumes();
    }

    private void applyVolumes() {
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(musicVolume, musicVolume);
        }
        // soundPool volume set trực tiếp khi play
    }

    public void release() {
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
