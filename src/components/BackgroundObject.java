package components;

public class BackgroundObject extends Sprite {
	
	public Vector2D position; // In meters
	public double rotation; // In degrees
	public Vector2D scale;
	
	protected double parallaxe; // Perspective Shift Factor
	
	public BackgroundObject(
			Vector2D position, double rotation, Vector2D scale, // Transform
			String path, double ppm, // Sprite
			double parallaxe // Other
			) {
		
		super(path, ppm, false, 1, 1, false, 0.0);
		
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		this.parallaxe = parallaxe;
	}
	
	public double GetParallaxe() {
		return parallaxe;
	}
}