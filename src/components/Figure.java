package components;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class Figure extends ColliderObject {
	
	public boolean isDead = false;
	
	// Movement Constants
	private final double walkSpeed = 3.0;
	private final double climbSpeed = 2.0;
	private final double runMuliplier = 2.0;
	private final double jumpVelocity = 10.0;
	private final double jumpChargeSpeed = 6.0;
	protected final double maxFallSpeed = -16.0;
	
	// Animation Constants
	private final double idleFrameDuration = 0.2;
	private final double walkFrameDuration = 0.05;
	private final double runFrameDuration = 0.05;
	private final double jumpFrameDuration = 0.1;
	private final double duckFrameDuration = 0.05;

	public Vector2D velocity;
	
	// Movement States
	protected boolean isGrounded;
	protected boolean leftCliff;
	protected boolean rightCliff;
	protected boolean isOnWall;
	protected double jumpCharge;
	protected boolean isInLadder;
	protected boolean jumpInLadder;
	
	// Animation
	private double timeSinceLastSprite;
	private AnimationState animationState = AnimationState.Idle;
	private AnimationState lastAnimationState = AnimationState.None;
	private double frameDuration = 0.0;
	
	// Controls
	protected double inputXAxis = 0.0;
	protected double inputYAxis = 0.0;
	protected boolean inputRun = false;
	protected boolean inputJump = false;
	protected boolean inputDuck = false;
	
	// Audio
	protected AudioSource footStepsSound;
	protected AudioSource dieSound;
	protected AudioSource shootSound;

	public Figure(Vector2D position, String path, double ppm, Vector2D colliderSize, int health, GameObjectType type, String footStepsSound, String dieSound, String shootSound) {

		super(position, 0.0, new Vector2D(1.0, 1.0), path, ppm, true, 8, 5, false, 0.0, colliderSize, health, 1.0, type);

		velocity = new Vector2D(0.0, 0.0);

		isGrounded = false;
		leftCliff = false;
		rightCliff = false;
		isOnWall = false;
		jumpCharge = 0.0;
		isInLadder = false;
		jumpInLadder = false;

		timeSinceLastSprite = 0.0;
		
		// Audio
		this.footStepsSound = new AudioSource(footStepsSound, true, 0.95, 1.0);
		this.dieSound = new AudioSource(dieSound, false, 0.7, 1.0);
		this.shootSound = new AudioSource(shootSound, false, 0.8, 1.0);
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Set Sprite Direction
		if (inputXAxis < 0.0)
			scale.x = -1.0;
		if (inputXAxis > 0.0)
			scale.x = 1.0;
		
		// Set Walk/Run (x-Velocity)
		if (inputRun) {
			velocity.x = walkSpeed * runMuliplier * inputXAxis;
		}
		else {
			velocity.x = walkSpeed * inputXAxis;
		}
		
		// If isn't in Ladder Tile/Block or if is Jump in Ladder
		if (!isInLadder || jumpInLadder) {
			// Apply Gravity (y-Velocity)
			velocity.y -= 9.81 * frameData.deltaTime;
		}
		
		if (!isInLadder) { // Stays in Normal Tile/Block (Air)
			// Reset Jump Charge when stays on Ground
			if (isGrounded) {
				jumpCharge = 0.0;
			}
			// Jump
			if (inputJump && (isGrounded || jumpCharge < 1.0)) {
				isGrounded = false;
				jumpCharge += jumpChargeSpeed * frameData.deltaTime;
				jumpCharge = UtilityFunctions.Clamp(jumpCharge, 0.0, 1.0);
				velocity.y = jumpVelocity * jumpCharge;
			}
			// Duck
			if (inputDuck && !isGrounded) {
				velocity.y = maxFallSpeed;
			}
		}
		else { // Stays in Ladder Tile/Block
			//
			if (!inputJump) {
				jumpCharge = 1.0;
			}
			// Jump
			if (inputJump && isInLadder && jumpCharge < 1.0) {
				isGrounded = false;
				jumpInLadder = true;
				jumpCharge += jumpChargeSpeed * frameData.deltaTime;
				jumpCharge = UtilityFunctions.Clamp(jumpCharge, 0.0, 1.0);
				velocity.y = jumpVelocity * jumpCharge;
			}
			// If Jump is finished, reset Jump Charge
			if (velocity.y <= 0.0) {
				jumpInLadder = false;
				jumpCharge = 0.0;
			}
			// Climb Ladder
			if (!jumpInLadder) {
				// Set Walk/Run
				if (inputRun) {
					velocity.y = climbSpeed * runMuliplier * inputYAxis;
				}
				else {
					velocity.y = climbSpeed * inputYAxis;
				}
			}
		}
		
		// Apply Maximum Fall Speed
		velocity.y = Math.max(velocity.y, maxFallSpeed);
		
		// Movements (by the Physic Engine)
		FigureCollisionData data = gameManager.physicEngine.MoveFigureObject(position, velocity, frameData.deltaTime, colliderSize);
		position = data.position;
		velocity = data.velocity;
		isGrounded = data.isGrounded;
		leftCliff = data.leftCliff;
		rightCliff = data.rightCliff;
		isOnWall = data.isOnWall;
		isInLadder = data.isInLadder;
		
		// Apply Tile Maps Bounds to x-Position
		position.x = UtilityFunctions.Clamp(position.x, 0.0, (double)gameManager.levelData.GetTileMapWidth() - 1.0);
		
		// Perform Death-check when is under Tile Map or out of Range
		if (position.y < -1.0 || position.y > Integer.MAX_VALUE) {
			OnDie(gameManager);
		}

		// Set Animation State
		if (!isInLadder) {
			if (velocity.y <= 0.0 || isGrounded) {
				if (velocity.y > maxFallSpeed + 0.01) {
					// Normal Moving
					if (Math.abs(velocity.x) > walkSpeed) {
						animationState = AnimationState.Run;
					}
					else if (Math.abs(velocity.x) > 0) {
						animationState = AnimationState.Walk;
					}
					else {
						animationState = AnimationState.Idle;
					}
				}
				else {
					// Ducking
					animationState = AnimationState.Duck;
				}
			}
			else {
				// Jumping
				animationState = AnimationState.Jump;
			}
		}
		else {
			if (jumpInLadder) {
				// Jumping
				animationState = AnimationState.Jump;
			}
			else {
				// Normal Moving
				if (Math.abs(velocity.x) > walkSpeed || Math.abs(velocity.y) > walkSpeed) {
					animationState = AnimationState.Run;
				}
				else if (Math.abs(velocity.x) > 0 || Math.abs(velocity.y) > 0) {
					animationState = AnimationState.Walk;
				}
				else {
					animationState = AnimationState.Idle;
				}
			}
		}
		
		// Check for Animation State Change
		if (lastAnimationState != animationState) {
			lastAnimationState = animationState;
			timeSinceLastSprite = 0.0;
			// Set new Animation State Parameters
			switch (animationState) {
				case Idle:
					spriteSheetIndex = 0;
					frameDuration = idleFrameDuration;
					break;
				case Walk:
					spriteSheetIndex = 8;
					frameDuration = walkFrameDuration;
					break;
				case Run:
					spriteSheetIndex = 16;
					frameDuration = runFrameDuration;
					break;
				case Jump:
					spriteSheetIndex = 24;
					frameDuration = jumpFrameDuration;
					break;
				case Duck:
					spriteSheetIndex = 32;
					frameDuration = duckFrameDuration;
					break;
				default:
					break;
			}
		}
		
		// Do Animating with Frame Duration
		timeSinceLastSprite += frameData.deltaTime;
		if (timeSinceLastSprite >= frameDuration) {
			timeSinceLastSprite -= frameDuration;
			switch (animationState) {
				case Idle:
					AnimationLoop(0, 7);
					break;
				case Walk:
					AnimationLoop(8, 15);
					break;
				case Run:
					AnimationLoop(16, 23);
					break;
				case Jump:
					AnimationLoop(24, 31);
					break;
				case Duck:
					AnimationLoop(32, 39);
					break;
				default:
					break;
			}
		}
		
		// Audio
		if (Math.abs(velocity.x) > 0.0 && (isGrounded || isInLadder)) {
			footStepsSound.Play();
		}
		else {
			footStepsSound.Stop();
		}
	}
	
	// Update Method which should also be performed when Figure is dead
	public void DeathInclusiveUpdate(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		// Apply Audio World Positions
		footStepsSound.UpdateAsWorldSound(position, gameManager.renderCamera);
		dieSound.UpdateAsWorldSound(position, gameManager.renderCamera);
		shootSound.UpdateAsWorldSound(position, gameManager.renderCamera);
	}
	
	@Override
	public void OnDie(GameManager gameManager) {
		if (!isDead) {
			super.OnDie(gameManager);
			health = 0;
			isDead = true;
			
			footStepsSound.Stop();
			dieSound.Play();
		}
	}
	
	// Animate Figure
	private void AnimationLoop(int indexFrom, int indexTo) {
		spriteSheetIndex++;
		if (spriteSheetIndex > indexTo) {
			spriteSheetIndex = indexFrom;
		}
	}
}
