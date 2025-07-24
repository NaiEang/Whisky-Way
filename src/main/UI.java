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

    int subState = 0;
    int commandNum = 0;

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
            drawPauseScreen();
        }
        if(gp.gameState == gp.dialogueState){
            drawDialogueScreen(g2, "Welcome to Whisky Way!\nFind box and deliver it to the witch cat.");
        }
    }
    public void drawPauseScreen(){

        int frameWidth = gp.tileSize*10;
        int frameHeight = gp.tileSize*12;
        int frameX = gp.screenWidth / 2 - frameWidth / 2;
        int frameY = gp.screenHeight / 2 - frameHeight / 2;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
        // String text = "PAUSED";
        // int x = getXforCenteredText1(text);
        // int y = gp.screenHeight / 2; 
          switch(subState) {
            case 0: options_top(frameX, frameY); break;
            case 1:break;
            case 2:break;
        }
        // g2.drawString(text, x, y);
    }

    public void drawDialogueScreen(Graphics2D g2, String text){

        //Window
        int x = gp.tileSize*3;
        int y = gp.tileSize*10;
        int width = gp.screenWidth - (gp.tileSize*8);
        int height = gp.tileSize*3;

        drawDialogueSub(x, y, width, height);

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

    public void drawDialogueSub(int x, int y, int width, int height){

        Color c = new Color(0,0,0,210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

        public void options_top(int frameX, int frameY){
            int textX;
            int textY;
            String text = "Options";
            textX = getXforCenteredText(text);
            textY = frameY + gp.tileSize;
            g2.drawString(text, textX, textY);

            int labelX = frameX + gp.tileSize;       // Label starting X position
            int controlX = frameX + gp.tileSize * 6; // UI element X position aligned to right

            int rowHeight = gp.tileSize * 2;         // Vertical spacing between each row
            int startY = frameY + gp.tileSize * 2;   // Starting Y position below "Options" title

            // 🖼 Row 1: Full Screen + Checkbox
            g2.drawString("Full Screen", labelX, startY);
            g2.drawRect(controlX, startY - gp.tileSize / 2, gp.tileSize - 4, gp.tileSize - 4);

            // 🎵 Row 2: Music + Slider
            startY += rowHeight;
            g2.drawString("Music", labelX, startY);
            g2.drawRect(controlX, startY - gp.tileSize / 2, gp.tileSize * 4, gp.tileSize - 6);

            // 🔊 Row 3: SE + Slider
            startY += rowHeight;
            g2.drawString("SE", labelX, startY);
            g2.drawRect(controlX, startY - gp.tileSize / 2, gp.tileSize * 4, gp.tileSize - 6);

            // 🎮 Row 4: Control label
            startY += rowHeight;
            g2.drawString("Control", labelX, startY);

            // 🛑 Row 5: End Game label
            startY += rowHeight;
            g2.drawString("End Game", labelX, startY);
            // 📦 FULL SCREEN CHECKBOX — smaller & centered
            int boxSize = gp.tileSize - 10;  // shrink by 10 pixels
            int boxX = frameX + gp.tileSize * 6;
            int boxY = frameY + gp.tileSize * 2 + 10;
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(boxX, boxY, boxSize, boxSize);

            // 🎵 MUSIC SLIDER — shorter height
            boxY += gp.tileSize * 2;
            g2.drawRect(boxX, boxY, gp.tileSize * 4, gp.tileSize - 10);

            // 🔊 SE SLIDER — same dimensions
            boxY += gp.tileSize * 2;
            g2.drawRect(boxX, boxY, gp.tileSize * 4, gp.tileSize - 10);
            
            // //FULL SCREEN CHECK BOX
            // int boxX = frameX + (int)(gp.tileSize * 6);
            // int boxY = frameY + gp.tileSize * 2 + 24;
            // g2.setStroke(new BasicStroke(3));
            // g2.drawRect(boxX, boxY, 24, 24);  // checkbox size 24x24

            // //MUSIC VOLUME SLIDER
            // boxY += gp.tileSize;
            // g2.drawRect(boxX, boxY, 120, 24); // slider width: 120

            // //SOUND EFFECTS VOLUME SLIDER
            // boxY += gp.tileSize;
            // g2.drawRect(boxX, boxY, 120, 24);

        }
        public int getXforCenteredText(String text){
            int x = gp.screenWidth / 2 - (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
            return x;
        }
        public void drawSubWindow(int width, int height) {
            int x = gp.screenWidth / 2 - width / 2;
            int y = gp.screenHeight / 2 - height / 2;

            Color bgColor = new Color(0, 0, 0, 200); // semi-transparent black
            g2.setColor(bgColor);
            g2.fillRoundRect(x, y, width, height, 35, 35);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
        }

        public int getXforCenteredText1(String text){
        int x = gp.screenWidth / 2 - (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
        return x;
        }
    
}


