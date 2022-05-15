package components;

import coreEngine.*;
import enumerations.*;

public class ColliderObject extends GameObject {
	
	protected Vector2D colliderSize;
	
	// Hit Parameters
	protected double hitCoolDown;
	protected double hitCountDown;
	
	// Status
	protected int health;
	
	public ColliderObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String path, double ppm, // Sprite
			boolean isSpriteSheet, int tileCountX, int tileCountY, // Sprite Sheet
			boolean isSpriteAnimation, double animationSpeed, // Sprite Animation
			Vector2D colliderSize, // Collision
			int health, double hitCoolDown,
			GameObjectType type
			) {
		
		super(position, rotation, scale, path, ppm, isSpriteSheet, tileCountX, tileCountY, isSpriteAnimation, animationSpeed, type);
		
		this.colliderSize = colliderSize;
		
		this.health = health;
		
		this.hitCoolDown = hitCoolDown;
		hitCountDown = 0.0;
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Change Hit Count Down
		if (hitCountDown > 0.0) {
			hitCountDown -= frameData.deltaTime;
			hitCountDown = Math.max(hitCountDown, 0.0);
		}
	}
	
	public void OnDie(GameManager gameManager) {
		DeleteMe(gameManager);
	}
	
	@Override
	public void DeleteMe(GameManager gameManager) {
		// When this Object should be deleted, add the corresponding Delete List
		switch (GetType()) {
			case Player:
				break;
			case Coin:
				gameManager.AddLevelObjectToDelete(this);
				break;
			case Heart:
				gameManager.AddLevelObjectToDelete(this);
				break;
			case Rotor:
				gameManager.AddLevelObjectToDelete(this);
				break;
			case RobotEnemy:
				gameManager.AddEntityToDelete(this);
				break;
			case Projectile:
				gameManager.AddEntityToDelete(this);
				break;
			case ParticleSystem:
				break;
			default:
				break;
		}
	}
	
	public int GetHealth() {
		return health;
	}
	
	// Add Damage to this Collider Object (return if Hit was successful)
	public boolean AddDamage(int damage, GameManager gameManager) {
		// If is Ready for Hit (Hit Count Down is Zero)
		if (hitCountDown <= 0.0) {
			health -= damage;
			hitCountDown = hitCoolDown; // Set Hit Count Down to Cool Down Time
			
			if (health <= 0) {
				health = 0;
				OnDie(gameManager);
			}
			return true;
		}
		return false;
	}
	
	public Vector2D GetColliderSize() {
		return colliderSize;
	}
}
