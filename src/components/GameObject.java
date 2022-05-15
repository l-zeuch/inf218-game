package components;

import coreEngine.*;
import enumerations.*;

public class GameObject extends Sprite {
	
	public Vector2D position; // In meters
	public double rotation; // In degrees
	public Vector2D scale;
	
	protected GameObjectType type; // Type of this Game Object
	
	public GameObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String path, double ppm, // Sprite
			boolean isSpriteSheet, int tileCountX, int tileCountY, // Sprite Sheet
			boolean isSpriteAnimation, double animationSpeed, // Sprite Animation
			GameObjectType type
			) {
		
		super(path, ppm, isSpriteSheet, tileCountX, tileCountY, isSpriteAnimation, animationSpeed);
		
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.type = type;
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
	}
	
	public void DeleteMe(GameManager gameManager) {
		// Overwritten by upper Classes
	}
	
	public GameObjectType GetType() {
		return type;
	}
}
