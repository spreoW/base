import java.util.concurrent.locks.ReentrantLock;

/**
 * @author :wangq
 * @date : 2023/7/6 8:41
 */
public class TestLock {
    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.unlock();
    }
}
