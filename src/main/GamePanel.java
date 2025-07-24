package src.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import src.entity.NPC_car;
import src.entity.NPC_dog;
import src.entity.Player;
import src.object.SuperObject;
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
    KeyHandler keyH = new KeyHandler(this);

    static Sound music = new Sound();
    Sound se = new Sound();
    static boolean windSound = false;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public Thread gameThread;

    //ENTITY AND OBJECTS
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10];
    public NPC_dog dog[] = new NPC_dog[10];
    public NPC_car car[] = new NPC_car[10];

    //GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    int dialogueTimer;

    //Constructor for game panel
    public GamePanel(){
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); //for smoother rendering
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }
    public void setupGame(){

        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setCar();
        playMusic(0); //play background music
        gameState = dialogueState;
        
    }
    public static void backgroundMusic() {
        playMusic(4);
    }

    public void startGameThread(){

        gameThread = new Thread(this);// this is to pass game panel to the thread
        gameThread.start(); //starts the thread, which will call the run method

    }

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

    //UPDATE
    public void update(){

        //Play state
        if(gameState == playState){
            //PLAYER
            player.update();

            //NPC
            for(int i = 0; i< dog.length ; i++){
                if(dog[i] != null){
                    dog[i].update();
                }
            }
            for(int i = 0; i<car.length ; i++){
                if(car[i]!=null){
                    car[i].update();
                }
            }
        }

        //Pause state
        if(gameState == pauseState){

        }

        //Dialogue
        if(gameState == dialogueState){
            dialogueTimer++;

            if(dialogueTimer>200){
                gameState = playState;
                dialogueTimer = 0;
            }
        }
        
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D) g;

        //Tile
        tileM.draw(g2);

        //Object
        for(int i = 0; i< obj.length; i++){
            if(obj[i]!= null){
                obj[i].draw(g2, this);
            }
        }

        //NPC
        for(int i = 0; i<dog.length ;i++){
            if(dog[i]!=null){
                dog[i].draw(g2);
            }
        }
        for(int i = 0; i<car.length ;i++){
            if(car[i]!=null){
                car[i].draw(g2);
            }
        }
        
        player.draw(g2);

        //UI
        ui.draw(g2);



        g2.dispose(); //to save some memory and resources

    }
    public static void playMusic(int i){
        System.out.println("Play music");
        music.setFile(i);
        music.setVolume(-10.0f);
        music.play();
        music.loop();
    }
    public static void stopMusic(){
        music.stop();
    }
    public void playSE(int i){
        se.setFile(i);
        se.setVolume(-5.0f);
        se.play();
        
    }

}
