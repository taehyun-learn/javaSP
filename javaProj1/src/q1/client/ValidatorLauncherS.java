package card.validator.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class ValidatorLauncher {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		Validator validator = new Validator();
		Scanner scanner = new Scanner(System.in);
		String strId, strPassword;

		while (true) {
			if (scanner.hasNext()) {
				String [] words = scanner.nextLine().split(" "); // id, password
				
				strId = words[0]; 
				strPassword = words[1]; 
	
				if (validator.login(strId, strPassword)) {
					System.out.println("LOGIN SUCCESS");
					break;
				} else {
					System.out.println("LOGIN FAIL");
				}
			}
		}
	}
}