package src.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import src.main.GamePanel;
import src.main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public int ShrimpCount = 0;
    int BoxCount = 0;
    public int coinCount = 0;
    int deliveredCount = 0;
    int standCounter;
    boolean isMoving = false;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        solidArea = new Rectangle();
        solidArea.x = 15;
        solidArea.y = 15;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y; //recall default value
        solidArea.width = gp.tileSize - 35;
        solidArea.height = gp.tileSize - 20;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize);
        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues(){

        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }
    public void getPlayerImage(){

        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/player/up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/res/player/up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/player/down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/res/player/down_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/res/player/right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/res/player/right_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/res/player/left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/res/player/left_2.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void update(){
        if(keyH.upPressed == true || keyH.downPressed == true || 
        keyH.rightPressed == true || keyH.leftPressed == true){
            // isMoving = true;

            if(keyH.upPressed){
                direction = "up";
                // worldY -= speed; //y values increase as they go down & x as right
            }else if(keyH.downPressed){
                direction = "down";
                // worldY += speed;
            }else if(keyH.leftPressed){
                direction = "left";
                // worldX -= speed;
            }else if(keyH.rightPressed){
                direction = "right";
                // worldX += speed;
            }
            collisionOn = false;
            gp.cChecker.checkTile(this); //check if player collides with tile

            // Check object collision
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObj(objIndex);

            //if collision is false, player can move
        //         if (collisionOn) {
        //     System.out.println("Collision detected, blocking movement.");
        
            // }

            // System.out.println("Collision" + collisionOn);
            if(!collisionOn){
                switch(direction){
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;  
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
                
            }
            spriteCounter++;
            if(spriteCounter > 12){
                if(spriteNum == 1){
                spriteNum = 2;
                }else if(spriteNum == 2){
                    spriteNum = 1;
                }
                spriteCounter = 0;
            
            }
        }else{
            standCounter++;
            if(standCounter == 20){
                spriteNum = 1; //set to standing sprite
                standCounter = 0; //reset stand counter
            }
        }
    }
    public void pickUpObj(int i){

        if(i!=999){
            
            String objName = gp.obj[i].name;

            switch(objName){
                case "Shrimp":
                    ShrimpCount+=10;
                    gp.playSE(1);
                    gp.obj[i] = null;
                    gp.ui.showMessage("You ate a shrimp!");
                    break;
                case "Box":
                    BoxCount++;
                    gp.playSE(2);
                    gp.obj[i] = null;
                    gp.ui.showMessage("You picked up a Box!");
                    break;
                case "NPC":
                    gp.obj[i].collision = true;  
                    
                    if(!gp.ui.messageOn){
                        if(BoxCount > 0){
                        gp.playSE(3);
                        BoxCount--;
                        coinCount += 10;
                        gp.ui.showMessage("You made a delivery!");
                        deliveredCount++;
                        }else{
                            gp.ui.showMessage("You have no boxes to deliver!");
                        }
                    }
                    break;
                
            }
            if(deliveredCount ==1){
                gp.stopMusic();
                gp.ui.gameFinished = true;
            }
        }
    }
    public void draw(Graphics2D g2){
        // g2.setColor(Color.white);
        
        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch(direction){
            case "up" :
            if(spriteNum == 1){
                image = up1;
            }
            if(spriteNum == 2){
                image = up2;
            }
            break;
            case "down":
            if(spriteNum == 1){
                image = down1;
            }
            if(spriteNum == 2){
                image = down2;
            }
                break;
            case "left":
            if(spriteNum == 1){
                image = left1;
            }
            if(spriteNum == 2){
                image = left2;
            }
            break;
            case "right":
            if(spriteNum == 1){
                image = right1;
            }
            if(spriteNum == 2){
                image = right2;
            }
            break;
        }
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
