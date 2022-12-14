
package com.ryancodesgames.apollo.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import javax.sound.sampled.FloatControl;

public class Sound 
{
    URL[] soundURL = new URL[50];
    Clip clip;
    
    public Sound()
    {
        soundURL[0] = getClass().getResource("com/ryancodesgames/sound/march.wav");
    }
    
    public void setFile(int i)
    {
        try
        {
           AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
           clip = AudioSystem.getClip();
           clip.open(ais);
           FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
           gainControl.setValue(-35.0f);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void play()
    {
        clip.start();
    }
    
    public void loop()
    {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stop()
    {
        clip.stop();
    }
}
