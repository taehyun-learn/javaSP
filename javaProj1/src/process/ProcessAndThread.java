package process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProcessAndThread {
	public static void main(String[] args) throws IOException, InterruptedException {		
		System.out.println(String.format("Start - " + new Date().toString())); // 현재시각출력
		
		FileReader fileReader = new FileReader("NUM.TXT");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        List<ProcessThread> thList = new ArrayList<ProcessThread>();
        while((line = bufferedReader.readLine()) != null) {
            String [] words = line.split(" ");
            int num1 = Integer.parseInt(words[0]);
            int num2 = Integer.parseInt(words[1]);
            
            ProcessThread th = new ProcessThread(num1, num2);
            th.start();
            thList.add(th);
        }   
        bufferedReader.close();
		
        for (ProcessThread th : thList)
        {
        	th.join();
        }
		System.out.println("End - " + new Date().toString()); // 현재시각출력
	}
}
class ProcessThread extends Thread { // 'Thread' Class를 상속받는다 
	int num1;
	int num2;
    public ProcessThread(int n1, int n2) { 
    	num1 = n1;
    	num2 = n2;
    } 

    public void run() {
		String output;
		try {
			output = getProcessOutput(Arrays.asList("add_2sec.exe",Integer.toString(num1),Integer.toString(num2)));
			System.out.println(num1 + " + " + num2 + " = " + output);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				    	
    } 
    
	public String getProcessOutput(List<String> cmdList) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(cmdList);		
		Process process = builder.start();
		InputStream psout = process.getInputStream();
		byte[] buffer = new byte[1024];
		int len = psout.read(buffer);
		return (new String(buffer, 0, len));
	}    
} 