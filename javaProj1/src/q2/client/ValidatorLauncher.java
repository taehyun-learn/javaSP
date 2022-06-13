package card.validator.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Scanner;

public class ValidatorLauncher {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ParseException {
		Scanner scanner = new Scanner(System.in);
		String line, strId, strPassword;

		Validator validator = new Validator();
		while (true) {
			
			String [] words = scanner.nextLine().split(" "); // id, password
			
			strId = words[0]; 
			strPassword = words[1]; 

			if (validator.login(strId, strPassword)) {
				System.out.println("LOGIN SUCCESS");
				break;
			}
			else {
				System.out.println("LOGIN FAIL");
			}			
		}
		
		// Inspection
		while (true) {
			line = scanner.nextLine();	// busId
			
			if (line.equals("LOGOUT")) {
				validator.logout();
				break;
			}
			
			validator.getOnBus(line);

			// Card Validation
			while (true) {
				line = scanner.nextLine();	//cardInfo
				
				if (line.equals("DONE")) {
					validator.getOffBus();
					break;
				}
				
				validator.inspectCard(line);
			}
		}
	} 
}