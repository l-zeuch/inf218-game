package coreEngine;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.List;

import components.*;
import utility.*;

public class EditorManager {
	
	private RenderCamera renderCamera;
	private int zoom = 0;
	
	public LevelData levelData;
	public List<ColliderObject> entities;
	public List<ColliderObject> levelObjects;
	
	// Mouse position in Tiles
	public int mousePositionX = 0;
	public int mousePositionY = 0;
	// Mouse position in World
	public Vector2D entityMousePosition = new Vector2D(0.0, 0.0);
	
	public byte selectedSpriteSheet = 0;
	public byte selectedBlock = 1; // Selected Tile/Block or Entity
	
	public int editMode = 0; // 0 = Paint, 1 = Replace
	public boolean isBackgroundSelected = false;
	
	public boolean isOverResizeButton = false;
	public boolean isResizeButtonPressed = false;
	
	public boolean isOverSaveButton = false;
	public boolean isSaveButtonPressed = false;
	
	public boolean isOverChangeSaveLocation = false;
	public boolean isChangeSaveLocationPressed = false;
	
	public boolean isEntitySelected = false;
	public double entityGridSize = 0.25;
	public double entityXAxis = 1.0;
	public double entityYAxis = 1.0;
	
	public boolean isOnUI = false;
	
	public String saveLocation;
	
	private Player player;
	
	public EditorManager(RenderCamera renderCamera) {
		entities = new ArrayList<>();
		levelObjects = new ArrayList<>();
		
		// Player
		player = new Player(new Vector2D(0.0, 0.0));
		entities.add(player);
		
		this.renderCamera = renderCamera;
		
		// Get Level
		boolean validInput = true; // Stays in the loop to the Input from the User is valid (similar Structure in the Game Manager)
		do {
			// New Level or Open
			int input = JOptionPane.showOptionDialog(null, "File:", "Start Tile Map Editor",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, Application.icon,
					new String[] { "New Level", "Open Level" }, "Open Level");
			if (input >= 0) {
				validInput = true;
				
				if (input == 0) {
					// Create New Level
					do {
						String newLevelName = JOptionPane.showInputDialog(null, "Enter New Level Name (without .txt)", "New Level Name", JOptionPane.QUESTION_MESSAGE);
						saveLocation = newLevelName + ".txt";
						
						if (newLevelName != null) {
							validInput = true;
							
							levelData = new LevelData(50, 20, entities, levelObjects, player);
						}
						else {
							validInput = false;
						}
					}
					while (!validInput);
				}
				else {
					// Load existing Level
					do {
						String levelName = LevelFileManagement.SelectALevel();
						saveLocation = levelName;
						
						if (levelName != null) {
							validInput = true;
							
							// Get Level Data
							try {
								levelData = new LevelData(saveLocation, entities, levelObjects, player);
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
				}
			}
			else {
				validInput = false;
			}
		}
		while (!validInput);
		
		// Set Camera Position
		renderCamera.position.x = player.position.x;
		renderCamera.position.y = player.position.y;
	}
	
	public void UpdateEditorManager(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager) {
		// Get Sprite Sheet Selection Input by Number Keys
		if (inputManager.NumberClicked()) {
			selectedSpriteSheet = (byte)UtilityFunctions.Clamp(inputManager.GetNumber() - 1, 0, 3);
		}
		
		// Get Zoom Input
		if (inputManager.PlusClicked() && zoom < 1) {
			// Zoom in
			renderCamera.cameraWidth *= 0.5;
			zoom++;
		}
		if (inputManager.MinusClicked() && zoom > -2) {
			// Zoom out
			renderCamera.cameraWidth *= 2.0;
			zoom--;
		}
		
		// Get Entity Axis Flip/Mirror Input
		if (isEntitySelected) {
			if (inputManager.XClicked()) {
				entityXAxis *= -1.0;
			}
			if (inputManager.YClicked()) {
				entityYAxis *= -1.0;
			}
		}
		
		// Reset Over Buttons Configuration
		isOverChangeSaveLocation = false;
		isOverSaveButton = false;
		isOverResizeButton = false;
		
		// Check if Mouse Cursor is over UI
		if (mouseInputManager.GetMousePosition().x <= 256.0) {
			isOnUI = true;
		}
		else {
			isOnUI = false;
		}
		
		// Do if is on UI
		if (isOnUI) {
			mousePositionX = (int)(mouseInputManager.GetMousePosition().x / 32.0);
			mousePositionY = (int)(mouseInputManager.GetMousePosition().y / 32.0);
			
			// If is Cursor on Tile Map Selection
			if (mousePositionX >= 0 && mousePositionX < 8 && mousePositionY >= 0 && mousePositionY < 8) {
				if (mouseInputManager.LeftMouseButtonClicked()) {
					// Set Selected Tile/Block
					selectedBlock = (byte)((mousePositionX + mousePositionY * 8) + 1);
					isEntitySelected = false;
				}
			}
			// If is Cursor on Tile Map Selection
			else if (mousePositionY == 8) {
				if (mouseInputManager.LeftMouseButtonClicked()) {
					if (mousePositionX >= 0 && mousePositionX < levelData.GetSpriteSheetCount()) {
						// Set Selected Sprite Sheet
						selectedSpriteSheet = (byte)mousePositionX;
					}
				}
			}
			// If is Cursor on Edit Mode Buttons
			else if (mousePositionY == 9) {
				// Set Edit Mode
				if (mousePositionX >= 0 && mousePositionX < 4) {
					if (mouseInputManager.LeftMouseButtonClicked()) {
						editMode = 0;
					}
				}
				else {
					if (mouseInputManager.LeftMouseButtonClicked()) {
						editMode = 1;
					}
				}
			}
			// If is Cursor on Layer Buttons
			else if (mousePositionY == 10) {
				// Set Selected Layer
				if (mousePositionX >= 0 && mousePositionX < 4) {
					if (mouseInputManager.LeftMouseButtonClicked()) {
						isBackgroundSelected = false;
					}
				}
				else {
					if (mouseInputManager.LeftMouseButtonClicked()) {
						isBackgroundSelected = true;
					}
				}
			}
			// If is Cursor on Resize Button
			else if (mousePositionY == 11) {
				isOverResizeButton = true;
				if (mouseInputManager.LeftMouseButtonClicked()) {
					isResizeButtonPressed = true;
					
					// Get old Level Height for editing
					int newWidth = levelData.GetTileMapWidth();
					int newHeight = levelData.GetTileMapHeight();
					
					boolean stopResize = false;
					
					// Get new Level Dimensions
					boolean validInput = true;
					do {
						// Get new Width
						String input = JOptionPane.showInputDialog(null, "Current Width: " + levelData.GetTileMapWidth() + " New Width:", "Width", JOptionPane.QUESTION_MESSAGE);

						if (input != null) {
							try {
								validInput = true;
								newWidth = Integer.parseInt(input);
								newWidth = UtilityFunctions.Clamp(newWidth, 0, 9999);
							}
							catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
								validInput = false;
							}
						}
						else {
							stopResize = true;
						}
					}
					while (!validInput && !stopResize);
					
					if (!stopResize) {
						do {
							// Get new Height
							String input = JOptionPane.showInputDialog(null, "Current Height: " + levelData.GetTileMapHeight() + " New Height:", "Height", JOptionPane.QUESTION_MESSAGE);

							if (input != null) {
								try {
									validInput = true;
									newHeight = Integer.parseInt(input);
									newHeight = UtilityFunctions.Clamp(newHeight, 0, 9999);
								}
								catch (Exception e) {
									JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
									validInput = false;
								}
							}
							else {
								stopResize = true;
							}
						}
						while (!validInput && !stopResize);
					}
					
					// If Resizing wasen't stopped, resize Level
					if (!stopResize) {
						levelData.Resize(newWidth, newHeight);
						
						List<ColliderObject> deleteObjects = new ArrayList<>();
						
						// Collect Entities which are out of range
						for (int i = 0; i < entities.size(); i++) {
							ColliderObject co = entities.get(i);
							
							// If Entity is out of Bounds
							if (co.position.x > (double)levelData.GetTileMapWidth() - 0.5 || co.position.y > (double)levelData.GetTileMapHeight() - 0.5) {
								if (co != (ColliderObject)player) {
									// Add Entity to Delete List
									deleteObjects.add(co);
								}
								else {
									// Clamp Player Position
									player.position.x = UtilityFunctions.Clamp(player.position.x, -0.5, (double)levelData.GetTileMapWidth() - 0.5);
									player.position.y = UtilityFunctions.Clamp(player.position.y, -0.5, (double)levelData.GetTileMapHeight() - 0.5);
								}
							}
						}
						
						// Remove Entities on Delete List
						while (deleteObjects.size() > 0) {
							ColliderObject co = deleteObjects.remove(0);
							entities.remove(co);
							co = null;
						}
						
						// Collect Level Objects which are out of range
						for (int i = 0; i < levelObjects.size(); i++) {
							ColliderObject co = levelObjects.get(i);
							
							// If Level Object is out of Bounds
							if (co.position.x > (double)levelData.GetTileMapWidth() - 0.5 || co.position.y > (double)levelData.GetTileMapHeight() - 0.5) {
								// Add Level Object to Delete List
								deleteObjects.add(co);
							}
						}
						
						// Remove Level Objects
						while (deleteObjects.size() > 0) {
							ColliderObject co = deleteObjects.remove(0);
							levelObjects.remove(co);
							co = null;
						}
					}
					
					mouseInputManager.ResetMouse();
				}
			}
			// If is Cursor over Entities Field
			else if (mousePositionY > 11 && mousePositionY < 19) {
				if (mouseInputManager.LeftMouseButtonClicked()) {
					// Set Selected Entity
					isEntitySelected = true;
					entityXAxis = 1.0;
					entityYAxis = 1.0;
					
					selectedBlock = (byte)((mousePositionX + (mousePositionY - 12) * 8));
				}
			}
			// If is Cursor over Save Button
			else if (mousePositionY == 19) {
				isOverSaveButton = true;
				if (mouseInputManager.LeftMouseButtonClicked()) {
					isSaveButtonPressed = true;
					// Save Level
					try {
						// Save Tile Map Data
						LevelFileManagement.SaveTileMap(saveLocation, saveLocation.substring(0, saveLocation.length() - 4), levelData, entities, levelObjects);
						
						JOptionPane.showMessageDialog(null, "Level File was successfully saved!", "Level Saved", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mouseInputManager.ResetMouse();
				}
			}
			// If is Cursor over Change Save location
			else if (mousePositionY == 20) {
				isOverChangeSaveLocation = true;
				if (mouseInputManager.LeftMouseButtonClicked()) {
					isChangeSaveLocationPressed = true;
					
					// Change Save Location
					String input = JOptionPane.showInputDialog(null, "Enter New Level Name (without .txt)", "New Level Name", JOptionPane.QUESTION_MESSAGE);
					
					if (input != null) {
						saveLocation = input + ".txt";
					}
					
					mouseInputManager.ResetMouse();
				}
			}
		}
		// Do if is in World
		else {
			isOverSaveButton = false;
			isOverChangeSaveLocation = false;
			isOverResizeButton = false;
			
			// Get Mouse Position in Tile Map
			mousePositionX = (int)Math.round((mouseInputManager.GetWorldMousePosition().x));
			mousePositionY = (int)Math.round((mouseInputManager.GetWorldMousePosition().y));
			
			// Apply Grid Size for Entity Placement (1/4 Tile)
			entityMousePosition.x = Math.round(mouseInputManager.GetWorldMousePosition().x / entityGridSize) * entityGridSize;
			entityMousePosition.y = Math.round(mouseInputManager.GetWorldMousePosition().y / entityGridSize) * entityGridSize;
			
			// Is Mouse Cursor on Tile Map (Inside Bounds)
			if (mousePositionX >= 0 && mousePositionX < levelData.GetTileMapWidth() && mousePositionY >= 0 && mousePositionY < levelData.GetTileMapHeight()) {
				if (!isEntitySelected) { // Tile/Block Editing
					// Set Block
					if (mouseInputManager.LeftMouseButton()) {
						if (!isBackgroundSelected) {
							// Set Solid Block
							levelData.tileMapSpriteIndexSolid[mousePositionY][mousePositionX] = selectedSpriteSheet;
							if (editMode == 0) {
								levelData.tileMapDataSolid[mousePositionY][mousePositionX] = selectedBlock;
							}
						}
						else {
							// Set Background Block
							levelData.tileMapSpriteIndexBackground[mousePositionY][mousePositionX] = selectedSpriteSheet;
							if (editMode == 0) {
								levelData.tileMapDataBackground[mousePositionY][mousePositionX] = selectedBlock;
							}
						}
					}
					else if (mouseInputManager.RightMouseButton()) {
						// Erase Block
						if (!isBackgroundSelected) {
							levelData.tileMapDataSolid[mousePositionY][mousePositionX] = 0;
						}
						else {
							levelData.tileMapDataBackground[mousePositionY][mousePositionX] = 0;
						}
					}
				}
				else { // Entity / Level Object Editing
					// Place Entity / Level Object
					if (mouseInputManager.LeftMouseButtonClicked()) {
						// Switch to selected Entity and add to corresponding List
						switch (selectedBlock) {
							case 0:
								player.position.x = entityMousePosition.x;
								player.position.y = entityMousePosition.y;
								break;	
							case 1:
								levelObjects.add(new Coin(new Vector2D(entityMousePosition.x, entityMousePosition.y), entityXAxis, entityYAxis));
								break;
							case 2:
								levelObjects.add(new Heart(new Vector2D(entityMousePosition.x, entityMousePosition.y), entityXAxis, entityYAxis));
								break;
							case 3:
								levelObjects.add(new Rotor(new Vector2D(entityMousePosition.x, entityMousePosition.y), entityXAxis, entityYAxis));
								break;
							case 4:
								entities.add(new RobotEnemy(new Vector2D(entityMousePosition.x, entityMousePosition.y), entityXAxis));
								break;
							case 5:
								levelObjects.add(new Checkpoint(new Vector2D(entityMousePosition.x, entityMousePosition.y)));
								break;
							case 6:
								levelObjects.add(new Goal(new Vector2D(entityMousePosition.x, entityMousePosition.y)));
								break;
						}
					}
					
					// Remove Entity / Level Object
					if (mouseInputManager.RightMouseButtonClicked()) {
						for (int i = 0; i < entities.size(); i++) {
							ColliderObject co = entities.get(i);
							
							if (co != (ColliderObject)player) {
								// Check Cursor Intersection
								if (UtilityFunctions.BoxIntersection(co.position, co.GetColliderSize(), entityMousePosition, new Vector2D(0.01, 0.01))) {
									// Delete
									entities.remove(co);
									co = null;
									break;
								}
							}
						}
						
						for (int i = 0; i < levelObjects.size(); i++) {
							ColliderObject co = levelObjects.get(i);
							
							if (co != (ColliderObject)player) {
								// Check Cursor Intersection
								if (UtilityFunctions.BoxIntersection(co.position, co.GetColliderSize(), entityMousePosition, new Vector2D(0.01, 0.01))) {
									// Delete
									levelObjects.remove(co);
									co = null;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		// Post Button Check
		if (!mouseInputManager.LeftMouseButton()) {
			isSaveButtonPressed = false;
			isChangeSaveLocationPressed = false;
			isResizeButtonPressed = false;
		}
	}
}
