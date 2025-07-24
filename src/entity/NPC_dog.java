package src.entity;

import java.awt.Rectangle;
import java.util.Random;

import src.main.GamePanel;

public class NPC_dog extends Entity{

    public NPC_dog(GamePanel gp){

        super(gp);

        direction = "down";
        speed = 1;
        
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
        setDialogue();
    }

    public void getImage(){

        up1 = setup("/res/NPC/dog_back1");
        up2 = setup("/res/NPC/dog_back");
        down1 = setup("/res/NPC/dog_front1");
        down2 = setup("/res/NPC/dog_front");
        left1 = setup("/res/NPC/dog_left1");
        left2 = setup("/res/NPC/dog_left");
        right1 = setup("/res/NPC/dog_right1");
        right2 = setup("/res/NPC/dog_right");
    }
    public void setDialogue(){

        dialogues[0] = "Hello, Player!" ;

    }
    @Override
    public void setAction() {

        // CALCULATE DISTANCE TO PLAYER
        int playerX = gp.player.worldX;
        int playerY = gp.player.worldY;
        int npcX = worldX;
        int npcY = worldY;

        int dx = Math.abs(playerX - npcX);
        int dy = Math.abs(playerY - npcY);

        // Sight of 5 tiles.
        int sightRange = gp.tileSize * 5;

        // CHECK IF PLAYER IS WITHIN SIGHT RANGE
        if (dx < sightRange && dy < sightRange) {

            // CHASING STATE
            speed = 2;

            // Determine which direction has the greater distance to close
            if (Math.abs(playerY - npcY) > Math.abs(playerX - npcX)) {
                // Prioritize vertical movement if the player is further away vertically
                if (playerY > npcY) {
                    direction = "down";
                } else {
                    direction = "up";
                }
            } else {
                // Prioritize horizontal movement
                if (playerX > npcX) {
                    direction = "right";
                } else {
                    direction = "left";
                }
            }

        } else {
            // PATROLLING STATE
            // If player is out of range, go back to normal speed and random movement.
            speed = 1;

            actionLockCounter++;

            if (actionLockCounter == 120) { //2 seconds
                Random random = new Random();
                int i = random.nextInt(100) + 1;

                if (i <= 25) {
                    direction = "up";
                }
                if (i > 25 && i <= 50) {
                    direction = "down";
                }
                if (i > 50 && i <= 75) {
                    direction = "left";
                }
                if (i > 75 && i <= 100) {
                    direction = "right";
                }
                actionLockCounter = 0;
            }
        }
    }

    public void speak(){

        gp.ui.currentDialogue = dialogues[0];
    }
}

