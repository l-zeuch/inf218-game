package coreEngine;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utility.*;

public class Application extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Window Icon
	public static ImageIcon icon;
	
	// General Application Data
	public static final String applicationName = "Steve Jobs - The Apple Game";
	public static boolean isEditor;
	public static int frameRate;
	public static boolean fullscreen;

	public Application(int frameRate, boolean isEditor, boolean fullscreen) {
		// Initialize Window
		InitializeJFrameUI(frameRate, isEditor, fullscreen);
	}
	
	// Initialize Window
	private void InitializeJFrameUI(int frameRate, boolean isEditor, boolean fullscreen) {
		// Window Size
		Dimension screenSize;
		if (fullscreen) {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenSize.width += 0;
			screenSize.height += 0;
		}
		else {
			screenSize = new Dimension(1280, 720);
		}
		
		// Add new Game Panel
		add(new GamePanel(frameRate, screenSize.width, screenSize.height, isEditor, this));
		
		// Set Window Size from Components Size (GamePanel)
		setResizable(false);
		pack();
		
		// Window Title
		setTitle(applicationName);
		// Window Icon
		setIconImage(icon.getImage());
		
		// Position
		if (fullscreen) {
			setLocation(-4, 0);
		}
		else {
			setLocationRelativeTo(null);
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		// Load Icon
		icon = new ImageIcon("res/textures/JFrame_Icon.png");
		
		// Application Type
		int input = JOptionPane.showOptionDialog(null, "Application Type:", applicationName,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
				new String[] { "Game", "Editor" }, "Game");
		
		// If valid Input
		if (input >= 0) {
			if (input == 0) {
				isEditor = false;
			}
			else {
				isEditor = true;
			}
			
			// Frame Rate
			input = JOptionPane.showOptionDialog(null, "Frame Rate:", applicationName,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
					new String[] { "25fps", "50fps", "100fps" }, "50fps");
			
			// If valid Input
			if (input >= 0) {
				switch (input) {
					case 0:
						frameRate = 25;
						break;
					case 1:
						frameRate = 50;
						break;
					case 2:
						frameRate = 100;
						break;
					default:
						frameRate = 50;
				}
				
				// Window Options
				input = JOptionPane.showOptionDialog(null, "Window Options:", applicationName,
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
						new String[] { "720p", "Fullscreen" }, "720p");
				
				// If valid Input
				if (input >= 0) {
					switch (input) {
						case 0:
							fullscreen = false;
							break;
						case 1:
							fullscreen = true;
							break;
						default:
							fullscreen = false;
					}
					
					int securityOutput = Security.CheckPassword(isEditor);
					
					if (securityOutput == 2) {
						isEditor = false;
					}
					
					// Restrict access to editor
					if (securityOutput != 0) {
						// Start Game
						System.out.println("Starting " + applicationName + "...");
						EventQueue.invokeLater(() -> {
							Application ex = new Application(frameRate, isEditor, fullscreen);
							ex.setVisible(true);
						});
					}
				}
			}
		}
	}

}
