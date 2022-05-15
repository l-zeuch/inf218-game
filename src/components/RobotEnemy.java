package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class RobotEnemy extends Figure {
	
	double timeSinceGameStart = 0.0;
	
	// AI Options
	private final double targetingRadius = 10.0;
	private final double attackRadius = 7.0;
	private final double brakeOffset = 0.5;
	
	private final double attackWaitTime = 1.0;
	private final double iPhoneThrowSpeed = 10.0;
	
	private double jumpTimer = 0.0;
	private double attackTimer = 0.0;
	
	public RobotEnemy(Vector2D position, double xAxis) {
		super(position, "res/textures/characters/Robot_Character_Animation.png", 30.0, new Vector2D(0.69, 1.99), 1, GameObjectType.RobotEnemy,
				"Foot_Steps.wav",
				"Steve_Jobs_Explode.wav",
				"Object_Throw.wav");
		
		scale.x = xAxis;
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		timeSinceGameStart = frameData.timeSinceGameStart;
		
		// If Game isn't finished
		if (!gameManager.reachedDestination) {
			// Reset Inputs of the Figure
			inputXAxis = 0.0;
			inputYAxis = 0.0;
			inputRun = false;
			if (jumpTimer <= 0.001) {
				inputJump = false;
			}
			inputDuck = false;
			
			// Update Attack Timer
			if (attackTimer > 0.0) {
				attackTimer -= frameData.deltaTime;
				attackTimer = Math.max(attackTimer, 0.0);
			}
			
			// Compute Distance to Player (d)
			double dx = position.x - gameManager.player.position.x;
			double dy = position.y - gameManager.player.position.y;
			double d = Math.sqrt(dx * dx + dy * dy); // Apply Pythagorean Theorem
			
			if (d <= targetingRadius) { // Targeting
				// Walking
				if (position.x < gameManager.player.position.x - brakeOffset && (!rightCliff || !isGrounded)) {
					inputXAxis = 1.0;
				}
				else if (position.x > gameManager.player.position.x + brakeOffset && (!leftCliff || !isGrounded)) {
					inputXAxis = -1.0;
				}
				
				// Update Jump Timer
				if (jumpTimer > 0.0) {
					jumpTimer -= frameData.deltaTime;
					jumpTimer = Math.max(jumpTimer, 0.0);
				}
				
				// Jumping
				if (inputXAxis != 0.0) {
					if (isOnWall) {
						inputJump = true;
						jumpTimer = 0.5;
					}
				}
				
				if (d <= attackRadius && attackTimer <= 0.0 && !gameManager.player.isDead) { // Attack
					// Create IPhone
					IPhone p = new IPhone(new Vector2D(position.x, position.y), new Vector2D(-(dx / d) * iPhoneThrowSpeed, -(dy / d) * iPhoneThrowSpeed), GameObjectType.Player);
					gameManager.AddEntity(p);
					
					// Play Throw Audio
					shootSound.Stop();
					shootSound.Reset();
					shootSound.Play();
					
					// Reset Attack Timer
					attackTimer = attackWaitTime + Math.sin(Math.cos(position.x * 93.2354 + timeSinceGameStart * 43.6541) * 54.8325 - position.y * 23.9123) * 0.1;
				}
			}
			
			// Check for Collision width other Characters
			for (int i = 0; i < gameManager.GetEntities().size(); i++) {
				ColliderObject co = gameManager.GetEntities().get(i);
				
				if (co != (ColliderObject)this) {
					if (co.GetType() != GameObjectType.Player) {
						if (UtilityFunctions.CollisionCheck(this, co)) {
							if (co.position.x < position.x && inputXAxis < 0.0 || co.position.x > position.x && inputXAxis > 0.0) {
								// Stop Moving
								inputXAxis = 0.0;
							}
						}
					}
				}
			}
		}
		
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Execute always
		super.DeathInclusiveUpdate(frameData, inputManager, mouseInputManager, gameManager);
	}
	
	@Override
	public void OnDie(GameManager gameManager) {
		if (!isDead) {
			super.OnDie(gameManager);
			
			isDead = true;
			
			SetVisibility(false);
			
			// Create Die Particle System
			ParticleSystem ps = new ParticleSystem(position, "res/textures/Airpod.png", 128.0, 1, 1,
					100, 1.0, 8.0, -velocity.x * 0.5, -velocity.y * 0.5, -720.0, 720.0, 0.5, 1.0, timeSinceGameStart);
			gameManager.AddParticleSystem(ps);
			
			// Apply Camera Shake
			gameManager.renderCamera.shakeAmount += 0.1;
			
			// Hydra Mode (Not stable)
			/*if (position.y > 1.0) {
				gameManager.AddEntity(new RobotEnemy(new Vector2D(position.x - 0.5, position.y + 2.0), 1.0));
				gameManager.AddEntity(new RobotEnemy(new Vector2D(position.x + 0.5, position.y + 2.0), 1.0));
			}*/
		}
	}
}
