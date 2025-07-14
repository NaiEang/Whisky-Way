package src.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import src.entity.Player;
import src.object.SuperObject;
import src.tile.FileUtils;
import src.tile.TMXMap;
import src.tile.TMXParser;
import src.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    
    //Screen Settings
    final int originalTileSize = 16; // 16x16 tile  //character size
    final int scale = 3; //scale character size by 3

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public int maxScreenCol = 30;
    public final int maxScreenRow = 15;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    //WORLD SETTING
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    // public final int worldWidth = tileSize* maxWorldCol;
    // public final int worldHeight = tileSize* maxWorldRow;

    //FPS
    int fps = 60;

    //SYSTEM
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Sound sound = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    Thread gameThread;

    //OBJECTS
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10];

    TMXMap map;
    Image tilesetImage;
    int tilesetColumns = 8; 

    //Constructor for game panel
    public GamePanel(){
        
        // this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        // this.setBackground(Color.BLACK);
        // this.setDoubleBuffered(true); //for smoother rendering
        // this.addKeyListener(keyH);
        // this.setFocusable(true);
        try{
            map = TMXParser.parseTMX("res/tiles/untitled.tmx");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setupGame(){

        aSetter.setObject();
        playMusic(0); //play background music
        
        
    }

    public void startGameThread(){

        gameThread = new Thread(this);// this is to pass game panel to the thread
        gameThread.start(); //starts the thread, which will call the run method

    }
    //1st Method of Game loop
    // @Override
    // public void run() {

    //     double drawInterval = 1000000000 / fps; //0.01666 seconds
    //     double nextDrawTime = System.nanoTime() + drawInterval; //next time to draw

    //     //create a game loop
    //     while(gameThread!=null){

    //         //1. Update information: character position
    //         update();

    //         //2. Draw the screen: with update info
    //         repaint(); //calls paintComponent method

            
    //         try {
    //             double remainingTime = (nextDrawTime - System.nanoTime());
    //             remainingTime /= 1000000; 

    //             if(remainingTime<0){
    //                 remainingTime = 0;
    //             }

    //             Thread.sleep((long) remainingTime);

    //             nextDrawTime += drawInterval; //update next draw time

    //         } catch (InterruptedException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //     }
    // }

    //2nd Method of Game loop
    public void run(){

        double drawInterval = 1000000000 / fps;
        double delta = 0;
        long lasttime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread!=null){

            currentTime = System.nanoTime();

            delta += (currentTime - lasttime)/drawInterval;
            timer += (currentTime - lasttime);
            lasttime = currentTime;

            if(delta >=1){
                update();
                repaint();
                delta --;
                drawCount++;
            }
            if(timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0; 
                timer = 0;
            }
        }
    }
    public void update(){
        player.update();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D) g;
        if (map != null && map.tilesetImage != null) {
            renderMap(g2, map, map.tilesetImage, map.tilesetImage.getWidth() / map.tileWidth);
    }

    //     // tileM.draw(g2);

    //     // for(int i = 0; i< obj.length; i++){
    //     //     if(obj[i]!= null){
    //     //         obj[i].draw(g2, this);
    //     //     }
    //     // }
    //     // player.draw(g2);

        g2.dispose(); //to save some memory and resources

    //     if (map != null && tilesetImage != null) {
    //         renderMap(g2, map, tilesetImage, tilesetColumns);
    //     }
    }
    public void playMusic(int i){
        System.out.println("Play music");
        sound.setFile(i);
        sound.setVolume(-10.0f);
        sound.play();
        sound.loop();
    }
    public void stopMusic(){

        sound.stop();
    }
    public void playSE(int i){
        sound.setFile(i);
        sound.setVolume(-5.0f);
        sound.play();
        
    }
    public void renderMap(Graphics2D g2, TMXMap map, Image tilesetImage, int tilesetColumns) {
        for (int row = 0; row < map.height; row++) {
            for (int col = 0; col < map.width; col++) {
                int tileId = map.tileData[row][col];
                if (tileId > 0) {
                    int tileIndex = tileId - 1; // TMX GIDs start at 1
                    int tileX = (tileIndex % tilesetColumns) * map.tileWidth;
                    int tileY = (tileIndex / tilesetColumns) * map.tileHeight;

                    g2.drawImage(tilesetImage,
                            col * map.tileWidth, row * map.tileHeight,
                            col * map.tileWidth + map.tileWidth, row * map.tileHeight + map.tileHeight,
                            tileX, tileY,
                            tileX + map.tileWidth, tileY + map.tileHeight,
                            null);
                }
            }
        }
    }

}
