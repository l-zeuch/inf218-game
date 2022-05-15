package coreEngine;

import components.*;
import utility.*;

public class PhysicEngine {
	
	// Solve Parameters
	private final int solveIterations = 4;
	private final double figureEdgeOffset = 0.2;
	
	private LevelData levelData;
	
	public PhysicEngine(LevelData levelData) {
		this.levelData = levelData;
	}
	
	// Move a Figure by this Physic Engine
	public FigureCollisionData MoveFigureObject(Vector2D position, Vector2D velocity, double deltaTime, Vector2D colliderSize) {
		// Reset State Data
		boolean isGrounded = false;
		boolean leftCliff = false;
		boolean rightCliff = false;
		boolean hitWall = false;
		
		// Length of one Physic Iteration
		double stepLength = 1.0 / (double)solveIterations;
		
		Vector2D newPosition = new Vector2D(0.0, 0.0);
		
		// Iterate through the Physic Iterations (multiple Iterations for better Collision Checks)
		for (int i = 0; i < solveIterations; i++) {
			// Compute new Position by Velocity
			newPosition = new Vector2D(position.x + velocity.x * deltaTime * stepLength, position.y + velocity.y * deltaTime * stepLength);
			
			// Tile Bounds which lie in the Collider of the Figure
			int fromTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x - colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
			int fromTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y - colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
			int toTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x + colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
			int toTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y + colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
			
			// Cliff Test (if is left or right under the Figure Air)
			int cliffY = UtilityFunctions.Clamp((int)Math.round(newPosition.y - colliderSize.y * 0.5 - 0.1), 0, levelData.GetTileMapHeight() - 1);
			int leftCliffX = Math.max((int)Math.round(newPosition.x - colliderSize.x * 0.5 + 0.1), 0);
			int rightCliffX = Math.min((int)Math.round(newPosition.x + colliderSize.x * 0.5 - 0.1), levelData.GetTileMapWidth() - 1);
			if (levelData.tileMapDataSolid[cliffY][leftCliffX] == 0) {
				leftCliff = true;
			}
			if (levelData.tileMapDataSolid[cliffY][rightCliffX] == 0) {
				rightCliff = true;
			}
			
			// Go through all Tiles which are possible Intersecting the Figure
			for (int y = fromTileY; y <= toTileY; y++) {
				for (int x = fromTileX; x <= toTileX; x++) {
					// If Tile is not empty
					if (levelData.tileMapDataSolid[y][x] != 0) {
						// Compute World Position of the current Tile
						Vector2D tilePosition = new Vector2D((double)x, (double)y);
						
						double v = 0.0; // Interpolation Variable
						
						// Check Tile Intersections from different Directions and solve them (Position Adjustment)
						
						// Right
						switch (rightWallData[levelData.tileMapDataSolid[y][x]]) {
							case 1:
								if (newPosition.x + colliderSize.x * 0.5 > tilePosition.x - 0.5 &&
										newPosition.x < tilePosition.x &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = tilePosition.x - 0.5 - (newPosition.x + colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 47:
								v = UtilityFunctions.Clamp(1 + (newPosition.y + colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x + colliderSize.x * 0.5 >= UtilityFunctions.Lerp(tilePosition.x + 0.5, tilePosition.x + 0.0, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x + 0.5, tilePosition.x + 0.0, v) - (newPosition.x + colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 39:
								v = UtilityFunctions.Clamp(1 + (newPosition.y + colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x + colliderSize.x * 0.5 >= UtilityFunctions.Lerp(tilePosition.x - 0.0, tilePosition.x - 0.5, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x - 0.0, tilePosition.x - 0.5, v) - (newPosition.x + colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							//
							case 55:
								v = UtilityFunctions.Clamp(1 + (newPosition.y - colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x + colliderSize.x * 0.5 >= UtilityFunctions.Lerp(tilePosition.x + 0.0, tilePosition.x + 0.5, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x + 0.0, tilePosition.x + 0.5, v) - (newPosition.x + colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 63:
								v = UtilityFunctions.Clamp(1 + (newPosition.y - colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x + colliderSize.x * 0.5 >= UtilityFunctions.Lerp(tilePosition.x - 0.5, tilePosition.x - 0.0, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x - 0.5, tilePosition.x - 0.0, v) - (newPosition.x + colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
						}
						
						// Left
						switch (leftWallData[levelData.tileMapDataSolid[y][x]]) {
							case 1:
								if (newPosition.x - colliderSize.x * 0.5 < tilePosition.x + 0.5 &&
										newPosition.x > tilePosition.x &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = tilePosition.x + 0.5 - (newPosition.x - colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 46:
								v = UtilityFunctions.Clamp(1 + (newPosition.y + colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x - colliderSize.x * 0.5 <= UtilityFunctions.Lerp(tilePosition.x - 0.5, tilePosition.x - 0.0, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x - 0.5, tilePosition.x - 0.0, v) - (newPosition.x - colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 38:
								v = UtilityFunctions.Clamp(1 + (newPosition.y + colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x - colliderSize.x * 0.5 <= UtilityFunctions.Lerp(tilePosition.x + 0.0, tilePosition.x + 0.5, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x + 0.0, tilePosition.x + 0.5, v) - (newPosition.x - colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							//
							case 54:
								v = UtilityFunctions.Clamp(1 + (newPosition.y - colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x - colliderSize.x * 0.5 <= UtilityFunctions.Lerp(tilePosition.x - 0.0, tilePosition.x - 0.5, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x - 0.0, tilePosition.x - 0.5, v) - (newPosition.x - colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
							case 62:
								v = UtilityFunctions.Clamp(1 + (newPosition.y - colliderSize.y * 0.5 - tilePosition.y - 0.5), 0, 1);
								if (newPosition.x - colliderSize.x * 0.5 <= UtilityFunctions.Lerp(tilePosition.x + 0.5, tilePosition.x + 0.0, v) &&
										newPosition.y - colliderSize.y * 0.5 + figureEdgeOffset < tilePosition.y + 0.5 &&
										newPosition.y + colliderSize.y * 0.5 - figureEdgeOffset > tilePosition.y - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.x + 0.5, tilePosition.x + 0.0, v) - (newPosition.x - colliderSize.x * 0.5);
									newPosition.x += diff;
									velocity.x = 0.0;
									hitWall = true;
								}
								break;
						}
						
						// Up
						switch (upWallData[levelData.tileMapDataSolid[y][x]]) {
							case 1:
								if (newPosition.y + colliderSize.y * 0.5 >= tilePosition.y - 0.5 &&
										newPosition.y < tilePosition.y &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = tilePosition.y - 0.5 - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									velocity.y = 0.0;
								}
								break;
								//
							case 26:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y + 0.0, tilePosition.y + 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.0, tilePosition.y + 0.5, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
							case 25:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y - 0.0, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y - 0.0, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
							//
							case 27:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y + 0.0, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y + 0.0, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
							case 28:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y - 0.0, tilePosition.y - 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.0, tilePosition.y - 0.5, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
							//
							case 29:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.5, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
							case 37:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y + colliderSize.y * 0.5 >= UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.5, v) - (newPosition.y + colliderSize.y * 0.5);
									newPosition.y += diff;
									if (velocity.y > 0.0)
										velocity.y = 0.0;
								}
								break;
						}
						
						// Down
						switch (downWallData[levelData.tileMapDataSolid[y][x]]) {
							case 1:
								if (newPosition.y - colliderSize.y * 0.5 <= tilePosition.y + 0.5 &&
										newPosition.y > tilePosition.y &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = tilePosition.y + 0.5 - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							//
							case 34:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y + 0.0, tilePosition.y - 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.0, tilePosition.y - 0.5, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							case 33:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.0, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.0, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							//
							case 35:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.0, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.0, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							case 36:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y - 0.0, tilePosition.y + 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.0, tilePosition.y + 0.5, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							//
							case 44:
								v = UtilityFunctions.Clamp(1 + (newPosition.x - colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y + 0.5, tilePosition.y - 0.5, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
							case 45:
								v = UtilityFunctions.Clamp(1 + (newPosition.x + colliderSize.x * 0.5 - tilePosition.x - 0.5), 0, 1);
								if (newPosition.y - colliderSize.y * 0.5 <= UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.5, v) &&
										newPosition.x - colliderSize.x * 0.5 + figureEdgeOffset < tilePosition.x + 0.5 &&
										newPosition.x + colliderSize.x * 0.5 - figureEdgeOffset > tilePosition.x - 0.5) {
									double diff = UtilityFunctions.Lerp(tilePosition.y - 0.5, tilePosition.y + 0.5, v) - (newPosition.y - colliderSize.y * 0.5);
									newPosition.y += diff;
									isGrounded = true;
									velocity.y = 0.0;
								}
								break;
						}
					}
				}
			}
			
			// Apply new Position
			position = newPosition;
		}
		
		// Ladder Check
		boolean isInLadder = false;
		boolean breakLoop = false;
		
		// Tile Bounds which lie in the Collider of the Figure
		int fromTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x - colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
		int fromTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y - colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
		int toTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x + colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
		int toTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y + colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
		
		// Go through all Background Tiles which are possible Intersecting the Figure
		for (int y = fromTileY; y <= toTileY; y++) {
			for (int x = fromTileX; x <= toTileX; x++) {
				// If current Tile is Ladder
				if (levelData.tileMapDataBackground[y][x] == 21 || levelData.tileMapDataBackground[y][x] == 13) {
					Vector2D tilePosition = new Vector2D((double)x, (double)y);
					
					// Check for Intersection
					if (UtilityFunctions.BoxIntersection(newPosition, colliderSize, tilePosition, new Vector2D(1.0, 1.0))) {
						isInLadder = true;
						breakLoop = true;
						break;
					}
				}
			}
			if (breakLoop)
				break;
		}
		
		return new FigureCollisionData(newPosition, velocity, isGrounded, leftCliff, rightCliff, hitWall, isInLadder, hitWall);
	}
	
	// Right Look Up Table for Tiles
	private final int[] rightWallData = new int[] {
		0,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 26, 27, 28, 1, 1, 1, 1,
		
		1, 34, 35, 36, 37, 1, 39, 1,
		1, 1, 1, 1, 45, 1, 47, 1,
		1, 1, 1, 1, 1, 1, 55, 1,
		1, 1, 1, 1, 1, 1, 63, 1,
	};
	
	// Left Look Up Table for Tiles
	private final int[] leftWallData = new int[] {
		0,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		25, 26, 27, 1, 29, 1, 1, 1,
		
		33, 34, 35, 1, 1, 38, 1, 1,
		1, 1, 1, 44, 1, 46, 1, 1,
		1, 1, 1, 1, 1, 54, 1, 1,
		1, 1, 1, 1, 1, 62, 1, 1,
	};
	
	// Up Look Up Table for Tiles
	private final int[] upWallData = new int[] {
		0,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		25, 26, 27, 28, 29, 1, 1, 1,
		
		1, 1, 1, 1, 37, 38, 39, 1,
		1, 1, 1, 1, 1, 46, 47, 1,
		1, 1, 1, 1, 1, 54, 55, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
	};
	
	// Down Look Up Table for Tiles
	private final int[] downWallData = new int[] {
		0,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1,
		
		33, 34, 35, 36, 1, 1, 1, 1,
		1, 1, 1, 44, 45, 46, 47, 1,
		1, 1, 1, 1, 1, 54, 55, 1,
		1, 1, 1, 1, 1, 62, 63, 1,
	};
	
	// Move a normal Collider Object by this Physic Engine
	public boolean MoveColliderObject(Vector2D position, Vector2D velocity, double deltaTime, Vector2D colliderSize) {
		// Reset State Data
		boolean hit = false;
		
		// Length of one Physic Iteration
		double stepLength = 1.0 / (double)solveIterations;
		
		Vector2D newPosition = new Vector2D(0.0, 0.0);
		
		for (int i = 0; i < solveIterations; i++) {
			// Compute new Position by Velocity
			newPosition = new Vector2D(position.x + velocity.x * deltaTime * stepLength, position.y + velocity.y * deltaTime * stepLength);
			
			// Tile Bounds which lie in the Collider of the Figure
			int fromTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x - colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
			int fromTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y - colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
			int toTileX = UtilityFunctions.Clamp((int)Math.round(newPosition.x + colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
			int toTileY = UtilityFunctions.Clamp((int)Math.round(newPosition.y + colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
			
			// Go through all Tiles which are possible Colliders
			for (int y = fromTileY; y <= toTileY; y++) {
				for (int x = fromTileX; x <= toTileX; x++) {
					// If Tile is not empty/Air
					if (levelData.tileMapDataSolid[y][x] != 0) {
						Vector2D tilePosition = new Vector2D((double)x, (double)y);
						
						// Check for Intersection
						if (UtilityFunctions.BoxIntersection(newPosition, colliderSize, tilePosition, new Vector2D(1.0, 1.0))) {
							double dx = Math.abs(tilePosition.x - newPosition.x);
							double dy = Math.abs(tilePosition.y - newPosition.y);
							
							// Invert corresponding Velocity-Axis
							if (dx >= dy) {
								velocity.x = -velocity.x;
								position.x += Math.signum(velocity.x) * 0.1; // Offset
							}
							else {
								velocity.y = -velocity.y;
								position.y += Math.signum(velocity.y) * 0.1; // Offset
							}
							
							hit = true;
						}
					}
					
					if (hit)
						break;
				}
				if (hit)
					break;
			}
			
			// Apply new Position
			position = newPosition;
			if (hit)
				break;
		}
		
		return hit;
	}
	
	// Old Version for simple Objects
	/*public Vector2D[] MoveObject(Vector2D position, double dx, double dy, double deltaTime, Vector2D colliderSize, double bounce) {
		// Collision Check and return correction
		
		Vector2D newPosition = new Vector2D(position.x + dx * deltaTime, position.y + dy * deltaTime);
		int newTileX = (int)Math.round(newPosition.x) - levelData.GetTileMapOffsetX();
		int newTileY = (int)Math.round(newPosition.y) - levelData.GetTileMapOffsetY();
		
		int fromTileX = UtilityFunctions.Clamp(newTileX - (int)Math.ceil(colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
		int fromTileY = UtilityFunctions.Clamp(newTileY - (int)Math.ceil(colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
		int toTileX = UtilityFunctions.Clamp(newTileX + (int)Math.ceil(colliderSize.x * 0.5), 0, levelData.GetTileMapWidth() - 1);
		int toTileY = UtilityFunctions.Clamp(newTileY + (int)Math.ceil(colliderSize.y * 0.5), 0, levelData.GetTileMapHeight() - 1);
		
		// Go through all Tiles which are possible Colliders
		for (int y = fromTileY; y <= toTileY; y++) {
			for (int x = fromTileX; x <= toTileX; x++) {
				// If Tile is not empty
				if (levelData.tileMapData[y][x] != 0) {
					Vector2D tilePosition = new Vector2D((double)(x + levelData.GetTileMapOffsetX()), (double)(y + levelData.GetTileMapOffsetY()));
					
					// If there is a Collision
					if (UtilityFunctions.BoxIntersection(tilePosition, new Vector2D(1.0, 1.0), newPosition, colliderSize)) {
						// Solve Collision (Approximation)
						double c = 0.5;
						double f = 0.25;
						boolean solved = true;
						
						Vector2D oldNewPosition = new Vector2D(newPosition.x, newPosition.y);
						
						for (int i = 0; i < solveIterations; i++) {
							newPosition = UtilityFunctions.LerpUnclamped(position, newPosition, c);
							
							if (UtilityFunctions.BoxIntersection(tilePosition, new Vector2D(1.0, 1.0), newPosition, colliderSize)) {
								solved = false;
								c -= f;
							}
							else {
								solved = true;
								c += f;
							}
							
							f *= 0.5;
						}
						
						if (!solved) {
							newPosition = position;
						}
						
						if (Math.abs(newPosition.x - oldNewPosition.x) >= 0.001 * deltaTime) {
							dx = -dx * bounce;
						}
						if (Math.abs(newPosition.y - oldNewPosition.y) >= 0.001 * deltaTime) {
							dy = -dy * bounce;
						}
					}
				}
			}
		}
		
		return new Vector2D[] {newPosition, new Vector2D(dx, dy)};
	}*/
}
