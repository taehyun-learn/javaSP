package toolkit.examples;

import java.io.File;
import java.io.IOException;

import toolkit.*;

/**
 * 케이스 2: 텍스트 파일 감시 + 새 줄이 추가되면 읽어서 소켓으로 전송
 *
 * 시나리오: data.txt에 데이터가 추가될 때마다 → 새 줄만 읽어서 → 소켓 서버로 전송
 */
public class Case2_FileTailAndSocket {

    public static void main(String[] args) throws Exception {

        // === 1. 소켓 서버 시작 (수신 측) ===
        SocketHelper.startStringServer(27015, data -> {
            System.out.println("[서버] 수신: " + data);
            // 데이터 처리 로직
            return "OK";  // 응답
        });
        System.out.println("소켓 서버 시작 (27015)");

        // === 2. 파일 감시 시작 ===
        // data.txt에 새로운 줄이 추가되면 자동으로 읽어서 소켓 전송
        String watchFile = "data.txt";

        // 파일이 없으면 생성
        FileHelper.writeAll(watchFile, "");

        Thread watcher = FileHelper.tailWatch(watchFile, (lineNum, line) -> {
            System.out.println("[감시] 새 줄 감지 (" + lineNum + "): " + line);
            try {
                // 새로 추가된 줄을 소켓으로 전송
                String response = SocketHelper.sendAndReceive("127.0.0.1", 27015, line);
                System.out.println("[감시] 서버 응답: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("파일 감시 시작: " + watchFile);

        // === 3. 테스트: 파일에 데이터 추가 ===
        Thread.sleep(1000);
        FileHelper.appendLine(watchFile, "첫번째 데이터");
        Thread.sleep(2000);
        FileHelper.appendLine(watchFile, "두번째 데이터");
        Thread.sleep(2000);
        FileHelper.appendLine(watchFile, "세번째 데이터");

        // 감시 종료
        Thread.sleep(3000);
        watcher.interrupt();
    }
}
