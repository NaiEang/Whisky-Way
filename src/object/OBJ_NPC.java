package src.object;

import src.main.GamePanel;

public class OBJ_NPC extends SuperObject {

    GamePanel gp;
    public OBJ_NPC(GamePanel gp) {
        this.gp = gp;
        name = "NPC";
        collision = true;
        try {
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/object/cat_front.png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
