package src.main;

import src.object.OBJ_Box;
import src.object.OBJ_NPC;
import src.object.OBJ_Shrimp;

public class AssetSetter {
    GamePanel gp;
    
    public AssetSetter(GamePanel gp){
        this.gp = gp;

    }

    public void setObject(){
        
        // gp.obj[0] = new OBJ_Shrimp();
        // gp.obj[0].worldX =  17* gp.tileSize;
        // gp.obj[0].worldY = 10* gp.tileSize;

        // gp.obj[1] = new OBJ_Shrimp();
        // gp.obj[1].worldX =  30* gp.tileSize;
        // gp.obj[1].worldY = 9* gp.tileSize;

        // gp.obj[2] = new OBJ_NPC();
        // gp.obj[2].worldX =  40* gp.tileSize;
        // gp.obj[2].worldY = 10* gp.tileSize;
        // gp.obj[2].collision = true;

        // gp.obj[3] = new OBJ_Box();
        // gp.obj[3].worldX =  20* gp.tileSize;
        // gp.obj[3].worldY = 10* gp.tileSize;
    }
}
