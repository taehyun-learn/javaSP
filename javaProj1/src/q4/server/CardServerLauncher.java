package card.validator.server;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class CardServerLauncher {	
	public static void main(String[] args) throws Exception {
		// Socket Server Start
		CardSocketServer cardSocketServer = new CardSocketServer();		
		Thread threadSocket = new Thread(cardSocketServer);
		threadSocket.start();
		
		// Http Server Start
		CardHttpServer cardHttpServer = new CardHttpServer();
		Thread threadHttp = new Thread(cardHttpServer);
		threadHttp.start();
		
		// For Report
		ValidatorReport report = new ValidatorReport();
		
		Scanner scanner = new Scanner(System.in);		
		String line;
		while ((line = scanner.nextLine()) != null) {
			if (line.equals("QUIT")) {
				cardSocketServer.close();
				cardHttpServer.quit();
				break;
			} else { 
				System.out.println("If you want to quit, input \"QUIT\"");
			}
		}
	}
}