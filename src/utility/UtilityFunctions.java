package utility;

import components.*;

public class UtilityFunctions {
	
	// Checks if two Collider Objects are intersecting
	public static boolean CollisionCheck(ColliderObject objectA, ColliderObject objectB) {
		if (BoxIntersection(objectA.position, objectA.GetColliderSize(), objectB.position, objectB.GetColliderSize())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Checks if two Boxes are intersecting
	public static boolean BoxIntersection(Vector2D boxACenter, Vector2D boxASize, Vector2D boxBCenter, Vector2D boxBSize) {
		Vector2D boxASizeHalf = new Vector2D(boxASize.x * 0.5, boxASize.y * 0.5);
		Vector2D boxBSizeHalf = new Vector2D(boxBSize.x * 0.5, boxBSize.y * 0.5);
		
		if (boxACenter.x + boxASizeHalf.x >= boxBCenter.x - boxBSizeHalf.x && boxACenter.x - boxASizeHalf.x <= boxBCenter.x + boxBSizeHalf.x) {
			if (boxACenter.y + boxASizeHalf.y >= boxBCenter.y - boxBSizeHalf.y && boxACenter.y - boxASizeHalf.y <= boxBCenter.y + boxBSizeHalf.y) {
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
	}
	
	// Clamps a Value "v" between "min" and "max"
	public static int Clamp(int v, int min, int max) {
		if (v < min)
			v = min;
		if (v > max)
			v = max;
		return v;
	}
	
	// Clamps a Value "v" between "min" and "max"
	public static double Clamp(double v, double min, double max) {
		if (v < min)
			v = min;
		if (v > max)
			v = max;
		return v;
	}
	
	// Interpolates between "from" and "to" width the Value "v" (v = 0.0 : from, v = 1.0 : to) and clamps between "from" and "to"
	public static double Lerp(double from, double to, double v) {
		return from * (1.0 - v) + to * v;
	}
	
	// Interpolates between "from" and "to" width the Value "v" (v = 0.0 : from, v = 1.0 : to)
	public static double LerpUnclamped(double min, double max, double v) {
		v = Clamp(0.0, 1.0, v);
		return min * (1.0 - v) + max * v;
	}
	
	// Interpolates between "from" and "to" width the Vector "v" (v = 0.0 : from, v = 1.0 : to)
	public static Vector2D LerpUnclamped(Vector2D from, Vector2D to, double v) {
		return new Vector2D(from.x * (1.0 - v) + to.x * v, from.y * (1.0 - v) + to.y * v);
	}
}
