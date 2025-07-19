package src.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import src.main.GamePanel;

import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    Font arial_40, arial_80B;
    BufferedImage heartImage, coinImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;

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

        if(gameFinished == true){

            g2.setFont(arial_40);
            g2.setColor(Color.white);
            
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
    }
}
