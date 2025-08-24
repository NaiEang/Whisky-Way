package io.naieang.whiskyway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import jdk.javadoc.internal.tool.Start;

public class WhiskyWayGame extends Game {

    private Screen startMenuScreen;
    private Screen gameScreen;
    private Screen winScreen;

    @Override
    public void create() {
        AssetManager.load();
        showStartMenu();
    }
    public void showStartMenu(){

        startMenuScreen = new StartMenuScreen(this);
        setScreen(startMenuScreen);
    }

    public void startGame() {

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }
    public void showWinScreen(){

        setScreen(new WinScreen(this));
    }
    public void restartGame(){
        showStartMenu();
    }

    @Override
        public void dispose(){
            super.dispose();

            AssetManager.dispose();
    }
}
