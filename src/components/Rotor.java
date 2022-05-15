package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class Rotor extends ColliderObject {
	
	public Rotor(Vector2D position, double xAxis, double yAxis) {
		super(position, 0.0, new Vector2D(xAxis, yAxis),
				"res/textures/Rotor_Animation.png", 16.0, true, 8, 1, true, 30.0,
				new Vector2D(0.5, 3.5), 1, 1.0, GameObjectType.Rotor);
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Check Rotor Hits
		for (int i = 0; i < gameManager.GetEntities().size(); i++) {
			ColliderObject co = gameManager.GetEntities().get(i);
			
			if (co.GetType() != GameObjectType.Rotor) {
				if (UtilityFunctions.CollisionCheck(this, co)) {
					// Add Rotor Damage
					co.AddDamage(1, gameManager);
				}
			}
		}
	}
}
