package src.tile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.Image;

import src.main.GamePanel;

public class TileManager {
    
    GamePanel gp;
    public Tile [] tile;
    public int mapTileNum[][]; 

    public TileManager(GamePanel gp){
        this.gp = gp;

        tile = new Tile[20];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        // loadMap("/res/map/WorldMap01.txt");
        // loadMap("/res/map/map01.txt");
        

    }
    public void getTileImage(){

        try{
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/grass3.jpg"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/wall.png"));
            tile[1].collision = true;

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/water.png"));
            tile[2].collision = true;

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/grasswithflower.jpg"));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/wood.png"));

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/sand.jpg"));

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/soil.png"));

            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tree1.png"));
            tile[7].collision = true;


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // public void loadMap(String filename){

    //     try {
    //         InputStream is = getClass().getResourceAsStream(filename);
    //         BufferedReader br = new BufferedReader(new InputStreamReader(is));

    //         int row = 0;
    //         while (row < gp.maxWorldRow) {
    //             String line = br.readLine();
    //             if (line == null) break; // safety

    //             String[] numbers = line.trim().split(" ");

    //             for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
    //                 int num = Integer.parseInt(numbers[col]);
    //                 mapTileNum[col][row] = num;
    //             }

    //             row++;
    //         }

    //         br.close();
    //     } catch (Exception e) {
    //         e.printStackTrace(); // Always print errors while debugging!
    //     }
    // }
    public Image loadTilesetImage(String source) {
    try {
        File imgFile = FileUtils.findFileRecursive(new File("res/"), new File(source).getName());
        if (imgFile != null) {
            System.out.println("Resolved tileset image path: " + imgFile.getPath());
            return ImageIO.read(imgFile);
        } else {
            System.err.println("Tileset image NOT FOUND: " + source);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

    public void draw(Graphics2D g2){
        // int maxWorldCol = 0;
        // int maxWorldRow = 0;

        // while(maxWorldCol < gp.maxWorldCol && maxWorldRow < gp.maxWorldRow){
        //     int tileNum = mapTileNum[maxWorldCol][maxWorldRow];

        //     int worldX = maxWorldCol * gp.tileSize;
        //     int worldY = maxWorldRow * gp.tileSize;
        //     int screenX = worldX - gp.player.worldX - gp.player.screenX;
        //     int screenY = worldY - gp.player.worldY - gp.player.screenY;

        //     if(worldX > gp.player.worldX - gp.player.screenX &&
        //        worldX < gp.player.worldX + gp.player.screenX &&
        //        worldY > gp.player.worldY - gp.player.screenY &&
        //        worldY < gp.player.worldY + gp.player.screenY)
        //        {
        //         g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        //        }
        //     // g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        //     maxWorldCol++;

        //     if(maxWorldCol == gp.maxWorldCol){
        //         maxWorldCol = 0;
        //         maxWorldRow ++;
    //     for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
    //     for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {
    //         int tileNum = mapTileNum[worldCol][worldRow];

    //         int worldX = worldCol * gp.tileSize;
    //         int worldY = worldRow * gp.tileSize;
    //         int screenX = worldX - gp.player.worldX + gp.player.screenX;
    //         int screenY = worldY - gp.player.worldY + gp.player.screenY;

    //         if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
    //             worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
    //             worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
    //             worldY - gp.tileSize*2 < gp.player.worldY + gp.player.screenY) {

    //             g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    //         }
    //     }  
    // }
}
}