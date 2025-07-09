package src.object;

public class OBJ_Box extends SuperObject {

    public OBJ_Box() {
        name = "Box";

        try {
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/object/box.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
