package toolkit.examples;

import toolkit.*;
import com.google.gson.*;
import java.util.*;

/**
 * [Q4 템플릿] HTTP 클라이언트 - 서버에 요청 보내서 응답 처리
 *
 * 시험에서 주어지는 형태:
 *   - main 함수만 있는 파일이 주어짐
 *   - 서버(이미 실행중)에 HTTP 요청 보내서 응답 JSON 처리
 */
public class Template_Q4_HttpClient {

    // === 응답 VO (시험에서 JSON 구조에 맞게 수정) ===
    static class ServerResponse {
        public String Result;
        public int Count;
        public List<Item> Items;

        static class Item {
            public String id;
            public String name;
            public int value;
        }
    }

    public static void main(String[] args) throws Exception {
        String serverUrl = "http://127.0.0.1:8080";

        HttpClientHelper client = new HttpClientHelper();

        // ============================================================
        // 패턴 A: GET 요청 → 응답 처리
        // ============================================================
        // URL에 파라미터를 경로로 전달
        JsonObject getRes = client.getJson(serverUrl + "/report/20240115");
        System.out.println("GET 응답: " + getRes);

        // 응답에서 값 꺼내기
        String result = JsonHelper.getString(getRes, "Result", "");
        if (result.equals("Ok")) {
            int count = JsonHelper.getInt(getRes, "TotalLines", 0);
            System.out.println("총 " + count + "건");
        }

        // ============================================================
        // 패턴 B: POST 요청 (JSON body) → 응답 처리
        // ============================================================
        JsonObject postBody = JsonHelper.newObject();
        postBody.addProperty("name", "홍길동");
        postBody.addProperty("age", 30);

        // 중첩 객체 추가
        JsonObject detail = JsonHelper.newObject();
        detail.addProperty("dept", "개발");
        detail.addProperty("level", 3);
        postBody.add("detail", detail);

        // 배열 추가
        JsonArray skills = JsonHelper.newArray();
        skills.add("Java");
        skills.add("Python");
        postBody.add("skills", skills);

        JsonObject postRes = client.postJson(serverUrl + "/submit", postBody);
        System.out.println("POST 응답: " + postRes);

        // ============================================================
        // 패턴 C: 응답을 VO로 매핑
        // ============================================================
        JsonObject listRes = client.getJson(serverUrl + "/list");
        ServerResponse vo = JsonHelper.parse(listRes.toString(), ServerResponse.class);
        if (vo.Items != null) {
            for (ServerResponse.Item item : vo.Items) {
                System.out.println(item.name + ": " + item.value);
            }
        }

        // ============================================================
        // 패턴 D: 파일 읽어서 서버에 전송 → 응답 저장
        // ============================================================
        List<String> lines = FileHelper.readLines("DATA.TXT");
        JsonObject uploadBody = JsonHelper.newObject();
        JsonArray dataArr = JsonHelper.newArray();
        for (String line : lines) {
            dataArr.add(line);
        }
        uploadBody.add("data", dataArr);
        uploadBody.addProperty("date", DateHelper.nowDate());

        JsonObject uploadRes = client.postJson(serverUrl + "/submit", uploadBody);
        JsonHelper.writeFile("response.json", uploadRes);  // 응답을 파일에 저장

        // ============================================================
        // 패턴 E: 루프 - 콘솔 메뉴 + 서버 요청
        // ============================================================
        boolean running = true;
        while (running) {
            int choice = ConsoleHelper.menu("메뉴", "조회", "등록", "검색", "종료");
            switch (choice) {
                case 1: {
                    // GET 조회
                    JsonObject res = client.getJson(serverUrl + "/list");
                    System.out.println(JsonHelper.toPrettyJson(res));
                    break;
                }
                case 2: {
                    // POST 등록
                    String name = ConsoleHelper.readLine("이름: ");
                    JsonObject body = JsonHelper.newObject();
                    body.addProperty("name", name);
                    JsonObject res = client.postJson(serverUrl + "/add", body);
                    System.out.println("결과: " + JsonHelper.getString(res, "Result", ""));
                    break;
                }
                case 3: {
                    // GET 검색
                    String keyword = ConsoleHelper.readLine("검색어: ");
                    JsonObject res = client.getJson(serverUrl + "/search/" + keyword);
                    JsonArray found = JsonHelper.getArray(res, "Found");
                    System.out.println(found.size() + "건 검색됨");
                    for (int i = 0; i < found.size(); i++) {
                        System.out.println("  " + found.get(i).getAsString());
                    }
                    break;
                }
                case 4:
                    running = false;
                    break;
            }
        }

        client.stop();
    }
}
