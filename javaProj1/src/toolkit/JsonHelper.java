package toolkit;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
 * Gson 기반 JSON 파싱/생성/읽기/쓰기 유틸리티
 */
public class JsonHelper {

    private static final Gson gson = new Gson();
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    // ===================== 파일에서 읽기 =====================

    /** JSON 파일 → JsonObject */
    public static JsonObject readFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return gson.fromJson(content, JsonObject.class);
    }

    /** JSON 파일 → JsonArray */
    public static JsonArray readFileAsArray(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return gson.fromJson(content, JsonArray.class);
    }

    /** JSON 파일 → Java 객체 (VO 클래스 매핑) */
    public static <T> T readFile(String filePath, Class<T> clazz) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            return gson.fromJson(reader, clazz);
        }
    }

    // ===================== 문자열에서 파싱 =====================

    /** JSON 문자열 → JsonObject */
    public static JsonObject parse(String json) {
        return gson.fromJson(json, JsonObject.class);
    }

    /** JSON 문자열 → JsonArray */
    public static JsonArray parseArray(String json) {
        return gson.fromJson(json, JsonArray.class);
    }

    /** JSON 문자열 → Java 객체 */
    public static <T> T parse(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // ===================== 생성 =====================

    /** 빈 JsonObject 생성 */
    public static JsonObject newObject() {
        return new JsonObject();
    }

    /** 빈 JsonArray 생성 */
    public static JsonArray newArray() {
        return new JsonArray();
    }

    /** Map → JsonObject (편의 메서드) */
    public static JsonObject fromMap(Map<String, Object> map) {
        return gson.toJsonTree(map).getAsJsonObject();
    }

    // ===================== 직렬화 =====================

    /** 객체 → JSON 문자열 */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /** 객체 → 보기좋은 JSON 문자열 (들여쓰기, null 포함) */
    public static String toPrettyJson(Object obj) {
        return gsonPretty.toJson(obj);
    }

    // ===================== 파일에 쓰기 =====================

    /** 객체를 JSON 파일로 저장 */
    public static void writeFile(String filePath, Object obj) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gsonPretty.toJson(obj, writer);
        }
    }

    // ===================== 값 꺼내기 헬퍼 =====================

    /** JsonObject에서 String 꺼내기 (없으면 기본값) */
    public static String getString(JsonObject obj, String key, String defaultVal) {
        JsonElement e = obj.get(key);
        return (e != null && !e.isJsonNull()) ? e.getAsString() : defaultVal;
    }

    /** JsonObject에서 int 꺼내기 (없으면 기본값) */
    public static int getInt(JsonObject obj, String key, int defaultVal) {
        JsonElement e = obj.get(key);
        return (e != null && !e.isJsonNull()) ? e.getAsInt() : defaultVal;
    }

    /** JsonObject에서 boolean 꺼내기 (없으면 기본값) */
    public static boolean getBool(JsonObject obj, String key, boolean defaultVal) {
        JsonElement e = obj.get(key);
        return (e != null && !e.isJsonNull()) ? e.getAsBoolean() : defaultVal;
    }

    /** JsonObject에서 JsonArray 꺼내기 */
    public static JsonArray getArray(JsonObject obj, String key) {
        JsonElement e = obj.get(key);
        return (e != null && e.isJsonArray()) ? e.getAsJsonArray() : new JsonArray();
    }

    /** JsonObject에서 JsonObject 꺼내기 */
    public static JsonObject getObject(JsonObject obj, String key) {
        JsonElement e = obj.get(key);
        return (e != null && e.isJsonObject()) ? e.getAsJsonObject() : new JsonObject();
    }

    // ===================== 타입 판별 =====================

    /** JsonElement의 타입을 문자열로 반환 */
    public static String getTypeName(JsonElement e) {
        if (e == null || e.isJsonNull()) return "null";
        if (e.isJsonArray()) return "Array";
        if (e.isJsonObject()) return "Object";
        if (e.isJsonPrimitive()) {
            if (e.getAsJsonPrimitive().isString()) return "String";
            if (e.getAsJsonPrimitive().isNumber()) return "Number";
            if (e.getAsJsonPrimitive().isBoolean()) return "Boolean";
        }
        return "Unknown";
    }
}
