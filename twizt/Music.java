
package twizt;

import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/*
Author(s): Jacob Adams
*/

public class Music
{
    
    private static Clip audio;
    
    private static Clip roar;
    
    private static FloatControl gainControl;

    /**
     * PLays some music for the game
     * @param name The name of the music to play
     */
    public static void playHum()
    { //Play the music with the current filename

        
        if (audio != null)
        {
            audio.stop();
            audio.close();
        }

        try
        {
            audio = AudioSystem.getClip();
            audio.open(AudioSystem.getAudioInputStream(Music.class.getResource("Hum.wav")));
        }
        catch (LineUnavailableException ex)
        {
        }
        catch (IOException ex)
        {
        }
        catch (UnsupportedAudioFileException ex)
        {
        }

        //AudioIntro.loop(0);
        audio.loop(Clip.LOOP_CONTINUOUSLY); //Loop forever

        
        

    }
    
    public static void setVolume(int distance)
    {
        if (distance == 0)
        {
            distance = 1;
        }
        float distanceFloat = 0.1f *distance;
        float inverseSquare = 20f * (float)Math.log10(1f / (distanceFloat*distanceFloat)); 
        
        if (inverseSquare < gainControl.getMinimum())
        {
            inverseSquare = gainControl.getMinimum();
        }
        if (inverseSquare > gainControl.getMaximum())
        {
            inverseSquare = gainControl.getMaximum();
        }
        
        gainControl.setValue(inverseSquare);
        //System.out.println("Set volume to: " + inverseSquare);
    }
    
    public static void playRoar()
    { //Play the music with the current filename

        
        System.out.println("MonsterRoar");
        
        if (roar != null)
        {
            roar.stop();
            roar.close();
        }

        try
        {
            roar = AudioSystem.getClip();
            roar.open(AudioSystem.getAudioInputStream(Music.class.getResource("MonsterRoar.wav")));
        }
        catch (LineUnavailableException ex)
        {
        }
        catch (IOException ex)
        {
        }
        catch (UnsupportedAudioFileException ex)
        {
        }
        
        gainControl = (FloatControl) roar.getControl(FloatControl.Type.MASTER_GAIN); 

        Music.setVolume(999);

        //AudioIntro.loop(0);
        roar.loop(Clip.LOOP_CONTINUOUSLY); //Loop forever
        
        

    }

}
