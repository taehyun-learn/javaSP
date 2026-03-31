package toolkit.examples;

import java.util.List;

/**
 * JSON VO 클래스 작성 예제
 *
 * JSON 구조에 맞춰 필드를 선언하면 Gson이 자동 매핑해줌.
 * - 필드명 = JSON 키 이름과 동일하게
 * - 중첩 객체 = 별도 클래스 또는 내부 클래스로
 * - 배열 = List<타입>으로
 *
 * 사용법:
 *   SampleVo vo = JsonHelper.parse(jsonString, SampleVo.class);
 *   SampleVo vo = JsonHelper.readFile("data.json", SampleVo.class);
 */
public class SampleVo {
    // ======== 아래 JSON에 대응하는 VO ========
    // {
    //   "name": "홍길동",
    //   "age": 30,
    //   "married": true,
    //   "address": { "city": "서울", "zip": "12345" },
    //   "children": [
    //     { "name": "홍아들", "age": 5 },
    //     { "name": "홍딸", "age": 3 }
    //   ],
    //   "scores": [90, 85, 100]
    // }

    public String name;
    public int age;
    public boolean married;
    public Address address;
    public List<Child> children;
    public List<Integer> scores;

    // 중첩 객체용 내부 클래스
    public static class Address {
        public String city;
        public String zip;
    }

    public static class Child {
        public String name;
        public int age;
    }

    // toString (디버깅용)
    @Override
    public String toString() {
        return String.format("name=%s, age=%d, married=%s, address=%s, children=%d명",
                name, age, married, address != null ? address.city : "null",
                children != null ? children.size() : 0);
    }
}
