package toolkit.examples;

import toolkit.*;
import com.google.gson.*;
import java.util.*;

/**
 * [Q1 템플릿] 콘솔 입출력 + 파일 읽기 처리
 *
 * 시험에서 주어지는 형태:
 *   - main 함수만 있는 파일이 주어짐
 *   - 콘솔에서 입력 받아서 파일 데이터와 비교/처리
 */
public class Template_Q1_Console {

    public static void main(String[] args) throws Exception {

        // ============================================================
        // 1. 콘솔 입력
        // ============================================================
        String id = ConsoleHelper.readLine("ID: ");
        String pw = ConsoleHelper.readLine("PW: ");

        // 메뉴 선택이 필요한 경우
        // int choice = ConsoleHelper.menu("메뉴", "로그인", "조회", "종료");

        // 숫자 입력
        // int num = ConsoleHelper.readInt("숫자: ");
        // int[] nums = ConsoleHelper.readInts("두 수: ");  // "10 20" → [10, 20]

        // ============================================================
        // 2. 파일 읽기 (텍스트)
        // ============================================================

        // 방법A: 전체를 한번에
        String allText = FileHelper.readAll("DATA.TXT");

        // 방법B: 줄 단위 리스트
        List<String> lines = FileHelper.readLines("DATA.TXT");
        for (String line : lines) {
            String[] parts = line.split(" ");  // 공백 분리
            // parts[0], parts[1] ... 사용
        }

        // 방법C: 줄 단위 콜백 (줄번호 포함)
        FileHelper.readLineByLine("DATA.TXT", (lineNum, line) -> {
            System.out.println(lineNum + ": " + line);
        });

        // ============================================================
        // 3. 파일 읽기 (JSON)
        // ============================================================
        JsonObject json = JsonHelper.readFile("config.json");
        String name = JsonHelper.getString(json, "name", "");
        int age = JsonHelper.getInt(json, "age", 0);
        JsonArray items = JsonHelper.getArray(json, "items");

        // JSON 배열 순회
        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            String itemName = JsonHelper.getString(item, "name", "");
            System.out.println(itemName);
        }

        // ============================================================
        // 4. 처리 로직 (예: 로그인 검증)
        // ============================================================
        String pwHash = CryptoHelper.sha256(pw);
        boolean loggedIn = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[0].equals(id) && parts[1].equals(pwHash)) {
                loggedIn = true;
                break;
            }
        }

        if (loggedIn) {
            System.out.println("로그인 성공");
        } else {
            System.out.println("로그인 실패");
        }

        // ============================================================
        // 5. 결과 파일 쓰기
        // ============================================================

        // 텍스트 파일
        FileHelper.writeAll("RESULT.TXT", "처리 결과: " + id);
        FileHelper.appendLine("LOG.TXT", DateHelper.nowDateTime() + " " + id + " LOGIN");

        // JSON 파일
        JsonObject result = JsonHelper.newObject();
        result.addProperty("id", id);
        result.addProperty("result", loggedIn ? "SUCCESS" : "FAIL");
        result.addProperty("time", DateHelper.nowDateTime());
        JsonHelper.writeFile("result.json", result);

        // ============================================================
        // 6. 반복 메뉴 (while 루프)
        // ============================================================
        boolean running = true;
        while (running) {
            int choice = ConsoleHelper.menu("메인", "조회", "등록", "종료");
            switch (choice) {
                case 1:
                    System.out.println("조회 처리...");
                    break;
                case 2:
                    System.out.println("등록 처리...");
                    break;
                case 3:
                    running = false;
                    break;
            }
        }
    }
}
