package components;

import coreEngine.FrameData;
import coreEngine.GameManager;
import coreEngine.InputManager;
import coreEngine.MouseInputManager;
import enumerations.GameObjectType;
import utility.UtilityFunctions;

public class Heart extends ColliderObject {
	
	private double yPosInit; // Initial y-Position
	
	private boolean isCollected = false; // Is Heart collected
	private double collectedTimer = 0.5; // Timer for Collecting Animation
	
	private AudioSource collectSound;
	
	public Heart(Vector2D position, double xAxis, double yAxis) {
		super(position, 0.0, new Vector2D(xAxis * 0.75, yAxis * 0.75),
				"res/textures/Heart_Animation.png", 64.0, true, 8, 4, true, 30.0,
				new Vector2D(0.99, 0.99), 1, 1.0, GameObjectType.Heart);
		
		yPosInit = position.y;
		
		collectSound = new AudioSource("Coin.wav", false, 0.85, 1.0);
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		if (isCollected) {
			// Collect Animation
			position.y += frameData.deltaTime * 10.0 * collectedTimer;
		}
		else {
			// No Animation
			position.y = yPosInit + (Math.sin(2.0 * frameData.timeSinceGameStart) + 1.0) * 0.25;
		}
		
		if (!isCollected) {
			// Check for Player Intersection
			if (UtilityFunctions.CollisionCheck(this, gameManager.player)) {
				gameManager.player.AddHealth();
				
				animationSpeed = 180.0;
				isCollected = true;
				
				collectSound.Play();
			}
		}
		
		// Change Collected Timer
		if (isCollected) {
			collectedTimer -= frameData.deltaTime;
			// Is Timer finished, delete Heart
			if (collectedTimer <= 0.0) {
				DeleteMe(gameManager);
			}
		}
	}
}
