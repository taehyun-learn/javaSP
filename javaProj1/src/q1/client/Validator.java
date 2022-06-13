package card.validator.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import card.validator.utils.CardUtility;

public class Validator {
	public boolean login(String id, String psw) throws NoSuchAlgorithmException, IOException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader("..//CLIENT//INSPECTOR.TXT"));
			String line;
			String encPsw = CardUtility.passwordEncryption(psw);
			while ((line = in.readLine()) != null) {
				String fileId = line.substring(0, 8);
				String filePsw = line.substring(9);
	
				if (id.equals(fileId) && filePsw.equals(encPsw)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) { in.close(); }
		}

		return false;
	}
}