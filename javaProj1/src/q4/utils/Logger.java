package card.validator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static synchronized void WriteLog(String...paramArray) throws IOException
    {
        String folderPath = "..\\SERVER\\LOG";
		File destFolder = new File(folderPath);
		if(!destFolder.exists()) {
		    destFolder.mkdirs(); 
		}

		LocalDateTime now = LocalDateTime.now();

        String filePath = String.format("%s\\LOG_%02d%02d%02d.TXT", folderPath, now.getHour(), now.getMinute(), now.getSecond());

        PrintWriter out = new PrintWriter(new FileWriter(filePath, true));        
        String strDT = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")); 
        String strParam = String.join(" | ", paramArray);
        String strLog = String.format("[%s] %s",strDT, strParam);
        out.println(strLog);
        out.close();         
    }
}
