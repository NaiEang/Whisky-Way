package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameUI {

    // --- State Management (from collaborator's code) ---
    public int gameState;
    public final int PLAY_STATE;
    public final int PAUSE_STATE;
    public final int DIALOGUE_STATE;

    // --- UI Elements ---
    Stage stage;
    private Table rootTable;
    private Label inventoryLabel;
    private Label timerLabel;
    private Label dialogueLabel;
    private Table dialogueTable;
    private Label energyLabel;

    // --- Data ---
    private Player player;
    public float playTimer;

    private Table pauseTable;
    private Label pauseLabel;

    public GameUI(Player player, int playState, int pauseState, int dialogueState, int startState) {
        this.player = player;
        this.PLAY_STATE = playState;
        this.PAUSE_STATE = pauseState;
        this.DIALOGUE_STATE = dialogueState;
        this.gameState = startState;

        stage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle dialogueStyle = new Label.LabelStyle(font, Color.BLACK);
        font.getData().setScale(2);

        // --- Create Labels ---
        inventoryLabel = new Label("Boxes: 0 | Coins: 0", style);
        timerLabel = new Label("Time: 0", style);
        energyLabel = new Label("Energy: 10", style);
        dialogueLabel = new Label("", dialogueStyle);
        dialogueLabel.setWrap(true);



        // --- Build Dialogue Box ---
        dialogueTable = new Table();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 1, 0.8f));
        pixmap.fill();
        dialogueTable.setBackground(new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();
        dialogueTable.add(dialogueLabel).expand().fill().pad(20);
        dialogueTable.setSize(Gdx.graphics.getWidth() * 0.8f, 150);
        dialogueTable.setPosition(Gdx.graphics.getWidth() * 0.1f, 20);
        dialogueTable.setVisible(false); // Start hidden

        // --- Build Main UI Table ---
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left().padLeft(20).padTop(20); // Anchor to top-left
        rootTable.add(inventoryLabel).pad(10);
        rootTable.row();
        rootTable.add(timerLabel).padRight(140);
        rootTable.row();
        rootTable.add(energyLabel).padRight(140);

        stage.addActor(rootTable);
        stage.addActor(dialogueTable);
    }

    public void update(float delta) {
        // Update the label texts with current game data
        inventoryLabel.setText("Boxes: " + player.boxCount + " | Coins: " + player.coinCount);
        timerLabel.setText(String.format("Time: %.0f", playTimer));

        // Update the stage (handles animations, etc., though we have none)
        stage.act(delta);
    }

    public void draw() {
        // The stage handles its own SpriteBatch
        stage.draw();
    }

    public void showDialogue(String text) {
        dialogueLabel.setText(text);
        dialogueTable.setVisible(true);
        gameState = DIALOGUE_STATE;
    }

    public void hideDialogue() {
        dialogueTable.setVisible(false);
        gameState = PLAY_STATE;
    }

    public void dispose() {
        stage.dispose();
    }
}
