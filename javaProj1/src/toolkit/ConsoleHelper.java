package toolkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * 콘솔 입출력 유틸리티
 */
public class ConsoleHelper {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /** 한 줄 입력 받기 */
    public static String readLine() throws IOException {
        return reader.readLine();
    }

    /** 프롬프트 출력 후 한 줄 입력 받기 */
    public static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return reader.readLine();
    }

    /** 정수 입력 받기 */
    public static int readInt(String prompt) throws IOException {
        return Integer.parseInt(readLine(prompt).trim());
    }

    /** 공백으로 분리된 정수 배열 입력 받기 */
    public static int[] readInts(String prompt) throws IOException {
        String[] parts = readLine(prompt).trim().split("\\s+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    /** 메뉴 출력 후 선택값 받기 */
    public static int menu(String title, String... options) throws IOException {
        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        return readInt("선택> ");
    }

    /** 포맷 출력 (줄바꿈 포함) */
    public static void println(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}
