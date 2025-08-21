package io.naieang.whiskyway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class WhiskyWayGame extends Game {

    // We keep track of the current screens
    private Screen startMenuScreen;
    private Screen gameScreen;

    @Override
    public void create() {
        AudioManager.load();

        // When the game starts, create and show the main menu screen.
        startMenuScreen = new StartMenuScreen(this); // Pass a reference of this game to the screen
        setScreen(startMenuScreen);
    }

    // This method will be called by the start menu button
    public void startGame() {
        // Dispose of the old menu screen to free up memory
        if (startMenuScreen != null) {
            startMenuScreen.dispose();
        }

        // Create and set the new game screen
        gameScreen = new GameScreen(); // GameScreen no longer needs a reference to this
        setScreen(gameScreen);
    }
    @Override
        public void dispose(){
            super.dispose();

            AudioManager.dispose();
    }
}
