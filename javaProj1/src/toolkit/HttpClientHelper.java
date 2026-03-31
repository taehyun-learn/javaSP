package toolkit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;

import com.google.gson.JsonObject;

/**
 * Jetty HTTP 클라이언트 유틸리티 - GET/POST JSON 요청/응답
 */
public class HttpClientHelper {

    private HttpClient client;

    public HttpClientHelper() throws Exception {
        client = new HttpClient();
        client.start();
    }

    /** 종료 */
    public void stop() throws Exception {
        client.stop();
    }

    // ===================== GET =====================

    /** GET 요청 → 응답 문자열 */
    public String get(String url) throws Exception {
        ContentResponse res = client.newRequest(url)
                .method(HttpMethod.GET)
                .send();
        return res.getContentAsString();
    }

    /** GET 요청 → 응답 JsonObject */
    public JsonObject getJson(String url) throws Exception {
        return JsonHelper.parse(get(url));
    }

    /** GET 요청 (JSON body 포함) → 응답 JsonObject */
    public JsonObject getJsonWithBody(String url, JsonObject body) throws Exception {
        ContentResponse res = client.newRequest(url)
                .method(HttpMethod.GET)
                .header(HttpHeader.CONTENT_TYPE, MimeTypes.Type.APPLICATION_JSON_UTF_8.asString())
                .content(new StringContentProvider(JsonHelper.toJson(body)))
                .send();
        return JsonHelper.parse(res.getContentAsString());
    }

    // ===================== POST =====================

    /** POST 요청 (JSON body) → 응답 문자열 */
    public String post(String url, JsonObject body) throws Exception {
        ContentResponse res = client.newRequest(url)
                .method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, MimeTypes.Type.APPLICATION_JSON_UTF_8.asString())
                .content(new StringContentProvider(JsonHelper.toJson(body)))
                .send();
        return res.getContentAsString();
    }

    /** POST 요청 (JSON body) → 응답 JsonObject */
    public JsonObject postJson(String url, JsonObject body) throws Exception {
        return JsonHelper.parse(post(url, body));
    }

    // ===================== 정적 편의 메서드 (일회성 요청) =====================

    /** 일회성 GET 요청 */
    public static JsonObject quickGet(String url) throws Exception {
        HttpClientHelper h = new HttpClientHelper();
        try {
            return h.getJson(url);
        } finally {
            h.stop();
        }
    }

    /** 일회성 POST 요청 */
    public static JsonObject quickPost(String url, JsonObject body) throws Exception {
        HttpClientHelper h = new HttpClientHelper();
        try {
            return h.postJson(url, body);
        } finally {
            h.stop();
        }
    }
}
