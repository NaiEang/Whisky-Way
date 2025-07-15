package src.main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    Clip clip;
    FloatControl volumeControl;
    URL soundURL[] = new URL[30];

    public Sound(){

        soundURL[0] = getClass().getResource("/res/sound/music.wav");
        soundURL[1] = getClass().getResource("/res/sound/eat2.wav");
        soundURL[2] = getClass().getResource("/res/sound/pickup.wav");
        soundURL[3] = getClass().getResource("/res/sound/complete.wav");
        soundURL[4] = getClass().getResource("/res/sound/wind.wav");

    }
    public void setVolume(float decibels){
        if(volumeControl!=null){
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            decibels = Math.max(min, Math.min(max, decibels));
            volumeControl.setValue(decibels);
            System.out.println("Volume set to: " + decibels);
        }
    }
    public void setFile(int i){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

            //Get volume control
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void play(){
        clip.start();
    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        clip.stop();
    }
}
