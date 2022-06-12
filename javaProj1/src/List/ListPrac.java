package List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ListPrac {
    public static void main(String[] args) throws IOException {
        ArrayList<Grade> al = new ArrayList<Grade>();
       
        try {
            ////////////////////////////////////////////////////////////////
            BufferedReader in = new BufferedReader(new FileReader("List_Sample.txt"));
            String str;

            while ((str = in.readLine()) != null) {
                String words[] = str.split("\t");
                Grade g = new Grade(words[0],Integer.parseInt(words[1]),Integer.parseInt(words[2]),Integer.parseInt(words[3]));
                al.add(g);
            }
            in.close();
            ////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println(e); // 에러가 있다면 메시지 출력
            System.exit(1);
        }
       
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            String strInput = br.readLine();
           
            switch(strInput) {
            case "PRINT": // 이름 순 출력
                Collections.sort(al, (g1, g2) -> g1.getName().compareTo(g2.getName()));
                break;
            case "KOREAN": // 국어 성적 순 출력
                // Lambda식
                Collections.sort(al, (g1, g2) -> (g2.getKorean() - g1.getKorean()) == 0 ? g1.getName().compareTo(g2.getName()) : g2.getKorean() - g1.getKorean());                
                break;
            case "ENGLISH": // 영어 성적 순 출력
                // Comparator
                Collections.sort(al, new Comparator<Grade>() {

                    @Override
                    public int compare(Grade x, Grade y) {
                        if (y.getEnglish() - x.getEnglish() == 0)
                        {
                            return x.getName().compareTo(y.getName());
                        }
                        else
                        {
                            return y.getEnglish() - x.getEnglish();
                        }
                    }
                   
                });                
                break;
            case "MATH":
                // Comparator
                Collections.sort(al, new sortByMath());                
                break;
            case "QUIT":
                return;
               default:
                break;
            }
           
            for (Grade val : al)
            {
                System.out.println(String.format("%s\t%d\t%d\t%d",val.getName(), val.getKorean(), val.getEnglish(), val.getMath()));
            }                
        }  
    }  
}

class Grade
{
    private String strName;
    private int Korean;
    private int English;
    private int Math;

    public Grade(String str, int k, int e, int m)
    {
        strName = str;
        Korean = k;
        English = e;
        Math = m;
    }

    public String getName()
    {
        return strName;
    }
    public void setName(String strName)
    {
        this.strName = strName;
    }
    public int getKorean()
    {
        return Korean;
    }
    public void setProjectA(int n)
    {
        Korean = n;
    }
    public int getEnglish()
    {
        return English;
    }
    public void setProjectB(int n)
    {
        English = n;
    }
    public int getMath()
    {
        return Math;
    }
    public void setMath(int n)
    {
        Math = n;
    }
}

class sortByMath implements Comparator<Grade>{
    public int compare(Grade x, Grade y) {
        if (y.getMath() - x.getMath() == 0)
        {
            return x.getName().compareTo(y.getName());
        }
        else
        {
            return y.getMath() - x.getMath();
        }
    }
}