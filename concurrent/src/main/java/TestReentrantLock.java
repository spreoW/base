import java.util.concurrent.locks.ReentrantLock;

/**
 * @author :wangq
 * @date : 2023/7/6 8:41
 */
public class TestReentrantLock {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(()->{
            try {
                lock.lock();
                System.out.println("t1获取到了锁");
                Thread.sleep(10000000L);
                lock.unlock();
                System.out.println("t1释放了锁");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(()->{
            System.out.println("t2尝试获取锁");
            lock.lock();
            System.out.println("t2获取到了锁");
            lock.unlock();
            System.out.println("t2释放了锁");
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("结束");
    }
}
