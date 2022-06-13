package card.validator.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;


public class CardHttpServer extends Thread { 

	Server server;
	public void run() {
		server = new Server();
		ServerConnector http = new ServerConnector(server);
		http.setHost("127.0.0.1");
		http.setPort(8081);
		server.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(CardServlet.class, "/*");
		server.setHandler(servletHandler);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void quit()
	{
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
