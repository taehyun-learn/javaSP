package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileReadCopy {

	static String rootPath = ".\\Input";
	
	public static void main(String[] args) {
		FileSearchAll(rootPath);
	}
	//���� ���� ���
	void PrintFile(String fileName) {
		String line = null;
		
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine())!=null) {
				System.out.println(line);
			}
			bufferedReader.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//���� ��ġ������ ���� ��� ���
	static void FileDirList() {
		File directory = new File(".");
		File[] fList = directory.listFiles();
		
		for(File file : fList) {
			if(file.isDirectory()) {
				System.out.println("["+file.getName()+"]");
			}else {
				System.out.println(file.getPath());
			}
		}
	}
	
	
	//���� ����
	static void CopyFile(String inputFile, String outputFile) {
		final int BUFFER_SIZE = 512;
		int readLen;
		try {
			InputStream inputStream = new FileInputStream(inputFile);
			OutputStream outputStream = new FileOutputStream(outputFile);
			
			byte[] buffer = new byte[BUFFER_SIZE];
			
			while((readLen = inputStream.read(buffer))!=-1) {
				outputStream.write(buffer, 0, readLen);
			}
			inputStream.close();
			outputStream.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//���� ����Ʈ ����
	static void FileSearchAll(String path) {
		File directory = new File(path);
		File[] fList = directory.listFiles();
			
		for(File file: fList) {
			if(file.isDirectory()) {
				FileSearchAll(file.getPath());
			}else {
				String partPath = path.substring(rootPath.length());
				System.out.println("."+partPath+file.getName()+": "+file.length()+"bytes");
				if(file.length()>3*1024) {
					String targetPath = file.getPath().replaceAll("INPUT", "OUTPUT");
					File target = new File(file.getPath());
					if(!target.exists()) {
						target.mkdirs();
					}
					CopyFile(file.getPath(), targetPath);
				}
			}
		}
	}
}
