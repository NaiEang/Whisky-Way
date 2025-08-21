package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

// A static class to manage all game audio. Can be accessed from anywhere.
public class AudioManager {

    // --- Sound Effects ---
    // Loaded into RAM for instant playback.
    public static Sound eatSound;
    public static Sound pickupSound;
    public static Sound completeSound;
    public static Sound clickSound;
    // You can add more sounds here

    // --- Background Music ---
    // Streamed from disk to save memory.
    public static Music gameMusic;
    public static Music windMusic;
    // You can add more music tracks here

    // This method should be called ONCE when the game starts.
    public static void load() {
        // Load sound effects
        eatSound = Gdx.audio.newSound(Gdx.files.internal("sound/eat2.wav"));
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("sound/pickup.wav"));
        completeSound = Gdx.audio.newSound(Gdx.files.internal("sound/complete.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sound/click.wav"));

        // Load music files
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music.wav"));
        windMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/wind.wav"));

        // --- Configure Music Properties ---
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f); // Volume is a float from 0.0 (silent) to 1.0 (full)

        windMusic.setLooping(true);
        windMusic.setVolume(0.7f);
    }

    public static void playSound(Sound sound) {
        // Play a sound effect at full volume.
        if (sound != null) {
            sound.play(1.0f);
        }
    }

    public static void playMusic(Music music) {
        // Stop any currently playing music and start the new one.
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

    // This method should be called ONCE when the game closes to prevent memory leaks.
    public static void dispose() {
        // Dispose of all loaded assets
        eatSound.dispose();
        pickupSound.dispose();
        completeSound.dispose();

        gameMusic.dispose();
        windMusic.dispose();
    }
}
