package toolkit;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.io.RandomAccessFile;

/**
 * 파일 읽기/쓰기/복사/검색/감시 유틸리티
 */
public class FileHelper {

    // ===================== 읽기 =====================

    /** 파일 전체를 String으로 읽기 */
    public static String readAll(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /** 파일을 줄 단위 List로 읽기 */
    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /** 파일을 줄 단위로 읽으면서 처리 (콜백) */
    public static void readLineByLine(String filePath, LineProcessor processor) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                processor.process(lineNum++, line);
            }
        }
    }

    public interface LineProcessor {
        void process(int lineNum, String line);
    }

    // ===================== 쓰기 =====================

    /** 문자열을 파일에 쓰기 (덮어쓰기) */
    public static void writeAll(String filePath, String content) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(content);
        }
    }

    /** 문자열을 파일에 추가 (append) */
    public static void appendLine(String filePath, String line) throws IOException {
        ensureParentDir(filePath);
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(line);
        }
    }

    /** 여러 줄을 파일에 쓰기 */
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        ensureParentDir(filePath);
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                pw.println(line);
            }
        }
    }

    // ===================== 복사 =====================

    /** 파일 복사 (바이너리 안전) */
    public static void copyFile(String src, String dest) throws IOException {
        ensureParentDir(dest);
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    // ===================== 디렉토리 =====================

    /** 부모 디렉토리가 없으면 생성 */
    public static void ensureParentDir(String filePath) {
        File parent = new File(filePath).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    /** 디렉토리 생성 */
    public static void ensureDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /** 디렉토리 내 파일/폴더 목록 */
    public static File[] listDir(String dirPath) {
        return new File(dirPath).listFiles();
    }

    /** 재귀적으로 모든 파일 검색 */
    public static List<File> searchAll(String dirPath) {
        List<File> result = new ArrayList<>();
        searchAllRecursive(new File(dirPath), result);
        return result;
    }

    private static void searchAllRecursive(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                searchAllRecursive(f, result);
            } else {
                result.add(f);
            }
        }
    }

    /** 특정 확장자 파일만 재귀 검색 */
    public static List<File> searchByExtension(String dirPath, String ext) {
        List<File> all = searchAll(dirPath);
        List<File> filtered = new ArrayList<>();
        for (File f : all) {
            if (f.getName().endsWith(ext)) {
                filtered.add(f);
            }
        }
        return filtered;
    }

    // ===================== 파일 감시 =====================

    /** 디렉토리 감시 - 파일 생성/수정 시 콜백 */
    public static void watchDirectory(String dirPath, FileEventHandler handler) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dirPath);
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filePath = path.resolve((Path) event.context());
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    handler.onCreated(filePath.toFile());
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    handler.onModified(filePath.toFile());
                }
            }
            key.reset();
        }
    }

    public interface FileEventHandler {
        void onCreated(File file);
        void onModified(File file);
    }

    // ===================== 파일 tail 감시 (새로 추가된 줄만 읽기) =====================

    /**
     * 파일을 감시하면서 새로 추가된 줄만 콜백으로 전달
     * - 파일이 수정될 때마다 마지막 읽은 위치 이후의 줄만 읽음
     * - 별도 스레드에서 실행됨
     * @param filePath 감시할 파일 경로
     * @param processor 새 줄이 추가될 때 호출되는 콜백
     * @return 감시 스레드 (interrupt로 중지 가능)
     */
    public static Thread tailWatch(String filePath, LineProcessor processor) {
        Thread t = new Thread(() -> {
            try {
                File file = new File(filePath);
                long lastPosition = file.exists() ? file.length() : 0;

                Path dir = file.getParentFile() != null ? file.getParentFile().toPath() : Paths.get(".");
                WatchService ws = FileSystems.getDefault().newWatchService();
                dir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

                int lineNum = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = ws.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = dir.resolve((Path) event.context());
                        if (changed.toFile().getName().equals(file.getName())) {
                            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                                raf.seek(lastPosition);
                                String line;
                                while ((line = raf.readLine()) != null) {
                                    if (!line.isEmpty()) {
                                        processor.process(lineNum++, line);
                                    }
                                }
                                lastPosition = raf.getFilePointer();
                            }
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                // 정상 종료
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    // ===================== 파일 읽고 비우기 감시 (consume 패턴) =====================

    /**
     * 파일을 감시하면서 내용이 생기면 전체를 읽고 파일을 비움
     * - 파일에 데이터 쓰기 → 읽어서 처리 → 파일 비우기 반복
     * - 별도 스레드에서 실행됨
     * @param filePath 감시할 파일 경로
     * @param consumer 파일 내용(줄 리스트)을 처리하는 콜백
     * @return 감시 스레드 (interrupt로 중지 가능)
     */
    public static Thread consumeWatch(String filePath, FileConsumer consumer) {
        Thread t = new Thread(() -> {
            try {
                File file = new File(filePath);
                Path dir = file.getParentFile() != null ? file.getParentFile().toPath() : Paths.get(".");
                WatchService ws = FileSystems.getDefault().newWatchService();
                dir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = ws.take();
                    boolean changed = false;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changedPath = dir.resolve((Path) event.context());
                        if (changedPath.toFile().getName().equals(file.getName())) {
                            changed = true;
                        }
                    }
                    key.reset();

                    if (changed && file.exists() && file.length() > 0) {
                        // 파일 전체 읽기
                        List<String> lines = readLines(filePath);
                        if (!lines.isEmpty()) {
                            // 파일 비우기
                            writeAll(filePath, "");
                            // 처리 콜백
                            consumer.consume(lines);
                        }
                    }
                }
            } catch (InterruptedException e) {
                // 정상 종료
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    public interface FileConsumer {
        /** 파일에서 읽은 전체 줄을 처리 (처리 후 파일은 자동으로 비워짐) */
        void consume(List<String> lines);
    }
}
