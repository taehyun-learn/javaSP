package Queue;

import java.util.HashMap;
import java.util.Scanner;

public class QueuesMain {
	static HashMap<String, MsgQueue> queues;
	
	public static void main(String[] args) {
        // Queue Name - Id - (size, status, msg)
        queues = new HashMap<String, MsgQueue>();
        Scanner sc = new Scanner(System.in);

        while (true)
        {
            String line = sc.nextLine();
            String[] words = line.split(" ");
            String command = words[0];
            String qname = words[1];
            switch(command)
            {
                case "CREATE":
                    System.out.println(QCreate(qname, Integer.parseInt(words[2])));
                    break;
                case "ENQUEUE":
                    String message = words[2];
                    System.out.println(QEnqueue(qname, message));
                    break;
                case "DEQUEUE":
                    System.out.println(QDequeue(qname));
                    break;
                case "GET":
                    System.out.println(QGet(qname));
                    break;
                case "SET":
                    System.out.println(QSet(qname, Integer.parseInt(words[2])));
                    break;
                case "DEL":
                    System.out.println(QDel(qname, Integer.parseInt(words[2])));
                    break;
                default:
                    break;
            }
        }
	}

    static String QCreate(String name, int size)
    {
        if (queues.containsKey(name))
            return "Queue Exist";

        MsgQueue q = new MsgQueue(size);

        queues.put(name, q);

        return "Queue Created";
    }

    static String QEnqueue(String name, String msg)
    {    	 
        return queues.get(name).MsgEnqueue(msg);
    }

    static String QDequeue(String name)
    {
        return queues.get(name).MsgDequeue(); 
    }

    static String QGet(String name)
    {
        return queues.get(name).MsgGet();
    }

    static String QSet(String name, int id)
    {
        return queues.get(name).MsgSet(id);
    }
    static String QDel(String name, int id)
    {
        return queues.get(name).MsgDel(id);
    }	
}
