package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;

import java.text.DecimalFormat;

public class GameUI {

    // --- Rendering ---
    private final SpriteBatch batch;
    private final int screenWidth, screenHeight, tileSize;
    private float keyTimer = 0f;        // optional: delay to prevent super fast input
    private float keyDelay = 0.15f;

    // --- Game refs/state ---
    private final Player player;
    public final int playState, pauseState, dialogueState;
    public int gameState;

    // --- Fonts ---
    private BitmapFont jerseyFont, maruMonicaFont;

    // --- Textures ---
    private Texture heartImage, coinImage, menuImage, arrowImage, dialogueImage, submenuImage;

    // --- UI runtime state ---
    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public boolean enterPressed = false;
    public boolean startTimer = false;
    public boolean timerStarted = false;
    public int secondsPassed = 0;

    private double playTime = 0;
    private final DecimalFormat dFormat = new DecimalFormat("#0.00");
    private Rectangle[] optionBounds;   // for main options: Music, SE, Control, Back, End Game
    private Rectangle[] confirmBounds;  // for End Game confirmation: Yes, No


    // Menu states
    public int subState = 0;
    public int switchwin = 0;
    public int commandNum = 0;
    public int confirmCommandNum = 0;

    // // --- Asset paths (NO "assets/" prefix!) ---
    // private static final String FONT_MARU = "font/x12y16pxMaruMonica.ttf";
    // private static final String FONT_JERSEY = "font/Jersey15-Regular.ttf";
    // private static final String IMG_HEART = "background&button/heart.png";
    // private static final String IMG_COIN = "background&button/coin.png";
    // private static final String IMG_MENU = "background&button/menu.png";
    // private static final String IMG_SUBMENU = "background&button/submenu.png";
    // private static final String IMG_ARROW = "background&button/arrow.png";
    // private static final String IMG_DIALOGUE = "background&button/dialogue.png";

    

    public GameUI(Player player,
                  int screenWidth, int screenHeight, int tileSize,
                  int playState, int pauseState, int dialogueState, int gameState) {

        this.batch = new SpriteBatch();

        this.player = player;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tileSize = tileSize;
        this.playState = playState;
        this.pauseState = pauseState;
        this.dialogueState = dialogueState;
        this.gameState = gameState;

        maruMonicaFont = loadFontSafe("font/x12y16pxMaruMonica.ttf", 28, Color.WHITE);
        jerseyFont = loadFontSafe("font/Jersey15-Regular.ttf", 36, Color.WHITE);

        // Make text bigger without reloading the font
        maruMonicaFont.getData().setScale(1.5f);
        jerseyFont.getData().setScale(1.8f);

        // Load textures
        heartImage = loadTextureSafe("background&button/heart.png");
        coinImage = loadTextureSafe("background&button/coin.png");
        menuImage = loadTextureSafe("background&button/menu.png");
        submenuImage = loadTextureSafe("background&button/submenu.png");
        arrowImage = loadTextureSafe("background&button/arrow.png");
        dialogueImage = loadTextureSafe("background&button/dialogue.png");
    }

    /** Safe font loader: falls back to new BitmapFont() if missing. */
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

    /** Safe texture loader: returns a 1x1 placeholder if missing. */
    private Texture loadTextureSafe(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new Texture(Gdx.files.internal(path));
            } else {
                Gdx.app.error("GameUI", "Texture missing: " + path + " — using 1x1 placeholder.");
                return makePlaceholderTexture();
            }
        } catch (Exception e) {
            Gdx.app.error("GameUI", "Failed to load texture " + path + ": " + e.getMessage());
            return makePlaceholderTexture();
        }
    }

    private Texture makePlaceholderTexture() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    /** Call from GameScreen.render() */
  public void updateTimer() {
    if (!timerStarted) {
        timerStarted = true;
        playTime = 0;
        secondsPassed = 0;
    }

    if (timerStarted && gameState == playState) {
        playTime += Gdx.graphics.getDeltaTime();
        secondsPassed = (int) playTime;
    }
}


    /** Draw the whole UI in screen space (not affected by world camera). */
    public void draw() {
        batch.begin();

        if (gameState == playState) {
            drawPlayState();
        } else if (gameState == pauseState) {
            drawOptionScreen();
        } else if (gameState == dialogueState) {
            drawDialogueScreen("Welcome to Whisky Way!\nFind box and deliver it to the witch cat.");
        }

        batch.end();
    }

    // --- Sections ---

    private void drawPlayState() {
        maruMonicaFont.setColor(Color.WHITE);

        // Timer top-center
        GlyphLayout layout = new GlyphLayout(maruMonicaFont, "Time: " + secondsPassed + "s");
        float centerX = (screenWidth - layout.width) / 2f;
        maruMonicaFont.draw(batch, layout, centerX, screenHeight - 50);

        if (gameFinished) {
            String msg = "You delivered all the boxes!";
            GlyphLayout finishLayout = new GlyphLayout(maruMonicaFont, msg);
            maruMonicaFont.draw(batch, msg, screenWidth / 2f - finishLayout.width / 2f, screenHeight / 2f);

            msg = "Your Time is: " + dFormat.format(playTime) + " seconds";
            maruMonicaFont.draw(batch, msg, screenWidth / 2f - finishLayout.width / 2f, screenHeight / 2f - tileSize);

            msg = "Congratulations!";
            maruMonicaFont.draw(batch, msg, screenWidth / 2f - finishLayout.width / 2f, screenHeight / 2f + tileSize * 2f);
            return;
        }

        int iconSize = 45;

        // HUD: Energy (heart) + value from your Player fields
        batch.draw(heartImage, 8, screenHeight - iconSize -  10, iconSize, iconSize);
        maruMonicaFont.draw(batch, "Energy " + player.ShrimpCount, 60, screenHeight - 18);

        // HUD: Coins
        batch.draw(coinImage, 8, screenHeight - iconSize * 2 -20, iconSize, iconSize);
        maruMonicaFont.draw(batch, player.coinCount + " $", 60, screenHeight - 73);

        // Running timer readout during play
        if (startTimer) {
            maruMonicaFont.draw(batch, "Time: " + dFormat.format(playTime), tileSize * 27f, screenHeight - 18);
        }

        // Ephemeral message
        if (messageOn) {
            maruMonicaFont.draw(batch, message, tileSize / 2f, tileSize * 5f);
            messageCounter++;
            if (messageCounter > 120) {
                messageOn = false;
                messageCounter = 0;
            }
        }
    }

    public void drawOptionScreen() {
        // Calculate menu frame size dynamically
        int frameWidth = tileSize * 35;
        int frameHeight = tileSize * 45;
        int frameX = (screenWidth - frameWidth) / 2  ;
        int frameY = (screenHeight - frameHeight) / 2;

        // Draw background
        if (switchwin == 0) {
            batch.draw(menuImage, frameX, frameY, frameWidth, frameHeight);
        } else {
            batch.draw(submenuImage, frameX, frameY, frameWidth, frameHeight);
        }

        // Draw content based on subState
        switch (subState) {
            case 0: drawOptionsTop(frameX, frameY, frameWidth, frameHeight); break;
            case 1: drawFullScreenNotification(frameX, frameY); break;
            case 2: drawControlScreen(frameX, frameY); break;
            case 3: drawEndGameConfirmation(frameX, frameY); break;
        }
    }


    private void drawOptionsTop(int frameX, int frameY, int frameWidth, int frameHeight) {

    // String[] options = { "Music", "SE", "Control", "Back", "End Game" };
    // float startY = frameY + frameHeight - tileSize * 3.5f - tileSize * 6f; // start below title
    // float spacing = tileSize * 5.5f;
    // float textX = frameX + tileSize * 4;

    // if (optionBounds == null || optionBounds.length != options.length) {
    //     optionBounds = new Rectangle[options.length];
    // }

    // for (int i = 0; i < options.length; i++) {
    //     String option = options[i];
    //     GlyphLayout layout = new GlyphLayout(maruMonicaFont, option);
    //     float textY = startY - (i * spacing);

    //     // Draw text
    //     maruMonicaFont.draw(batch, option, textX, textY);

    //     // Define clickable rectangle
    //     optionBounds[i] = new Rectangle(textX, textY - layout.height, layout.width, layout.height);
    // }
        // Title styling
        String title = "Options";
        GlyphLayout titleLayout = new GlyphLayout(jerseyFont, title);
        float titleX = frameX + (frameWidth - titleLayout.width) / 2f;
        float titleY = frameY + frameHeight - tileSize * 3.5f;
        jerseyFont.draw(batch, title, titleX, titleY);

        // Options list
        String[] options = { "Music", "SE", "Control", "Back", "End Game"};
        float startY = titleY - tileSize * 6f;  // Start drawing options below the title
        float spacing = tileSize * 4f;          // Vertical spacing between options
        float textX = frameX + tileSize * 5;      // Left padding for text

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            GlyphLayout layout = new GlyphLayout(maruMonicaFont, option);
            float textY = startY - (i * spacing);

            // Draw option text aligned on the left
            maruMonicaFont.draw(batch, option, textX, textY);

            // Draw arrow next to the selected option
            if (commandNum == i) {
                float arrowX = textX - tileSize - 40;
                float arrowY = textY - layout.height - 15;
                batch.draw(arrowImage, arrowX, arrowY, 35, 35);
            }
        }

        //     // Draw placeholders for sliders/checkboxes
        //     // if (option.equals("Fullscreen")) {
        //     //     // Checkbox placeholder
        //     //     // batch.draw(submenuImage, textX + 220, textY - tileSize, tileSize, tileSize);
        //     // } else if (option.equals("Music") || option.equals("SE")) {
        //     //     // Slider placeholder (black + white bar)
        //     //     float barWidth = tileSize * 4f;
        //     //     float barHeight = tileSize / 1.5f;
        //     //     float barX = textX + 220;
        //     //     float barY = textY - barHeight;
        //     //     // Black filled bar
        //     //     Pixmap blackBar = new Pixmap((int) barWidth, (int) barHeight, Pixmap.Format.RGBA8888);
        //     //     blackBar.setColor(Color.WHITE);
        //     //     blackBar.fill(); 
        //     //     batch.draw(new Texture(blackBar), barX, barY);
        //     //     blackBar.dispose();
        //     // }
        // }
    }



    private void drawFullScreenNotification(int frameX, int frameY) {
        maruMonicaFont.draw(batch, "The change will take\neffect after restarting the game.", frameX + 50, frameY + tileSize * 5);
        maruMonicaFont.draw(batch, "Back", frameX + 50, frameY + tileSize * 3);
        if (commandNum == 0) {
            batch.draw(arrowImage, frameX + 20, frameY + tileSize * 3 - 20, 24, 24);
        }
    }

    private void drawControlScreen(int frameX, int frameY) {
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

    private void drawEndGameConfirmation(int frameX, int frameY) {
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

    public void drawDialogueScreen(String text) {
        int boxX = tileSize - 10;
        int boxY = tileSize * 9;
        int boxW = screenWidth - tileSize * 6;
        int boxH = tileSize * 15;

        batch.draw(dialogueImage, boxX, boxY, boxW, boxH);

        maruMonicaFont.setColor(Color.BLACK);
        float textX = boxX + 120;
        float textY = boxY + boxH - 50;

        for (String line : text.split("\n")) {
            maruMonicaFont.draw(batch, line, textX, textY);
            textY -= 40;
        }
    }

    public void dispose() {
        batch.dispose();
        safeDispose(heartImage);
        safeDispose(coinImage);
        safeDispose(menuImage);
        safeDispose(submenuImage);
        safeDispose(arrowImage);
        safeDispose(dialogueImage);
        safeDispose(maruMonicaFont);
        safeDispose(jerseyFont);
    }

    private void safeDispose(Object o) {
        try {
            if (o instanceof Texture) ((Texture) o).dispose();
            if (o instanceof BitmapFont) ((BitmapFont) o).dispose();
        } catch (Exception ignore) {}
    }
}