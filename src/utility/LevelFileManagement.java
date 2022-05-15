package utility;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import components.*;
import coreEngine.*;
import enumerations.*;

public class LevelFileManagement {
	
	// Shows Level Select Window
	public static String SelectALevel() {
		// Get Level Folder
		File folder = new File("res/levels/");
		// Get all Files of the Folder
		File[] listOfFiles = folder.listFiles();
		
		// Level Path Names
		String[] levelNames = new String[listOfFiles.length];
		
		// Get Level Path Names
		int i = 0;
		for (File file : listOfFiles) {
    		if (file.isFile()) {
        		levelNames[i] = file.getName();
        		i++;
    		}
		}
		
		// Create Level Names without Underscores and File Ending
		String[] readablelevelNames = new String[listOfFiles.length];
		for (i = 0; i < readablelevelNames.length; i++) {
			String levelName = "";
			
			for (int c = 0; c < levelNames[i].length(); c++) {
				char ch = levelNames[i].charAt(c);
				if (ch == '.') {
					break;
				}
				if (ch == '_') {
					levelName += " ";
				}
				else {
					levelName += ch;
				}
			}
			
			readablelevelNames[i] = levelName;
		}
		
		boolean validInput = true;
		// Load existing Level
		do {
			// Show Selection Window
			String input = (String)JOptionPane.showInputDialog(null, "Select a Level:", "Select Level",
			        JOptionPane.QUESTION_MESSAGE, Application.icon, readablelevelNames, readablelevelNames[0]);
			
			if (input != null) {
				validInput = true;
				
				int index = -1;
				
				// Get corresponding Level Path Name
				for (i = 0; i < readablelevelNames.length; i++) {
					if (readablelevelNames[i] == input) {
						index = i;
						break;
					}
				}
				
				// Return Level Path Name
				return levelNames[index];
			}
			else {
				validInput = false;
			}
		}
		while (!validInput);
		
		return null;
	}
	
	// Load and setup a Level and return its Coin Count
	public static int LoadTileMap(String levelName, LevelData levelData, List<ColliderObject> entities, List<ColliderObject> levelObjects, Player player) throws FileNotFoundException {
		int coinCount = 0;
		
		// Read File and Scanner for File Reading
		File file = new File("res/levels/" + levelName);
	    Scanner sc = new Scanner(file);
	    
	    // Read Level Name
	    String l0 = sc.nextLine();
	    levelData.levelName = l0.substring(10, l0.length() - 1);
	    
	    // Check for valid Tile Map Data
	    String l1 = sc.nextLine();
	    if (l1.substring(0, 11).equalsIgnoreCase("tileMapData")) {
	    	System.out.println("Load Level...");
	    	
	    	// Read Tile Map Dimensions
	    	int tileMapWidth = Integer.parseInt(l1.substring(12, 16));
	    	int tileMapHeight = Integer.parseInt(l1.substring(17, 21));
	    	levelData.SetTileMapWidth(tileMapWidth);
	    	levelData.SetTileMapHeight(tileMapHeight);
	    	
	    	// Initialize Tile Map Data
	    	levelData.tileMapDataSolid = new byte[tileMapHeight][tileMapWidth];
	    	levelData.tileMapSpriteIndexSolid = new byte[tileMapHeight][tileMapWidth];
	    	levelData.tileMapDataBackground = new byte[tileMapHeight][tileMapWidth];
	    	levelData.tileMapSpriteIndexBackground = new byte[tileMapHeight][tileMapWidth];
	    	
	    	// Temporary Variables
	    	String line;
	    	String buffer;
	    	int xTileMap;
	    	int yTileMap;
	    	
	    	int y = 0;
	    	
	    	// Read Solid Tiles/Blocks
	    	byte solid;
	    	byte solidIndex;
	    	while (y < tileMapHeight) {
	    		// Get current Data Line
	    		line = sc.nextLine();
	    		
	    		// If line doesn't begin Skip Symbol (">")
	    		if (line.charAt(0) != '>') {
	    			// Iterate through line and Read single Tiles/Blocks
	    			for (int x = 0; x < tileMapWidth; x++) {
	    				// Get current Data Block (<spriteSheetID><tileID>)
	    				buffer = line.substring(x * 4, (x + 1) * 4 - 1);
	    				
	    				// Compute current Coordinate
	    				xTileMap = x;
		    			yTileMap = tileMapHeight - y - 1;
		    			
		    			// Reset Tile Variables
		    			solid = 0;
		    	    	solidIndex = 0;
		    	    	
		    	    	if (buffer.charAt(0) == '0') { // Current Tile is Air
		    	    		solid = 0;
			    	    	solidIndex = 0;
		    	    	}
		    	    	else { // Current Tile is solid Tile/Block
		    	    		// Encode Data Block
		    	    		solid = (byte)Integer.parseInt(buffer.substring(1, 3));
		    	    		solidIndex = CharToSpriteSheetIndex(buffer.charAt(0));
		    	    	}
		    			
		    	    	// Apply to Tile Map
		    	    	levelData.tileMapDataSolid[yTileMap][xTileMap] = solid;
		    	    	levelData.tileMapSpriteIndexSolid[yTileMap][xTileMap] = solidIndex;
			    	}
	    			y++;
	    		}
	    	}
	    	
	    	// Read Background Tiles/Blocks
	    	y = 0;
	    	byte background;
	    	byte backgroundIndex;
	    	while (y < tileMapHeight) {
	    		// Get current Data Line
	    		line = sc.nextLine();
	    		
	    		// If line doesn't begin Skip Symbol (">")
	    		if (line.charAt(0) != '>') {
	    			// Iterate through line and Read single Tiles/Blocks
	    			for (int x = 0; x < tileMapWidth; x++) {
		    			// Get current Data Block (<spriteSheetID><tileID>)
	    				buffer = line.substring(x * 4, (x + 1) * 4 - 1);
	    				
	    				// Compute current Coordinate
		    			xTileMap = x;
		    			yTileMap = tileMapHeight - y - 1;
		    			
		    			// Reset Tile Variables
		    			background = 0;
		    			backgroundIndex = 0;
		    	    	
		    			if (buffer.charAt(0) == '0') { // Current Tile is Air
		    	    		background = 0;
		    	    		backgroundIndex = 0;
		    	    	}
		    	    	else { // Current Tile is normal Background Tile/Block
		    	    		// Encode Data Block
		    	    		background = (byte)Integer.parseInt(buffer.substring(1, 3));
		    	    		backgroundIndex = CharToSpriteSheetIndex(buffer.charAt(0));
		    	    	}
		    			
		    			// Apply to Tile Map
		    			levelData.tileMapDataBackground[yTileMap][xTileMap] = background;
		    			levelData.tileMapSpriteIndexBackground[yTileMap][xTileMap] = backgroundIndex;
			    	}
	    			y++;
	    		}
	    	}
	    	
	    	// Skip some Lines
	    	line = sc.nextLine(); // {
	    	line = sc.nextLine(); // entities{
	    	
	    	// Read Entities/Level Objects
	    	double xPos = 0.0;
	    	double yPos = 0.0;
	    	double xScale = 0.0;
	    	double yScale = 0.0;
	    	while (true) {
	    		// Get current Data Line
	    		line = sc.nextLine();
	    		
	    		// If is at the End of the Entities/Level Objects break loop
	    		if (line.charAt(0) == '}') {
	    			break;
	    		}
	    		
	    		// Encode Entity/Level Object Position
	    		xPos = Double.parseDouble(line.substring(4, 10)) / 100.0;
	    		yPos = Double.parseDouble(line.substring(11, 17)) / 100.0;
	    		
	    		// Encode Entity/Level Object Orientation (Mirroring)
	    		if (line.charAt(18) == '+')
	    			xScale = 1.0;
	    		else
	    			xScale = -1.0;
	    		if (line.charAt(20) == '+')
	    			yScale = 1.0;
	    		else
	    			yScale = -1.0;
	    		
	    		// Create Entity/Level Object and increase Coin Count if Entity is a Coin
	    		coinCount += StringToGameObject(line.substring(0, 3), entities, levelObjects, player, xPos, yPos, xScale, yScale);
	    	}
	    }
	    
	    // Close Scanner for Performance
	    sc.close();
	    
	    // Get Level Name in Console
	    System.out.println(levelName);
	    
	    return coinCount;
	}
	
	// Save a Level
	public static void SaveTileMap(String levelPathName, String levelName, LevelData levelData, List<ColliderObject> entities, List<ColliderObject> levelObjects) throws IOException {
		// Variable for current Line
		String line = "";
		
		// Create Buffered Writer for File Saving
		BufferedWriter b = new BufferedWriter(new FileWriter("res/levels/" + levelPathName));
		
		// Write Level Name
		line = "levelName:" + levelName + ";";
		b.write(line);
		b.newLine();
		
		// Write Tile Map Dimensions
		String width = Integer.toString(levelData.GetTileMapWidth());
		width = SetStringLength(width, 4);
		String height = Integer.toString(levelData.GetTileMapHeight());
		height = SetStringLength(height, 4);
		line = "tileMapData(" + width + "," + height + "){";
		b.write(line);
		b.newLine();
		
		// Variable for current Tile/Block
		String block = "";
		
		// Write Solid Tiles/Blocks Headline
		b.write(">");
		b.newLine();
		b.write(">> Solid");
		b.newLine();
		b.write(">");
		b.newLine();
		
		// Write Solid Tile Map to File
		for (int y = levelData.GetTileMapHeight() - 1; y >= 0; y--) {
			// Reset Line
			line = "";
			
			// Iterate through Solid Tile Map Row
			for (int x = 0; x < levelData.GetTileMapWidth(); x++) {
				if (levelData.tileMapDataSolid[y][x] != 0) { // Current Tile/Block is solid Tile/Block
					// Decode current Tile/Block
					block = SpriteSheetIndexToString(levelData.tileMapSpriteIndexSolid[y][x]);
					block += SetStringLength(Integer.toString(levelData.tileMapDataSolid[y][x]), 2);
				
					// Add Tile/Block to Line
					line += block;
				}
				else { // Current Tile/Block is Air
					// Add Air Tile/Block to Line
					line += "000";
				}
				
				// Add Data Block Separator to Line
				if (x < levelData.GetTileMapWidth() - 1) {
					if ((x + 1) % 5 == 0) {
						line += "|";
					}
					else {
						line += ".";
					}
				}
			}
			
			// Write current Line
			b.write(line);
			b.newLine();
			
			if (y > 0) {
				if ((levelData.GetTileMapHeight() - y) % 5 == 0) {
					// Write Separator Line (every 5th Row)
					line = ">";
					b.write(line);
					b.newLine();
				}
			}
		}
		
		// Reset Variable for current Tile/Block
		block = "";
		
		// Write Background Tiles/Blocks Comments
		b.write(">");
		b.newLine();
		b.write(">> Background");
		b.newLine();
		b.write(">");
		b.newLine();
		
		// Write Background Tile Map to File
		for (int y = levelData.GetTileMapHeight() - 1; y >= 0; y--) {
			// Reset Line
			line = "";
			
			// Iterate through Background Tile Map Row
			for (int x = 0; x < levelData.GetTileMapWidth(); x++) {
				if (levelData.tileMapDataBackground[y][x] != 0) { // Current Tile/Block is Background Tile/Block
					// Decode current Tile/Block
					block = SpriteSheetIndexToString(levelData.tileMapSpriteIndexBackground[y][x]);
					block += SetStringLength(Integer.toString(levelData.tileMapDataBackground[y][x]), 2);
				
					// Add Tile/Block to Line
					line += block;
				}
				else { // Current Tile/Block is Air
					// Add Air Tile/Block to Line
					line += "000";
				}
				
				// Add Data Block Separator to Line
				if (x < levelData.GetTileMapWidth() - 1) {
					if ((x + 1) % 5 == 0) {
						line += "|";
					}
					else {
						line += ".";
					}
				}
			}
			
			// Write current Line
			b.write(line);
			b.newLine();
			
			if (y > 0) {
				if ((levelData.GetTileMapHeight() - y) % 5 == 0) {
					// Write Separator Line (every 5th Row)
					line = ">";
					b.write(line);
					b.newLine();
				}
			}
		}
		
		// Write Close-Bracket
		b.write("}");
		b.newLine();
		
		// Write Entities Headline
		b.write("entities{");
		b.newLine();
		
		// Write Entities to File
		for (int i = 0; i < entities.size(); i++) {
			// Get current Entity as Game Object
			GameObject go = entities.get(i);
			// Decode Game Object Type
			line = GameObjectTypeToString(go.GetType());
			
			line += "(";
			
			// Decode Entity Position
			line += SetStringLength(Integer.toString((int)(go.position.x * 100)), 6); // To cm
			line += ",";
			line += SetStringLength(Integer.toString((int)(go.position.y * 100)), 6); // To cm
			line += ",";
			
			// Decode Entity Orientation (Mirroring)
			if (go.scale.x >= 0.0)
				line += "+";
			else
				line += "-";
			line += ",";
			if (go.scale.y >= 0.0)
				line += "+";
			else
				line += "-";
			
			line += ");";
			
			// Write Entity as Line
			b.write(line);
			b.newLine();
		}
		
		// Write Level Objects to File
		for (int i = 0; i < levelObjects.size(); i++) {
			// Get current Level Object as Game Object
			GameObject go = levelObjects.get(i);
			// Decode Game Object Type
			line = GameObjectTypeToString(go.GetType());
			
			line += "(";
			
			// Decode Level Object Position
			line += SetStringLength(Integer.toString((int)(go.position.x * 100)), 6); // To cm
			line += ",";
			line += SetStringLength(Integer.toString((int)(go.position.y * 100)), 6); // To cm
			line += ",";
			
			// Decode Level Object Orientation (Mirroring)
			if (go.scale.x >= 0.0)
				line += "+";
			else
				line += "-";
			line += ",";
			if (go.scale.y >= 0.0)
				line += "+";
			else
				line += "-";
			
			line += ");";
			
			// Write Level Object as Line
			b.write(line);
			b.newLine();
		}
		
		b.write("}");
		
		// Close Buffered Writer for Performance
		b.close();
	}
	
	// Encodes the Sprite Sheet Index from a Char
	private static byte CharToSpriteSheetIndex(char c) {
		switch(c) {
			case 'a':
				return 0;
			case 'b':
				return 1;
			case 'c':
				return 2;
			case 'd':
				return 3;
			default:
				return 0;
		}
	}
	
	// Decodes the Sprite Sheet Index to a String
	private static String SpriteSheetIndexToString(byte spriteSheetIndex) {
		switch(spriteSheetIndex) {
			case 0:
				return "a";
			case 1:
				return "b";
			case 2:
				return "c";
			case 3:
				return "d";
			default:
				return "0";
		}
	}
	
	// Encodes the Game Object Type from a String and return 1 if Game Object Type is a Coin
	private static int StringToGameObject(String s, List<ColliderObject> entities, List<ColliderObject> levelObjects, Player player, double xPos, double yPos, double xScale, double yScale) {
		switch(s.toLowerCase()) {
			case "ply":
				player.position.x = xPos;
				player.position.y = yPos;
				return 0;
			case "cn_":
				levelObjects.add(new Coin(new Vector2D(xPos, yPos), xScale, yScale));
				return 1;
			case "hrt":
				levelObjects.add(new Heart(new Vector2D(xPos, yPos), xScale, yScale));
				return 0;
			case "rtr":
				levelObjects.add(new Rotor(new Vector2D(xPos, yPos), xScale, yScale));
				return 0;
			case "rbt":
				entities.add(new RobotEnemy(new Vector2D(xPos, yPos), xScale));
				return 0;
			case "chc":
				levelObjects.add(new Checkpoint(new Vector2D(xPos, yPos)));
				return 0;
			case "gl_":
				levelObjects.add(new Goal(new Vector2D(xPos, yPos)));
				return 0;
			default:
				return 0;
		}
	}
	
	// Decodes the Game Object Type to a String
	private static String GameObjectTypeToString(GameObjectType type) {
		switch (type) {
			case Player:
				return "ply";
			case Coin:
				return "cn_";
			case Heart:
				return "hrt";
			case Rotor:
				return "rtr";
			case RobotEnemy:
				return "rbt";
			case CheckPoint:
				return "chc";
			case Goal:
				return "gl_";
			default:
				return "000";
		}
	}
	
	// Adds "0"s to a Number-String to reach the given String Length
	private static String SetStringLength(String s, int length) {
		while (s.length() < length) {
			s = "0" + s;
		}
		return s;
	}
}
