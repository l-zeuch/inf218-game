package coreEngine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import components.*;

public class RenderEngine {
	
	// Corresponding Camera
	public RenderCamera renderCamera;
	
	public double screenWidthD; // Screen Width as Double
	public double screenHeightD; // Screen Height as Double
	
	public double screenScale; // Screen Scale (Screen Width / Camera Width);
	public double invScreenScale; // Invert Screen Scale (1.0 / Screen Scale)
	
	public RenderEngine(RenderCamera renderCamera) {
		this.renderCamera = renderCamera;
		screenScale = (double)renderCamera.GetScreenWidth() / renderCamera.cameraWidth;
		invScreenScale = 1.0 / screenScale;
		screenWidthD = (double)renderCamera.GetScreenWidth();
		screenHeightD = (double)renderCamera.GetScreenHeight();
	}
	
	// Per Frame Update
	public void RenderEngineUpdate() {
		// Update Parameters
		screenScale = (double)renderCamera.GetScreenWidth() / renderCamera.cameraWidth;
		invScreenScale = 1.0 / screenScale;
		screenWidthD = (double)renderCamera.GetScreenWidth();
		screenHeightD = (double)renderCamera.GetScreenHeight();
	}
	
	// Draw Background Objects
	public void RenderBackgroundObjects(List<BackgroundObject> backgroundObjects, Graphics2D g2d, ImageObserver o) {
		// Iterate though all Background Objects
		for (int i = 0; i < backgroundObjects.size(); i++) {
	        BackgroundObject bo = backgroundObjects.get(i);
	        
	        // If is Visible
	        if (bo.GetVisibility()) {
	        	// World Position to Pixel Position width Parallax
	        	double pixelTranslationX = (bo.position.x - renderCamera.position.x * bo.GetParallaxe()) * screenScale;
		        double pixelTranslationY = -(bo.position.y - renderCamera.position.y * bo.GetParallaxe()) * screenScale;
		        double pixelPosX = screenWidthD * 0.5 + pixelTranslationX;
		        double pixelPosY = screenHeightD * 0.5 + pixelTranslationY;
		        
		        // Sprite Resolution
		        double spriteWidth = (double)bo.GetPixelWidth();
		        double spriteHeight = (double)bo.GetPixelHeight();
		        
		        double pixelScale = screenScale * bo.GetMPP(); // Scale of one Pixel of the Sprite
		        
		        // Radius of the Sprite in Screen Space
		        double pixelRadiusX = spriteWidth * 0.5 * Math.abs(bo.scale.x) * pixelScale;
		        double pixelRadiusY = spriteHeight * 0.5 * Math.abs(bo.scale.y) * pixelScale;
		        
		        // Position loop (doesn't really work)
		        /*
		        double invP = 1.0 / Math.max(bo.GetParallaxe(), 0.0001); // Invert Parallax
		        pixelPosX = (pixelPosX + spriteScale * spriteWidth * invP) % (screenWidthD + spriteScale * spriteWidth * 2.0 * invP) - spriteScale * spriteWidth * invP;
		        pixelPosY = (pixelPosY + spriteScale * spriteHeight * invP) % (screenHeightD + spriteScale * spriteHeight * 2.0 * invP) - spriteScale * spriteHeight * invP;
		        */
		        
		        // Clipping Test
		        if (pixelPosX + pixelRadiusX >= 0.0 && pixelPosX - pixelRadiusX <= screenWidthD &&
		        		pixelPosY + pixelRadiusY >= 0.0 && pixelPosY - pixelRadiusY <= screenHeightD) {
		        	
		        	// Transformation Matrix for the Sprite
		        	AffineTransform at = AffineTransform.getTranslateInstance(pixelPosX, pixelPosY); // Apply Screen Space Translation
			        at.scale(pixelScale, pixelScale); // Apply Pixel Scale
			        if (bo.rotation != 0.0) {
			        	at.rotate(Math.toRadians(bo.rotation)); // Apply Rotation of the Background Object
			        }
			        at.scale(bo.scale.x, bo.scale.y); // Apply x- and y-Scale of the Background Object
			        at.translate(-spriteWidth * 0.5, -spriteHeight * 0.5); // Center the Background Object (Origin is in the Middle of the Sprite)
					
			        g2d.drawImage(bo.GetSprite(), at, o); // Draw Sprite
		        }
	        }
		}
	}
	
	// Draw Collider Objects
	public void RenderColliderObjects(List<ColliderObject> colliderObjects, boolean drawGizmos, Graphics2D g2d, ImageObserver o) {
		// Iterate though all Collider Objects
		for (int i = 0; i < colliderObjects.size(); i++) {
			ColliderObject co = colliderObjects.get(i);
	        
			// If is Visible
	        if (co.GetVisibility()) {
	        	// World Position to Pixel Position
	        	double pixelPosX = WorldPositionXToPixelDouble(co.position.x);
		        double pixelPosY = WorldPositionYToPixelDouble(co.position.y);
		        
		        // Half Sprite Resolution
		        double spriteWidthHalf = (double)co.GetPixelWidth() * 0.5;
		        double spriteHeightHalf = (double)co.GetPixelHeight() * 0.5;
		        
		        double spriteScale = screenScale * co.GetMPP(); // Scale of one Pixel of the Sprite
		        
		        // Radius of the Sprite in Screen Space
		        double pixelRadiusX = spriteWidthHalf * Math.abs(co.scale.x) * spriteScale;
		        double pixelRadiusY = spriteHeightHalf * Math.abs(co.scale.y) * spriteScale;
		        
		        // Clipping Test
		        if (pixelPosX + pixelRadiusX >= 0.0 && pixelPosX - pixelRadiusX <= screenWidthD &&
		        		pixelPosY + pixelRadiusY >= 0.0 && pixelPosY - pixelRadiusY <= screenHeightD) {
		        	
		        	// Transformation Matrix for the Sprite
		        	AffineTransform at = AffineTransform.getTranslateInstance(pixelPosX, pixelPosY); // Apply Screen Space Translation
			        at.scale(spriteScale, spriteScale); // Apply Pixel Scale
			        if (co.rotation != 0.0) {
			        	at.rotate(Math.toRadians(co.rotation)); // Apply Rotation of the Collider Object
			        }
			        at.scale(co.scale.x, co.scale.y); // Apply x- and y-Scale of the Collider Object
			        at.translate(-spriteWidthHalf, -spriteHeightHalf); // Center the Collider Object (Origin is in the Middle of the Sprite)
					
			        g2d.drawImage(co.GetSprite(), at, o); // Draw Sprite
			        
			        // { --- Editor only ---
			        if (drawGizmos) {
			        	// Draw Collider Size Rectangle
						g2d.setColor(Color.blue);
						Vector2D c = co.GetColliderSize();
						DrawWireRectangleInWorld(
								new Vector2D(co.position.x - c.x * 0.5, co.position.y - c.y * 0.5),
								new Vector2D(co.position.x + c.x * 0.5, co.position.y + c.y * 0.5),
								g2d);
						
						// Draw Sprite Size Rectangle
						g2d.setColor(Color.yellow);
						double width = (double)co.GetPixelWidth() * co.GetMPP();
						double height = (double)co.GetPixelHeight() * co.GetMPP();
						DrawWireRectangleInWorld(
								new Vector2D(co.position.x - width * 0.5, co.position.y - height * 0.5),
								new Vector2D(co.position.x + width * 0.5, co.position.y + height * 0.5),
								g2d);
			        }
			        // } --- Editor only ---
		        }
	        }
		}
	}
	
	// Draw Particle Systems
	public void RenderParticleSystems(List<ParticleSystem> particleSystems, Graphics2D g2d, ImageObserver o) {
		// Iterate though all Particle Systems
		for (int i = 0; i < particleSystems.size(); i++) {
			ParticleSystem ps = particleSystems.get(i);
			
			// If is Visible
			if (ps.GetVisibility()) {
		        List<Particle> particles = ps.GetParticles();
				
		        // Iterate though all Particles of the current System
				for (int p = 0; p < particles.size(); p++) {
		        	Particle pa = particles.get(p);
					
		        	// World Position to Pixel Position
		        	double pixelPosX = WorldPositionXToPixelDouble(pa.position.x);
				    double pixelPosY = WorldPositionYToPixelDouble(pa.position.y);
				    
				    // Particle Sprite Resolution
				    double spriteWidth = (double)ps.GetPixelWidth();
				    double spriteHeight = (double)ps.GetPixelHeight();
			        
			        double spriteScale = screenScale * ps.GetMPP(); // Scale of one Pixel of the Particle Sprite
				    
			        // Transformation Matrix for the Particle Sprite
				    AffineTransform at = AffineTransform.getTranslateInstance(pixelPosX, pixelPosY); // Apply Screen Space Translation
			        at.scale(spriteScale, spriteScale); // Apply Pixel Scale
			        if (pa.rotation != 0.0) {
			        	at.rotate(Math.toRadians(pa.rotation)); // Apply Rotation of the Particle
			        }
			        at.scale(ps.scale.x, ps.scale.y); // Apply x- and y-Scale of the Particle
			        at.translate(-spriteWidth * 0.5, -spriteHeight * 0.5); // Center the Particle (Origin is in the Middle of the Sprite)
					
			        g2d.drawImage(ps.GetSpriteWidthIndex(pa.GetSpriteIndex()), at, o); // Draw Particle Sprite
		        }
	        }
		}
	}
	
	// Draw Tile Map
	public void RenderTileMap(LevelData levelData, Graphics2D g2d, ImageObserver o, double parallax, boolean background) {
		double invParallax = 1.0 / parallax;
		
		// Render Bounds
		int fromX = (int)Math.floor(-screenWidthD * 0.5 * invScreenScale * invParallax + renderCamera.position.x);
		fromX = Math.max(fromX, 0);
		
		int fromY = (int)Math.floor(-screenHeightD * 0.5 * invScreenScale * invParallax + renderCamera.position.y);
		fromY = Math.max(fromY, 0);
		
		int toX = (int)Math.ceil(screenWidthD * 0.5 * invScreenScale * invParallax + renderCamera.position.x);
		toX = Math.min(toX, levelData.GetTileMapWidth() - 1);
		
		int toY = (int)Math.ceil(screenHeightD * 0.5 * invScreenScale * invParallax + renderCamera.position.y);
		toY = Math.min(toY, levelData.GetTileMapHeight() - 1);
		
		Sprite[] spriteSheets = levelData.GetSpriteSheets(); // All Sprite Sheets of the Tile Map
		
		byte block; // Current Tile/Block
		byte spriteSheetIndex; // Current Sprite Sheet Index
		
		// Iterate through all visible Tiles/Blocks
		for (int y = fromY; y <= toY; y++) {
			for (int x = fromX; x <= toX; x++) {
				// If is drawing Background, get Tile Sprite from Background List, else from Solid List
				if (background) {
					block = levelData.tileMapDataBackground[y][x];
					spriteSheetIndex = levelData.tileMapSpriteIndexBackground[y][x];
				}
				else {
					block = levelData.tileMapDataSolid[y][x];
					spriteSheetIndex = levelData.tileMapSpriteIndexSolid[y][x];
				}
				
				// If Tile/Block is no air, render Tile/Block
				if (block > 0) {
					// World Position to Pixel Position width Parallax
			        double pixelTranslationX = ((double)x * parallax - renderCamera.position.x * parallax) * screenScale;
			        double pixelTranslationY = -((double)y * parallax - renderCamera.position.y * parallax) * screenScale;
			        double pixelPosX = screenWidthD * 0.5 + pixelTranslationX;
			        double pixelPosY = screenHeightD * 0.5 + pixelTranslationY;
			        
			        // Tile Sprite Resolution
			        double spriteWidth = (double)spriteSheets[spriteSheetIndex].GetPixelWidth();
			        double spriteHeight = (double)spriteSheets[spriteSheetIndex].GetPixelHeight();
			        
			        double spriteScale = screenScale * spriteSheets[spriteSheetIndex].GetMPP() * parallax; // Scale of one Pixel of the Tile Sprite
			        
			        // Transformation Matrix for the Tile Sprite
			        AffineTransform at = AffineTransform.getTranslateInstance(pixelPosX, pixelPosY); // Apply Screen Space Translation
			        at.scale(spriteScale, spriteScale); // Apply Pixel Scale
			        at.translate(-spriteWidth * 0.5, -spriteHeight * 0.5); // Center the Tile/Block (Origin is in the Middle of the Sprite)
					
			        g2d.drawImage(spriteSheets[spriteSheetIndex].GetSpriteWidthIndex(block - 1), at, o); // Draw Tile Sprite
				}
			}
		}
	}
	
	// Draw Single Sprite
	public void RenderSprite(Sprite sprite, int screenPosX, int screenPosY, Graphics2D g2d) {
		// Offset to center the Sprite (Origin is in the Middle of the Sprite)
		int xOffset = -(int)((double)sprite.GetPixelWidth() * 0.5);
		int yOffset = -(int)((double)sprite.GetPixelWidth() * 0.5);
		
		g2d.drawImage(sprite.GetSprite(), screenPosX + xOffset, screenPosY + yOffset, null);
	}
	
	// Draw Screen UI
	public void RenderUI(Graphics2D g2d, Player p, Sprite appleHeart, int maxCoins) {
		// Set UI Font
		g2d.setFont(new Font("Calibri", Font.BOLD, 40));
		
		// Coin Counter Text
		String coinsText = "Coins: " + p.coinCount + " / " + maxCoins;
		
		// Draw Outline of the Coin Counter Text
		g2d.setColor(new Color(120, 100, 0, 255)); // Set Font Color: Dark Gold
        int s = 2;
        // For a Outline Effect the Text is drawn multiple Times with different Offsets
		for (int y = -s; y <= s; y += 2) {
			for (int x = -s; x <= s; x += 2) {
				g2d.drawString(coinsText, 10 + x, 42 + y); // Draw Text
			}
		}
		
		// Draw Coin Counter Text
		g2d.setColor(new Color(240, 180, 0, 255)); // Set Font Color: Light Gold
		g2d.drawString(coinsText, 10, 42); // Draw Text
		
		// Draw Health Counter
		Composite oldC = g2d.getComposite(); // Save old Composite
        float alpha = 0.5f; // Draw at half Transparency
        // Iterate through Lifes and there Health
        for (int l = 0; l < p.lifes; l++) {
        	int h = 3;
        	if (l == p.lifes - 1) {
        		h = p.GetHealth();
        		// Draw Hearts width Transparency
        		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2d.setComposite(ac);
        		for (int i = 0; i < 3; i++) {
        			g2d.drawImage(appleHeart.GetSprite(), renderCamera.GetScreenWidth() - 64 - i * 64, 8 + l * 16, null);
        		}
        		g2d.setComposite(oldC);
        	}
        	
        	// Draw Hearts solid
        	for (int i = 0; i < h; i++) {
    			g2d.drawImage(appleHeart.GetSprite(), renderCamera.GetScreenWidth() - 64 - i * 64, 8 + l * 16, null);
    		}
        }
	}
	
	// Draw Full Screen Images like Vignette
	public void RenderFullScreenImage(Sprite sprite, Graphics2D g2d) {
		g2d.drawImage(sprite.GetSprite(), 0, 0, renderCamera.GetScreenWidth() + 12, renderCamera.GetScreenHeight() + 12, null);
	}
	
	// Same as above with scale Parameters
	public void RenderFullScreenImage(Sprite sprite, double sx, double sy, boolean keepAspect, Graphics2D g2d) {
		double w = (double)(renderCamera.GetScreenWidth() + 12);
		double ws = 1.0;
		double h = (double)(renderCamera.GetScreenHeight() + 12);
		
		// If Sprite should keep its Aspect, compute additional Correction Parameter "ws"
		if (keepAspect) {
			ws = ((double)sprite.GetPixelWidth() / (double)sprite.GetPixelHeight()) / ((double)(renderCamera.GetScreenWidth() + 12) / (double)(renderCamera.GetScreenHeight() + 12));
		}
		
		g2d.drawImage(sprite.GetSprite(), (int)(w * 0.5 * (1.0 - (sx * ws))), (int)(h * 0.5 * (1.0 - sy)), (int)(w * sx * ws), (int)(h * sy), null);
	}
	
	//
	// Editor
	//
	
	// List of Entities
	private final ColliderObject[] entities = new ColliderObject[] {
			new Player(new Vector2D(0.0, 0.0)),
			new Coin(new Vector2D(0.0, 0.0), 1.0, 1.0),
			new Heart(new Vector2D(0.0, 0.0), 1.0, 1.0),
			new Rotor(new Vector2D(0.0, 0.0), 1.0, 1.0),
			new RobotEnemy(new Vector2D(0.0, 0.0), 1.0),
			new Checkpoint(new Vector2D(0.0, 0.0)),
			new Goal(new Vector2D(0.0, 0.0))
	};
	
	// List of Editor Toggle Buttons
	private final String[] editorToggleNames = new String[] {
			// Tool
			"Paint",
			"Replace",
			// Selected Layer
			"Foreground",
			"Background"
	};
	
	// Draw Editor UI/Layout
	public void RenderEditorLayout(EditorManager editorManager, Graphics2D g2d) {
		// Set UI Font
		g2d.setFont(new Font("Calibri", Font.PLAIN, 20));
		
		//// World Gizmos
		
		// Draw Tile Map Grid
		// Draw-Area
		int xFrom = (int)Math.floor(renderCamera.position.x - renderCamera.cameraWidth * 0.5);
		int yFrom = (int)Math.floor(renderCamera.position.y - renderCamera.cameraHeight * 0.5);
		int xTo = (int)Math.ceil(renderCamera.position.x + renderCamera.cameraWidth * 0.5);
		int yTo = (int)Math.ceil(renderCamera.position.y + renderCamera.cameraHeight * 0.5);
		
		for (int y = yFrom; y <= yTo; y++) {
			double a = 1.0; // Blend Factor
			if (y < 0) {
				a = 1.0 / (-(double)(y - 2) * 0.5);
			}
			else if (y > editorManager.levelData.GetTileMapHeight()) {
				a = 1.0 / ((double)((y + 2) - editorManager.levelData.GetTileMapHeight()) * 0.5);
			}
			g2d.setColor(new Color(127, 127, 127, (int)(127.0 * a)));
			g2d.drawLine(0, WorldPositionYToPixel((double)y - 0.5), (int)screenWidthD + 12, WorldPositionYToPixel((double)y - 0.5));
		}
		for (int x = xFrom; x <= xTo; x++) {
			double a = 1.0; // Blend Factor
			if (x < 0) {
				a = 1.0 / (-(double)(x - 2) * 0.5);
			}
			else if (x > editorManager.levelData.GetTileMapWidth()) {
				a = 1.0 / ((double)((x + 2) - editorManager.levelData.GetTileMapWidth()) * 0.5);
			}
			g2d.setColor(new Color(127, 127, 127, (int)(127.0 * a)));
			g2d.drawLine(WorldPositionXToPixel((double)x - 0.5), 0, WorldPositionXToPixel((double)x - 0.5), (int)screenHeightD + 12);
		}
		
		// Draw Tile Map Borders
		// Physic Border
		g2d.setColor(Color.white);
		DrawWireRectangleInWorld(
				new Vector2D(-0.5, -0.5),
				new Vector2D(editorManager.levelData.GetTileMapWidth() - 0.5, editorManager.levelData.GetTileMapHeight() - 0.5), g2d);
		// Camera Border
		g2d.setColor(Color.red);
		DrawWireRectangleInWorld(
				new Vector2D(0.5, 0.5),
				new Vector2D(editorManager.levelData.GetTileMapWidth() - 1.5, editorManager.levelData.GetTileMapHeight() - 1.5), g2d);
		
		// Draw Mouse World Cursor
		// If is Mouse Cursor not on UI
		if (!editorManager.isOnUI) {
			// If  isn't an Entity selected
			if (!editorManager.isEntitySelected) {
				// Draw Tile/Block Cursor
				g2d.setColor(Color.yellow);
				DrawWireRectangleInWorld(
						new Vector2D((double)editorManager.mousePositionX - 0.5, (double)editorManager.mousePositionY - 0.5),
						new Vector2D((double)editorManager.mousePositionX + 0.5, (double)editorManager.mousePositionY + 0.5),
						g2d);
			}
			else { // Draw Gizmos of the selected Entity
				if (editorManager.selectedBlock < entities.length) {
					// Selected Collider Object
					ColliderObject co = entities[editorManager.selectedBlock];
					
			        // World Position to Pixel Position
		        	double pixelPosX = WorldPositionXToPixelDouble(editorManager.entityMousePosition.x);
				    double pixelPosY = WorldPositionYToPixelDouble(editorManager.entityMousePosition.y);
				    
				    // Entity Sprite Resolution
				    double spriteWidth = (double)co.GetPixelWidth();
			        double spriteHeight = (double)co.GetPixelHeight();
			        
			        double spriteScale = screenScale * co.GetMPP(); // Scale of one Pixel of the Entity Sprite
			        
			        // Transformation Matrix for the Entity Sprite
			        AffineTransform at = AffineTransform.getTranslateInstance(pixelPosX, pixelPosY); // Screen Translation
			        at.scale(spriteScale, spriteScale); // Apply Pixel Scale
			        at.scale(co.scale.x * editorManager.entityXAxis, co.scale.y * editorManager.entityYAxis); // Apply x- and y-Scale of the Entity
			        at.translate(-spriteWidth * 0.5, -spriteHeight * 0.5); // Center the Entity (Origin is in the Middle of the Sprite)
					
			        // Draw Sprite with Transparency
			        Composite oldC = g2d.getComposite(); // Save old Composite
			        float alpha = 0.5f; // Draw width half Transparency
			        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			        g2d.setComposite(ac);
			        g2d.drawImage(co.GetSprite(), at, null);
			        g2d.setComposite(oldC);
					
					// Draw Collider Size
					g2d.setColor(Color.blue);
					Vector2D c = co.GetColliderSize();
					DrawWireRectangleInWorld(
							new Vector2D(editorManager.entityMousePosition.x - c.x * 0.5, editorManager.entityMousePosition.y - c.y * 0.5),
							new Vector2D(editorManager.entityMousePosition.x + c.x * 0.5, editorManager.entityMousePosition.y + c.y * 0.5),
							g2d);
					
					// Draw Sprite Size
					g2d.setColor(Color.yellow);
					double width = (double)co.GetPixelWidth() * co.GetMPP();
					double height = (double)co.GetPixelHeight() * co.GetMPP();
					DrawWireRectangleInWorld(
							new Vector2D(editorManager.entityMousePosition.x - width * 0.5, editorManager.entityMousePosition.y - height * 0.5),
							new Vector2D(editorManager.entityMousePosition.x + width * 0.5, editorManager.entityMousePosition.y + height * 0.5),
							g2d);
					
					// Draw X-Axis
					g2d.setColor(Color.red);
					DrawWireRectangleInWorld(
							new Vector2D(editorManager.entityMousePosition.x - 0.05 * editorManager.entityXAxis, editorManager.entityMousePosition.y - 0.05),
							new Vector2D(editorManager.entityMousePosition.x + 1.05 * editorManager.entityXAxis, editorManager.entityMousePosition.y + 0.05),
							g2d);
					
					// Draw Y-Axis
					g2d.setColor(Color.green);
					DrawWireRectangleInWorld(
							new Vector2D(editorManager.entityMousePosition.x - 0.05, editorManager.entityMousePosition.y - 0.05 * editorManager.entityYAxis),
							new Vector2D(editorManager.entityMousePosition.x + 0.05, editorManager.entityMousePosition.y + 1.05 * editorManager.entityYAxis),
							g2d);
				}
			}
		}
		
		//// Basic UI Elements
		
		// Draw UI Background
		g2d.setColor(Color.darkGray);
		g2d.fillRect(0, 0, 256, (int)screenHeightD + 12);
		g2d.setColor(Color.black);
		g2d.drawLine(256, 0, 256, (int)screenHeightD + 12);
		
		// Tiles for Tile Selection
		Sprite[] spriteSheets = editorManager.levelData.GetSpriteSheets();
		
		// Draw all Tiles of the Selected Tile/Sprite Sheet
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				g2d.drawImage(spriteSheets[editorManager.selectedSpriteSheet].GetSpriteWidthIndex(x + y * 8), x * 32, y * 32, 32, 32, null);
			}
		}
		
		// Draw Tile Map Selection Grid
		g2d.setColor(Color.black);
		for (int y = 0; y <= 8; y++) {
			g2d.drawLine(0, y * 32, 256, y * 32);
		}
		for (int x = 0; x <= 8; x++) {
			g2d.drawLine(x * 32, 0, x * 32, 256);
		}
		
		// Draw Entities for Entity Selection
		for (int y = 0; y < 7; y++) {
			for (int x = 0; x < 8; x++) {
				int index = x + y * 8;
				if (index >= entities.length) {
					break;
				}
				g2d.drawImage(entities[index].GetSprite(), x * 32, y * 32 + 32 * 12, 32, 32, null);
			}
		}
		
		// Draw Entities Selection Grid
		g2d.setColor(Color.black);
		for (int y = 0; y <= 7; y++) {
			g2d.drawLine(0, y * 32 + 32 * 12, 256, y * 32 + 32 * 12);
		}
		for (int x = 0; x <= 8; x++) {
			g2d.drawLine(x * 32, 0 + 32 * 12, x * 32, 256 - 32 + 32 * 12);
		}
		
		//// Selections
		
		// Draw Selected Tile/Entity
		if (!editorManager.isEntitySelected) {
			// Draw Selected Entity
			g2d.setColor(new Color(0, 255, 0, 80));
			g2d.fillRect(((editorManager.selectedBlock - 1) % 8) * 32 + 1, ((editorManager.selectedBlock - 1) / 8) * 32 + 1, 32 - 1, 32 - 1);
		}
		else {
			// Draw Selected Tile
			g2d.setColor(new Color(0, 255, 0, 80));
			g2d.fillRect(((editorManager.selectedBlock) % 8) * 32 + 1, ((editorManager.selectedBlock) / 8) * 32 + 32 * 12 + 1, 32 - 1, 32 - 1);
		}
		
		// Draw Mouse Selection Cursor
		if (editorManager.isOnUI) {
			// Draw Mouse Selection Cursor for Tiles Selection
			if (editorManager.mousePositionX >= 0 && editorManager.mousePositionX < 8 && editorManager.mousePositionY >= 0 && editorManager.mousePositionY < 8) {
				g2d.setColor(Color.yellow);
				g2d.drawRect(editorManager.mousePositionX * 32, editorManager.mousePositionY * 32, 32, 32);
			}
			// Draw Mouse Selection Cursor for Entity Selection
			if (editorManager.mousePositionX >= 0 && editorManager.mousePositionX < 8 && editorManager.mousePositionY > 11 && editorManager.mousePositionY < 19) {
				g2d.setColor(Color.yellow);
				g2d.drawRect(editorManager.mousePositionX * 32, editorManager.mousePositionY * 32, 32, 32);
			}
		}
		
		//// Toggles
		
		// Draw Toggle Frames for Tile/Sprite Sheet Selection
		for (int i = 0; i < 4; i++) {
			g2d.setColor(Color.black);
			g2d.drawRect(2 + 32 * i, 256 + 2, 32 - 4, 32 - 4);
			g2d.setColor(Color.white);
			g2d.drawString(Integer.toString(i + 1), 11 + 32 * i, 279);
		}
		
		// Draw Toggle Frames for Tool Selection
		for (int i = 0; i < 4; i++) {
			int x = i % 2; 
			int y = i / 2;
			g2d.setColor(Color.black);
			g2d.drawRect(2 + 128 * x, 288 + 2 + 32 * y, 128 - 4, 32 - 4);
			g2d.setColor(Color.white);
			g2d.drawString(editorToggleNames[i], 10 + 128 * x, 310 + 32 * y);
		}
		
		// Draw Sprite Sheet Toggle Selected
		g2d.setColor(new Color(255, 255, 255, 63));
		g2d.fillRect(2 + 32 * editorManager.selectedSpriteSheet + 1, 256 + 2 + 1, 32 - 4 - 1, 32 - 4 - 1);
		
		// Draw Tool Toggle Selected
		g2d.setColor(new Color(255, 255, 255, 63));
		g2d.fillRect(2 + 128 * (editorManager.editMode % 2) + 1, 288 + 2 + 1, 128 - 4 - 1, 32 - 4 - 1);
		
		// Draw Layer Toggle Selected
		g2d.setColor(new Color(255, 255, 255, 63));
		if (editorManager.isBackgroundSelected) {
			g2d.fillRect(2 + 128 + 1, 288 + 2 + 32 + 1, 128 - 4 - 1, 32 - 4 - 1);
		}
		else {
			g2d.fillRect(2 + 1, 288 + 2 + 32 + 1, 128 - 4 - 1, 32 - 4 - 1);
		}
		
		//// Buttons
		
		// Draw Resize Button
		g2d.setColor(Color.black);
		g2d.drawRect(2, 288 + 2 + 32 * 2, 256 - 4, 32 - 4);
		g2d.setColor(Color.white);
		g2d.drawString("Resize Tile Map", 10 + 50, 310 + 32 * 2);
		
		if (editorManager.isOverResizeButton) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 2 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		if (editorManager.isResizeButtonPressed) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 2 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		
		// Draw Save Button
		g2d.setColor(Color.black);
		g2d.drawRect(2, 288 + 2 + 32 * 10, 256 - 4, 32 - 4);
		g2d.setColor(Color.white);
		g2d.drawString("Save Level", 10 + 75, 310 + 32 * 10);
		
		if (editorManager.isOverSaveButton) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 10 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		if (editorManager.isSaveButtonPressed) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 10 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		
		// Draw Change Save Location Button
		g2d.setColor(Color.black);
		g2d.drawRect(2, 288 + 2 + 32 * 11, 256 - 4, 32 - 4);
		g2d.setColor(Color.white);
		g2d.drawString("Change Save Location", 10 + 25, 310 + 32 * 11);
		
		if (editorManager.isOverChangeSaveLocation) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 11 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		if (editorManager.isChangeSaveLocationPressed) {
			g2d.setColor(new Color(255, 255, 255, 63));
			g2d.fillRect(2 + 1, 288 + 2 + 32 * 11 + 1, 256 - 4 - 1, 32 - 4 - 1);
		}
		
		// Draw Level Save Path Text Field
		g2d.setColor(Color.black);
		g2d.drawRect(2, 288 + 2 + 32 * 12 + (32 - 4), 256 - 4, -(32 - 4));
		g2d.setColor(Color.white);
		if (editorManager.saveLocation.length() > 0) {
			g2d.drawString(editorManager.saveLocation, 10, 310 + 32 * 12);
		}
		else {
			g2d.drawString("<No level file selected!>", 10, 310 + 32 * 12);
		}
	}
	
	// Draw an empty Rectangle from world space Coordinates
	private void DrawWireRectangleInWorld(Vector2D from, Vector2D to, Graphics2D g2d) {
		int xFrom = WorldPositionXToPixel(from.x);
		int yFrom = WorldPositionYToPixel(from.y);
		int xTo = WorldPositionXToPixel(to.x);
		int yTo = WorldPositionYToPixel(to.y);
		
		g2d.drawLine(xFrom, yFrom, xFrom, yTo);
		g2d.drawLine(xTo, yFrom, xTo, yTo);
		g2d.drawLine(xFrom, yFrom, xTo, yFrom);
		g2d.drawLine(xFrom, yTo, xTo, yTo);
	}
	
	// Transformation Functions
	private int WorldPositionXToPixel(double x) {
		return (int)(screenWidthD * 0.5 + (x - renderCamera.position.x) * screenScale);
	}
	
	private int WorldPositionYToPixel(double y) {
		return (int)(screenHeightD * 0.5 - (y - renderCamera.position.y) * screenScale);
	}
	
	private double WorldPositionXToPixelDouble(double x) {
		return screenWidthD * 0.5 + (x - renderCamera.position.x) * screenScale;
	}
	
	private double WorldPositionYToPixelDouble(double y) {
		return screenHeightD * 0.5 - (y - renderCamera.position.y) * screenScale;
	}
}
