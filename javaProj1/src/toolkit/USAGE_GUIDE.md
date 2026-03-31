# Toolkit 사용 가이드

시험에서 `import toolkit.*;` 한 줄이면 모든 유틸리티를 사용할 수 있습니다.

---

## 1. FileHelper - 파일 읽기/쓰기/검색

```java
import toolkit.FileHelper;

// 파일 전체 읽기
String content = FileHelper.readAll("input.txt");

// 줄 단위 읽기
List<String> lines = FileHelper.readLines("input.txt");

// 줄 단위 읽으면서 처리
FileHelper.readLineByLine("input.txt", (lineNum, line) -> {
    String[] words = line.split(" ");
    System.out.println(words[0]);
});

// 파일 쓰기
FileHelper.writeAll("output.txt", "hello world");
FileHelper.appendLine("output.txt", "추가 내용");
FileHelper.writeLines("output.txt", lines);

// 파일 복사
FileHelper.copyFile("src.dat", "dest.dat");

// 디렉토리 내 모든 파일 재귀 검색
List<File> allFiles = FileHelper.searchAll("./INPUT");
List<File> txtFiles = FileHelper.searchByExtension("./INPUT", ".txt");

// 디렉토리 감시 (파일 생성/수정 시 콜백)
FileHelper.watchDirectory("./WATCH", new FileHelper.FileEventHandler() {
    public void onCreated(File file) { System.out.println("생성: " + file); }
    public void onModified(File file) { System.out.println("수정: " + file); }
});
```

---

## 2. JsonHelper - JSON 파싱/생성 (Gson)

```java
import toolkit.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

// === JSON 파일 읽기 ===
JsonObject obj = JsonHelper.readFile("data.json");
JsonArray arr = JsonHelper.readFileAsArray("list.json");

// === 문자열 파싱 ===
JsonObject obj = JsonHelper.parse("{\"name\":\"홍길동\"}");
JsonArray arr = JsonHelper.parseArray("[1,2,3]");

// === 값 꺼내기 (안전하게) ===
String name = JsonHelper.getString(obj, "name", "기본값");
int age = JsonHelper.getInt(obj, "age", 0);
boolean active = JsonHelper.getBool(obj, "active", false);
JsonArray children = JsonHelper.getArray(obj, "children");
JsonObject address = JsonHelper.getObject(obj, "address");

// === JSON 생성 ===
JsonObject res = JsonHelper.newObject();
res.addProperty("Result", "Ok");
res.addProperty("Count", 5);

JsonArray items = JsonHelper.newArray();
items.add("item1");
items.add("item2");
res.add("Items", items);

// === JSON 직렬화 ===
String json = JsonHelper.toJson(res);           // 한 줄
String pretty = JsonHelper.toPrettyJson(res);   // 들여쓰기

// === JSON 파일 저장 ===
JsonHelper.writeFile("output.json", res);

// === VO 클래스 매핑 ===
MyVo vo = JsonHelper.readFile("data.json", MyVo.class);  // 파일에서
MyVo vo = JsonHelper.parse(jsonString, MyVo.class);       // 문자열에서
```

---

## 3. HttpServerHelper - HTTP 서버

```java
import toolkit.HttpServerHelper;

// === 방법 1: 간단한 JsonServlet 상속 ===
public class MyServlet extends HttpServerHelper.JsonServlet {
    @Override
    protected JsonObject handleGet(HttpServletRequest req, HttpServletResponse res, 
                                    String[] pathSegments) throws IOException {
        // GET /api/REPORT/admin → pathSegments = ["REPORT", "admin"]
        String command = pathSegments[0];
        
        JsonObject result = JsonHelper.newObject();
        result.addProperty("Result", "Ok");
        return result;  // 자동으로 JSON 응답 전송
    }

    @Override
    protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                     String[] pathSegments, JsonObject body) throws IOException {
        // body는 자동으로 파싱됨
        String name = JsonHelper.getString(body, "name", "");
        
        JsonObject result = JsonHelper.newObject();
        result.addProperty("Result", "Ok");
        return result;
    }
}

// === 서버 시작 ===
HttpServerHelper server = new HttpServerHelper();

// 블로킹 (메인 스레드에서)
server.startAndWait("127.0.0.1", 8080, "/*", MyServlet.class);

// 논블로킹 (별도 스레드)
server.startInThread("127.0.0.1", 8080, "/*", MyServlet.class);

// === 방법 2: 기존 HttpServlet에서 헬퍼 사용 ===
protected void doPost(HttpServletRequest req, HttpServletResponse res) {
    JsonObject body = HttpServerHelper.readBodyAsJson(req);
    String[] segments = HttpServerHelper.getPathSegments(req);
    
    JsonObject result = JsonHelper.newObject();
    result.addProperty("status", "ok");
    HttpServerHelper.sendJson(res, result);
}
```

---

## 4. HttpClientHelper - HTTP 클라이언트

```java
import toolkit.HttpClientHelper;

// === 인스턴스 사용 (여러 요청) ===
HttpClientHelper client = new HttpClientHelper();

// GET
JsonObject res = client.getJson("http://127.0.0.1:8080/api/status");

// GET with body
JsonObject reqBody = JsonHelper.newObject();
reqBody.addProperty("id", "123");
JsonObject res = client.getJsonWithBody("http://127.0.0.1:8080/api", reqBody);

// POST
JsonObject body = JsonHelper.newObject();
body.addProperty("name", "홍길동");
JsonObject res = client.postJson("http://127.0.0.1:8080/api", body);

client.stop();

// === 일회성 요청 (간편) ===
JsonObject res = HttpClientHelper.quickGet("http://127.0.0.1:8080/api");
JsonObject res = HttpClientHelper.quickPost("http://127.0.0.1:8080/api", body);
```

---

## 5. ProcessHelper - 외부 프로세스 실행

```java
import toolkit.ProcessHelper;

// 단일 프로세스 실행
String output = ProcessHelper.execute("calc.exe", "10", "20");

// stdin 입력이 필요한 프로세스
String output = ProcessHelper.executeWithInput(
    Arrays.asList("program.exe"), "input1", "input2"
);

// 여러 프로세스 병렬 실행
List<String[]> commands = new ArrayList<>();
commands.add(new String[]{"add.exe", "1", "2"});
commands.add(new String[]{"add.exe", "3", "4"});
commands.add(new String[]{"add.exe", "5", "6"});
List<String> results = ProcessHelper.executeParallel(commands);
```

---

## 6. ThreadHelper - 멀티스레드/Mutex

```java
import toolkit.ThreadHelper;
import java.util.concurrent.locks.ReentrantLock;

// 스레드 실행
Thread t = ThreadHelper.run(() -> {
    System.out.println("작업 실행");
});

// 여러 작업 병렬 실행 후 대기
ThreadHelper.runAndWaitAll(
    () -> { /* 작업1 */ },
    () -> { /* 작업2 */ },
    () -> { /* 작업3 */ }
);

// Mutex (전역 락)
ThreadHelper.withGlobalLock(() -> {
    // 이 블록은 한 스레드만 실행
    System.out.println("동기화된 작업");
});

// 개별 락
ReentrantLock myLock = new ReentrantLock();
ThreadHelper.withLock(myLock, () -> {
    // myLock으로 보호됨
});

// 타이머 (5초 후 실행, 취소 가능)
Thread timer = ThreadHelper.runAfter(5000, () -> {
    System.out.println("5초 경과!");
});
ThreadHelper.cancel(timer);  // 취소
```

---

## 7. CryptoHelper - 암호화/인코딩

```java
import toolkit.CryptoHelper;

// SHA-256
String hash = CryptoHelper.sha256("password");       // 대문자
String hash = CryptoHelper.sha256Lower("password");   // 소문자

// Base64
String encoded = CryptoHelper.base64Encode("hello");
String decoded = CryptoHelper.base64Decode(encoded);

// 바이트 변환
byte[] buf = new byte[4];
CryptoHelper.intToBytes(buf, 0, 12345);
int num = CryptoHelper.bytesToInt(buf, 0);
```

---

## 8. ConsoleHelper - 콘솔 입출력

```java
import toolkit.ConsoleHelper;

// 입력 받기
String name = ConsoleHelper.readLine("이름: ");
int age = ConsoleHelper.readInt("나이: ");
int[] nums = ConsoleHelper.readInts("두 수 입력: ");  // "10 20" → [10, 20]

// 메뉴
int choice = ConsoleHelper.menu("메인 메뉴", "로그인", "승차", "하차", "종료");
// === 메인 메뉴 ===
// 1. 로그인
// 2. 승차
// 3. 하차
// 4. 종료
// 선택>

// 포맷 출력
ConsoleHelper.println("결과: %s (%d건)", name, count);
```

---

## 9. SocketHelper - 소켓 통신

```java
import toolkit.SocketHelper;

// === 서버 (파일 수신) ===
SocketHelper.startFileServer(27015, "./RECEIVED", file -> {
    System.out.println("수신 완료: " + file.getName());
});

// === 클라이언트 (파일 전송) ===
SocketHelper.sendFile("127.0.0.1", 27015, "data.txt");

// === 서버 (문자열 수신/응답) ===
SocketHelper.startStringServer(27015, data -> {
    System.out.println("수신: " + data);
    return "OK";  // 응답 (null이면 응답 안 보냄)
});

// === 클라이언트 (문자열 전송/응답 수신) ===
String response = SocketHelper.sendAndReceive("127.0.0.1", 27015, "hello");
```

---

## 10. LogHelper - 파일 로깅

```java
import toolkit.LogHelper;

LogHelper.setLogDir("./SERVER/LOG");  // 로그 폴더 설정 (기본: LOG)
LogHelper.log("admin", "LOGIN", "SUCCESS");
// → [2024-01-15 14:30:22.123] admin | LOGIN | SUCCESS

LogHelper.logAndPrint("admin", "REPORT", "001");  // 콘솔 + 파일 동시
```

---

## 11. DateHelper - 날짜/시간

```java
import toolkit.DateHelper;

String dt = DateHelper.nowDateTime();  // "20240115143022"
String d = DateHelper.nowDate();       // "20240115"
String t = DateHelper.nowTime();       // "143022"
String custom = DateHelper.now("yyyy-MM-dd HH:mm:ss");

long hours = DateHelper.hourDiff("20240115100000", "20240115150000");  // 5
long mins = DateHelper.minuteDiff("20240115100000", "20240115103000"); // 30
```

---

## 12. StringHelper - 문자열 처리

```java
import toolkit.StringHelper;

// 분리
String[] parts = StringHelper.splitBySpace("홍길동  30  개발");  // 연속공백 OK → ["홍길동","30","개발"]
String[] csv = StringHelper.split("a,b,c", ",");

// 결합
String joined = StringHelper.join(new String[]{"A","B","C"}, ", ");  // "A, B, C"

// 안전한 숫자 변환 (파싱 실패 시 기본값)
int num = StringHelper.toInt("123", 0);      // 123
int bad = StringHelper.toInt("abc", -1);     // -1
double d = StringHelper.toDouble("3.14", 0); // 3.14

// 패딩
StringHelper.padLeft("5", 3, '0');    // "005"
StringHelper.padRight("AB", 5, ' ');  // "AB   "

// 포맷
String s = StringHelper.fmt("%s: %d점", "홍길동", 95);

// 검사
StringHelper.isEmpty(null);              // true
StringHelper.isNumeric("123");           // true
StringHelper.containsIgnoreCase("Hello", "hello");  // true

// 정규식 추출
String first = StringHelper.extractFirst("ID:E001 Name:홍길동", "ID:(\\w+)");  // "E001"
List<String> all = StringHelper.extractAll("10+20+30", "\\d+");  // ["10","20","30"]

// 안전한 부분 문자열
StringHelper.sub("ABCDE", 0, 3);   // "ABC"
StringHelper.sub("AB", 0, 10);     // "AB" (초과해도 에러 안남)

// 치환
StringHelper.replaceMulti("A-B-C", "-", "_");                  // "A_B_C"
StringHelper.replaceMulti("hello world", "hello", "hi", "world", "java");  // "hi java"

// 카운트/반복
StringHelper.count("abcabc", "abc");  // 2
StringHelper.repeat("*", 10);         // "**********"
```

---

## 13. CollectionHelper - 자료구조/정렬/집계

```java
import toolkit.CollectionHelper;

// === 리스트/맵 생성 ===
List<String> list = CollectionHelper.listOf("A", "B", "C");
Map<String, Object> map = CollectionHelper.mapOf("name", "홍길동", "age", 30);

// === 정렬 (시험에서 자주 나오는 패턴!) ===

// VO 클래스가 있을 때:
// static class Employee { public String name; public String dept; public int salary; }
List<Employee> emps = ...;

// 단일 기준 정렬
CollectionHelper.sortBy(emps, CollectionHelper.comparingInt(e -> e.salary));         // 급여 오름차순
CollectionHelper.sortBy(emps, CollectionHelper.comparingIntDesc(e -> e.salary));     // 급여 내림차순
CollectionHelper.sortBy(emps, CollectionHelper.comparing(e -> e.name));              // 이름 오름차순

// 다중 조건 정렬 (1차: 부서 오름차순, 2차: 급여 내림차순, 3차: 이름 오름차순)
CollectionHelper.sortBy(emps, CollectionHelper.multiSort(
    CollectionHelper.comparing(e -> e.dept),
    CollectionHelper.comparingIntDesc(e -> e.salary),
    CollectionHelper.comparing(e -> e.name)
));

// 기본 정렬
List<Integer> nums = CollectionHelper.listOf(3,1,4,1,5);
CollectionHelper.sortAsc(nums);   // [1,1,3,4,5]
CollectionHelper.sortDesc(nums);  // [5,4,3,1,1]

// === 필터링 ===
List<Employee> devs = CollectionHelper.filter(emps, e -> e.dept.equals("개발"));
Employee found = CollectionHelper.findFirst(emps, e -> e.name.equals("홍길동"));

// === 그룹핑 ===
Map<String, List<Employee>> byDept = CollectionHelper.groupBy(emps, e -> e.dept);
// {"개발": [...], "영업": [...]}

// === 집계 ===
int totalSalary = CollectionHelper.sumInt(emps, e -> e.salary);
int maxSalary = CollectionHelper.maxInt(emps, e -> e.salary);
int minSalary = CollectionHelper.minInt(emps, e -> e.salary);
int devCount = CollectionHelper.countIf(emps, e -> e.dept.equals("개발"));

// 필드 추출
List<String> names = CollectionHelper.map(emps, e -> e.name);  // ["홍길동", "김철수", ...]

// === Map 집계 (카운팅/합산) ===
Map<String, Integer> deptCount = new HashMap<>();
Map<String, Integer> deptSalary = new HashMap<>();
for (Employee e : emps) {
    CollectionHelper.increment(deptCount, e.dept);          // 부서별 인원수
    CollectionHelper.addTo(deptSalary, e.dept, e.salary);   // 부서별 급여합
}

// Map을 값 기준 정렬
List<Map.Entry<String, Integer>> ranked = CollectionHelper.sortMapByValueDesc(deptSalary);
for (Map.Entry<String, Integer> entry : ranked) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

---

## 14. JSON → VO 클래스 매핑 (가장 중요!)

시험에서 JSON이 나오면 **해당 JSON 구조에 맞는 VO 클래스를 먼저 만들고** `JsonHelper.parse()`로 매핑하면 됩니다.

```java
// JSON 예시:
// { "Result": "Ok", "ReportID": "1", "Items": [{"id":"A","count":5}] }

// 1단계: VO 클래스 만들기 (필드명 = JSON 키와 동일)
public class ReportResponse {
    public String Result;
    public String ReportID;
    public List<Item> Items;

    public static class Item {
        public String id;
        public int count;
    }
}

// 2단계: 파싱
ReportResponse vo = JsonHelper.parse(jsonString, ReportResponse.class);      // 문자열에서
ReportResponse vo = JsonHelper.readFile("report.json", ReportResponse.class); // 파일에서

// 3단계: 사용
System.out.println(vo.Result);
for (ReportResponse.Item item : vo.Items) {
    System.out.println(item.id + ": " + item.count);
}
```

**VO 작성 규칙:**
- 필드명은 JSON 키와 **완전히 동일**해야 함 (대소문자 포함)
- 중첩 객체는 내부 static class로 선언
- 배열은 `List<타입>`으로 선언
- 없을 수 있는 필드는 그냥 null이 됨
- `toolkit/examples/SampleVo.java` 참고

---

## 13. 파일 tail 감시 (새로 추가된 줄만)

```java
// 파일에 새 줄이 추가될 때마다 해당 줄만 콜백
Thread watcher = FileHelper.tailWatch("data.txt", (lineNum, line) -> {
    System.out.println("새 줄: " + line);
    // 소켓 전송, 처리 등...
});

// 감시 종료
watcher.interrupt();
```

---

## 14. exe 실행 → JSON 파싱

```java
// exe 결과가 JSON 문자열일 때 바로 파싱
JsonObject result = ProcessHelper.executeAndParseJson("calc.exe", "10", "20");
int answer = JsonHelper.getInt(result, "result", 0);
```

---

## 케이스별 완성 예제

`toolkit/examples/` 폴더에 4개 케이스 완성 예제가 있습니다:
- `Case1_HttpServerMultiThread.java` - HTTP 요청 → 멀티스레드 처리 → 응답
- `Case2_FileTailAndSocket.java` - 파일 감시 → 새 줄 → 소켓 전송
- `Case3_ExeProcess.java` - exe 실행 → 결과 처리 (단일/병렬/stdin)
- `Case4_HttpJsonParsing.java` - HTTP JSON 송수신 + VO 매핑

---

## 시험 문제 유형별 빠른 조합

### Q1 유형: 콘솔 + 파일 읽기 처리
```java
import toolkit.*;

public class Q1Main {
    public static void main(String[] args) throws Exception {
        String id = ConsoleHelper.readLine("ID: ");
        String pw = ConsoleHelper.readLine("PW: ");
        String pwHash = CryptoHelper.sha256(pw);
        
        // 파일에서 계정 확인
        List<String> lines = FileHelper.readLines("accounts.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(id) && parts[1].equals(pwHash)) {
                System.out.println("로그인 성공");
            }
        }
    }
}
```

### Q2 유형: 파일 데이터 처리 + JSON
```java
import toolkit.*;

public class Q2Main {
    public static void main(String[] args) throws Exception {
        // JSON 파일 읽어서 처리
        JsonObject config = JsonHelper.readFile("config.json");
        String dataFile = JsonHelper.getString(config, "dataFile", "data.txt");
        
        List<String> data = FileHelper.readLines(dataFile);
        // ... 처리 로직 ...
        
        // 결과를 JSON으로 저장
        JsonObject result = JsonHelper.newObject();
        result.addProperty("count", data.size());
        JsonHelper.writeFile("result.json", result);
    }
}
```

### Q3 유형: 소켓 통신
```java
import toolkit.*;

public class Q3Server {
    public static void main(String[] args) throws Exception {
        SocketHelper.startFileServer(27015, "./DATA", file -> {
            System.out.println("수신: " + file.getName());
        });
    }
}

public class Q3Client {
    public static void main(String[] args) throws Exception {
        SocketHelper.sendFile("127.0.0.1", 27015, "report.txt");
    }
}
```

### Q4 유형: HTTP 서버 + JSON + 멀티스레드
```java
import toolkit.*;

// 서블릿
public class Q4Servlet extends HttpServerHelper.JsonServlet {
    static ReentrantLock lock = new ReentrantLock();
    
    @Override
    protected JsonObject handleGet(HttpServletRequest req, HttpServletResponse res, 
                                    String[] segments) throws IOException {
        String command = segments[0];
        JsonObject result = JsonHelper.newObject();
        
        if (command.equals("REPORT")) {
            String id = segments[1];
            ThreadHelper.withLock(lock, () -> {
                // 동기화 필요한 처리
            });
            result.addProperty("Result", "Ok");
        }
        return result;
    }
    
    @Override
    protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res,
                                     String[] segments, JsonObject body) throws IOException {
        String reportId = JsonHelper.getString(body, "ReportID", "");
        JsonObject result = JsonHelper.newObject();
        result.addProperty("Result", "Ok");
        return result;
    }
}

// 메인
public class Q4Main {
    public static void main(String[] args) throws Exception {
        // 서버 시작 (논블로킹)
        HttpServerHelper server = new HttpServerHelper();
        server.startInThread("127.0.0.1", 8080, "/*", Q4Servlet.class);
        
        // 클라이언트 요청
        HttpClientHelper client = new HttpClientHelper();
        JsonObject res = client.getJson("http://127.0.0.1:8080/REPORT/admin/20240115");
        System.out.println(res);
        client.stop();
    }
}
```
