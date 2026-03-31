package toolkit;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 자료구조(List, Map, 정렬, 그룹핑, 필터링) 유틸리티
 */
public class CollectionHelper {

    // ===================== List 생성 =====================

    /** 가변 인자로 리스트 생성 */
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }

    /** 문자열 배열 → 리스트 */
    public static List<String> toList(String[] arr) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, arr);
        return list;
    }

    // ===================== Map 생성 =====================

    /** key-value 쌍으로 Map 생성: mapOf("a", 1, "b", 2) */
    public static Map<String, Object> mapOf(Object... pairs) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length - 1; i += 2) {
            map.put(pairs[i].toString(), pairs[i + 1]);
        }
        return map;
    }

    // ===================== 정렬 =====================

    /** 리스트 오름차순 정렬 (원본 변경) */
    public static <T extends Comparable<T>> void sortAsc(List<T> list) {
        Collections.sort(list);
    }

    /** 리스트 내림차순 정렬 (원본 변경) */
    public static <T extends Comparable<T>> void sortDesc(List<T> list) {
        list.sort(Collections.reverseOrder());
    }

    /** 특정 필드 기준 오름차순 정렬 */
    public static <T> void sortBy(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
    }

    /**
     * 다중 조건 정렬 빌더
     * 사용법:
     *   sortBy(list, multiSort(
     *       comparing(e -> e.dept),           // 1차: 부서 오름차순
     *       comparingDesc(e -> e.salary),     // 2차: 급여 내림차순
     *       comparing(e -> e.name)            // 3차: 이름 오름차순
     *   ));
     */
    @SafeVarargs
    public static <T> Comparator<T> multiSort(Comparator<T>... comparators) {
        Comparator<T> result = comparators[0];
        for (int i = 1; i < comparators.length; i++) {
            result = result.thenComparing(comparators[i]);
        }
        return result;
    }

    /** 필드 기준 오름차순 Comparator */
    public static <T> Comparator<T> comparing(Function<T, Comparable> keyExtractor) {
        return (a, b) -> {
            Comparable ka = keyExtractor.apply(a);
            Comparable kb = keyExtractor.apply(b);
            return ka.compareTo(kb);
        };
    }

    /** 필드 기준 내림차순 Comparator */
    public static <T> Comparator<T> comparingDesc(Function<T, Comparable> keyExtractor) {
        return (a, b) -> {
            Comparable ka = keyExtractor.apply(a);
            Comparable kb = keyExtractor.apply(b);
            return kb.compareTo(ka);
        };
    }

    /** int 필드 기준 오름차순 Comparator */
    public static <T> Comparator<T> comparingInt(java.util.function.ToIntFunction<T> keyExtractor) {
        return (a, b) -> Integer.compare(keyExtractor.applyAsInt(a), keyExtractor.applyAsInt(b));
    }

    /** int 필드 기준 내림차순 Comparator */
    public static <T> Comparator<T> comparingIntDesc(java.util.function.ToIntFunction<T> keyExtractor) {
        return (a, b) -> Integer.compare(keyExtractor.applyAsInt(b), keyExtractor.applyAsInt(a));
    }

    // ===================== 필터 =====================

    /** 조건에 맞는 요소만 필터링 */
    public static <T> List<T> filter(List<T> list, Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /** 조건에 맞는 첫 번째 요소 */
    public static <T> T findFirst(List<T> list, Predicate<T> condition) {
        for (T item : list) {
            if (condition.test(item)) return item;
        }
        return null;
    }

    // ===================== 그룹핑/집계 =====================

    /** 특정 키 기준으로 그룹핑 */
    public static <T> Map<String, List<T>> groupBy(List<T> list, Function<T, String> keyExtractor) {
        Map<String, List<T>> groups = new LinkedHashMap<>();
        for (T item : list) {
            String key = keyExtractor.apply(item);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        return groups;
    }

    /** 특정 필드의 int 합계 */
    public static <T> int sumInt(List<T> list, java.util.function.ToIntFunction<T> field) {
        int sum = 0;
        for (T item : list) sum += field.applyAsInt(item);
        return sum;
    }

    /** 특정 필드의 int 최대값 */
    public static <T> int maxInt(List<T> list, java.util.function.ToIntFunction<T> field) {
        int max = Integer.MIN_VALUE;
        for (T item : list) max = Math.max(max, field.applyAsInt(item));
        return max;
    }

    /** 특정 필드의 int 최소값 */
    public static <T> int minInt(List<T> list, java.util.function.ToIntFunction<T> field) {
        int min = Integer.MAX_VALUE;
        for (T item : list) min = Math.min(min, field.applyAsInt(item));
        return min;
    }

    /** 특정 필드 값들만 추출 */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) result.add(mapper.apply(item));
        return result;
    }

    /** 카운트: 조건에 맞는 개수 */
    public static <T> int countIf(List<T> list, Predicate<T> condition) {
        int count = 0;
        for (T item : list) if (condition.test(item)) count++;
        return count;
    }

    // ===================== Map 집계 =====================

    /** Map에 값 누적 (카운팅) */
    public static void increment(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    /** Map에 값 누적 (합산) */
    public static void addTo(Map<String, Integer> map, String key, int value) {
        map.put(key, map.getOrDefault(key, 0) + value);
    }

    /** Map을 value 기준 내림차순 정렬된 리스트로 변환 */
    public static List<Map.Entry<String, Integer>> sortMapByValueDesc(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return entries;
    }

    /** Map을 value 기준 오름차순 정렬된 리스트로 변환 */
    public static List<Map.Entry<String, Integer>> sortMapByValueAsc(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getValue));
        return entries;
    }

    // ===================== 변환 =====================

    /** 문자열 리스트 → int 리스트 */
    public static List<Integer> toIntList(List<String> strList) {
        List<Integer> result = new ArrayList<>();
        for (String s : strList) result.add(Integer.parseInt(s.trim()));
        return result;
    }

    /** int 배열 → 리스트 */
    public static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int v : arr) list.add(v);
        return list;
    }
}
