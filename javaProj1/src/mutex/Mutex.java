package mutex;

import java.util.concurrent.locks.ReentrantLock;

public class Mutex {
	public static void main(String[] args) throws InterruptedException { 
        ThreadClass tc1 = new ThreadClass("[Thread1] ");
        ThreadClass tc2 = new ThreadClass("[Thread2] ");
        
        tc1.start(); 
        tc2.start();
        
        ThreadClass.lock.lock();
    	try {        
    		ThreadClass.PrintNums("[Main]");
    	}
    	finally
    	{
    		ThreadClass.lock.unlock();
    	}
    	 
    	tc1.join();
        tc2.join();
        
    } 
}
class ThreadClass extends Thread { // 'Thread' Class�� ��ӹ޴´� 
	
	static ReentrantLock lock = new ReentrantLock();
	
    String thread_name; 
    public ThreadClass(String name) { 
        this.thread_name = name; 
    } 

    public void run() { 
    	
    	lock.lock();
    	try {
    		PrintNums(thread_name);
    	}
    	finally {
    		lock.unlock();
    	}
    } 
    
    static void PrintNums(String str)
    {
    	int i;
    	
    	System.out.println(str);
    	
    	for (i=1; i<=30; i++)
    	{
    		System.out.print(i+" ");
    	}	
    	System.out.println();
    }
} 