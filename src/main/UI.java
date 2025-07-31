package src.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font arial_40, arial_80B;
    BufferedImage heartImage, coinImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public boolean enterPressed = false; // to handle enter key press


    int subState = 0;
    int commandNum = 0;
    public int confirmCommandNum = 0;

    double playTime = 0;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 20);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        try{
            heartImage = ImageIO.read(getClass().getResourceAsStream("/res/background&button/heart.png"));
            coinImage = ImageIO.read(getClass().getResourceAsStream("/res/background&button/coin.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void showMessage(String text){

        message = text;
        messageOn = true;

    }   

    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        if(gameFinished == true){

                String text;
                int textLength;
                int x;
                int y;

                text = "You delivered all the boxes!";
                textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
                x = gp.screenWidth / 2 - textLength / 2;
                y = gp.screenHeight / 2;
                g2.drawString(text, x, y);
                
                text = "Your Time is: " + dFormat.format(playTime) + " seconds";
                textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
                x = gp.screenWidth / 2 - textLength / 2;
                y += gp.tileSize;
                g2.drawString(text, x, y);

                g2.setFont(arial_80B);
                g2.setColor(Color.pink);
                text = "Congratualtions!";
                textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
                x = gp.screenWidth / 2 - textLength / 2;
                y = gp.screenHeight / 2 - (gp.tileSize * 2);
                g2.drawString(text, x, y);

                gp.gameThread = null;

            }else{
                g2.setFont(arial_40);
                g2.setColor(Color.white);
                g2.drawImage(heartImage, 8, 7, 30, 30, gp);
                g2.drawString("Energy "+ gp.player.ShrimpCount, 40, 30);
                g2.drawImage(coinImage, 8, 38, 30, 30, gp);
                g2.drawString(gp.player.coinCount + " $", 40, 60);

                //Time
                playTime += (double)1/60;
                g2.drawString("Time: "+ dFormat.format(playTime), gp.tileSize*27, 30);

                //Message
                if(messageOn == true){
                    g2.setFont(g2.getFont().deriveFont(30F));
                    g2.drawString(message, gp.tileSize/2, gp.tileSize*5);

                    messageCounter++;
                    if(messageCounter > 80){ //message will disappear after 2 seconds
                        messageCounter = 0;
                        messageOn = false;
                    }
                }
            }  

        if(gp.gameState == gp.playState){

        }
        if(gp.gameState == gp.pauseState){
            drawOptionScreen();
        }
        if(gp.gameState == gp.dialogueState){
            drawDialogueScreen(g2, "Welcome to Whisky Way!\nFind box and deliver it to the witch cat.");
        }
    }
    public void drawOptionScreen(){

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(24F));
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;
        drawoptionbWindow(g2, frameX, frameY, frameWidth, frameHeight);

            switch (subState){
            case 0: options_top(frameX, frameY); break;
            case 1: options_fullScreenNotification(frameX, frameY); break;
            case 2: options_control(frameX, frameY); break;
            case 3: option_endGameConfirmation(frameX, frameY); break;
            
            
        }
        gp.keyH.enterPressed = false; // reset after handling
      
    }
    public void options_top(int frameX, int frameY){

        int textX;
        int textY;

        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize * 3 - (gp.tileSize / 2);
        g2.drawString (text, textX, textY);

        textX = frameX + gp.tileSize * 6;
        textY += gp.tileSize * 2 - (gp.tileSize / 2);
        g2.drawString("Fullscreen", textX, textY);
        if(commandNum == 0){
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true){
                if(gp.fullscreenOn == false){
                    gp.fullscreenOn = true;
                }
                else if(gp.fullscreenOn == true){
                    gp.fullscreenOn = false;
                }
                subState = 1; // move to fullscreen notification
            }
            
        }
        //music
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if(commandNum == 1){
            g2.drawString(">", textX - 25, textY);
        }


        //Sound Effects
        textY += gp.tileSize;
        g2.drawString("SE", textX, textY);
        if(commandNum == 2){
            g2.drawString(">", textX - 25, textY);
        }


        //Control
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if(commandNum == 3){
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true){
                subState = 2; // move to control options
                commandNum = 0; // reset command number for control options
            }
        }


        //Back
        textY += gp.tileSize;
        g2.drawString("Back", textX, textY);
        if(commandNum == 4){
            g2.drawString(">", textX - 25, textY);
        }


        //end game
        textY += gp.tileSize * 2;
        g2.drawString("End Game", textX, textY);
        if(commandNum == 5){
            g2.drawString(">", textX - 25, textY);
            // subState = 3; // move to end game confirmation
            if(gp.keyH.enterPressed == true){
                subState = 3; // move to end game confirmation
                confirmCommandNum = 0; // reset selection for confirmation screen
            }
        }
        //fullscreen
        textY =frameX - 70;
        textX = frameX + gp.tileSize * 8 + 40;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect( textX, textY, 24, 24);
        if(gp.fullscreenOn == true){
            g2.fillRect(textX, textY, 24, 24);
        }

        //music volume
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 169, 24);
        int volumeWidth = 24 * gp.music.volumeScale; // 24 pixels per volume scale
        g2.fillRect(textX, textY, volumeWidth, 24); // fill the rectangle based on volume scale
        
        //se volume
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 169, 24);
        volumeWidth = 24 * gp.se.volumeScale; // 24 pixels per volume scale
        g2.fillRect(textX, textY, volumeWidth, 24); // fill the rectangle based on volume scale
        
    }

    public void options_fullScreenNotification(int frameX,int frameY){
        int textX = frameX + gp.tileSize* 6;
        int textY = frameY + gp.tileSize * 5;

        currentDialogue = "The change will take \neffect after restarting \nthe game.";
        for(String line: currentDialogue.split("\n")){
            g2.drawString(line, textX, textY);
            textY += 40; // line height
        }
        //back
        textY =frameX + gp.tileSize * 5; // space before back option
        g2.drawString("Back", textX, textY);
        if(commandNum == 0){
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true){
                subState = 0; // go back to options menu
            }
        }
    }

    public void options_control(int frameX, int frameY){
        int textX;
        int textY;

        String text = "Control";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize * 3 - (gp.tileSize / 2);
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize * 6;
        textY += gp.tileSize * 2 - (gp.tileSize / 2);
        g2.drawString("Move UP", textX, textY); textY += gp.tileSize;
        g2.drawString("Move DOWN", textX, textY); textY += gp.tileSize;
        g2.drawString("Move LEFT", textX, textY); textY += gp.tileSize;
        g2.drawString("Move RIGHT", textX, textY); textY += gp.tileSize;
        g2.drawString("Pause", textX, textY); textY += gp.tileSize;

        //back
        textY =frameX + gp.tileSize * 5; // space before back option
        g2.drawString("Back", textX, textY);
        if(commandNum == 0){
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true){
                subState = 0; // go back to options menu
                commandNum = 4; // reset command number for options menu
                // commandNum = 5; // reset command number for end game confirmation
            }
        }
        
        textX = frameX + gp.tileSize * 9 + 80; // position for control keys
        textY = gp.tileSize * 5; 
        g2.drawString("W", textX, textY); textY += gp.tileSize;
        g2.drawString("S", textX, textY); textY += gp.tileSize;
        g2.drawString("A", textX, textY); textY += gp.tileSize;
        g2.drawString("D", textX, textY); textY += gp.tileSize;
        g2.drawString("SPACE", textX, textY); textY += gp.tileSize;

    }
    
    public void option_endGameConfirmation(int frameX, int frameY){
        int textX = frameX + gp.tileSize * 7;
        int textY = frameY + gp.tileSize * 3;

        currentDialogue = "Are you sure you want \nto end the game?";
        for(String line: currentDialogue.split("\n")){
            g2.drawString(line, textX, textY);
            textY += 40; // line height
        }

        // "Yes" Option
        String textYes = "Yes";
        int yesX = getXforCenteredText(textYes);
        int yesY = frameY + gp.tileSize * 6;
        g2.drawString(textYes, yesX, yesY);
        if(confirmCommandNum == 0){
            g2.drawString(">", yesX - 25, yesY);
            if(gp.keyH.enterPressed == true){
                gp.stopMusic();
                System.exit(0);  
            }
        }

        // "No" Option
        String textNo = "No";
        int noX = getXforCenteredText(textNo);
        int noY = frameY + gp.tileSize * 7;
        g2.drawString(textNo, noX, noY);
        if(confirmCommandNum == 1){
            g2.drawString(">", noX - 25, noY);
            if(gp.keyH.enterPressed == true){
                subState = 0; // go back to options menu
                commandNum = 5;
            }
        }
        // System.out.println("confirmCommandNum = " + confirmCommandNum);
    }

    public void drawDialogueScreen(Graphics2D g2, String text){

        //Window
        int x = gp.tileSize*3;
        int y = gp.tileSize*10;
        int width = gp.screenWidth - (gp.tileSize*8);
        int height = gp.tileSize*3;

        drawSubWindow(x, y, width, height);

        //TEXT
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;

        //Use loop to handlw line breaks(\n)
        for (String line : text.split("\n")){
            g2.drawString(line , x, y);
            y+= 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height){

        Color c = new Color(0,0,0,210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }
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
    
    public int getXforCenteredText(String text){
        int x = gp.screenWidth / 2 - (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
        return x;
    }
    
}


