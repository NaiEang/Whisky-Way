package src.object;

import javax.imageio.ImageIO;

public class OBJ_Shrimp extends SuperObject {

    public OBJ_Shrimp(){

        name = "Shrimp";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/object/shrimp.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
