package toolkit.examples;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import toolkit.*;

/**
 * 케이스 1: HttpServer를 통해 들어오는 데이터에 대해 내부 멀티스레드 처리
 *
 * 시나리오: POST로 숫자 리스트를 받아서 → 각 숫자를 별도 스레드에서 처리 → 결과 취합 → 응답
 */
public class Case1_HttpServerMultiThread {

    // === 서블릿 ===
    public static class MyServlet extends HttpServerHelper.JsonServlet {
        static ReentrantLock lock = new ReentrantLock();

        @Override
        protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                         String[] segments, JsonObject body) throws IOException {
            // 1. 요청에서 데이터 추출
            JsonArray items = JsonHelper.getArray(body, "items");

            // 2. 결과를 모을 공유 리스트 (스레드 안전)
            java.util.List<String> results = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

            // 3. 각 항목을 별도 스레드에서 처리
            java.util.List<Runnable> tasks = new java.util.ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                final String item = items.get(i).getAsString();
                tasks.add(() -> {
                    // 무거운 처리 로직
                    String processed = item.toUpperCase() + "_처리완료";

                    // 공유 자원에 쓸 때 lock 사용
                    ThreadHelper.withLock(lock, () -> {
                        results.add(processed);
                    });
                });
            }

            try {
                // 4. 모든 스레드 실행 후 완료 대기
                ThreadHelper.runAndWaitAll(tasks);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 5. 결과를 JSON으로 응답
            JsonObject response = JsonHelper.newObject();
            response.addProperty("Result", "Ok");
            response.addProperty("Count", results.size());
            JsonArray resArr = JsonHelper.newArray();
            for (String r : results) resArr.add(r);
            response.add("Results", resArr);
            return response;
        }
    }

    // === 메인 ===
    public static void main(String[] args) throws Exception {
        // 서버 시작
        HttpServerHelper server = new HttpServerHelper();
        server.startInThread("127.0.0.1", 8080, "/*", MyServlet.class);
        System.out.println("서버 시작: http://127.0.0.1:8080");

        // 테스트 요청
        Thread.sleep(1000); // 서버 기동 대기
        HttpClientHelper client = new HttpClientHelper();

        JsonObject reqBody = JsonHelper.newObject();
        JsonArray items = JsonHelper.newArray();
        items.add("apple");
        items.add("banana");
        items.add("cherry");
        reqBody.add("items", items);

        JsonObject res = client.postJson("http://127.0.0.1:8080/process", reqBody);
        System.out.println("응답: " + JsonHelper.toPrettyJson(res));

        client.stop();
        server.stop();
    }
}
