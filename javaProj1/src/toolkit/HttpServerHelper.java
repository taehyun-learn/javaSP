package toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.gson.JsonObject;

/**
 * Jetty HTTP 서버를 간단히 띄우고, 요청 처리하는 유틸리티
 */
public class HttpServerHelper {

    private Server server;

    /**
     * 서버 생성 및 시작
     * @param host "127.0.0.1"
     * @param port 8080
     * @param path "/*" 또는 "/api/*"
     * @param servletClass 요청을 처리할 서블릿 클래스
     */
    public void start(String host, int port, String path, Class<? extends HttpServlet> servletClass) throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);
        server.addConnector(connector);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(servletClass, path);
        server.setHandler(handler);

        server.start();
    }

    /** 서버 시작 후 종료까지 대기 (메인 스레드에서 호출) */
    public void startAndWait(String host, int port, String path, Class<? extends HttpServlet> servletClass) throws Exception {
        start(host, port, path, servletClass);
        server.join();
    }

    /** 별도 스레드에서 서버 시작 (non-blocking) */
    public Thread startInThread(String host, int port, String path, Class<? extends HttpServlet> servletClass) {
        Thread t = new Thread(() -> {
            try {
                startAndWait(host, port, path, servletClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    /** 서버 중지 */
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    // ===================== 서블릿 헬퍼 메서드 (static) =====================

    /** 요청 Body를 String으로 읽기 */
    public static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /** 요청 Body를 JsonObject로 파싱 */
    public static JsonObject readBodyAsJson(HttpServletRequest req) throws IOException {
        return JsonHelper.parse(readBody(req));
    }

    /** URL 경로에서 segments 추출 (예: /REPORT/admin/20240101 → ["REPORT","admin","20240101"]) */
    public static String[] getPathSegments(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return new String[0];
        return pathInfo.substring(1).split("/");
    }

    /** JSON 응답 보내기 */
    public static void sendJson(HttpServletResponse res, int status, JsonObject json) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().print(json.toString());
        res.getWriter().flush();
    }

    /** JSON 응답 보내기 (200 OK) */
    public static void sendJson(HttpServletResponse res, JsonObject json) throws IOException {
        sendJson(res, 200, json);
    }

    /** 텍스트 응답 보내기 */
    public static void sendText(HttpServletResponse res, int status, String text) throws IOException {
        res.setStatus(status);
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().print(text);
        res.getWriter().flush();
    }

    // ===================== 간단한 서블릿 베이스 클래스 =====================

    /**
     * 간편하게 상속해서 사용하는 서블릿 베이스
     * - doGet/doPost에서 JSON 파싱/응답이 자동으로 처리됨
     */
    public static abstract class JsonServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            String[] segments = getPathSegments(req);
            JsonObject result = handleGet(req, res, segments);
            if (result != null) {
                sendJson(res, result);
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            String[] segments = getPathSegments(req);
            JsonObject body = readBodyAsJson(req);
            JsonObject result = handlePost(req, res, segments, body);
            if (result != null) {
                sendJson(res, result);
            }
        }

        /** GET 요청 처리 - 오버라이드해서 사용 */
        protected JsonObject handleGet(HttpServletRequest req, HttpServletResponse res, String[] pathSegments) throws IOException {
            return null;
        }

        /** POST 요청 처리 - 오버라이드해서 사용 */
        protected JsonObject handlePost(HttpServletRequest req, HttpServletResponse res, String[] pathSegments, JsonObject body) throws IOException {
            return null;
        }
    }
}
