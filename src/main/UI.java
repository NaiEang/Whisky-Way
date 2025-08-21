package src.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.FontFormatException;
import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font jersey_15Font, MaruMonicaB;
    BufferedImage heartImage, coinImage, menuImage, arrowImage, dialogueImage, submenuImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public boolean enterPressed = false; // to handle enter key press
    public boolean startTimer = false; // timer is off at game start
    public boolean timerStarted = false;
    public int secondsPassed = 0;
    private int frameCounter = 0;
    public boolean showTimer = false; // timer display off at start



    int subState = 0;
    int switchwin = 0;
    int commandNum = 0;
    public int confirmCommandNum = 0;

    double playTime = 0;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
        
        try {
            File fontFile = new File("res/font/x12y16pxMaruMonica.ttf");
            InputStream is = new FileInputStream(fontFile);
            MaruMonicaB = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 28f);
        } catch (Exception e) {
            e.printStackTrace();
            MaruMonicaB = new Font("SansSerif", Font.PLAIN, 28); // fallback
        }
        try {
            File fontFile = new File("res/font/Jersey15-Regular.ttf");
            InputStream is = new FileInputStream(fontFile);
            jersey_15Font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 28f);
        } catch (Exception e) {
            e.printStackTrace();
            jersey_15Font = new Font("SansSerif", Font.PLAIN, 28); // fallback
        }
      



        // arial_40 = new Font("Arial", Font.PLAIN, 20);
        // arial_80B = new Font("Arial", Font.BOLD, 80);
        try{
        heartImage =
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/heart.png"));
        coinImage =
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/coin.png"));
        menuImage = 
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/menu.png"));
        submenuImage = 
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/submenu.png"));
        arrowImage = 
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/arrow.png"));
        dialogueImage = 
        ImageIO.read(getClass().getResourceAsStream("/res/background&button/dialogue.png"));
        }catch(Exception e){
        e.printStackTrace();
        }
    }

    public void showMessage(String text) {

        message = text;
        messageOn = true;

    }
    public void updateTimer() {
        // Start timer only after message disappears
        if (!messageOn && !timerStarted) {
            timerStarted = true; // now start counting
            playTime = 0;        // reset to 0
            secondsPassed = 0;   // reset to 0
        }

        if (timerStarted) {
            playTime += 1.0 / 60.0;        // increment by 1/60 sec per frame
            secondsPassed = (int) playTime;
        }
    }



    public void draw(Graphics2D g2) {

        if (gp.gameState == gp.playState) {
            // other HUD drawing code...

            // Draw Timer in middle of screen
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(30F));
            String timeText = "Time: " + gp.secondsPassed + "s";
            int textWidth = g2.getFontMetrics().stringWidth(timeText);
            int centerX = (gp.screenWidth / 2) - (textWidth / 2);
            int topY = 50;
            g2.drawString(timeText, centerX, topY);
        }


        this.g2 = g2;
        g2.setFont(MaruMonicaB.deriveFont(Font.PLAIN, 28F));
        g2.setColor(Color.black);

        if (gameFinished == true) {

            String text;
            int textLength;
            int x;
            int y;

            text = "You delivered all the boxes!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2;
            g2.drawString(text, x, y);

            text = "Your Time is: " + dFormat.format(playTime) + " seconds";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y += gp.tileSize;
            g2.drawString(text, x, y);

            g2.setFont(MaruMonicaB);
            g2.setColor(Color.black);
            text = "Congratualtions!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 - (gp.tileSize * 2);
            g2.drawString(text, x, y);

            gp.gameThread = null;

        } else {
            g2.setFont(MaruMonicaB);
            g2.setColor(Color.white);
            g2.drawImage(heartImage, 8, 7, 30, 30, gp);
            g2.drawString("Energy " + gp.player.ShrimpCount, 40, 30);
            g2.drawImage(coinImage, 8, 38, 30, 30, gp);
            g2.drawString(gp.player.coinCount + " $", 40, 60);

            // Time
            if (startTimer) {
                // playTime += (double) 1 / 60;
                // secondsPassed = (int) playTime;
                g2.drawString("Time: " + dFormat.format(playTime), gp.tileSize * 27, 30);
            }


            // Message
            if (messageOn == true) {
                g2.setFont(MaruMonicaB.deriveFont(30F));
                g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);

                messageCounter++;
                if (messageCounter > 80) { // message will disappear after 2 seconds
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }

        if (gp.gameState == gp.playState) {

        }
        if (gp.gameState == gp.pauseState) {
            drawOptionScreen();
        }
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen(g2, "Welcome to Whisky Way!\nFind box and deliver it to the witch cat.");
        }
    }

    public void drawOptionScreen() {

        g2.setColor(Color.black);
        g2.setFont(MaruMonicaB.deriveFont(24F));

        int frameWidth = gp.tileSize * 7;
        int frameHeight = gp.tileSize * 9;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = (gp.screenHeight - frameHeight) / 2;
        g2.drawImage(menuImage, frameX, frameY, frameWidth, frameHeight, null);
        g2.drawImage(submenuImage, frameX, frameY, frameWidth, frameHeight, null);
        
        // drawoptionbWindow(g2, frameX, frameY, frameWidth, frameHeight);
        if (switchwin == 0) {
            g2.drawImage(menuImage, frameX, frameY, frameWidth, frameHeight, null);
        } else {
            g2.drawImage(submenuImage, frameX, frameY, frameWidth, frameHeight, null);
        }

        switch (subState) {
            case 0:
                options_top(frameX, frameY, frameWidth);
                break;
            case 1:
                options_fullScreenNotification(frameX, frameY);
                break;
            case 2:
                options_control(frameX, frameY);
                break;
            case 3:
                option_endGameConfirmation(frameX, frameY);
                break;

        }
        gp.keyH.enterPressed = false; // reset after handling

    }

    public void options_top(int frameX, int frameY, int frameWidth) {

        int textX;
        int textY;
        int textOffsetX = 40; 

        // ===== Draw "Options" with Jersey15 font =====
        String text = "Options";
        g2.setFont(jersey_15Font.deriveFont(36f)); // Adjust size as needed
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize * 3 - 90;
        g2.drawString(text, textX, textY);

        // ===== Use MaruMonicaB font for the rest =====
        g2.setFont(MaruMonicaB);

        textX = frameX + textOffsetX;
        textY += gp.tileSize * 2 - (gp.tileSize / 2);

        // Fullscreen
        g2.drawString("Fullscreen", textX, textY);
        if (commandNum == 0) {
            g2.drawImage(arrowImage, textX - 30, textY - 12, 24, 24, null);
            if (gp.keyH.enterPressed) {
                gp.fullscreenOn = !gp.fullscreenOn;
                subState = 1;
                // switchwin = 1;
                // commandNum = 0;
            }
        }

        // Music
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if (commandNum == 1) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
        }

        // SE
        textY += gp.tileSize;
        g2.drawString("SE", textX, textY);
        if (commandNum == 2) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
        }

        // Control
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if (commandNum == 3) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
            if (gp.keyH.enterPressed) {
                subState = 2;
                commandNum = 0;
                switchwin = 0;
            }
        }

        // Back
        textY += gp.tileSize;
        g2.drawString("Back", textX, textY);
        if (commandNum == 4) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
        }

        // End Game
        textY += gp.tileSize + 20;
        g2.drawString("End Game", textX, textY);
        if (commandNum == 5) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
            if (gp.keyH.enterPressed) {
                subState = 3;
                confirmCommandNum = 0;
                switchwin = 1;
            }
        }

        // Fullscreen checkbox
        textY = frameX - 300;
        textX = frameX + gp.tileSize * 3 + 20;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX, textY, 24, 24);
        if (gp.fullscreenOn) {
            g2.fillRect(textX, textY, 24, 24);
        }

        // Music volume bar
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 153, 24);
        int volumeWidth = 22 * gp.music.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SE volume bar
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 153, 24);
        volumeWidth = 22 * gp.se.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);
    }



    public void options_fullScreenNotification(int frameX, int frameY) {
        int textX = frameX + gp.tileSize * 6 ;
        int textY = frameY + gp.tileSize * 5;

        currentDialogue = "The change will take \neffect after \nrestarting the game.";
        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40; // line height
        }
        // back
        textY = frameX + gp.tileSize * 5; // space before back option
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
           g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
                subState = 0; // go back to options menu
            }
        }

    public void options_control(int frameX, int frameY) {
        int textX;
        int textY;

        String text = "Control";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize * 3 - (gp.tileSize * 2 - 8);
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize - 10 ;
        textY += gp.tileSize * 2 - (gp.tileSize / 2 + 2);
        g2.drawString("Move UP", textX, textY);
        textY += gp.tileSize;
        g2.drawString("Move DOWN", textX, textY);
        textY += gp.tileSize;
        g2.drawString("Move LEFT", textX, textY);
        textY += gp.tileSize;
        g2.drawString("Move RIGHT", textX, textY);
        textY += gp.tileSize;
        g2.drawString("Pause", textX, textY);
        textY += gp.tileSize;

        // back
        textY = frameX + gp.tileSize - 80; // space before back option
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawImage(arrowImage, textX - 25, textY - 12, 24, 24, null);
            if (gp.keyH.enterPressed == true) {
                subState = 0; // go back to options menu
                commandNum = 4; // reset command number for options menu
                // commandNum = 5; // reset command number for end game confirmation
            }
        }

        textX = frameX + gp.tileSize * 4 ; // position for control keys
        textY = gp.tileSize * 5 + 25;
        g2.drawString("W", textX, textY);
        textY += gp.tileSize;
        g2.drawString("S", textX, textY);
        textY += gp.tileSize;
        g2.drawString("A", textX, textY);
        textY += gp.tileSize;
        g2.drawString("D", textX, textY);
        textY += gp.tileSize;
        g2.drawString("SPACE", textX, textY);
        textY += gp.tileSize;

    }

    public void option_endGameConfirmation(int frameX, int frameY) {
        int textX = frameX + gp.tileSize * 2 - 20;
        int textY = frameY + gp.tileSize * 2;

        currentDialogue = "Are you sure you want to \n     end the game?";
        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40; // line height
        }

        // "Yes" Option
        String textYes = "Yes";
        int yesX = getXforCenteredText(textYes);
        int yesY = frameY + gp.tileSize * 5 ;
        g2.drawString(textYes, yesX, yesY);
        if (confirmCommandNum == 0) {
            g2.drawImage(arrowImage, yesX - 25, yesY - 12, 24, 24, null);
            if (gp.keyH.enterPressed == true) {
                gp.stopMusic();
                System.exit(0);
            }
        }

        // "No" Option
        String textNo = "No";
        int noX = getXforCenteredText(textNo);
        int noY = frameY + gp.tileSize * 6;
        g2.drawString(textNo, noX, noY);
        if (confirmCommandNum == 1) {
            g2.drawImage(arrowImage, noX - 25, noY - 12, 24, 24, null);
            if (gp.keyH.enterPressed == true) {
                subState = 0; // go back to options menu
                commandNum = 5;
            }
        }
        // System.out.println("confirmCommandNum = " + confirmCommandNum);
    }

public void drawDialogueScreen(Graphics2D g2, String text) {
        int boxX = gp.tileSize - 10;
        int boxY = gp.tileSize * 9;
        int boxW = gp.screenWidth - gp.tileSize * 6;
        int boxH = gp.tileSize * 15;

        // Center image inside the box or stretch to fit
        if (dialogueImage != null) {
            g2.drawImage(dialogueImage, boxX, boxY, boxW, boxH, null);
        } else {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 35, 35);
        }

        g2.setFont(MaruMonicaB.deriveFont(Font.PLAIN, 28f));
        g2.setColor(Color.black);

        int textX = boxX + 120;
        int textY = boxY + 100;
        for (String line : text.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }
    }

    // public void drawDialogueScreen(Graphics2D g2, String text) {
    //     int x = 50;
    //     int y = 100;


    //     x += 20;
    //     y += 40;

    //     for (String line : text.split("\n")) {
    //         g2.drawString(line, x, y);
    //         y += 40;
    //     }

    //     if (dialogueImage != null) {
    //         g2.drawImage(dialogueImage, x, y + 10, 24, 24, null);
    //         System.out.println("dialogueImage width: " + dialogueImage.getWidth());
    //         System.out.println("dialogueImage height: " + dialogueImage.getHeight());
    //     } else {
    //         System.out.println("dialogueImage is null!");
    //     }
    // }



    // public void drawSubWindow(int x, int y, int width, int height) {

    //     Color c = new Color(0, 0, 0, 210);
    //     g2.setColor(c);
    //     g2.fillRoundRect(x, y, width, height, 35, 35);

    //     c = new Color(255, 255, 255);
    //     g2.setColor(c);
    //     g2.setStroke(new BasicStroke(5));
    //     g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    // }

    public void drawoptionbWindow(Graphics2D g2, int panelWidth, int panelHeight, int width, int height) {
        // Center horizontally, but shift vertically downward
        int x = gp.screenWidth / 2 - width / 2;
        int y = gp.screenHeight / 2 - height / 2;

        Color bgColor = new Color(0, 0, 0, 200); // semi-transparent black
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public int getXforCenteredText(String text) {
        int x = gp.screenWidth / 2 - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
        return x;
    }

}
