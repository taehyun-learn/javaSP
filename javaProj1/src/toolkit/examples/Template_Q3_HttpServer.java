package toolkit.examples;

import toolkit.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;

/**
 * [Q3 템플릿] HTTP 서버 - 요청 받아서 처리
 *
 * 시험에서 주어지는 형태:
 *   - main 함수만 있는 파일이 주어짐
 *   - HTTP 서버를 띄우고 요청에 따라 파일 읽기/가공/응답
 *
 * ★ 핵심: 서블릿 클래스를 main과 같은 파일에 static inner class로 선언
 */
public class Template_Q3_HttpServer {

    // === 공유 데이터 (서블릿에서 접근 필요한 것) ===
    static List<String> dataList = Collections.synchronizedList(new ArrayList<>());
    static ReentrantLock lock = new ReentrantLock();

    // === 서블릿: GET/POST 처리 ===
    public static class MyServlet extends HttpServerHelper.JsonServlet {

        // ----- GET 처리 -----
        // 예: GET /search/홍길동  → pathSegments = ["search", "홍길동"]
        // 예: GET /report/20240115 → pathSegments = ["report", "20240115"]
        @Override
        protected JsonObject handleGet(HttpServletRequest req, HttpServletResponse res,
                                        String[] pathSegments) throws IOException {
            JsonObject result = JsonHelper.newObject();

            if (pathSegments.length == 0) {
                result.addProperty("Result", "Error");
                result.addProperty("Message", "No command");
                return result;
            }

            String command = pathSegments[0];

            switch (command) {
                case "list": {
                    // 전체 목록 반환
                    result.addProperty("Result", "Ok");
                    result.addProperty("Count", dataList.size());
                    JsonArray arr = JsonHelper.newArray();
                    for (String item : dataList) arr.add(item);
                    result.add("Items", arr);
                    break;
                }
                case "search": {
                    // 검색
                    String keyword = pathSegments.length > 1 ? pathSegments[1] : "";
                    JsonArray found = JsonHelper.newArray();
                    for (String item : dataList) {
                        if (item.contains(keyword)) found.add(item);
                    }
                    result.addProperty("Result", "Ok");
                    result.add("Found", found);
                    break;
                }
                case "report": {
                    // 파일에서 데이터 읽어서 보고서 생성
                    String date = pathSegments.length > 1 ? pathSegments[1] : DateHelper.nowDate();
                    List<String> lines = FileHelper.readLines("DATA.TXT");

                    result.addProperty("Result", "Ok");
                    result.addProperty("Date", date);
                    result.addProperty("TotalLines", lines.size());
                    break;
                }
                default:
                    result.addProperty("Result", "Error");
                    result.addProperty("Message", "Unknown command: " + command);
            }

            return result;
        }

        // ----- POST 처리 -----
        // body 예: {"name": "홍길동", "action": "register"}
        @Override
        protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                         String[] pathSegments, JsonObject body) throws IOException {
            JsonObject result = JsonHelper.newObject();
            String command = pathSegments.length > 0 ? pathSegments[0] : "";

            switch (command) {
                case "add": {
                    String name = JsonHelper.getString(body, "name", "");
                    ThreadHelper.withLock(lock, () -> {
                        dataList.add(name);
                    });
                    result.addProperty("Result", "Ok");
                    result.addProperty("Added", name);
                    break;
                }
                case "submit": {
                    // body 전체를 파일에 저장
                    String fileName = "SUBMIT_" + DateHelper.nowDateTime() + ".json";
                    JsonHelper.writeFile(fileName, body);
                    result.addProperty("Result", "Ok");
                    result.addProperty("SavedTo", fileName);
                    break;
                }
                default:
                    result.addProperty("Result", "Error");
                    result.addProperty("Message", "Unknown command");
            }

            return result;
        }
    }

    // === 메인 ===
    public static void main(String[] args) throws Exception {
        // 초기 데이터 로드
        if (new File("DATA.TXT").exists()) {
            dataList.addAll(FileHelper.readLines("DATA.TXT"));
        }

        // 서버 시작 (블로킹 - 서버가 종료될 때까지 대기)
        System.out.println("서버 시작: http://127.0.0.1:8080");
        HttpServerHelper server = new HttpServerHelper();
        server.startAndWait("127.0.0.1", 8080, "/*", MyServlet.class);
    }
}
