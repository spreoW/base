package juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :wangq
 * @date : 2023/8/3 16:07
 */
public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        getShellData();
    }

    public static void getShellData() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5));
        CountDownLatch latch = new CountDownLatch(5);
        List<ShellData> shellDataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            threadPoolExecutor.execute(()->{
                shellDataList.add(new ShellData());
                latch.countDown();
            });
        }
        latch.wait();
        // 5个shell页的数据都获取完了，再对汇总后的数据进行处理
       // doOtherByShellDataList(shellDataList);
    }

    static class ShellData{

    }
}
