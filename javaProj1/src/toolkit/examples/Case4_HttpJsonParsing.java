package toolkit.examples;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import toolkit.*;

/**
 * 케이스 4: HttpServer를 통해 받은 응답값에 대한 JSON 파싱
 *
 * 방법1: JsonObject로 직접 파싱 (필드를 하나씩 꺼냄)
 * 방법2: VO 클래스로 자동 매핑 (Gson이 자동으로 필드 매핑)
 */
public class Case4_HttpJsonParsing {

    // === 응답용 VO 클래스 (시험 때 JSON 구조에 맞게 수정) ===
    public static class ReportResponse {
        public String Result;
        public String ReportID;
        public String Report;
        public List<ReportItem> Items;

        public static class ReportItem {
            public String id;
            public String name;
            public int count;
        }
    }

    // === 서블릿: JSON 데이터를 받아서 VO로 파싱 ===
    public static class MyServlet extends HttpServerHelper.JsonServlet {

        @Override
        protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                         String[] segments, JsonObject body) throws IOException {

            // --- 방법 1: JsonObject에서 직접 꺼내기 ---
            String name = JsonHelper.getString(body, "name", "");
            int age = JsonHelper.getInt(body, "age", 0);
            JsonArray items = JsonHelper.getArray(body, "items");
            JsonObject nested = JsonHelper.getObject(body, "address");
            String city = JsonHelper.getString(nested, "city", "");

            System.out.println("방법1: " + name + ", " + age + ", " + city);

            // --- 방법 2: VO 클래스로 자동 매핑 ---
            // body를 다시 문자열로 → VO로 파싱
            SampleVo vo = JsonHelper.parse(body.toString(), SampleVo.class);
            System.out.println("방법2: " + vo);

            // 응답
            JsonObject response = JsonHelper.newObject();
            response.addProperty("Result", "Ok");
            response.addProperty("Processed", name);
            return response;
        }
    }

    // === 메인: 클라이언트에서 서버 응답을 JSON 파싱 ===
    public static void main(String[] args) throws Exception {
        // 서버 시작
        HttpServerHelper server = new HttpServerHelper();
        server.startInThread("127.0.0.1", 8080, "/*", MyServlet.class);
        Thread.sleep(1000);

        // --- 요청 보내기 ---
        HttpClientHelper client = new HttpClientHelper();

        JsonObject reqBody = JsonHelper.newObject();
        reqBody.addProperty("name", "홍길동");
        reqBody.addProperty("age", 30);
        reqBody.addProperty("married", true);

        JsonObject addr = JsonHelper.newObject();
        addr.addProperty("city", "서울");
        addr.addProperty("zip", "12345");
        reqBody.add("address", addr);

        JsonArray children = JsonHelper.newArray();
        JsonObject child1 = JsonHelper.newObject();
        child1.addProperty("name", "홍아들");
        child1.addProperty("age", 5);
        children.add(child1);
        reqBody.add("children", children);

        // POST 요청
        JsonObject res = client.postJson("http://127.0.0.1:8080/submit", reqBody);

        // --- 서버 응답 파싱 ---

        // 방법 1: JsonObject에서 직접 꺼내기
        String result = JsonHelper.getString(res, "Result", "");
        String processed = JsonHelper.getString(res, "Processed", "");
        System.out.println("응답 - Result: " + result + ", Processed: " + processed);

        // 방법 2: VO로 매핑
        ReportResponse report = JsonHelper.parse(res.toString(), ReportResponse.class);
        System.out.println("응답 VO - Result: " + report.Result);

        client.stop();
        server.stop();
    }
}
