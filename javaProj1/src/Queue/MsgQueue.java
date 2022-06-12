package Queue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MsgQueue {
    private int size;
    private int seqNo;

    // id - (status, msg)
    private LinkedHashMap<Integer, List<String>> hashMsg; 

    public MsgQueue(int size)
    {
        this.size = size;
        this.seqNo = 0;
        hashMsg = new LinkedHashMap<Integer, List<String>>();
    }

    public String MsgEnqueue(String msg)
    {
        if (hashMsg.size() == size)
            return "Queue Full";

        List<String> listMsg = new ArrayList<String>();
        listMsg.add("A"); // status : available
        listMsg.add(msg); // message
        hashMsg.put(seqNo++, listMsg);

        return "Enqueued";
    }

    public String MsgDequeue()
    {
        if (hashMsg.size() == 0)
            return "Queue Empty";

        int key = (int)hashMsg.keySet().iterator().next();
        
        String res = hashMsg.get(key).get(1) + "(" + key + ")";

        hashMsg.remove(key);

        return res;
    }

    public String MsgGet()
    {
        if (hashMsg.size() > 0)
            for(Integer key : hashMsg.keySet())
            {
                if (hashMsg.get(key).get(0).equals("A"))
                {
                	List<String> val = hashMsg.get(key);
                	val.set(0, "U");
                    hashMsg.put(key, val); 
                    return val.get(1) + "(" + key + ")";
                }
            }

        return "No Msg";
    }

    public String MsgSet(int id)
    {
        if (hashMsg.size() > 0)
        {
            if (hashMsg.containsKey(id))
            {
                hashMsg.get(id).set(0, "A");  
                return "Msg Set";
            }
        }

        return "Set Fail";
    }

    public String MsgDel(int id)
    {
        if (hashMsg.size() > 0)
        {
            if (hashMsg.containsKey(id))
            {
                hashMsg.remove(id);
                return "Deleted";
            }
        }

        return "Not Deleted";
    }
}
