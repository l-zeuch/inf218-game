package components;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import coreEngine.*;

public class Sprite {
	
	private boolean visible;
	
	private final int pixelWidth; // Pixel Width of a single Sprite
	private final int pixelHeight; // Pixel Height of a single Sprite
	private final double ppm; // Pixels per Meter
	private final double mpp; // Meters per Pixel
	
	private final boolean isSpriteSheet;
	private final int spritesCount;
	public int spriteSheetIndex = 0; // Current Sprite Sheet Index
	public boolean isSpriteAnimation;
	public double animationSpeed; // Sprites/Frames per Second
	
	private BufferedImage[] sprites; // Sprites of the Sprite Sheet
	
	public Sprite(String path, double ppm,
			boolean isSpriteSheet, int tileCountX, int tileCountY,
			boolean isSpriteAnimation, double animationSpeed) {
		
		visible = true;
		
		// Get Full Sprite Sheet Image
		Image fullImage = LoadImage(path);
		this.ppm = ppm;
		mpp = 1.0 / ppm;
		
		this.isSpriteSheet = isSpriteSheet;
		
		if (!isSpriteSheet) { // Sprite Sheet contains Single Sprite
			// Compute Pixel Size
			pixelWidth = fullImage.getWidth(null);
			pixelHeight = fullImage.getHeight(null);
			
			// Create Array
			spritesCount = 1;
			sprites = new BufferedImage[1];
			
			// Create Buffered Image
			BufferedImage newTile = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TRANSLUCENT);
			
			// Create Graphics for the Buffered Image for Writing
			Graphics2D g2d = newTile.createGraphics();
			// Draw Sprite Sheet to the Buffered Image
			g2d.drawImage(fullImage, 0, 0, pixelWidth, pixelHeight, null);
			g2d.dispose();
			
			// Apply Buffered Image to Array
			sprites[0] = newTile;
		}
		else { // Sprite Sheet contains multiple Sprites
			// Compute Pixel Size
			pixelWidth = fullImage.getWidth(null) / tileCountX;
			pixelHeight = fullImage.getHeight(null) / tileCountY;
			
			// Create Array
			spritesCount = tileCountX * tileCountY;
			sprites = new BufferedImage[spritesCount];
			
			// Iterate through the Array
			for (int ty = 0; ty < tileCountY; ty++) {
				for (int tx = 0; tx < tileCountX; tx++) {
					// Create Buffered Image
					BufferedImage newTile = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TRANSLUCENT);
					
					// Create Graphics for the Buffered Image for Writing
					Graphics2D g2d = newTile.createGraphics();
					// Draw a part of the Sprite Sheet to the Buffered Image
					g2d.drawImage(fullImage,
							0, 0, pixelWidth, pixelHeight, // Where to write on the Destination
							tx * pixelWidth, ty * pixelHeight, (tx + 1) * pixelWidth, (ty + 1) * pixelHeight, // Source Position of writing on the Source
							null);
					g2d.dispose();
					
					// Apply Buffered Image to Array
					sprites[tx + ty * tileCountX] = newTile;
				}
			}
		}
		
		this.isSpriteAnimation = isSpriteAnimation;
		this.animationSpeed = animationSpeed;
	}
	
	// Load Image by path
	private Image LoadImage(String path) {
        ImageIcon ii = new ImageIcon(path);
        return ii.getImage();
    }
	
	// Per Frame Update
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		if (isSpriteSheet && isSpriteAnimation) {
			spriteSheetIndex = (int)(frameData.timeSinceGameStart * animationSpeed) % spritesCount;
		}
	}
	
	// Set Visibility
	public void SetVisibility(boolean isVisible) {
		visible = isVisible;
	}
	
	public boolean GetVisibility() {
		return visible;
	}
	
	public boolean HasSpriteSheet() {
		return isSpriteSheet;
	}
	
	// Get current Sprite
	public BufferedImage GetSprite() {
		if (!isSpriteSheet) {
			return sprites[0];
		}
		else {
			return sprites[spriteSheetIndex];
		}
	}
	
	// Get Sprite by Index
	public BufferedImage GetSpriteWidthIndex(int index) {
		if (!isSpriteSheet) {
			return sprites[0];
		}
		else {
			return sprites[index];
		}
	}
	
	public int GetTileCount() {
		return spritesCount;
	}
	
	public int GetPixelWidth() {
		return pixelWidth;
	}
	
	public int GetPixelHeight() {
		return pixelHeight;
	}
	
	public double GetPPM() {
		return ppm;
	}
	
	public double GetMPP() {
		return mpp;
	}
}
