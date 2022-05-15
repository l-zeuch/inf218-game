package components;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import coreEngine.*;
import utility.*;

public class AudioSource {
	
    private String filePath; // Path to Audio File
    private AudioInputStream audioInputStream; // Audio Stream for Reading
    private Clip clip; // Audio Clip
    private boolean loop; // Should Audio Source loop
    
    private boolean isPlaying = false;
    
    private double startVolume; // Initialize Volume
    private double volume = 1.0;
    private FloatControl gainControl; // Volume Controller
    private float minGain;
    private float gainRange;
    
    private double balance = 0.0; // Stereo Pan
    private FloatControl balanceControl; // Stereo Pan Controller
    private float minBalance;
    private float balanceRange;
    
    private double falloff; // Distance Fall Off of Volume
    
    public AudioSource(String name, boolean loop, double startVolume, double intensity) {
    	filePath = "res/sounds/" + name; // Get Path
    	this.loop = loop;
    	
    	LoadAudioStream();
    	
    	// Get Volume Controls
    	gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
    	minGain = gainControl.getMinimum();
    	gainRange = gainControl.getMaximum() - minGain;
    	
    	// Get Stereo Pan Controls
    	balanceControl = (FloatControl)clip.getControl(FloatControl.Type.BALANCE);
    	minBalance = balanceControl.getMinimum();
    	balanceRange = balanceControl.getMaximum() - minBalance;
    	
    	// Compute Fall Off width Intensity
    	this.falloff = (1.0 / intensity) * 0.01;
    	
    	this.startVolume = startVolume;
    	SetVolume(startVolume);
    	
    	// Reset Audio Clip
    	clip.stop();
    }
    
    // Reset Sample Position in the Audio Clip
    public void Reset() {
        clip.setMicrosecondPosition(0);
    }
    
    // Start Audio Source
    public void Play() {
    	if (!isPlaying) {
    		if (loop)
	        	clip.loop(Clip.LOOP_CONTINUOUSLY);
    		clip.start();
        	isPlaying = true;
    	}
    }
    
    // Stop/Pause Clip
    public void Stop() {
    	if (isPlaying) {
    		clip.stop();
        	isPlaying = false;
    	}
    }
    
    public boolean IsPlaying() {
    	return isPlaying;
    }
    
    // Set Audio Volume (Range: 0.0, 1.0)
    public void SetVolume(double volume) {
    	volume = UtilityFunctions.Clamp(volume, 0.0, 1.0);
    	
    	this.volume = volume;
    	
    	float gain = (gainRange * (float)volume) + minGain;
    	gainControl.setValue(gain);
    }
    
    // Set Audio Volume multiplied with Start Volume (Range: 0.0, 1.0)
    public void SetVolumeWidthStartVolume(double volume) {
    	volume = UtilityFunctions.Clamp(volume * startVolume, 0.0, 1.0);
    	
    	this.volume = volume;
    	
    	float gain = (gainRange * (float)volume) + minGain;
    	gainControl.setValue(gain);
    }
    
    // Set Stereo Pan (Range: -1.0, 1.0)
    public void SetBalance(double balance) {
    	balance = UtilityFunctions.Clamp(balance, -1.0, 1.0);
    	
    	this.balance = balance;
    	
    	float b = balanceRange * (float)(balance * 0.5 + 0.5) + minBalance;
    	
    	balanceControl.setValue(b);
    }
    
    // Update Audio Source Parameters when Audio Source is a Point in the World (myPos)
    public void UpdateAsWorldSound(Vector2D myPos, RenderCamera cam) {
    	double dx = myPos.x - cam.position.x;
    	double dy = myPos.y - cam.position.y;
    	double d = dx * dx + dy * dy; // Not real Distance (Square Root was omitted for Performance)
    	
    	SetBalance((dx / cam.cameraWidth) * 2.0); // Left/Right Volume
    	
    	double v = (1.0 / ((d + 1.0) * falloff)) * startVolume;
    	v = UtilityFunctions.Clamp(v, 0, startVolume);
    	
    	SetVolume(v);
    }
    
    public double GetVolume() {
    	return volume;
    }
    
    public double GetBalance() {
    	return balance;
    }
    
    // Load the Audio File as Stream for the Audio Clip
    private void LoadAudioStream() {
        try {
        	audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile()); // Get Audio Stream
			
	        clip = AudioSystem.getClip(); // Get Audio Clip
	        
	        clip.open(audioInputStream); // Open the Audio Stream for Reading
	        
	        if (loop)
	        	clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
        catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
        catch (IOException e) {
			e.printStackTrace();
		}
        catch (LineUnavailableException e) {
			//e.printStackTrace();
		}
    }
}
