package coreEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import components.*;
import utility.*;

public class GamePanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 1L;

	// Is Editor?
	private final boolean isEditor;
	
	// Window and Game Parameter
	private final int screenWidth;
	private final int screenHeight;
	private final int frameDelay;
	private long gameStartTimeInMilliseconds;
	
	// Game Basis
	private Application application;
	private Thread gameThread;
	
	// Logic Manager
	private GameManager gameManager;
	private EditorManager editorManager;
	
	// Other Basic Modules
	private InputManager inputManager;
	private MouseInputManager mouseInputManager;
	private RenderCamera renderCamera;
	private FrameData frameData;
	private RenderEngine renderEngine;
	
	// UI Images
	private Sprite cursor;
	private Sprite vignette;
	private Sprite editorLayer;
	private Sprite appleHeart;
	private Sprite levelCompleteSprite;
	
	// Should Game still Running?
	public boolean isRunning = true;
	
	public GamePanel(int frameRate, int windowWidth, int windowHeight, boolean isEditor, Application application) {
		this.application = application;
		
		this.isEditor = isEditor;
		frameDelay = 1000 / frameRate;

		screenWidth = windowWidth;
		screenHeight = windowHeight;
		
		InitializeGamePanel();
	}
	
	// Initialize Game Panel (JFrame Component)
	private void InitializeGamePanel() {
		// Set Screen Parameters
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setBackground(Color.black);
		setFocusable(true);
		
		// Initialize Basic Modules
		renderCamera = new RenderCamera(new Vector2D(0.0, 0.0), 1280.0 / (64.0 * 0.75), screenWidth, screenHeight);
		if (isEditor) {
			editorManager = new EditorManager(renderCamera);
		}
		else {
			gameManager = new GameManager(renderCamera, this);
		}
		inputManager = new InputManager();
		addKeyListener(inputManager);
		mouseInputManager = new MouseInputManager();
		addMouseListener(mouseInputManager);
		frameData = new FrameData();
		renderEngine = new RenderEngine(renderCamera);
		
		// Initialize Images(Sprites)
		cursor = new Sprite("res/textures/Cursor.png", 1.0, false, 1, 1, false, 0.0);
		vignette = new Sprite("res/textures/Vignette.png", 1.0, false, 1, 1, false, 0.0);
		editorLayer = new Sprite("res/textures/BlackSemi.png", 1.0, false, 1, 1, false, 0.0);
		appleHeart = new Sprite("res/textures/Apple_Heart.png", 1.0, false, 1, 1, false, 0.0);
		levelCompleteSprite = new Sprite("res/textures/Level_Complete.png", 1.0, false, 1, 1, false, 0.0);
	}
	
	// Update Game State (executed per Frame)
	private void Update(long deltaTimeInMilliseconds, long currentSystemTimeInMilliseconds) {
		// Update Basic Modules
		frameData.UpdateFrameData(deltaTimeInMilliseconds, currentSystemTimeInMilliseconds, gameStartTimeInMilliseconds);
		inputManager.InputUpdate();
		Point mousePos = this.getMousePosition(); // Gets Mouse Position on JPanel
		if (mousePos != null) {
			mouseInputManager.SetMousePosition(mousePos, renderCamera);
		}

		if (!isEditor) {
			// Update Game Manager and Render Camera
			gameManager.GlobalUpdate(frameData, inputManager, mouseInputManager);
			
			renderCamera.CameraUpdate(frameData, inputManager, mouseInputManager, gameManager.levelData, gameManager.player);
			
			// Decrease Music Volume and Exit Game if Game is over
			double volume = UtilityFunctions.Clamp(2.0 - gameManager.timeSinceGoal, 0.0, 1.0);
			gameManager.gameMusic.SetVolumeWidthStartVolume(volume);
			gameManager.windSound.SetVolumeWidthStartVolume(volume);
			if (gameManager.reachedDestination && gameManager.timeSinceGoal >= 3.0) {
				// Exit Game
				isRunning = false;
			}
		}
		else {
			// Update Editor Manager and Render Camera
			editorManager.UpdateEditorManager(frameData, inputManager, mouseInputManager);
			
			renderCamera.EditorUpdate(frameData, inputManager, mouseInputManager, editorManager.levelData);
		}
		
		// After all Modules are updated, Post-Updates are called
		inputManager.PostInputUpdate();
		mouseInputManager.PostMouseInputUpdate();
	}

	// Update Graphics (executed per Frame, after "Update")
	private void DrawGraphics(Graphics g, ImageObserver o) {
		Graphics2D g2d = (Graphics2D) g;
		// Smoothes Edges of Text
	    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
	    // Update Parameter of the Render Engine
		renderEngine.RenderEngineUpdate();
		
		if (!isEditor) {
			// Draw:...
			// Background
			renderEngine.RenderBackgroundObjects(gameManager.GetBackgroundObjects(), g2d, o);
			
			// Tile Map Parallax Behind
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.976, true);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.976, false);
			
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.982, true);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.982, false);
			
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.988, false);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 0.994, false);
			// Tile Map Normal
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 1.0, false);
			
			// Entities
			renderEngine.RenderColliderObjects(gameManager.GetEntities(), false, g2d, o);
			// Level Objects
			renderEngine.RenderColliderObjects(gameManager.GetLevelObjects(), false, g2d, o);
			// Particle Systems
			renderEngine.RenderParticleSystems(gameManager.GetParticlesystems(), g2d, o);
			
			// Tile Map Parallax In Front
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 1.006, false);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 1.012, false);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 1.018, false);
			renderEngine.RenderTileMap(gameManager.levelData, g2d, o, 1.024, false);
			
			// Cursor
			renderEngine.RenderSprite(cursor, (int)mouseInputManager.GetMousePosition().x, (int)mouseInputManager.GetMousePosition().y, g2d);
			
			// Vignette
			renderEngine.RenderFullScreenImage(vignette, g2d);
			
			// UI (Coins, Health, etc...)
			renderEngine.RenderUI(g2d, gameManager.player, appleHeart, gameManager.maxCoins);
			
			// "Level Complete"-Text
			if (gameManager.reachedDestination) {
				double s = Math.sin(gameManager.timeSinceGoal);
				if (gameManager.timeSinceGoal > Math.PI * 0.5) {
					s = 1.0;
				}
				renderEngine.RenderFullScreenImage(levelCompleteSprite, s, s, true, g2d);
			}
		}
		else {
			// Draw:...
			// Tile Map
			renderEngine.RenderTileMap(editorManager.levelData, g2d, o, 1.0, true);
			renderEngine.RenderFullScreenImage(editorLayer, g2d);
			renderEngine.RenderTileMap(editorManager.levelData, g2d, o, 1.0, false);
			
			// Collider Objects
			renderEngine.RenderColliderObjects(editorManager.entities, true, g2d, o);
			renderEngine.RenderColliderObjects(editorManager.levelObjects, true, g2d, o);
			
			// Editor UI/Layout
			renderEngine.RenderEditorLayout(editorManager, g2d);
		}
		
		// In Sync with Game State
		Toolkit.getDefaultToolkit().sync();
	}

	// Executed after JPanel has been added to JFrame, creates Game Thread
	@Override
	public void addNotify() {
		super.addNotify();

		gameThread = new Thread(this);
		gameThread.start();
	}

	// Paints the JPanel
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		DrawGraphics(g, this);
	}

	// Only executed once (at the Start of the Threat) -> while-loop for Updating
	@Override
	public void run() {
		gameStartTimeInMilliseconds = System.currentTimeMillis();

		long timeSinceLastUpdate = System.currentTimeMillis(); // Time since Update was called
		long deltaTimeInMilliseconds; // Time for on loop Cycle

		long timeBeforeUpdating; // Time before "Update" and "repaint" are called
		long timeDifference; // Time of the Update and repaint function
		long sleepTime; // Time, which must be slept to reach the frame rate

		while (isRunning) {
			timeBeforeUpdating = System.currentTimeMillis();

			deltaTimeInMilliseconds = System.currentTimeMillis() - timeSinceLastUpdate;
			timeSinceLastUpdate = System.currentTimeMillis();
			
			// Update Game State and Graphics
			Update(deltaTimeInMilliseconds, System.currentTimeMillis());
			repaint(); // Calls "paintComponent" (was overwritten above)

			timeDifference = System.currentTimeMillis() - timeBeforeUpdating;
			sleepTime = frameDelay - timeDifference; // Computes waiting Time of the Threat to reach the target Frame Rate

			if (sleepTime < 0) {
				sleepTime = 2;
			}

			try {
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e) {
				String msg = String.format("Thread interrupted: %s", e.getMessage());

				JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		System.out.println("Exit Game");
		// Close Window (Stop Game)
		application.dispatchEvent(new WindowEvent(application, WindowEvent.WINDOW_CLOSING));
	}
}
