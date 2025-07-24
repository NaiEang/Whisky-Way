package src.entity;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import src.main.GamePanel;

public class NPC_car extends Entity{

    public NPC_car(GamePanel gp){

        super(gp);

        direction = "right";
        speed = 2;
        
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 25;
        solidArea.height = 20;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage(){

        up1 = setup("/res/NPC/carup");
        up2 = setup("/res/NPC/carup");
        down1 = setup("/res/NPC/cardown");
        down2 = setup("/res/NPC/cardown");
        left1 = setup("/res/NPC/carleft");
        left2 = setup("/res/NPC/carleft1");
        right1 = setup("/res/NPC/carright");
        right2 = setup("/res/NPC/carright1");
    }

    @Override
    public void setAction() {
    // Only make a turning decision if perfectly aligned on a tile.
    if (worldX % gp.tileSize == 0 && worldY % gp.tileSize == 0) {

        int currentCol = worldX / gp.tileSize;
        int currentRow = worldY / gp.tileSize;
        
        // --- NEW LOGIC: Build a list of possible turns ---
        ArrayList<String> possibleDirections = new ArrayList<>();

        // Check each of the four directions
        boolean canGoUp = gp.cChecker.isRoadTile(currentCol, currentRow - 1);
        boolean canGoDown = gp.cChecker.isRoadTile(currentCol, currentRow + 1);
        boolean canGoLeft = gp.cChecker.isRoadTile(currentCol - 1, currentRow);
        boolean canGoRight = gp.cChecker.isRoadTile(currentCol + 1, currentRow);

        if (canGoUp) {
            possibleDirections.add("up");
        }
        if (canGoDown) {
            possibleDirections.add("down");
        }
        if (canGoLeft) {
            possibleDirections.add("left");
        }
        if (canGoRight) {
            possibleDirections.add("right");
        }
        
        // --- Prevent the car from immediately turning around ---
        // If the car has more than one option, remove the option to go backward.
        if (possibleDirections.size() > 1) {
            if (direction.equals("up")) possibleDirections.remove("down");
            if (direction.equals("down")) possibleDirections.remove("up");
            if (direction.equals("left")) possibleDirections.remove("right");
            if (direction.equals("right")) possibleDirections.remove("left");
        }
        
        // --- Choose a new direction ---
        // If there are any available paths, pick one randomly.
        if (!possibleDirections.isEmpty()) {
            Random random = new Random();
            int choice = random.nextInt(possibleDirections.size());
            direction = possibleDirections.get(choice);
        }
        // If there are no possible directions (a dead end), the car will automatically turn around
        // because its previous direction was removed from the list.
    }
}

    @Override
    public void update() {
        // 1. Get the car's AI to decide on a direction
        setAction();

        // 2. Perform a new, simpler collision check
        collisionOn = false;
        
        // Get the coordinates of the center point on the car's leading edge
        int checkCol = 0;
        int checkRow = 0;

        switch (direction) {
            case "up":
                checkCol = (worldX + solidArea.width / 2) / gp.tileSize;
                checkRow = (worldY - speed) / gp.tileSize;
                break;
            case "down":
                checkCol = (worldX + solidArea.width / 2) / gp.tileSize;
                checkRow = (worldY + solidArea.height + speed) / gp.tileSize;
                break;
            case "left":
                checkCol = (worldX - speed) / gp.tileSize;
                checkRow = (worldY + solidArea.height / 2) / gp.tileSize;
                break;
            case "right":
                checkCol = (worldX + solidArea.width + speed) / gp.tileSize;
                checkRow = (worldY + solidArea.height / 2) / gp.tileSize;
                break;
        }

        // If the center point is about to drive off the road, set collision to true
        if (!gp.cChecker.isRoadTile(checkCol, checkRow)) {
            collisionOn = true;
        }

        //Check if the car hit player
        gp.cChecker.checkPlayer(this);

        // --- DEBUGGING TOOL: UNCOMMENT THIS LINE TO SEE THE CAR'S THOUGHTS ---
        // System.out.println("Direction: " + direction + " | Collision: " + collisionOn);

        // 3. Move the car if there is no collision
        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
        }

        // 4. Animate the car's sprite
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
}

