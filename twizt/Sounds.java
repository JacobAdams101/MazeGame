
package twizt;


import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
Author(s): Jacob Adams
*/
public class Sounds
{
    public static Clip theNote;
    public static Clip theMonster;
   
    
    static
    {
        try
        { //Load the audio files
            theNote = AudioSystem.getClip();
            theNote.open(AudioSystem.getAudioInputStream(Sounds.class.getResource("The Note.wav")));
            
            theMonster = AudioSystem.getClip();
            theMonster.open(AudioSystem.getAudioInputStream(Sounds.class.getResource("The Monster.wav")));


        } catch (LineUnavailableException ex)
        {
        }
        catch (IOException ex)
        {
        }
        catch (UnsupportedAudioFileException ex)
        {
        }
    }

    public static void playTheNote()
    {
        theNote.stop(); //Stops the sound if it's currently playing
        theNote.setFramePosition(0); //Restarts the sound
        theNote.start(); //Plays the sound
        
    }
    public static void playTheMonster()
    {
        theMonster.stop(); //Stops the sound if it's currently playing
        theMonster.setFramePosition(0); //Restarts the sound
        theMonster.start(); //Plays the sound
        
    }
}
