package toolkit;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 동기화된 파일 로깅 유틸리티
 */
public class LogHelper {

    private static String logDir = "LOG";

    /** 로그 디렉토리 설정 */
    public static void setLogDir(String dir) {
        logDir = dir;
    }

    /** 로그 기록 (스레드 안전) */
    public static synchronized void log(String... params) throws IOException {
        FileHelper.ensureDir(logDir);
        LocalDateTime now = LocalDateTime.now();
        String fileName = String.format("%s/LOG_%s.TXT", logDir, now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String message = String.join(" | ", params);
        String logLine = String.format("[%s] %s", timestamp, message);

        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.println(logLine);
        }
    }

    /** 콘솔 + 파일 동시 로그 */
    public static synchronized void logAndPrint(String... params) throws IOException {
        String message = String.join(" | ", params);
        System.out.println(message);
        log(params);
    }
}
