package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameUI {
    // --- Original Fields ---
    public int gameState;
    public final int PLAY_STATE;
    public final int PAUSE_STATE;
    public final int DIALOGUE_STATE;
    Stage stage;
    private Table rootTable;
    private Label inventoryLabel;
    private Label timerLabel;
    private Label dialogueLabel;
    private Table dialogueTable;
    private Player player;
    public float playTimer;
    private Table pauseTable;
    private Label pauseLabel;
    BitmapFont font = new BitmapFont(); // default white font

    // --- Added Fields for Options Menu ---
    public int subState = 0;         // 0=options,1=fullscreen,2=control,3=endgame
    public int commandNum = 0;
    public int confirmCommandNum = 0;
    public Texture menuImage, submenuImage, arrowImage;
    public BitmapFont maruMonicaFont, jerseyFont;
    public int tileSize = 32;
    public int screenWidth = Gdx.graphics.getWidth();
    public int screenHeight = Gdx.graphics.getHeight();

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

        inventoryLabel = new Label("Boxes: 0 | Coins: 0", style);
        timerLabel = new Label("Time: 0", style);
        dialogueLabel = new Label("", dialogueStyle);
        dialogueLabel.setWrap(true);

        dialogueTable = new Table();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 1, 0.8f));
        pixmap.fill();
        dialogueTable.setBackground(new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();
        dialogueTable.add(dialogueLabel).expand().fill().pad(20);
        dialogueTable.setSize(Gdx.graphics.getWidth() * 0.8f, 150);
        dialogueTable.setPosition(Gdx.graphics.getWidth() * 0.1f, 20);
        dialogueTable.setVisible(false);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left().padLeft(20).padTop(20);
        rootTable.add(inventoryLabel).pad(10);
        rootTable.row();
        rootTable.add(timerLabel).padRight(140);

        stage.addActor(rootTable);
        stage.addActor(dialogueTable);

        // --- Load textures/fonts ---
        menuImage = new Texture("background&button/menu.png");
        submenuImage = new Texture("background&button/submenu.png");
        arrowImage = new Texture("background&button/arrow.png");
        maruMonicaFont = loadFontSafe("font/x12y16pxMaruMonica.ttf", 8, Color.WHITE);
        jerseyFont = loadFontSafe("font/Jersey15-Regular.ttf", 8, Color.WHITE);

        // Make text bigger without reloading the font
        maruMonicaFont.getData().setScale(0.5f);
        jerseyFont.getData().setScale(0.5f);


   

        // Make text bigger without reloading the font
        maruMonicaFont.getData().setScale(1.5f);
        jerseyFont.getData().setScale(1.8f);
    }
      private BitmapFont loadFontSafe(String path, int size, Color color) {
        try {
            if (Gdx.files.internal(path).exists()) {
                FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(path));
                FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
                p.size = size;
                p.color = color;
                BitmapFont f = gen.generateFont(p);
                gen.dispose();
                return f;
            } else {
                Gdx.app.error("GameUI", "Font missing: " + path + " — falling back to default BitmapFont.");
                BitmapFont fallback = new BitmapFont(); // default font
                fallback.setColor(color);
                return fallback;
            }
        } catch (Exception e) {
            Gdx.app.error("GameUI", "Failed to load font " + path + ": " + e.getMessage());
            BitmapFont fallback = new BitmapFont();
            fallback.setColor(color);
            return fallback;
        }
    }




    public void update(float delta) {
        inventoryLabel.setText("Boxes: " + player.boxCount + " | Coins: " + player.coinCount);
        timerLabel.setText(String.format("Time: %.0f", playTimer));
        stage.act(delta);
    }

    public void draw() {
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
        menuImage.dispose();
        submenuImage.dispose();
        arrowImage.dispose();
        maruMonicaFont.dispose();
        jerseyFont.dispose();
    }

    // ================== Menu Drawing ==================
    public void drawOptionScreen(SpriteBatch batch) {
    int frameWidth  = 100;
    int frameHeight = 150;

    // center on the screen
    int frameX = (Gdx.graphics.getWidth()  - frameWidth)  / 5;
    int frameY = (Gdx.graphics.getHeight() - frameHeight) / 3;

        // Draw background
        if (subState == 1) {
            batch.draw(submenuImage, frameX, frameY, frameWidth, frameHeight);
        } else {
            batch.draw(menuImage, frameX, frameY, frameWidth, frameHeight);
        }

        // Draw menu content
        switch (subState) {
            case 0: drawOptionsTop(batch, frameX, frameY, frameWidth, frameHeight); break;
            case 1: drawFullScreenNotification(batch, frameX, frameY); break;
            case 2: drawControlScreen(batch, frameX, frameY); break;
            case 3: drawEndGameConfirmation(batch, frameX, frameY); break;
        }
    }

    private void drawOptionsTop(SpriteBatch batch, int frameX, int frameY, int frameWidth, int frameHeight) {
        String title = "Options";
        GlyphLayout titleLayout = new GlyphLayout(jerseyFont, title);
        float titleX = frameX + (frameWidth - titleLayout.width) / 2f;
        float titleY = frameY + frameHeight - tileSize * 3.5f;
        jerseyFont.draw(batch, title, titleX, titleY);

        String[] options = { "Music", "SE", "Control", "Back", "End Game" };
        float startY = titleY - tileSize * 6f;
        float spacing = tileSize * 4f;
        float textX = frameX + tileSize * 5;

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            GlyphLayout layout = new GlyphLayout(maruMonicaFont, option);
            float textY = startY - (i * spacing);
            maruMonicaFont.draw(batch, option, textX, textY);

            if (commandNum == i) {
                float arrowX = textX - tileSize - 40;
                float arrowY = textY - layout.height - 15;
                batch.draw(arrowImage, arrowX, arrowY, 35, 35);
            }
        }
    }

    private void drawFullScreenNotification(SpriteBatch batch, int frameX, int frameY) {
        maruMonicaFont.draw(batch, "The change will take\neffect after restarting the game.", frameX + 50, frameY + tileSize * 5);
        maruMonicaFont.draw(batch, "Back", frameX + 50, frameY + tileSize * 3);
        if (commandNum == 0) {
            batch.draw(arrowImage, frameX + 20, frameY + tileSize * 3 - 20, 24, 24);
        }
    }

    private void drawControlScreen(SpriteBatch batch, int frameX, int frameY) {
        maruMonicaFont.draw(batch, "Controls", frameX + tileSize, frameY + tileSize * 7);
        String[] controls = {"Move UP        W", "Move DOWN        S", "Move LEFT        A", "Move RIGHT        D", "SPACE        Pause"};
        float textY = frameY + tileSize * 5;
        for (String control : controls) {
            maruMonicaFont.draw(batch, control, frameX + tileSize, textY);
            textY -= tileSize;
        }
        maruMonicaFont.draw(batch, "Back", frameX + tileSize, textY);
        if (commandNum == 0) {
            batch.draw(arrowImage, frameX + tileSize - 30, textY - 20, 24, 24);
        }
    }

    private void drawEndGameConfirmation(SpriteBatch batch, int frameX, int frameY) {
        maruMonicaFont.draw(batch, "Are you sure you want to\nend the game?", frameX + 40, frameY + tileSize * 7);
        String[] confirm = {"Yes", "No"};
        float textY = frameY + tileSize * 5;
        for (int i = 0; i < confirm.length; i++) {
            maruMonicaFont.draw(batch, confirm[i], frameX + tileSize * 2, textY);
            if (confirmCommandNum == i) {
                batch.draw(arrowImage, frameX + tileSize * 2 - 30, textY - 20, 24, 24);
            }
            textY -= tileSize;
        }
    }
}
