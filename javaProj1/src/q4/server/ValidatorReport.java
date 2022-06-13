package card.validator.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import card.validator.server.vo.ReportVo;
import card.validator.utils.CardUtility;

public class ValidatorReport {
	private static Integer seqNo = 0;
	private static ConcurrentHashMap<Integer, String> mapReport = new ConcurrentHashMap<Integer, String>(); 
	private static ConcurrentHashMap<Integer, Thread> mapThread = new ConcurrentHashMap<Integer, Thread>();
	
    public static Integer increaseSeqNo()
    {
        seqNo++;
        return seqNo;
    }
    
    public static void storeReport(String reportId, String report)
    {
        mapReport.put(Integer.parseInt(reportId), report);
    }
    
    public static void removeReport(String reportId)
    {
        System.out.println("Remove : " + reportId);
        mapReport.remove(Integer.parseInt(reportId)); 
    }   
    
	public static String makeReport(final String strDate) throws IOException {
		Map<String, ReportVo> map = new TreeMap();
		//final String strToday = strInputDate;

		// File Find
		File directory = new File("..\\SERVER");
		File[] list = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().length() == 27 
						&& file.getName().substring(9, 17).equals(strDate);
			}
		});
		
		for (File file : list) {
			analysisData(map, file.getPath());
		}

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ReportVo> items : map.entrySet())
        {
            String s = items.getValue().getInsId() + " " + items.getValue().getCheckCard() + " " + items.getValue().getFailCard() + "\n";
            sb.append(s);
        }		

		return sb.toString();
	}

	private static void analysisData(Map<String, ReportVo> map, String path) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(path));
			
			String line;
			while ((line = br.readLine()) != null) {
				ReportVo userReport = new ReportVo();
	
				String key = line.substring(0, 8);
	
				// insert key & value
				if (map.get(key) == null) {
					userReport.setInsId(line.substring(0, 8));
					userReport.setCheckCard(1);
					if (line.charAt(49) == '1') {
						userReport.setFailCard(0);
					} else {
						userReport.setFailCard(1);
					}
					map.put(key, userReport);
				} else {	// just change value
					map.get(key).increaseCheckCard();
					if (line.charAt(49) != '1') {
						map.get(key).increaseFailCard();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) { br.close(); }
		}
	}

    public static boolean saveReportFile(String reportId, String type) throws IOException
    {
    	System.out.println("saveReportFile : " + reportId);
    	
        if (!mapReport.containsKey(Integer.parseInt(reportId)))
            return false;

        String path = "..\\SERVER\\REPORT";
		File destFolder = new File(path);
		if(!destFolder.exists()) {
		    destFolder.mkdirs(); 
		}
		       
        String filename = String.format("%s\\%s_%s.TXT", path, type, reportId);
        
		FileWriter fw = null;
		fw = new FileWriter(filename);			
		fw.write(mapReport.get(Integer.parseInt(reportId)).toString());
		fw.close();
		
        return true;
    }   
    
	public static void startTimer(int id, Runnable task) {
		mapThread.put(id, new Thread(task));
		mapThread.get(id).start(); 
	}
	
	public static void cancelTimer(int id) {
		mapThread.get(id).interrupt();
	}	
}
