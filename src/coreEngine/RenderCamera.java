package coreEngine;

import components.*;
import utility.*;

public class RenderCamera {
	
	// Camera Movement- and Animation-Parameters
	private final double cameraSpeed = 0.5;
	private final double maxCameraSpeed = 10.0;
	private final double wavingSpeed = 0.5;
	private final double wavingStrength = 0.5;
	
	public Vector2D rawPosition; // Target Position
	public Vector2D position; // Real Position
	
	// Amount of Camera Shaking
	public double shakeAmount = 0.0;
	
	private int screenWidth; // In Pixels
	private int screenHeight; // In Pixels
	
	public double cameraWidth; // Width of the Camera in Meters
	public double cameraHeight; // Height of the Camera in Meters
	private double aspect; // Aspect of the Camera (height / width)
	
	public RenderCamera(Vector2D position, double width, int screenWidth, int screenHeight) {
		this.rawPosition = new Vector2D(position.x, position.y);
		this.position = position;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		aspect = (double)screenHeight / (double)screenWidth;
		
		cameraWidth = width;
		cameraHeight = cameraWidth * aspect;
	}
	
	// Per Frame Update for Game
	public void CameraUpdate(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, LevelData levelData, GameObject target) {
		// Move
		double diffX = Math.abs(rawPosition.x - target.position.x);
		double diffY = Math.abs(rawPosition.y - target.position.y);
		// Smooth Camera Movement
		rawPosition.x = UtilityFunctions.LerpUnclamped(rawPosition.x, target.position.x, UtilityFunctions.Clamp(cameraSpeed * diffX * frameData.deltaTime, 0.1, maxCameraSpeed));
		rawPosition.y = UtilityFunctions.LerpUnclamped(rawPosition.y, target.position.y, UtilityFunctions.Clamp(cameraSpeed * diffY * frameData.deltaTime, 0.1, maxCameraSpeed));
    	
    	// Bounds the Position in Tile Map
    	Vector2D boundsFrom = levelData.GetLeftBottomCorner();
    	boundsFrom.x += cameraWidth * 0.5 - 0.25 + 1.0;
    	boundsFrom.y += cameraWidth * 0.5 * aspect - 0.25 + 1.0;
    	Vector2D boundsTo = levelData.GetRightTopCorner();
    	boundsTo.x -= cameraWidth * 0.5 + 0.75 + 1.0;
    	boundsTo.y -= cameraWidth * 0.5 * aspect + 0.75 + 1.0;
    	rawPosition.x = UtilityFunctions.Clamp(rawPosition.x, boundsFrom.x, boundsTo.x);
    	rawPosition.y = UtilityFunctions.Clamp(rawPosition.y, boundsFrom.y, boundsTo.y);
    	
    	// Camera Shake
    	if (shakeAmount > 0.0) {
    		shakeAmount = UtilityFunctions.Clamp(shakeAmount - 0.5 * frameData.deltaTime, 0.0, 1.0);
    	}
    	
    	// Apply Camera Shake and update Real Position
    	position.x = rawPosition.x + (Math.cos(Math.sin(frameData.timeSinceGameStart * 5.0) * 20.0) * shakeAmount) + (Math.sin(Math.cos(frameData.timeSinceGameStart * wavingSpeed * 1.8345 - 84.2464) * wavingSpeed) * wavingStrength * 2.0);
    	position.y = rawPosition.y + (Math.sin(Math.cos(frameData.timeSinceGameStart * 5.0) * 17.0) * shakeAmount) + (Math.sin(Math.cos(frameData.timeSinceGameStart * wavingSpeed * 1.1854) * wavingSpeed * 0.7235) * wavingStrength);
    	
    	// Update Camera Height
    	cameraHeight = cameraWidth * aspect;
	}
	
	// Per Frame Update for Editor
	public void EditorUpdate(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, LevelData levelData) {
		// Get Camera Speed
		double speed = 0.5;
		if (inputManager.Run()) {
			speed = 2.0;
		}
		// Update Camera Position
		position.x += speed * inputManager.GetXAxis() * cameraWidth * frameData.deltaTime;
		position.y += speed * inputManager.GetYAxis() * cameraWidth * frameData.deltaTime;
		
		// Bounds the Position in Tile Map
		Vector2D boundsFrom = levelData.GetLeftBottomCorner();
		Vector2D boundsTo = levelData.GetRightTopCorner();
		position.x = UtilityFunctions.Clamp(position.x, boundsFrom.x, boundsTo.x);
    	position.y = UtilityFunctions.Clamp(position.y, boundsFrom.y, boundsTo.y);
    	
    	// Update Camera Height
    	cameraHeight = cameraWidth * aspect;
	}
	
	public int GetScreenWidth() {
		return screenWidth;
	}
	
	public int GetScreenHeight() {
		return screenHeight;
	}
	
	public double GetScreenAspect() {
		return aspect;
	}
}
