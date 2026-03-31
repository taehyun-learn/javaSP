package toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열 처리 유틸리티
 */
public class StringHelper {

    // ===================== 분리/결합 =====================

    /** 구분자로 분리 (공백: " ", 탭: "\t", 콤마: ",") */
    public static String[] split(String str, String delimiter) {
        return str.split(delimiter);
    }

    /** 공백으로 분리 (연속 공백 처리) */
    public static String[] splitBySpace(String str) {
        return str.trim().split("\\s+");
    }

    /** 배열을 구분자로 결합 */
    public static String join(String[] arr, String delimiter) {
        return String.join(delimiter, arr);
    }

    /** 리스트를 구분자로 결합 */
    public static String join(List<String> list, String delimiter) {
        return String.join(delimiter, list);
    }

    // ===================== 변환 =====================

    /** 문자열 → int (실패 시 기본값) */
    public static int toInt(String str, int defaultVal) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /** 문자열 → long (실패 시 기본값) */
    public static long toLong(String str, long defaultVal) {
        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /** 문자열 → double (실패 시 기본값) */
    public static double toDouble(String str, double defaultVal) {
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    // ===================== 패딩/포맷 =====================

    /** 왼쪽 패딩 (예: padLeft("5", 3, '0') → "005") */
    public static String padLeft(String str, int length, char padChar) {
        while (str.length() < length) {
            str = padChar + str;
        }
        return str;
    }

    /** 오른쪽 패딩 (예: padRight("AB", 5, ' ') → "AB   ") */
    public static String padRight(String str, int length, char padChar) {
        while (str.length() < length) {
            str = str + padChar;
        }
        return str;
    }

    /** 포맷 출력 (String.format 단축) */
    public static String fmt(String format, Object... args) {
        return String.format(format, args);
    }

    // ===================== 검사 =====================

    /** null이거나 빈 문자열인지 */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** 숫자만으로 구성되어 있는지 */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        return str.trim().matches("-?\\d+(\\.\\d+)?");
    }

    /** 특정 문자열 포함 여부 (대소문자 무시) */
    public static boolean containsIgnoreCase(String str, String search) {
        return str.toLowerCase().contains(search.toLowerCase());
    }

    // ===================== 추출 =====================

    /** 정규식으로 매칭되는 첫 번째 그룹 추출 */
    public static String extractFirst(String str, String regex) {
        Matcher m = Pattern.compile(regex).matcher(str);
        if (m.find()) {
            return m.groupCount() > 0 ? m.group(1) : m.group();
        }
        return "";
    }

    /** 정규식으로 매칭되는 모든 결과 추출 */
    public static List<String> extractAll(String str, String regex) {
        List<String> results = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(str);
        while (m.find()) {
            results.add(m.groupCount() > 0 ? m.group(1) : m.group());
        }
        return results;
    }

    /** 부분 문자열 (안전 - 범위 초과 시 빈 문자열) */
    public static String sub(String str, int start, int end) {
        if (str == null) return "";
        if (start >= str.length()) return "";
        if (end > str.length()) end = str.length();
        return str.substring(start, end);
    }

    // ===================== 치환 =====================

    /** 여러 치환을 한번에 (pairs: old1, new1, old2, new2, ...) */
    public static String replaceMulti(String str, String... pairs) {
        for (int i = 0; i < pairs.length - 1; i += 2) {
            str = str.replace(pairs[i], pairs[i + 1]);
        }
        return str;
    }

    /** 정규식 치환 */
    public static String replaceRegex(String str, String regex, String replacement) {
        return str.replaceAll(regex, replacement);
    }

    // ===================== 반복/카운트 =====================

    /** 문자열 반복 */
    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(str);
        return sb.toString();
    }

    /** 특정 문자열이 몇 번 등장하는지 */
    public static int count(String str, String target) {
        int count = 0, idx = 0;
        while ((idx = str.indexOf(target, idx)) != -1) {
            count++;
            idx += target.length();
        }
        return count;
    }
}
