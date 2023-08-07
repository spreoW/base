package thread;

/**
 * @author :wangq
 * @date : 2023/7/20 16:27
 * 打断线程3种方法
 * 1.stop
 * 2.使用共享变量
 * 3.interrupt
 */
public class TestInterruptThread {
    static volatile boolean flag = true;
    public static void main(String[] args) throws InterruptedException {
        TestInterruptThread t = new TestInterruptThread();
        t.interrupt3();
    }

    private void interrupt1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        Thread.sleep(500);
        t1.stop();
        System.out.println(t1.getState());
    }

    private void interrupt2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (flag) {
                // 处理任务
            }
            System.out.println("任务结束");
        });
        t1.start();
        Thread.sleep(500);
        flag = false;
    }

    private void interrupt3() throws InterruptedException {
        //线程默认情况，interrupt=false
        System.out.println(Thread.currentThread().isInterrupted());
        // 设置为true
        Thread.currentThread().interrupt();
        // true
        System.out.println(Thread.currentThread().isInterrupted());
        // 返回当前线程，重置interrupt标志位
        System.out.println(Thread.interrupted());
        System.out.println(Thread.currentThread().isInterrupted());

        Thread t1 = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                // 处理业务
            }
            System.out.println("t1结束");
        });
        t1.start();
        Thread.sleep(500);
        t1.interrupt();
    }
}
