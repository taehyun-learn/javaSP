package card.validator.server;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class CardServerLauncher {	
	public static void main(String[] args) throws Exception {
		CardSocketServer cardServer = new CardSocketServer();		
		Thread thread = new Thread(cardServer);
		thread.start();
		
		Scanner scanner = new Scanner(System.in);
		
		String line;
		while ((line = scanner.nextLine()) != null) {
			if (line.equals("QUIT")) {
				cardServer.close();
				break;
			}
		}
	}
	
}