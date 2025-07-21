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

        setup(0,"grass3", false);
        setup(1,"wall", true);
        setup(2,"water", true);
        setup(3,"grasswithflower", false);
        setup(4,"wood", false);
        setup(5,"sand", false);
        setup(6,"soil", false);
        setup(7,"tree1", true);
    }
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
                    InputStream is = getClass().getResourceAsStream("/res/tiles/" + imageName + ".png");
        if (is == null) {
            System.out.println("Missing tile image: " + imageName);
            return;
        }
            tile[tileNum].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/" + imageName + ".png"));
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

                        if (tileNum >= tile.length || tile[tileNum] == null || tile[tileNum].image == null) {
                // Draw a default tile (like grass) or skip
                tileNum = 0; // Use grass as default
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