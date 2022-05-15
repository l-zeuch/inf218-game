package coreEngine;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.List;

import components.*;
import utility.*;

public class GameManager {
	
	private double timeSinceGameStart = 0.0;
	
	// Basic Modules
	private GamePanel gamePanel;
	public RenderCamera renderCamera;
	public LevelData levelData;
	public PhysicEngine physicEngine;
	
	// Lists for Object Handling (Delete Lists are holding the Objects, which will be deleted at the End of the current Frame)
	private List<BackgroundObject> backgroundObjects;
	//
	private List<ColliderObject> entities;
	private List<ColliderObject> entitiesDeleteList;
	//
	private List<ColliderObject> levelObjects;
	private List<ColliderObject> levelObjectsDeleteList;
	//
	private List<ParticleSystem> particleSystems;
	private List<ParticleSystem> particleSystemsDeleteList;
	//
	
	// Global Sounds
	public AudioSource gameMusic;
	public AudioSource windSound;
	
	// Player
	public Player player;
	
	// Maximal Coins in one Level
	public int maxCoins;
	
	// Parameters for Level Ending
	public boolean reachedDestination = false;
	private double destinationTimer = 0.0;
	public double timeSinceGoal = 0.0;
	
	public GameManager(RenderCamera renderCamera, GamePanel gamePanel) {
		this.renderCamera = renderCamera;
		this.gamePanel = gamePanel;
		
		// Create new List Objects
		backgroundObjects = new ArrayList<>();
		
		entities = new ArrayList<>();
		entitiesDeleteList = new ArrayList<>();
		
		levelObjects = new ArrayList<>();
		levelObjectsDeleteList = new ArrayList<>();
		
		particleSystems = new ArrayList<>();
		particleSystemsDeleteList = new ArrayList<>();
		
		// Player
		player = new Player(new Vector2D(0.0, 0.0));
		entities.add(player);
		
		// Get Level File
		String levelName;
		boolean validInput = true; // Stays in the loop to the Input from the User is valid (similar Structure in the Editor Manager)
		// Load existing Level
		do {
			levelName = LevelFileManagement.SelectALevel();
			
			if (levelName != null) {
				validInput = true;
				
				// Get Level Data
				try {
					levelData = new LevelData(levelName, entities, levelObjects, player);
					maxCoins = levelData.maxCoins;
				}
				catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "File Name couldn't be found!", "Error", JOptionPane.ERROR_MESSAGE);
					validInput = false;
				}
			}
			else {
				validInput = false;
			}
		}
		while (!validInput);
		
		// Set Positions
		player.SetCheckPoint(player.position);
		renderCamera.rawPosition.x = player.position.x;
		renderCamera.rawPosition.y = player.position.y;
		
		// Initialize Physic Engine
		physicEngine = new PhysicEngine(levelData);
		
		// Create Sky
		CreateBackgroundObject(new Vector2D(0.0, 0.0), 0.0, new Vector2D(2.0, 2.0), "Sky_Day2.png", 128.0, 0.0);
		CreateBackgroundObject(new Vector2D(-3.0, 3.0), 0.0, new Vector2D(-1.0, 1.0), "Sun_64x64.png", 16.0, 0.0);
		
		// Create Clouds
		CreateBackgroundObject(new Vector2D(-7.0, 3.0), 0.0, new Vector2D(0.5, 0.5), "Cloud.png", 8.0, 0.03);
		CreateBackgroundObject(new Vector2D(9.0, 2.5), 0.0, new Vector2D(0.5, 0.5), "Cloud2.png", 8.0, 0.03);
		CreateBackgroundObject(new Vector2D(2.0, 2.0), 0.0, new Vector2D(0.8, 0.8), "Cloud.png", 8.0, 0.05);
		CreateBackgroundObject(new Vector2D(-3.0, 2.0), 0.0, new Vector2D(1.0, 1.0), "Cloud2.png", 8.0, 0.1);
		CreateBackgroundObject(new Vector2D(7.0, 2.0), 0.0, new Vector2D(1.0, 1.0), "Cloud.png", 8.0, 0.1);
		CreateBackgroundObject(new Vector2D(-7.0, 5.0), 0.0, new Vector2D(1.5, 1.5), "Cloud.png", 8.0, 0.15);
		CreateBackgroundObject(new Vector2D(10.0, 8.0), 0.0, new Vector2D(-2.0, 2.0), "Cloud2.png", 8.0, 0.2);
		CreateBackgroundObject(new Vector2D(-8.0, 0.0), 0.0, new Vector2D(-1.5, 1.5), "Cloud2.png", 8.0, 0.2);
		CreateBackgroundObject(new Vector2D(-4.0, 2.0), 0.0, new Vector2D(-2.0, 2.0), "Cloud.png", 8.0, 0.3);
		CreateBackgroundObject(new Vector2D(8.0, 0.0), 0.0, new Vector2D(4.0, 4.0), "Cloud2.png", 8.0, 0.4);
		
		// Load Music/Ambient
		gameMusic = new AudioSource("sounds/music/Menu_Theme2.wav", true, 1.0, 1.0);
		gameMusic.Play();
		windSound = new AudioSource("Wind_Ambient.wav", true, 0.8, 1.0);
		windSound.Play();
	}

	// Per Frame Update
	public void GlobalUpdate(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager) {
		timeSinceGameStart = frameData.timeSinceGameStart;
		
		// Update Entities
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).Update(frameData, inputManager, mouseInputManager, this);
		}
		// Update Level Objects
		for (int i = 0; i < levelObjects.size(); i++) {
			levelObjects.get(i).Update(frameData, inputManager, mouseInputManager, this);
		}
		// Update Particle Systems
		for (int i = 0; i < particleSystems.size(); i++) {
			particleSystems.get(i).Update(frameData, inputManager, mouseInputManager, this);
		}
		
		//

		// Delete Entities on Delete List
		while (entitiesDeleteList.size() > 0) {
			ColliderObject co = entitiesDeleteList.remove(0);
			DeleteEntity(co);
		}
		// Delete Level Objects on Delete List
		while (levelObjectsDeleteList.size() > 0) {
			ColliderObject co = levelObjectsDeleteList.remove(0);
			DeleteLevelObject(co);
		}
		// Delete Particle Systems on Delete List
		while (particleSystemsDeleteList.size() > 0) {
			ParticleSystem ps = particleSystemsDeleteList.remove(0);
			DeleteParticleSystem(ps);
		}
		
		// Level Ending
		if (reachedDestination) {
			timeSinceGoal = frameData.timeSinceGameStart - destinationTimer;
		}
		// Close Application width Escape-Key
		if (inputManager.Escape()) {
			ExitGame();
		}
	}
	
	// Create Functions for new Game Objects
	public BackgroundObject CreateBackgroundObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String name, double ppm, // Sprite
			double parallaxe // Other
			) {
		
		BackgroundObject bo = new BackgroundObject(position, rotation, scale, "res/textures/" + name, ppm, parallaxe);
		backgroundObjects.add(bo);
		return bo;
	}
	
	public Coin CreateCoin(Vector2D position) {
		Coin co = new Coin(position, 1.0, 1.0);
		levelObjects.add(co);
		return co;
	}
	
	public Heart CreateHeart(Vector2D position) {
		Heart he = new Heart(position, 1.0, 1.0);
		levelObjects.add(he);
		return he;
	}
	
	public Rotor CreateRotor(Vector2D position, boolean flipXAxis) {
		double scaleX = 1.0;
		if (flipXAxis)
			scaleX = -1.0;
		
		Rotor ro = new Rotor(position, scaleX, 1.0);
		levelObjects.add(ro);
		return ro;
	}
	
	public RobotEnemy CreateRobotEnemy(Vector2D position, boolean flipXAxis) {
		double scaleX = 1.0;
		if (flipXAxis)
			scaleX = -1.0;
		
		RobotEnemy ro = new RobotEnemy(position, scaleX);
		levelObjects.add(ro);
		return ro;
	}
	
	public Checkpoint CreateCheckpoint(Vector2D position) {
		Checkpoint ch = new Checkpoint(position);
		levelObjects.add(ch);
		return ch;
	}
	
	// Add Functions for external created Game/Collider Objects
	public void AddEntity(ColliderObject co) {
		entities.add(co);
	}
	
	public void AddLevelObject(ColliderObject co) {
		levelObjects.add(co);
	}
	
	public void AddParticleSystem(ParticleSystem ps) {
		particleSystems.add(ps);
	}

	// Create Functions for regular Game Object (no use)
	/*public GameObject CreateDynamicGameObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String name, double ppm // Sprite
			) {
		
		GameObject go = new GameObject(
				position, rotation, scale,
				"res/textures/" + name, ppm,
				false, 1, 1,
				false, 0.0,
				"000"
				);
		dynamicGameObjects.add(go);
		return go;
	}*/

	/*public GameObject CreateDynamicGameObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String name, double ppm, // Sprite
			boolean isSpriteSheet, int tileCountX, int tileCountY // Sprite Sheet
			) {
		
		GameObject go = new GameObject(
				position, rotation, scale,
				"res/textures/" + name, ppm,
				isSpriteSheet, tileCountX, tileCountY,
				false, 0.0,
				"000"
				);
		dynamicGameObjects.add(go);
		return go;
	}*/

	/*public GameObject CreateDynamicGameObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String name, double ppm, // Sprite
			boolean isSpriteSheet, int tileCountX, int tileCountY, // Sprite Sheet
			boolean isSpriteAnimation, double animationSpeed // Sprite Animation
			) {
		
		GameObject go = new GameObject(
				position, rotation, scale,
				"res/textures/" + name, ppm,
				isSpriteSheet, tileCountX, tileCountY,
				isSpriteAnimation, animationSpeed,
				"000"
				);
		dynamicGameObjects.add(go);
		return go;
	}*/

	// Add Functions for Game/Collider Objects, which should be set on there Delete Lists
	public void AddEntityToDelete(ColliderObject co) {
		if (!entitiesDeleteList.contains(co)) {
			entitiesDeleteList.add(co);
		}
	}
	
	public void AddLevelObjectToDelete(ColliderObject co) {
		if (!levelObjectsDeleteList.contains(co)) {
			levelObjectsDeleteList.add(co);
		}
	}
	
	public void AddParticleSystemToDelete(ParticleSystem ps) {
		if (!particleSystemsDeleteList.contains(ps)) {
			particleSystemsDeleteList.add(ps);
		}
	}
	
	// Delete Functions
	private void DeleteEntity(ColliderObject co) {
		entities.remove(co);
		co = null;
	}
	
	private void DeleteLevelObject(ColliderObject co) {
		levelObjects.remove(co);
		co = null;
	}
	
	private void DeleteParticleSystem(ParticleSystem ps) {
		particleSystems.remove(ps);
		ps = null;
	}

	// Get Functions for Objects
	public List<BackgroundObject> GetBackgroundObjects() {
		return backgroundObjects;
	}

	public List<ColliderObject> GetEntities() {
		return entities;
	}
	
	public List<ColliderObject> GetLevelObjects() {
		return levelObjects;
	}
	
	public List<ParticleSystem> GetParticlesystems() {
		return particleSystems;
	}
	
	// Level End Functions
	public void ReachDestination() {
		reachedDestination = true;
		destinationTimer = timeSinceGameStart;
	}
	
	public void ExitGame() {
		gamePanel.isRunning = false;
	}
}
