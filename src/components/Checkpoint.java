package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class Checkpoint extends ColliderObject {
	
	private double yPosInit; // Initial y-Position
	
	private boolean isChecked = false; // Is Check Point checked
	private double checkedTimer = 1.0; // Timer for Check Animation
	
	private AudioSource checkSound;
	
	public Checkpoint(Vector2D position) {
		super(position, 0.0, new Vector2D(1.0, 1.0), "res/textures/Check_Point.png",
				64.0, true, 4, 1, true, 10.0, new Vector2D(0.5, 1.99), 1, 1.0, GameObjectType.CheckPoint);
		
		yPosInit = position.y;
		
		checkSound = new AudioSource("Check_Point.wav", false, 0.9, 1.0);
	}
	
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		if (isChecked) {
			// Check Animation
			scale.x = 1.0 + Math.sin(checkedTimer * Math.PI) * 0.2;
			scale.y = 1.0 + Math.sin(checkedTimer * Math.PI) * 0.2;
			position.y = yPosInit + Math.sin(checkedTimer * Math.PI) * 0.2;
		}
		else {
			// No Animation
			scale.x = 1.0;
			scale.y = 1.0;
			position.y = yPosInit;
		}
		
		if (!isChecked) {
			// Check for Player Intersection
			if (UtilityFunctions.CollisionCheck(this, gameManager.player)) {
				// If this isn't the last Check Point of the Player
				if (gameManager.player.lastCheckPoint != this) {
					gameManager.player.SetCheckPoint(new Vector2D(position.x, position.y + 0.01));
					gameManager.player.lastCheckPoint = this;
					isChecked = true;
					
					checkSound.Stop();
					checkSound.Reset();
					checkSound.Play();
				}
			}
		}
		
		// Change Animation Timer
		if (isChecked) {
			checkedTimer -= frameData.deltaTime;
			if (checkedTimer <= 0.0) {
				checkedTimer = 1.0;
				isChecked = false;
			}
		}
	}
}