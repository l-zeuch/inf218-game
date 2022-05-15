package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class Goal extends ColliderObject {
	
	private double yPosInit; // Initial y-Position
	
	private boolean isInGoal = false; // Is Goal/Finish checked
	private double inGoalTimer = 1.0; // Timer for Check Animation
	
	private AudioSource goalSound;
	
	public Goal(Vector2D position) {
		super(position, 0.0, new Vector2D(1.0, 1.0), "res/textures/Goal.png",
				64.0, true, 4, 1, true, 10.0, new Vector2D(0.5, 1.99), 1, 1.0, GameObjectType.Goal);
		
		yPosInit = position.y;
		
		goalSound = new AudioSource("Check_Point.wav", false, 0.95, 1.0);
	}
	
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		if (isInGoal) {
			// Check Animation
			scale.x = 1.0 + Math.sin(inGoalTimer * Math.PI) * 0.2;
			scale.y = 1.0 + Math.sin(inGoalTimer * Math.PI) * 0.2;
			position.y = yPosInit + Math.sin(inGoalTimer * Math.PI) * 0.2;
		}
		else {
			// No Animation
			scale.x = 1.0;
			scale.y = 1.0;
			position.y = yPosInit;
		}
		
		if (!isInGoal) {
			// Check for Player Intersection
			if (UtilityFunctions.CollisionCheck(this, gameManager.player)) {
				// Finish Game
				gameManager.ReachDestination();
				isInGoal = true;
				
				goalSound.Play();
			}
		}
		
		// Change Animation Timer
		if (isInGoal) {
			inGoalTimer -= frameData.deltaTime;
			if (inGoalTimer <= 0.0) {
				inGoalTimer = 0.0;
			}
		}
	}
}
