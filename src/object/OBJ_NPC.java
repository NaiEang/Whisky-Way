package src.object;

public class OBJ_NPC extends SuperObject {

    public OBJ_NPC() {
        name = "NPC";
        collision = true;
        try {
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/object/cat_front.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
