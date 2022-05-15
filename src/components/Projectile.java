package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class Projectile extends ColliderObject {
	
	protected Vector2D velocity;
	
	protected GameObjectType hitType; // Game Object Type which is able to be hit by this Projectile
	protected int damage; // Amount of Damage this Projectile applies
	
	public Projectile(Vector2D position, double colliderSize, String path, double ppm, Vector2D initVelocity, GameObjectType hitType, int damage) {
		super(position, 0.0, new Vector2D(1.0, 1.0),
				path, ppm, false, 1, 1, false, 0.0,
				new Vector2D(colliderSize, colliderSize), 1, 1.0, GameObjectType.Projectile);
		
		velocity = initVelocity;
		
		this.hitType = hitType;
		this.damage = damage;
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Update Position
		position.x += velocity.x * frameData.deltaTime;
		position.y += velocity.y * frameData.deltaTime;
		
		// Screen Bounds
		double offsetX = gameManager.renderCamera.cameraWidth * 0.5 + 1.0;
		double offsetY = gameManager.renderCamera.cameraHeight * 0.5 + 1.0;
		
		// Check if Projectile is out of Screen
		if (position.x < gameManager.renderCamera.position.x - offsetX ||
				position.x > gameManager.renderCamera.position.x + offsetX ||
				position.y < gameManager.renderCamera.position.y - offsetY ||
				position.y > gameManager.renderCamera.position.y + offsetY) {
			
			// Delete Projectile
			DeleteMe(gameManager);
		}
		// Check if Projectile is out of Tile Map
		else if (position.x < -1.0 || position.x > gameManager.levelData.GetTileMapWidth() || position.y < -1.0 || position.y > gameManager.levelData.GetTileMapHeight()) {
			// Delete Projectile
			DeleteMe(gameManager);
		}
		// Check Entities
		else {
			for (int i = 0; i < gameManager.GetEntities().size(); i++) {
				ColliderObject co = gameManager.GetEntities().get(i);
				
				// If Entity is this Hit Type
				if (co.GetType().toString() == hitType.toString()) {
					// Check for Intersection with Entities
					if (UtilityFunctions.CollisionCheck(this, co)) {
						// Add Damage
						if (co.AddDamage(damage, gameManager)) {
							OnDie(gameManager);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void OnDie(GameManager gameManager) {
		super.OnDie(gameManager);
	}
}
