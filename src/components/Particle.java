package components;

import coreEngine.*;

public class Particle {
	
	public Vector2D position;
	public double rotation;
	public Vector2D velocity;
	public double rotationVelocity;
	public double gravity;
	
	private int spriteIndex;
	
	private double initTime; // Time when Particle was Initialized
	private double lifeTime;
	
	public boolean deleteMe = false;
	
	public Particle(Vector2D position, double rotation, Vector2D velocity, double rotationVelocity, double gravity, int spriteIndex, double initTime, double lifeTime) {
		this.position = position;
		this.rotation = rotation;
		this.velocity = velocity;
		this.rotationVelocity = rotationVelocity;
		this.gravity = gravity;
		
		this.spriteIndex = spriteIndex;
		
		this.initTime = initTime;
		this.lifeTime = lifeTime;
	}
	
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager) {
		// Update Velocity
		velocity.y += gravity * frameData.deltaTime;
		
		// Update Position
		position.x += velocity.x * frameData.deltaTime;
		position.y += velocity.y * frameData.deltaTime;
		
		// Update Position
		rotation += rotationVelocity * frameData.deltaTime;
		
		// Delete Check
		if (frameData.timeSinceGameStart - initTime >= lifeTime) {
			deleteMe = true;
		}
	}
	
	public int GetSpriteIndex() {
		return spriteIndex;
	}
}
