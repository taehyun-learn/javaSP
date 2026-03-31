package toolkit.examples;

import java.io.IOException;

import toolkit.*;

/**
 * 케이스 2b: 파일에 값이 입력되면 → 읽어서 처리 → 파일 비우기
 *
 * 흐름:
 *   1. 누군가 data.txt에 데이터를 씀
 *   2. consumeWatch가 감지 → 전체 내용 읽기 → 파일 자동 비움
 *   3. 읽은 데이터를 처리 (소켓 전송, 가공 등)
 *   4. 다시 대기 (파일에 새 데이터가 쓰일 때까지)
 */
public class Case2b_FileConsumeAndSocket {

    public static void main(String[] args) throws Exception {

        String watchFile = "data.txt";
        FileHelper.writeAll(watchFile, ""); // 초기화

        // === 파일 감시 시작 (읽고 비우기 패턴) ===
        Thread watcher = FileHelper.consumeWatch(watchFile, lines -> {
            System.out.println("[감시] " + lines.size() + "줄 감지, 처리 시작");
            for (String line : lines) {
                System.out.println("  처리: " + line);
                // 여기서 소켓 전송, HTTP 요청 등 수행
                // try {
                //     SocketHelper.sendString("127.0.0.1", 27015, line);
                // } catch (IOException e) { e.printStackTrace(); }
            }
            System.out.println("[감시] 처리 완료, 파일 비워짐");
        });
        System.out.println("파일 감시 시작: " + watchFile);

        // === 테스트: 파일에 데이터 쓰기 ===
        Thread.sleep(1000);

        // 1차 - 여러 줄 한번에
        FileHelper.writeAll(watchFile, "AAA 100\nBBB 200\nCCC 300\n");
        Thread.sleep(3000);
        // → 3줄 읽고 처리 후 파일 비워짐

        // 2차 - 다시 쓰기
        FileHelper.writeAll(watchFile, "DDD 400\nEEE 500\n");
        Thread.sleep(3000);
        // → 2줄 읽고 처리 후 파일 비워짐

        watcher.interrupt();
        System.out.println("종료");
    }
}
