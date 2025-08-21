package io.naieang.whiskyway.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.naieang.whiskyway.WhiskyWayGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // The main method now calls a single, clean method to create and run the application.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // Create the configuration object. This is the only place we will set window properties.
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        // --- CONFIGURE YOUR WINDOW HERE ---
        configuration.setTitle("Whisky Way");

        // By default, VSync is enabled, which is good practice. It caps your FPS at your monitor's refresh rate.
        configuration.useVsync(true);

        // --- CHOOSE YOUR DISPLAY MODE ---
        // Pick ONE of the two options below.

        // Option A: To run in Fullscreen
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        // Option B: To run in a specific window size (like 1280x720)
        // To use this, comment out the Fullscreen line above and uncomment the line below.
        // configuration.setWindowedMode(1280, 720);


        // You can add your window icon here if you have the files.
        // Make sure "wwlogo.png" exists in your assets folder if you uncomment this.
        // configuration.setWindowIcon("wwlogo.png");
        configuration.setWindowIcon("wwlogo.png", "wwlogo.png", "wwlogo.png", "wwlogo.png");

        // --- LAUNCH THE GAME ---
        // Create a new application, passing in your main game class and the final configuration.
        return new Lwjgl3Application(new WhiskyWayGame(), configuration);
    }
}
