package src.object;

import src.main.GamePanel;

public class OBJ_Box extends SuperObject {

    GamePanel gp;
    public OBJ_Box(GamePanel gp) {
        name = "Box";
        this.gp = gp;

        try {
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/object/box.png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
