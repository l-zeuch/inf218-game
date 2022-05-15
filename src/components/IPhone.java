package components;

import coreEngine.*;
import enumerations.*;

public class IPhone extends Projectile {
	
	double timeSinceGameStart = 0.0;
	
	private AudioSource hitSound;
	
	public IPhone(Vector2D position, Vector2D initVelocity, GameObjectType hitType) {
		super(position, 0.5, "res/textures/IPhone.png", 256.0, initVelocity, hitType, 1);
		
		hitSound = new AudioSource("IPhone_Hit.wav", false, 0.72, 2.0);
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		timeSinceGameStart = frameData.timeSinceGameStart;
		
		// Rotation
		rotation += frameData.deltaTime * 720.0;
		
		// Move by Physic Engine
		if (gameManager.physicEngine.MoveColliderObject(position, velocity, frameData.deltaTime, colliderSize)) {
			OnDie(gameManager);
		}
	}
	
	@Override
	public void OnDie(GameManager gameManager) {
		super.OnDie(gameManager);
		
		// Create Destroy Particles
		ParticleSystem ps = new ParticleSystem(position, "res/textures/Iphone_Partikel.png", 64.0, 2, 2,
				8, 2.0, 4.0, velocity.x * 0.5, velocity.y * 0.5, -1440.0, 1440.0, 0.5, 1.0, timeSinceGameStart);
		gameManager.AddParticleSystem(ps);
		
		hitSound.Play();
		hitSound.UpdateAsWorldSound(position, gameManager.renderCamera);
	}
}
