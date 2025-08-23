package io.naieang.whiskyway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class WhiskyWayGame extends Game {

    private Screen startMenuScreen;
    private Screen gameScreen;

    @Override
    public void create() {
        AudioManager.load();

        startMenuScreen = new StartMenuScreen(this);
        setScreen(startMenuScreen);
    }

    public void startGame() {

        if (startMenuScreen != null) {
            startMenuScreen.dispose();
        }

        gameScreen = new GameScreen();
        setScreen(gameScreen);
    }
    @Override
        public void dispose(){
            super.dispose();

            AudioManager.dispose();
    }
}
