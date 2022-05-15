package coreEngine;

public class FrameData {
	
	public double deltaTime; // Length of on Frame in Seconds
	public double timeSinceGameStart; // Time since the Start of the Game in Seconds
	
	public FrameData() {
		deltaTime = 0.0;
		timeSinceGameStart = 0.0;
	}
	
	// Update Frame Data
	public void UpdateFrameData(long deltaTimeInMilliseconds, long currentSystemTimeInMilliseconds, long gameStartTime) {
		timeSinceGameStart = (double)(currentSystemTimeInMilliseconds - gameStartTime) / 1000.0;
    	deltaTime = (double)deltaTimeInMilliseconds / 1000.0;
	}
}
