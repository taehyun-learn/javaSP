package toolkit;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SHA-256 해시, Base64 인코딩/디코딩 유틸리티
 */
public class CryptoHelper {

    // ===================== SHA-256 =====================

    /** SHA-256 해시 (대문자) */
    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(Integer.toString((b & 0xFF) + 0x100, 16).substring(1).toUpperCase());
        }
        return sb.toString();
    }

    /** SHA-256 해시 (소문자) */
    public static String sha256Lower(String input) throws NoSuchAlgorithmException {
        return sha256(input).toLowerCase();
    }

    // ===================== Base64 =====================

    /** Base64 인코딩 (문자열 → 문자열) */
    public static String base64Encode(String input) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(input.getBytes("UTF-8"));
    }

    /** Base64 디코딩 (문자열 → 문자열) */
    public static String base64Decode(String encoded) throws UnsupportedEncodingException {
        byte[] decoded = Base64.getDecoder().decode(encoded);
        return new String(decoded, "UTF-8");
    }

    /** Base64 인코딩 (바이트 → 바이트) */
    public static byte[] base64Encode(byte[] input) {
        return Base64.getEncoder().encode(input);
    }

    /** Base64 디코딩 (바이트 → 바이트) */
    public static byte[] base64Decode(byte[] input) {
        return Base64.getDecoder().decode(input);
    }

    // ===================== 바이트 변환 =====================

    /** int → byte[] (4바이트, little-endian) */
    public static void intToBytes(byte[] buffer, int offset, int num) {
        buffer[offset + 0] = (byte) (num);
        buffer[offset + 1] = (byte) (num >> 8);
        buffer[offset + 2] = (byte) (num >> 16);
        buffer[offset + 3] = (byte) (num >> 24);
    }

    /** byte[] → int (4바이트, little-endian) */
    public static int bytesToInt(byte[] buffer, int offset) {
        return ((buffer[offset + 3] & 0xFF) << 24)
             | ((buffer[offset + 2] & 0xFF) << 16)
             | ((buffer[offset + 1] & 0xFF) << 8)
             | ((buffer[offset] & 0xFF));
    }
}
