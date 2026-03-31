package toolkit.examples;

import toolkit.*;
import com.google.gson.*;
import java.util.*;

/**
 * [Q2 템플릿] 파일 데이터 처리 + 결과 저장
 *
 * 시험에서 주어지는 형태:
 *   - main 함수만 있는 파일이 주어짐
 *   - 파일에서 데이터를 읽어서 가공/계산 후 결과 저장
 */
public class Template_Q2_FileProcess {

    // === 필요시 VO 클래스를 main 위에 선언 ===
    static class Employee {
        public String id;
        public String name;
        public int salary;
        public String dept;
    }

    static class EmployeeList {
        public List<Employee> employees;
    }

    public static void main(String[] args) throws Exception {

        // ============================================================
        // 패턴 A: 텍스트 파일 읽어서 처리
        // ============================================================
        // DATA.TXT 내용 예시:
        // E001 홍길동 5000 개발
        // E002 김철수 4500 영업
        List<String> lines = FileHelper.readLines("DATA.TXT");
        List<String[]> records = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(" ");
            records.add(parts);
        }

        // 집계: 부서별 급여 합계
        Map<String, Integer> deptTotal = new HashMap<>();
        for (String[] r : records) {
            String dept = r[3];
            int salary = Integer.parseInt(r[2]);
            deptTotal.put(dept, deptTotal.getOrDefault(dept, 0) + salary);
        }

        // 결과 출력
        for (Map.Entry<String, Integer> e : deptTotal.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        // 결과 파일 저장
        List<String> resultLines = new ArrayList<>();
        for (Map.Entry<String, Integer> e : deptTotal.entrySet()) {
            resultLines.add(e.getKey() + " " + e.getValue());
        }
        FileHelper.writeLines("RESULT.TXT", resultLines);

        // ============================================================
        // 패턴 B: JSON 파일 읽어서 처리
        // ============================================================
        // data.json 예시:
        // { "employees": [{"id":"E001","name":"홍길동","salary":5000,"dept":"개발"}, ...] }

        // 방법1: VO 매핑
        EmployeeList empList = JsonHelper.readFile("data.json", EmployeeList.class);
        for (Employee emp : empList.employees) {
            System.out.println(emp.name + ": " + emp.salary);
        }

        // 방법2: JsonObject로 직접
        JsonObject jsonData = JsonHelper.readFile("data.json");
        JsonArray empArr = JsonHelper.getArray(jsonData, "employees");
        for (int i = 0; i < empArr.size(); i++) {
            JsonObject emp = empArr.get(i).getAsJsonObject();
            String name = JsonHelper.getString(emp, "name", "");
            int salary = JsonHelper.getInt(emp, "salary", 0);
            System.out.println(name + ": " + salary);
        }

        // ============================================================
        // 패턴 C: 결과를 JSON으로 저장
        // ============================================================
        JsonObject result = JsonHelper.newObject();
        result.addProperty("date", DateHelper.nowDate());
        result.addProperty("totalCount", records.size());

        JsonArray deptArr = JsonHelper.newArray();
        for (Map.Entry<String, Integer> e : deptTotal.entrySet()) {
            JsonObject dept = JsonHelper.newObject();
            dept.addProperty("dept", e.getKey());
            dept.addProperty("total", e.getValue());
            deptArr.add(dept);
        }
        result.add("departments", deptArr);

        JsonHelper.writeFile("report.json", result);
        System.out.println("결과 저장 완료: " + JsonHelper.toPrettyJson(result));
    }
}
