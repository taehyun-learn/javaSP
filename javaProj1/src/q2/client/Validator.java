package card.validator.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import card.validator.utils.CardUtility;

public class Validator {
	private String insId;
	private String onBusId;
	private String onBusTime;
	
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
					insId = id;
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
	
	public void logout() {
		insId = null;
	}
	
	public void getOnBus(String busId) {
		if (busId.length() < 7 || busId.substring(0, 4).equals("BUS_") != true) {
			System.out.println("Wrong Bus ID");
			return;
		}
		
		onBusId = busId;
		onBusTime = CardUtility.getCurrentDateTimeString();// Get Start Time
	}
	
	public void getOffBus() {
		onBusId = null;
		onBusTime = null;
	}

	// cardInfo : [카드ID(8)][버스번호(7)][승차/하차 코드(1)][최근 승차시각(14)]
	// sample : CARD_001BUS_001N20171019143610
	public void inspectCard(String cardInfo) throws IOException, ParseException {
		if (cardInfo.length() != 30) {
			System.out.println("Wrong Card Info");
			return;
		}
		
		if (onBusId != null) {
			String validateCode;
	
			// cardInfo parsing
			// String cardID = cardInfo.substring(0, 8);
			String cardBusID = cardInfo.substring(8, 15);
			String code = cardInfo.substring(15, 16);
			String rideTime = cardInfo.substring(16);
	
			// Get Inspect Time
			String inpectTime = CardUtility.getCurrentDateTimeString();
	
			// Validation
			// 1. Bus ID Match
			if (onBusId.equals(cardBusID)) {
				// 2. Check Aboard Code
				if (code.equals("N")) {
					// 3. Time Difference
					if (CardUtility.hourDiff(inpectTime, rideTime) < 3) {
						validateCode = "R1";
					} else {
						validateCode = "R4";
					}
				} else {
					validateCode = "R3";
				}
			} else {
				validateCode = "R2";
			}
	
			saveFile(cardInfo, validateCode, inpectTime);
		}
	}

	private void saveFile(String cardInfo, String validateCode, String inpectTime) throws IOException {
		// Create Folder
		File destFolder = new File("..//" + insId);
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}

		// File Writing
		String strFilename = destFolder + "//" + insId + "_" + onBusTime + ".TXT";
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(strFilename, true);
			fw.write(insId + "#" + onBusId + "#" + cardInfo + "#" + validateCode + "#" + inpectTime + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) { fw.close(); }
		}
	}
}