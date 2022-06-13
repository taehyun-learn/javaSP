package json;

import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class JsonSolve2 {

	public static void main(String[] args) {
		String filePath = "sample.json";
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(filePath));
			JsonObject jsonObj = gson.fromJson(reader, JsonObject.class);

			String name = jsonObj.get("name").getAsString();
			int age = jsonObj.get("age").getAsInt();
			System.out.println("name:"+name + "("+age+")");
			
			JsonArray jsonArr = jsonObj.get("children").getAsJsonArray();
			JsonObject jsonObj2 = jsonArr.get(1).getAsJsonObject();
			name = jsonObj2.get("name").getAsString();
			age = jsonObj2.get("age").getAsInt();
			System.out.println("name:"+name + "("+age+")");
					
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
