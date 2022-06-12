package encdec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class EncDec {
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
	    InputStreamReader reader = new InputStreamReader(System.in);
	    BufferedReader br = new BufferedReader(reader);
	    String str;
	    	    
	    while (true)
        {
	    	str = br.readLine();
	    	Base64CaseString(str);
	    	SHA256(str);
        }
		
		//Base64CaseByte();
	    
	}
	
	public static void Base64CaseByte(String TestString) throws UnsupportedEncodingException
	{
		//String TestString = "This is a Base64 test.";
		byte[] TestBytes = TestString.getBytes("UTF-8");
		
		System.out.println(new String(TestBytes));
		
	    // Base64 인코딩 ///////////////////////////////////////////////////
	    Encoder encoder = Base64.getEncoder();
	
	    byte[] encodedBytes = encoder.encode(TestBytes);
	    
	    System.out.println(new String(encodedBytes));
	    
	    // Base64 디코딩 ///////////////////////////////////////////////////
	    Decoder decoder = Base64.getDecoder();
	    
	    byte[] decodedBytes = decoder.decode(encodedBytes);
	    
	    System.out.println(new String(decodedBytes, "UTF-8"));
	}
	
	public static void Base64CaseString(String TestString) throws UnsupportedEncodingException
	{
		//String TestString = "This is a Base64 test.";
		
		System.out.println(TestString);

	    // Base64 인코딩 ///////////////////////////////////////////////////
	    Encoder encoder = Base64.getEncoder();
	    
	    String encodedString = encoder.encodeToString(TestString.getBytes("UTF-8"));
	    
	    System.out.println(encodedString);

	    // Base64 디코딩 ///////////////////////////////////////////////////
	    Decoder decoder = Base64.getDecoder();
	
	    byte[] decodedBytes = decoder.decode(encodedString);
	    
	    String decodedString = new String(decodedBytes, "UTF-8");
	    
	    System.out.println(decodedString);
	}
	
	public static void SHA256(String input) throws NoSuchAlgorithmException 
	{
        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xFF) + 0x100, 16).substring(1));
        }
         
        System.out.println(sb.toString());		
	}
}
