package utility;

import javax.swing.JOptionPane;

public class Security {

	private static final String Strpassword = "editor";

	// Checks the Password for the Editor
	public static int CheckPassword(boolean isEditor) {
		boolean correctPassword = false;
		boolean safeToExecute = true;

		// Loops while Input isn't valid
		do {
			try {
				// If is Editor ask for the Password
				if (isEditor) {
					// Show Password Dialog
					String pwInput = JOptionPane.showInputDialog(null, "Please enter your password:",
							"Access restricted!", JOptionPane.WARNING_MESSAGE);

					// Switch to corresponding Input
					switch (pwInput) {
						// Correct Password
						case Strpassword:
							JOptionPane.showMessageDialog(null, "Password correct!", "Access granted!",
									JOptionPane.PLAIN_MESSAGE);
							correctPassword = true;
							break;
						// Some Easter Egg
						case "sudo exec game.exe":
							JOptionPane.showMessageDialog(null, "executing game.exe...", "Access granted!",
									JOptionPane.ERROR_MESSAGE);
							correctPassword = false;
							isEditor = false;
							break;
						// Incorrect Password
						default:
							JOptionPane.showMessageDialog(null, "Password incorrect!", "Access denied!",
									JOptionPane.ERROR_MESSAGE);
							correctPassword = false;
							break;
					}
					safeToExecute = true;
				}
			}
			catch (Exception e) {
				System.out.println("Error in Utiliy.Security.CheckPassword");
				safeToExecute = false;
			}
		}
		while (!safeToExecute);

		// Return corresponding Integer
		if (!correctPassword && isEditor) {
			return 0; // Incorrect Password
		}
		else if (correctPassword && isEditor) {
			return 1; // Correct Password
		}
		else if (!correctPassword && !isEditor) {
			return 2; // Easter Egg
		}

		return 0;
	}
}
