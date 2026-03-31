package toolkit;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜/시간 유틸리티
 */
public class DateHelper {

    /** 현재 날짜시간 문자열 (yyyyMMddHHmmss) */
    public static String nowDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /** 현재 날짜 문자열 (yyyyMMdd) */
    public static String nowDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /** 현재 시간 문자열 (HHmmss) */
    public static String nowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    /** 포맷 지정 현재 시간 */
    public static String now(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /** 두 시간 문자열 간 시간차 (시) - format: yyyyMMddHHmmss */
    public static long hourDiff(String time1, String time2) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date d1 = fmt.parse(time1);
        java.util.Date d2 = fmt.parse(time2);
        long gap = d2.getTime() - d1.getTime();
        return gap / 1000 / 60 / 60;
    }

    /** 두 시간 문자열 간 분차 */
    public static long minuteDiff(String time1, String time2) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date d1 = fmt.parse(time1);
        java.util.Date d2 = fmt.parse(time2);
        long gap = d2.getTime() - d1.getTime();
        return gap / 1000 / 60;
    }
}
