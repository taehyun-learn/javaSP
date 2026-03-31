package toolkit.examples;

import toolkit.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;

/**
 * [Q5 템플릿] 서버 + 클라이언트 통합 - 하나의 main에서 모두 처리
 *
 * 시험에서 서버/클라이언트 양쪽 다 구현해야 하는 경우
 *   - 서버를 별도 스레드로 띄우고
 *   - 같은 main에서 클라이언트 요청
 */
public class Template_Q5_ServerAndClient {

    static Map<String, JsonObject> storage = Collections.synchronizedMap(new HashMap<>());
    static ReentrantLock lock = new ReentrantLock();

    // === 서블릿 (inner class) ===
    public static class ApiServlet extends HttpServerHelper.JsonServlet {

        @Override
        protected JsonObject handleGet(HttpServletRequest req, HttpServletResponse res,
                                        String[] seg) throws IOException {
            JsonObject r = JsonHelper.newObject();
            String cmd = seg.length > 0 ? seg[0] : "";

            if (cmd.equals("get") && seg.length > 1) {
                String key = seg[1];
                JsonObject data = storage.get(key);
                if (data != null) {
                    r.addProperty("Result", "Ok");
                    r.add("Data", data);
                } else {
                    r.addProperty("Result", "NotFound");
                }
            }
            return r;
        }

        @Override
        protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                         String[] seg, JsonObject body) throws IOException {
            JsonObject r = JsonHelper.newObject();
            String cmd = seg.length > 0 ? seg[0] : "";

            if (cmd.equals("save")) {
                String key = JsonHelper.getString(body, "key", "");
                ThreadHelper.withLock(lock, () -> {
                    storage.put(key, body);
                });
                r.addProperty("Result", "Ok");
                r.addProperty("Key", key);

                // 파일에도 저장
                FileHelper.ensureDir("OUTPUT");
                JsonHelper.writeFile("OUTPUT/" + key + ".json", body);
            }
            return r;
        }
    }

    // === 메인 ===
    public static void main(String[] args) throws Exception {

        // 1. 서버를 별도 스레드로 시작
        HttpServerHelper server = new HttpServerHelper();
        server.startInThread("127.0.0.1", 8080, "/*", ApiServlet.class);
        Thread.sleep(1000); // 서버 기동 대기
        System.out.println("서버 시작됨");

        // 2. 클라이언트 생성
        HttpClientHelper client = new HttpClientHelper();

        // 3. 파일 읽어서 서버에 등록
        List<String> lines = FileHelper.readLines("DATA.TXT");
        for (String line : lines) {
            String[] parts = line.split(" ");
            JsonObject body = JsonHelper.newObject();
            body.addProperty("key", parts[0]);
            body.addProperty("name", parts[1]);
            body.addProperty("value", Integer.parseInt(parts[2]));

            JsonObject res = client.postJson("http://127.0.0.1:8080/save", body);
            System.out.println("등록: " + parts[0] + " → " + JsonHelper.getString(res, "Result", ""));
        }

        // 4. 등록된 데이터 조회
        JsonObject getRes = client.getJson("http://127.0.0.1:8080/get/E001");
        System.out.println("조회 결과: " + JsonHelper.toPrettyJson(getRes));

        // 5. 정리
        client.stop();
        server.stop();
    }
}
