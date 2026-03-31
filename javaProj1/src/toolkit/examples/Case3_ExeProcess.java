package toolkit.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import toolkit.*;

/**
 * 케이스 3: 특정 exe 파일을 실행해서 응답값을 기반으로 처리
 *
 * 시나리오A: exe 실행 → stdout 결과 문자열 처리
 * 시나리오B: exe 실행 → stdout이 JSON → 파싱해서 처리
 * 시나리오C: 파일에서 인자 읽어서 → 여러 exe 병렬 실행 → 결과 취합
 */
public class Case3_ExeProcess {

    public static void main(String[] args) throws Exception {

        // === 시나리오 A: 단순 실행 + 결과 처리 ===
        String result = ProcessHelper.execute("add_2sec.exe", "10", "20");
        System.out.println("결과: " + result.trim());

        // === 시나리오 B: exe 결과가 JSON일 때 ===
        // exe가 {"result": 30, "status": "ok"} 같은 JSON을 출력한다면:
        // JsonObject json = ProcessHelper.executeAndParseJson("calc.exe", "10", "20");
        // int answer = JsonHelper.getInt(json, "result", 0);
        // String status = JsonHelper.getString(json, "status", "");

        // === 시나리오 C: 파일에서 읽어서 병렬 실행 ===
        List<String> lines = FileHelper.readLines("NUM.TXT");
        List<String[]> commands = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            commands.add(new String[]{"add_2sec.exe", parts[0], parts[1]});
        }
        // 모든 프로세스를 병렬 실행
        List<String> results = ProcessHelper.executeParallel(commands);
        for (int i = 0; i < results.size(); i++) {
            System.out.println("결과 " + (i + 1) + ": " + results.get(i).trim());
        }

        // === 시나리오 D: stdin 입력이 필요한 exe ===
        String output = ProcessHelper.executeWithInput(
                Arrays.asList("interactive.exe"),
                "10",   // 첫번째 입력
                "20"    // 두번째 입력
        );
        System.out.println("결과: " + output);
    }
}
