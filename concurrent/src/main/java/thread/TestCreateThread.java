package thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author :wangq
 * @date : 2023/7/20 11:46
 * 创建线程3种方式
 * 1.进程Thread，重写run方法
 * 2.实现Runnable接口，重新run方法
 * 3.实现Callable接口，重写call方法，配合FutureTask接收线程返回值
 */
public class TestCreateThread {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Create3 create3= new Create3();
        FutureTask<String> futureTask = new FutureTask<>(create3);
        new Thread(futureTask).start();
        String s = futureTask.get();
        System.out.println(s);
    }

    static class Create1 extends Thread{
        @Override
        public void run() {

        }
    }

    static class Create2 implements Runnable{
        @Override
        public void run() {

        }
    }

    static class Create3 implements Callable<String> {
        @Override
        public String call() throws Exception {
            return Thread.currentThread().getName();
        }
    }
}
