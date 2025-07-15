package src.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class UI {
    GamePanel gp;
    Font arial_40;
    BufferedImage heartImage, coinImage;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 20);
        try{
            heartImage = ImageIO.read(getClass().getResourceAsStream("/res/background&button/heart.png"));
            coinImage = ImageIO.read(getClass().getResourceAsStream("/res/background&button/coin.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){

        g2.setFont(arial_40);
        g2.setColor(Color.white);
        g2.drawImage(heartImage, 8, 7, 30, 30, gp);
        g2.drawString("Energy "+ gp.player.ShrimpCount, 40, 30);
        g2.drawImage(coinImage, 8, 38, 30, 30, gp);
        g2.drawString(gp.player.coinCount + " $", 40, 60);
    }
}
