package coreEngine;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import components.*;

public class MouseInputManager extends MouseAdapter {
	
	private boolean leftMouseButton = false;
	private boolean leftMouseButtonClicked = false;
	private boolean rightMouseButton = false;
	private boolean rightMouseButtonClicked = false;
	private boolean middleMouseButton = false;
	private boolean middleMouseButtonClicked = false;
	
	private Vector2D mousePosition = new Vector2D(0.0, 0.0);
	
	private Vector2D worldMousePosition = new Vector2D(0.0, 0.0);
	
	// Resets ...-Clicked Variables (they are only active for one Frame)
	public void PostMouseInputUpdate() {
		leftMouseButtonClicked = false;
		rightMouseButtonClicked = false;
		middleMouseButtonClicked = false;
	}
	
	// Reset Mouse Buttons
	public void ResetMouse() {
		leftMouseButton = false;
		leftMouseButtonClicked = false;
		rightMouseButton = false;
		rightMouseButtonClicked = false;
		middleMouseButton = false;
		middleMouseButtonClicked = false;
	}
	
	// Get current Mouse Position (Screen and World Space)
	public void SetMousePosition(Point point, RenderCamera renderCamera) {
		// Screen Space
		mousePosition.x = point.getX();
		mousePosition.y = point.getY();
		
		// World Space
		double screenWidth = (double)renderCamera.GetScreenWidth();
		double screenHeight = (double)renderCamera.GetScreenHeight();
		double screenScale = screenWidth / renderCamera.cameraWidth;
		worldMousePosition.x = (mousePosition.x - screenWidth * 0.5) / screenScale + renderCamera.position.x;
		worldMousePosition.y = -(mousePosition.y - screenHeight * 0.5) / screenScale + renderCamera.position.y;
	}
	
	// Mouse Buttons
	
	public boolean LeftMouseButton() {
		return leftMouseButton;
	}
	
	public boolean LeftMouseButtonClicked() {
		return leftMouseButtonClicked;
	}
	
	public boolean RightMouseButton() {
		return rightMouseButton;
	}
	
	public boolean RightMouseButtonClicked() {
		return rightMouseButtonClicked;
	}
	
	public boolean MiddleMouseButton() {
		return middleMouseButton;
	}
	
	public boolean MiddleMouseButtonClicked() {
		return middleMouseButtonClicked;
	}
	
	// Other Mouse Functions
	
	public Vector2D GetWorldMousePosition() {
		return worldMousePosition;
	}
	
	public Vector2D GetMousePosition() {
		return mousePosition;
	}
	
	// Overwritten Functions
	
	@Override
    public void mousePressed(MouseEvent e) {
		int key = e.getButton();

		if (key == MouseEvent.BUTTON1) {
			leftMouseButton = true;
			leftMouseButtonClicked = true;
        }
        if (key == MouseEvent.BUTTON2) {
        	middleMouseButton = true;
        	middleMouseButtonClicked = true;
        }
        if (key == MouseEvent.BUTTON3) {
        	rightMouseButton = true;
        	rightMouseButtonClicked = true;
        }
    }
	
	@Override
    public void mouseReleased(MouseEvent e) {
		int key = e.getButton();

    	if (key == MouseEvent.BUTTON1) {
    		leftMouseButton = false;
        }
        if (key == MouseEvent.BUTTON2) {
        	middleMouseButton = false;
        }
        if (key == MouseEvent.BUTTON3) {
        	rightMouseButton = false;
        }
    }
}
