package src.object;

import javax.imageio.ImageIO;

import src.main.GamePanel;

public class OBJ_Shrimp extends SuperObject {

    GamePanel gp;

    public OBJ_Shrimp(GamePanel gp){
        this.gp = gp;
        name = "Shrimp";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/object/shrimp.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
