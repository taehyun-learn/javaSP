package httpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;


public class MyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final Gson gson = new Gson();

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setStatus(200);
		res.getWriter().write(new Date().toString());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServerCloneException, IOException{
		StringBuilder requestBody = new StringBuilder();
		try(BufferedReader reader = req.getReader()){
			String line;
			while((line = reader.readLine())!=null){
				requestBody.append(line);
			}
		}

		Map<String, Object> requestData = gson.fromJson(requestBody.toString(), Map.class);

		requestData.put("processed", true);

		String jsonResponse = gson.toJson(requestData);

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.setStatus(HttpServletResponse.SC_OK)
		res.getWriter().write(jsonResponse);
	}
}
