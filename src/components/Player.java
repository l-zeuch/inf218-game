package components;

import coreEngine.*;
import enumerations.*;

public class Player extends Figure {
	
	double timeSinceGameStart = 0.0;
	
	private final double iPhoneThrowSpeed = 10.0;
	
	// Check Points and Respawn
	private Vector2D checkpoint;
	private double respawnCounter = 2.0;
	public Checkpoint lastCheckPoint;
	
	public int lifes = 3;
	
	public int coinCount;
	
	private AudioSource stampSound;

	public Player(Vector2D position) {
		super(position, "res/textures/characters/SteveJobs_Character_Animation.png", 30.0, new Vector2D(0.69, 1.99), 3, GameObjectType.Player,
				"Foot_Steps.wav",
				"Steve_Jobs_Explode.wav",
				"Object_Throw.wav");
		
		checkpoint = new Vector2D(position.x, position.y);
		
		coinCount = 0;
		
		stampSound = new AudioSource("Stamp.wav", false, 0.9, 1.0);
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		timeSinceGameStart = frameData.timeSinceGameStart;
		
		if (!isDead && !gameManager.reachedDestination) { // Is controllable
			// Get Keyboard Inputs
			inputXAxis = inputManager.GetXAxis();
			inputYAxis = inputManager.GetYAxis();
			inputRun = inputManager.Run();
			inputJump = inputManager.Jump();
			inputDuck = inputManager.Duck();
			
			// Set Parameters before Figure Update
			boolean wasGrounded = isGrounded;
			boolean hadMaxFallSpeed = false;
			if (velocity.y <= maxFallSpeed + 0.01) {
				hadMaxFallSpeed = true;
			}
			
			// Figure Update
			super.Update(frameData, inputManager, mouseInputManager, gameManager);
			
			// Check for Ground Stamp
			if (!wasGrounded && isGrounded) {
				if (hadMaxFallSpeed) {
					// Perform Ground Stamp
					gameManager.renderCamera.shakeAmount += 0.1;
					stampSound.Stop();
					stampSound.Reset();
					stampSound.Play();
				}
			}
			
			// Throw IPhones
			if (mouseInputManager.LeftMouseButtonClicked()) {
				// Compute Throw Direction
				double dx = mouseInputManager.GetWorldMousePosition().x - position.x;
				double dy = mouseInputManager.GetWorldMousePosition().y - position.y;
				double l = Math.sqrt(dx * dx + dy * dy); // Compute Length of "d" for Normalization
				// Normalize "d"
				dx /= l;
				dy /= l;
				
				// Create IPhone
				IPhone p = new IPhone(new Vector2D(position.x, position.y), new Vector2D(dx * iPhoneThrowSpeed, dy * iPhoneThrowSpeed), GameObjectType.RobotEnemy);
				gameManager.AddEntity(p);
				
				// Play Throw Audio
				shootSound.Stop();
				shootSound.Reset();
				shootSound.Play();
			}
			
			// If was hit
			if (hitCountDown > 0.0) {
				// Apply Hit Flickering
				if ((int)(hitCountDown * 10.0) % 2 == 0) {
					SetVisibility(true);
				}
				else {
					SetVisibility(false);
				}
			}
			else if (!GetVisibility()) {
				SetVisibility(true);
			}
		}
		else { // Is dead or has reached Finish
			if (isDead) { // Is dead
				respawnCounter -= frameData.deltaTime;
				if (respawnCounter <= 0.0) {
					respawnCounter = 0.0;
					
					Respawn(gameManager);
				}
			}
			if (gameManager.reachedDestination) { // Has reached Finish
				inputXAxis = 0.0;
				inputYAxis = 0.0;
				inputRun = false;
				inputJump = false;
				inputDuck = false;
				
				super.Update(frameData, inputManager, mouseInputManager, gameManager);
			}
		}
		
		// Execute always
		super.DeathInclusiveUpdate(frameData, inputManager, mouseInputManager, gameManager);
	}
	
	public boolean AddHealth() {
		if (health < 3) {
			health++;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean AddDamage(int damage, GameManager gameManager) {
		// Apply Hit Camera Shake
		if (super.AddDamage(damage, gameManager)) {
			gameManager.renderCamera.shakeAmount += 0.1;
			return true;
		}
		return false;
	}
	
	@Override
	public void OnDie(GameManager gameManager) {
		if (!isDead) {
			super.OnDie(gameManager);
			
			respawnCounter = 2.0;
			isDead = true;
			
			SetVisibility(false);
			
			// Create Death Particle System
			ParticleSystem ps = new ParticleSystem(position, "res/textures/Airpod.png", 128.0, 1, 1,
					100, 1.0, 8.0, -velocity.x * 0.5, -velocity.y * 0.5, -720.0, 720.0, 0.5, 1.0, timeSinceGameStart);
			gameManager.AddParticleSystem(ps);
			
			// Apply Death Camera Shake
			gameManager.renderCamera.shakeAmount += 0.25;
			
			lifes--;
			health = 3;
		}
	}
	
	// Set the Check Point of the Player
	public void SetCheckPoint(Vector2D newPosition) {
		checkpoint.x = newPosition.x;
		checkpoint.y = newPosition.y;
	}
	
	// Respawn (Reset Position to last Check Point) or end Game when lifes = 0
	public void Respawn(GameManager gameManager) {
		if (lifes <= 0) {
			gameManager.ExitGame();
		}
		else {
			isDead = false;
			GoToCheckPoint();
			health = 3;
			SetVisibility(true);
			
			hitCountDown = 1.0;
			
			// Reset Die Sound
			dieSound.Stop();
			dieSound.Reset();
		}
	}
	
	// Move Player to last Check Point
	private void GoToCheckPoint() {
		position.x = checkpoint.x;
		position.y = checkpoint.y;
		velocity.x = 0;
		velocity.y = 0;
	}
}
