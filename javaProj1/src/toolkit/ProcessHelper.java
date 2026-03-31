package toolkit;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 외부 프로세스 실행 및 멀티스레드 프로세스 처리 유틸리티
 */
public class ProcessHelper {

    // ===================== 단일 프로세스 실행 =====================

    /** 명령어 실행 후 stdout 결과 반환 */
    public static String execute(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(Arrays.asList(cmd));
        Process process = builder.start();
        String output = readStream(process.getInputStream());
        process.waitFor();
        return output;
    }

    /** 명령어 실행 후 stdout 결과 반환 (List 버전) */
    public static String execute(List<String> cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();
        String output = readStream(process.getInputStream());
        process.waitFor();
        return output;
    }

    /** 프로세스에 stdin 입력을 보내고 stdout 결과 반환 */
    public static String executeWithInput(List<String> cmd, String... inputs) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            for (String input : inputs) {
                writer.write(input + "\n");
            }
            writer.flush();
        }

        String output = readStream(process.getInputStream());
        process.waitFor();
        return output;
    }

    // ===================== 멀티스레드 프로세스 실행 =====================

    /**
     * 여러 프로세스를 동시에 실행하고 모든 결과를 모아서 반환
     * @param commands 실행할 명령어들 (각각 String[])
     * @return 각 명령의 결과 리스트
     */
    public static List<String> executeParallel(List<String[]> commands) throws InterruptedException {
        List<ProcessThread> threads = new ArrayList<>();
        for (String[] cmd : commands) {
            ProcessThread t = new ProcessThread(cmd);
            t.start();
            threads.add(t);
        }
        List<String> results = new ArrayList<>();
        for (ProcessThread t : threads) {
            t.join();
            results.add(t.getResult());
        }
        return results;
    }

    /** 내부 스레드 클래스 */
    private static class ProcessThread extends Thread {
        private final String[] cmd;
        private String result = "";

        ProcessThread(String[] cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                result = execute(cmd);
            } catch (Exception e) {
                result = "ERROR: " + e.getMessage();
            }
        }

        String getResult() {
            return result;
        }
    }

    // ===================== 유틸 =====================

    /** 프로세스 실행 후 결과를 JsonObject로 파싱 */
    public static com.google.gson.JsonObject executeAndParseJson(String... cmd) throws Exception {
        String output = execute(cmd);
        return JsonHelper.parse(output);
    }

    /** 프로세스 실행 후 결과를 JsonObject로 파싱 (List 버전) */
    public static com.google.gson.JsonObject executeAndParseJson(List<String> cmd) throws Exception {
        String output = execute(cmd);
        return JsonHelper.parse(output);
    }

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
