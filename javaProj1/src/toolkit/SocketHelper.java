package toolkit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 소켓 통신 (파일 전송/수신) 유틸리티
 */
public class SocketHelper {

    // ===================== 클라이언트: 파일 전송 =====================

    /** 파일을 소켓으로 전송 */
    public static void sendFile(String host, int port, String filePath) throws IOException {
        File file = new File(filePath);
        try (Socket socket = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {

            // 파일명 전송
            dos.writeUTF(file.getName());
            // 파일 크기 전송
            dos.writeLong(file.length());
            // 파일 내용 전송
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, len);
            }
            dos.flush();
        }
    }

    /** 문자열 데이터를 소켓으로 전송 */
    public static void sendString(String host, int port, String data) throws IOException {
        try (Socket socket = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            dos.writeUTF(data);
            dos.flush();
        }
    }

    /** 문자열 전송 후 응답 수신 */
    public static String sendAndReceive(String host, int port, String data) throws IOException {
        try (Socket socket = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            dos.writeUTF(data);
            dos.flush();
            return dis.readUTF();
        }
    }

    // ===================== 서버: 파일 수신 =====================

    /**
     * 소켓 서버 시작 - 파일을 수신하여 지정 폴더에 저장
     * @param port 포트
     * @param saveDir 저장 폴더
     * @param handler 파일 수신 완료 시 콜백 (null 가능)
     */
    public static Thread startFileServer(int port, String saveDir, FileReceivedHandler handler) {
        Thread t = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                FileHelper.ensureDir(saveDir);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleFileReceive(socket, saveDir, handler)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    private static void handleFileReceive(Socket socket, String saveDir, FileReceivedHandler handler) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            File outFile = new File(saveDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[4096];
                long remaining = fileSize;
                while (remaining > 0) {
                    int len = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (len == -1) break;
                    fos.write(buffer, 0, len);
                    remaining -= len;
                }
            }

            if (handler != null) {
                handler.onReceived(outFile);
            }
        } catch (EOFException e) {
            // 클라이언트 연결 종료
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 소켓 서버 시작 - 문자열 수신 처리
     */
    public static Thread startStringServer(int port, StringReceivedHandler handler) {
        Thread t = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> {
                        try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                            String data = dis.readUTF();
                            String response = handler.onReceived(data);
                            if (response != null) {
                                dos.writeUTF(response);
                                dos.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    public interface FileReceivedHandler {
        void onReceived(File file);
    }

    public interface StringReceivedHandler {
        /** 수신된 문자열 처리 후 응답 반환 (null이면 응답 안 보냄) */
        String onReceived(String data);
    }
}
