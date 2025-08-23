package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    public static Sound eatSound;
    public static Sound pickupSound;
    public static Sound completeSound;
    public static Sound clickSound;

    public static Music gameMusic;
    public static Music windMusic;

    public static void load() {

        pickupSound = Gdx.audio.newSound(Gdx.files.internal("sound/pickup.wav"));
        completeSound = Gdx.audio.newSound(Gdx.files.internal("sound/complete.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sound/click.wav"));

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music.wav"));
        windMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/wind.wav"));

        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);

        windMusic.setLooping(true);
        windMusic.setVolume(0.7f);
    }

    public static void playSound(Sound sound) {
        if (sound != null) {
            sound.play(1.0f);
        }
    }

    public static void playMusic(Music music) {
        if (gameMusic.isPlaying()) gameMusic.stop();
        if (windMusic.isPlaying()) windMusic.stop();

        if (music != null) {
            music.play();
        }
    }

    public static void stopAllMusic() {
        if (gameMusic.isPlaying()) gameMusic.stop();
        if (windMusic.isPlaying()) windMusic.stop();
    }

    public static void dispose() {
        eatSound.dispose();
        pickupSound.dispose();
        completeSound.dispose();

        gameMusic.dispose();
        windMusic.dispose();
    }
}
