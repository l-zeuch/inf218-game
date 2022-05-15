package coreEngine;

import java.io.FileNotFoundException;
import java.util.List;

import components.*;
import utility.*;

public class LevelData {
	
	// Path Names of the Sprite Sheets for the Tile Map
	private static String[] spriteSheetNames = new String[] {
			"tileMaps/Tile_Map_Wood_Planks.png",
			"tileMaps/Tile_Map_Iron.png",
			"tileMaps/Tile_Map_Grass_Rock.png",
			"tileMaps/Tile_Map_Bricks.png"
	};
	
	public String levelName;
	
	// Solid Tiles
	public byte[][] tileMapDataSolid;
	public byte[][] tileMapSpriteIndexSolid;
	// Background Tiles
	public byte[][] tileMapDataBackground;
	public byte[][] tileMapSpriteIndexBackground;
	
	// Sprite Sheets
	private Sprite[] spriteSheets;
	
	// Size of the Tile Map
 	private int tileMapWidth;
 	private int tileMapHeight;
 	
 	public int maxCoins;
	
 	// Create New Level Data
	public LevelData(int newWidth, int newHeight, List<ColliderObject> entities, List<ColliderObject> levelObjects, Player player) {
		// Load Tile Map Sprite Sheets
		spriteSheets = new Sprite[spriteSheetNames.length];
		for (int i = 0; i < spriteSheets.length; i++) {
			spriteSheets[i] = new Sprite("res/textures/" + spriteSheetNames[i], 16.0, true, 8, 8, false, 0.0);
		}
		
		// Create Tile Map Data
		tileMapWidth = newWidth;
    	tileMapHeight = newHeight;
    	
    	tileMapDataSolid = new byte[tileMapHeight][tileMapWidth];
    	tileMapSpriteIndexSolid = new byte[tileMapHeight][tileMapWidth];
    	tileMapDataBackground = new byte[tileMapHeight][tileMapWidth];
    	tileMapSpriteIndexBackground = new byte[tileMapHeight][tileMapWidth];
    	
    	tileMapDataSolid[0][0] = 50;
    	tileMapDataSolid[0][1] = 50;
    	tileMapDataSolid[0][2] = 50;
    	tileMapDataSolid[0][3] = 51;
    	
    	tileMapDataSolid[1][0] = 50;
    	tileMapDataSolid[1][1] = 50;
    	tileMapDataSolid[1][2] = 50;
    	tileMapDataSolid[1][3] = 51;
    	
    	tileMapDataSolid[2][0] = 42;
    	tileMapDataSolid[2][1] = 42;
    	tileMapDataSolid[2][2] = 42;
    	tileMapDataSolid[2][3] = 43;
    	
    	player.position.x = 3.0;
    	player.position.y = 3.5;
    	
    	maxCoins = 0;
	}
	
	// Load Existing Level Data
	public LevelData(String levelName, List<ColliderObject> entities, List<ColliderObject> levelObjects, Player player) throws FileNotFoundException {
		// Load Tile Map Sprite Sheets
		spriteSheets = new Sprite[spriteSheetNames.length];
		for (int i = 0; i < spriteSheets.length; i++) {
			spriteSheets[i] = new Sprite("res/textures/" + spriteSheetNames[i], 16.0, true, 8, 8, false, 0.0);
		}
		
		// Load Tile Map Data
		maxCoins = LevelFileManagement.LoadTileMap(levelName, this, entities, levelObjects, player);
	}
	
	// Resize Tile Map
	public void Resize(int newWidth, int newHeight) {
		// Create Temporary Arrays
		byte[][] tmpTileMapDataSolid = new byte[tileMapHeight][tileMapWidth];
		byte[][] tmpTileMapSpriteIndexSolid = new byte[tileMapHeight][tileMapWidth];
		byte[][] tmpTileMapDataBackground = new byte[tileMapHeight][tileMapWidth];
		byte[][] tmpTileMapSpriteIndexBackground = new byte[tileMapHeight][tileMapWidth];
		
		// Apply current Tile Map Data to temporary Arrays
		for (int y = 0; y < tileMapHeight; y++) {
			for (int x = 0; x < tileMapWidth; x++) {
				tmpTileMapDataSolid[y][x] = tileMapDataSolid[y][x];
				tmpTileMapSpriteIndexSolid[y][x] = tileMapSpriteIndexSolid[y][x];
				tmpTileMapDataBackground[y][x] = tileMapDataBackground[y][x];
				tmpTileMapSpriteIndexBackground[y][x] = tileMapSpriteIndexBackground[y][x];
			}
		}
		
		// Create new Arrays for Tile Map Data
		tileMapDataSolid = new byte[newHeight][newWidth];
		tileMapSpriteIndexSolid = new byte[newHeight][newWidth];
		tileMapDataBackground = new byte[newHeight][newWidth];
		tileMapSpriteIndexBackground = new byte[newHeight][newWidth];
		
		// Apply Tile Map Data from temporary Arrays to new Tile Map Data
		for (int y = 0; y < newHeight; y++) {
			for (int x = 0; x < newWidth; x++) {
				if (x < tileMapWidth && y < tileMapHeight) {
					tileMapDataSolid[y][x] = tmpTileMapDataSolid[y][x];
					tileMapSpriteIndexSolid[y][x] = tmpTileMapSpriteIndexSolid[y][x];
					tileMapDataBackground[y][x] = tmpTileMapDataBackground[y][x];
					tileMapSpriteIndexBackground[y][x] = tmpTileMapSpriteIndexBackground[y][x];
				}
				else {
					tileMapDataSolid[y][x] = 0;
					tileMapSpriteIndexSolid[y][x] = 0;
					tileMapDataBackground[y][x] = 0;
					tileMapSpriteIndexBackground[y][x] = 0;
				}
			}
		}
		
		// Apply new Dimensions
		tileMapWidth = newWidth;
		tileMapHeight = newHeight;
	}
	
	public void SetTileMapWidth(int width) {
		tileMapWidth = width;
	}
	
	public void SetTileMapHeight(int height) {
		tileMapHeight = height;
	}
	
	public int GetTileMapWidth() {
		return tileMapWidth;
	}
	
	public int GetTileMapHeight() {
		return tileMapHeight;
	}
	
	public Vector2D GetLeftBottomCorner() {
		return new Vector2D(0.0, 0.0);
	}
	
	public Vector2D GetRightTopCorner() {
		return new Vector2D((double)tileMapWidth, (double)tileMapHeight);
	}
	
	public Sprite[] GetSpriteSheets() {
		return spriteSheets;
	}
	
	public int GetSpriteSheetCount() {
		return spriteSheetNames.length;
	}
}
