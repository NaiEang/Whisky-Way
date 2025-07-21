package src.entity;

import src.main.GamePanel;

public class NPC_dog extends Entity{
    
    public NPC_dog (GamePanel gp){
        
        super(gp);

        direction = "down";
        speed = 4;
    }
}
