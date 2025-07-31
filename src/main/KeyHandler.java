package src.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

    public class KeyHandler implements KeyListener{

        GamePanel gp;
        public boolean upPressed, downPressed, leftPressed, rightPressed;
        public boolean enterPressed;
        public boolean spacePressed = false;



        public KeyHandler(GamePanel gp){
            this.gp = gp;
        }
        @Override
        public void keyTyped(KeyEvent e) {

        }
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode(); //turn number key code into a number

            // Confirmation menu navigation
            if (code == KeyEvent.VK_SPACE) {
                spacePressed = true;
            }
            if(gp.ui.subState == 3){
                if(code == KeyEvent.VK_W){
                    gp.ui.confirmCommandNum--;
                    if(gp.ui.confirmCommandNum < 0) gp.ui.confirmCommandNum = 1;
                }
                if(code == KeyEvent.VK_S){
                    gp.ui.confirmCommandNum++;
                    if(gp.ui.confirmCommandNum > 1) gp.ui.confirmCommandNum = 0;
                }
            }


            if(code == KeyEvent.VK_W){
                upPressed = true;
            }
            if(code == KeyEvent.VK_A){
                leftPressed = true;
            }
            if(code == KeyEvent.VK_S){
                downPressed = true;
            }
            if(code == KeyEvent.VK_D){
                rightPressed = true;
                
            }
            if(code == KeyEvent.VK_SPACE){
                if(gp.gameState == gp.playState){
                    gp.gameState = gp.pauseState;
                }else if(gp.gameState == gp.pauseState){
                    gp.gameState = gp.playState;
                }
            }
            
            if(code == KeyEvent.VK_ENTER){
                gp.keyH.enterPressed = true; // set enterPressed to true
                
            }

            int maxCommandNum = 0;
            switch(gp.ui.subState){
                case 0: maxCommandNum = 5;
            } 
            // Maximum command number for the menu
            if(code == KeyEvent.VK_W){
               gp.ui.commandNum--;
               gp.playSE(2);
               if (gp.ui.commandNum < 0) {
                   gp.ui.commandNum = maxCommandNum;
               }
            }
            if(code == KeyEvent.VK_S){
                gp.ui.commandNum++;
                gp.playSE(2);
                if(gp.ui.commandNum > maxCommandNum){
                    gp.ui.commandNum = 0;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int code = e.getKeyCode(); //turn number key code into a number

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                spacePressed = false;
            }
            if(code == KeyEvent.VK_W){
                upPressed = false;
            }
            if(code == KeyEvent.VK_A){
                leftPressed = false;
            }
            if(code == KeyEvent.VK_S){
                downPressed = false;
            }
            if(code == KeyEvent.VK_D){
                rightPressed = false;
            }
            if(code == KeyEvent.VK_A){
                if(gp.ui.subState == 0){
                    if(gp.ui.commandNum == 1 && gp.music.volumeScale > 0){
                        gp.music.volumeScale--;
                        gp.music.checkVolume();
                        gp.playMusic(2);
                    }
                    if(gp.ui.commandNum == 2 && gp.se.volumeScale > 0){
                        gp.se.volumeScale--;
                        gp.se.checkVolume();
                        // gp.playSE(2);
                    }
                }
            }
            if(code == KeyEvent.VK_D){
                if(gp.ui.subState == 0){
                     if(gp.ui.commandNum == 1 && gp.music.volumeScale < 7){
                        gp.music.volumeScale++;
                        gp.music.checkVolume();
                        // gp.playMusic(2);
                    }
                    if(gp.ui.commandNum == 2 && gp.se.volumeScale < 7){
                        gp.se.volumeScale++;
                        gp.se.checkVolume();
                        // gp.playSE(2);
                    }
                }
            }
        }
}
