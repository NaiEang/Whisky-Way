package src.tile;

import javax.imageio.ImageIO;
import javax.swing.text.Utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import src.main.GamePanel;
import src.main.UtilityTool;

public class TileManager {
    
    GamePanel gp;
    public Tile [] tile;
    public int mapTileNum[][]; 

    public TileManager(GamePanel gp){
        this.gp = gp;

        tile = new Tile[20];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/res/map/WorldMap01.txt");
        // loadMap("/res/map/map01.txt");

    }
    public void getTileImage(){

        for(int i = 0; i < tile.length; i++) {
            tile[i] = new Tile();
        }

        setup(0,"grass3.png", false);
        setup(1,"wall.png", true);
        setup(2,"water.png", true);
        setup(3,"grasswithflower.png", false);
        setup(4,"wood.png", false);
        setup(5,"sand.png", false);
        setup(6,"soil.png", false);
        setup(7,"tree1.png", true);
    }
    // Add this method to your TileManager class for debugging
public void debugTileImages() {
    System.out.println("=== TILE DEBUG INFO ===");
    
    String[] tileNames = {"grass3", "wall", "water", "grasswithflower", 
                         "wood", "sand", "soil", "tree1"};
    
    for (int i = 0; i < tileNames.length; i++) {
        if (i < tile.length && tile[i] != null) {
            System.out.println("Tile " + i + " (" + tileNames[i] + "): " + 
                             (tile[i].image != null ? "LOADED" : "NULL IMAGE"));
        } else {
            System.out.println("Tile " + i + " (" + tileNames[i] + "): NOT INITIALIZED");
        }
    }
    
    System.out.println("=== END TILE DEBUG ===");
}

// Call this method in your TileManager constructor after getTileImage()
// debugTileImages();
    public void loadMap(String filename){

        try {
            InputStream is = getClass().getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;
            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break; // safety
                
                String[] numbers = line.trim().split(" ");

                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                }

                row++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace(); // Always print errors while debugging!
        }
    }
    
    public void setup(int tileNum, String imageName, boolean collision){

        UtilityTool uTool = new UtilityTool();

        try{
            tile[tileNum] = new Tile();
                    InputStream is = getClass().getResourceAsStream("/res/tiles/" + imageName);
        if (is == null) {
            System.out.println("Missing tile image: " + imageName);
            return;
        }
            tile[tileNum].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/" + imageName));
            tile[tileNum].image = uTool.scaleImage(tile[tileNum].image, gp.tileSize, gp.tileSize);
            tile[tileNum].collision = collision;


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){

        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
        for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
            int tileNum = mapTileNum[worldCol][worldRow];

            // In TileManager.draw()
int originalTileNum = mapTileNum[worldCol][worldRow];


if (tileNum >= tile.length || tile[tileNum] == null || tile[tileNum].image == null) {
    
    // THIS IS THE IMPORTANT PART - PRINT A LOUD ERROR
    System.err.println("!!! FAILED TO DRAW TILE: " + originalTileNum + ". Image is NULL. Check file path and name!");

    tileNum = 0; // Still draw grass to prevent a crash
}
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize*2 < gp.player.worldY + gp.player.screenY) {

                                if (tile[tileNum].image != null) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, null);
                } else {
                    System.out.println("Tile " + tileNum + " image is null");
                }
            }

        }  
    }
}
}