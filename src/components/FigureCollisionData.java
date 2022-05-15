package components;

public class FigureCollisionData {
	
	// Contains only Data, no Methods or something else
	
	public Vector2D position;
	public Vector2D velocity;
	public boolean isGrounded;
	public boolean leftCliff;
	public boolean rightCliff;
	public boolean isOnWall;
	public boolean isInLadder;
	public boolean hitWall;
	
	public FigureCollisionData(Vector2D position, Vector2D velocity, boolean isGrounded, boolean leftCliff, boolean rightCliff, boolean isOnWall, boolean isInLadder, boolean hitWall) {
		this.position = new Vector2D(position.x, position.y);
		this.velocity = velocity;
		this.isGrounded = isGrounded;
		this.leftCliff = leftCliff;
		this.rightCliff = rightCliff;
		this.isOnWall = isOnWall;
		this.isInLadder = isInLadder;
		this.hitWall = hitWall;
	}
}
