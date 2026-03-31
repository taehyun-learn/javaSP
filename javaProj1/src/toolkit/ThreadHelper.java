package toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 멀티스레드, Mutex, 작업 병렬 실행 유틸리티
 */
public class ThreadHelper {

    // ===================== 간편 스레드 실행 =====================

    /** Runnable을 스레드로 실행하고 Thread 반환 */
    public static Thread run(Runnable task) {
        Thread t = new Thread(task);
        t.start();
        return t;
    }

    /** 여러 Runnable을 병렬 실행 후 모두 끝날 때까지 대기 */
    public static void runAndWaitAll(Runnable... tasks) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (Runnable task : tasks) {
            Thread t = new Thread(task);
            t.start();
            threads.add(t);
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    /** 여러 Runnable을 병렬 실행 후 모두 끝날 때까지 대기 (List 버전) */
    public static void runAndWaitAll(List<Runnable> tasks) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (Runnable task : tasks) {
            Thread t = new Thread(task);
            t.start();
            threads.add(t);
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    // ===================== Mutex (ReentrantLock) =====================

    /** 전역 락 (간단한 동기화가 필요할 때) */
    private static final ReentrantLock globalLock = new ReentrantLock();

    /** 전역 락으로 보호되는 작업 실행 */
    public static void withGlobalLock(Runnable task) {
        globalLock.lock();
        try {
            task.run();
        } finally {
            globalLock.unlock();
        }
    }

    /** 지정한 락으로 보호되는 작업 실행 */
    public static void withLock(ReentrantLock lock, Runnable task) {
        lock.lock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    /** 지정한 락으로 보호되는 작업 실행 (결과 반환) */
    public static <T> T withLock(ReentrantLock lock, java.util.concurrent.Callable<T> task) throws Exception {
        lock.lock();
        try {
            return task.call();
        } finally {
            lock.unlock();
        }
    }

    // ===================== 타이머/타임아웃 =====================

    /** 일정 시간 후 작업 실행 (cancelable) - Thread 반환 */
    public static Thread runAfter(long delayMs, Runnable task) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                task.run();
            } catch (InterruptedException e) {
                // cancelled
            }
        });
        t.start();
        return t;
    }

    /** 타이머 취소 (interrupt) */
    public static void cancel(Thread timerThread) {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }
}
