package socket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileClient {
    public static void main(String[] args) throws IOException {
		Socket socket = null;
		DataOutputStream os = null;
		try {
			socket = new Socket("127.0.0.1", 27015);
			os = new DataOutputStream(socket.getOutputStream());
			
			byte[] buffer = new byte[4096];
			int length;
			
			// get all the files from a directory
			File directory = new File("./ClientFiles");
			File[] fList = directory.listFiles();
			for (File file : fList) {
				if (file.isFile()) {
					// 파일이름 전송 
					os.writeUTF(file.getName());
					// 파일크기 전송
					os.writeInt((int) file.length());
					
					FileInputStream is = null;
					try {
						is = new FileInputStream(file.getPath());
						while ((length = is.read(buffer)) != -1) {
							// 파일내용 전송
							os.write(buffer, 0, length);
						}
					} finally {
						if (is != null) { is.close(); }
					}	
				}
			}
			System.out.println("Sent All Files.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) { os.close(); }
			if (socket != null) { socket.close(); }
		}
    }   
}