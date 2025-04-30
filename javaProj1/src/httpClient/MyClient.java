package httpClient;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;

public class MyClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();

		Gson gson = new Gson();

		JsonObject requestData = new JsonObject();
		requestData.addProperty("name", "hyun");

		
		ContentResponse contentRes = httpClient.newRequest("http://127.0.0.1:8080/mypath").method(HttpMethod.GET)
		.header(HttpHeader.CONTENT_TYPE, MimeTypes.Type.APPLICATION_JSON_UTF_8.asString())
		.content(new StringContentProvider(gson.toJson(requestData)))
				.send();
		System.out.println(contentRes.getContentAsString());


		JsonObject responseData = gson.fromJson(contentRes.getContentAsString(), JsonObject.class);

		httpClient.stop();
	}
}
