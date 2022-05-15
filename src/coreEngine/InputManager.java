package coreEngine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputManager extends KeyAdapter {
	
	private boolean right = false;
	private boolean left = false;
	private boolean up = false;
	private boolean down = false;
	
	private boolean d = false;
	private boolean a = false;
	private boolean w = false;
	private boolean s = false;
	
	private boolean shift = false;
	
	private boolean space = false;
	private boolean spaceClicked = false;
	
	// Last Press Number Key
	private int number = 0;
	private boolean numberClicked = false;
	
	private boolean plus = false;
	private boolean plusClicked = false;
	private boolean minus = false;
	private boolean minusClicked = false;
	
	private boolean x = false;
	private boolean xClicked = false;
	private boolean y = false;
	private boolean yClicked = false;
	
	private boolean escape = false;
	
	// Custom Axis, controlled by "wasd"- or Arrow-Keys
	private double xAxis = 0.0;
	private double yAxis = 0.0;
	
	// Per Frame Update
	public void InputUpdate() {
		if ((right || d) && !(left || a))
			xAxis = 1.0;
		else if (!(right || d) && (left || a))
			xAxis = -1.0;
		else
			xAxis = 0.0;
		
		if ((up || w) && !(down || s))
			yAxis = 1.0;
		else if (!(up || w) && (down || s))
			yAxis = -1.0;
		else
			yAxis = 0.0;
	}
	
	// Resets ...-Clicked Variables (they are only active for one Frame)
	public void PostInputUpdate() {
		spaceClicked = false;
		numberClicked = false;
		plusClicked = false;
		minusClicked = false;
		xClicked = false;
		yClicked = false;
	}
	
	// Get Methods
	
	public double GetXAxis() {
		return xAxis;
	}
	
	public double GetYAxis() {
		return yAxis;
	}
	
	public boolean Space() {
		return space;
	}
	
	public boolean SpaceClicked() {
		return spaceClicked;
	}
	
	public boolean Shift() {
		return shift;
	}
	
	public int GetNumber() {
		return number;
	}
	
	public boolean NumberClicked() {
		return numberClicked;
	}
	
	public boolean Plus() {
		return plus;
	}
	
	public boolean PlusClicked() {
		return plusClicked;
	}
	
	public boolean Minus() {
		return minus;
	}
	
	public boolean MinusClicked() {
		return minusClicked;
	}
	
	public boolean X() {
		return x;
	}
	
	public boolean XClicked() {
		return xClicked;
	}
	
	public boolean Y() {
		return y;
	}
	
	public boolean YClicked() {
		return yClicked;
	}
	
	public boolean Escape() {
		return escape;
	}
	
	// Get Custom Keys
	
	public boolean Jump() {
		return space;
	}
	
	public boolean Run() {
		return shift;
	}
	
	public boolean Duck() {
		return yAxis < -0.01;
	}
	
	@Override // Executed when Key is pressed
    public void keyPressed(KeyEvent e) {
    	int key = e.getKeyCode();
    	
    	// Check for Keys
    	
    	if (key == KeyEvent.VK_LEFT) {
        	left = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
        	right = true;
        }
        if (key == KeyEvent.VK_DOWN) {
        	down = true;
        }
        if (key == KeyEvent.VK_UP) {
        	up = true;
        }
        
        if (key == KeyEvent.VK_A) {
        	a = true;
        }
        if (key == KeyEvent.VK_D) {
        	d = true;
        }
        if (key == KeyEvent.VK_S) {
        	s = true;
        }
        if (key == KeyEvent.VK_W) {
        	w = true;
        }
        
        if (key == KeyEvent.VK_SPACE) {
        	space = true;
        	spaceClicked = true;
        }
        
        if (key == KeyEvent.VK_SHIFT) {
        	shift = true;
        }
        
        // Numbers
        if (key == KeyEvent.VK_0) {
        	number = 0;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_1) {
        	number = 1;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_2) {
        	number = 2;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_3) {
        	number = 3;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_4) {
        	number = 4;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_5) {
        	number = 5;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_6) {
        	number = 6;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_7) {
        	number = 7;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_8) {
        	number = 8;
        	numberClicked = true;
        }
        else if (key == KeyEvent.VK_9) {
        	number = 9;
        	numberClicked = true;
        }
        
        if (key == KeyEvent.VK_PLUS) {
        	plus = true;
        	plusClicked = true;
        }
        if (key == KeyEvent.VK_MINUS) {
        	minus = true;
        	minusClicked = true;
        }
        
        if (key == KeyEvent.VK_X) {
        	x = true;
        	xClicked = true;
        }
        if (key == KeyEvent.VK_Y) {
        	y = true;
        	yClicked = true;
        }
        
        if (key == KeyEvent.VK_ESCAPE) {
        	escape = true;
        }
    }
	
    @Override // Executed when Key is released
    public void keyReleased(KeyEvent e) {
    	int key = e.getKeyCode();
    	
    	// Check for Keys
    	
        if (key == KeyEvent.VK_LEFT) {
        	left = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
        	right = false;
        }
        if (key == KeyEvent.VK_DOWN) {
        	down = false;
        }
        if (key == KeyEvent.VK_UP) {
        	up = false;
        }
        
        if (key == KeyEvent.VK_A) {
        	a = false;
        }
        if (key == KeyEvent.VK_D) {
        	d = false;
        }
        if (key == KeyEvent.VK_S) {
        	s = false;
        }
        if (key == KeyEvent.VK_W) {
        	w = false;
        }
        
        if (key == KeyEvent.VK_SPACE) {
        	space = false;
        }
        
        if (key == KeyEvent.VK_SHIFT) {
        	shift = false;
        }
        
        if (key == KeyEvent.VK_PLUS) {
        	plus = false;
        }
        if (key == KeyEvent.VK_MINUS) {
        	minus = false;
        }
        
        if (key == KeyEvent.VK_X) {
        	x = false;
        }
        if (key == KeyEvent.VK_Y) {
        	y = false;
        }
        
        if (key == KeyEvent.VK_ESCAPE) {
        	escape = false;
        }
    }
}
