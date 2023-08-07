package juc;

import java.util.concurrent.*;

/**
 * @author :wangq
 * @date : 2023/8/3 16:22
 */
public class TestCyclicBarrier {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5));
        CyclicBarrier barrier = new CyclicBarrier(5);
        for (int i = 0; i < 5; i++) {
            threadPoolExecutor.execute(()->{
                try {
                    Thread.sleep(Math.round(1000));
                    System.out.println(Thread.currentThread().getName() + ": 玩家准备完成。");
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        barrier.await();
        System.out.println("玩家全部准备完成！开始游戏。");
        threadPoolExecutor.shutdown();
    }
}
