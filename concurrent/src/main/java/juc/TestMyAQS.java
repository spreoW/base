package juc;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author :wangq
 * @date : 2023/7/31 10:42
 */
public class TestMyAQS extends AbstractQueuedSynchronizer {
    @Override
    protected boolean tryAcquire(int arg) {
        while (true){
            if(compareAndSetState(0, arg)){
                return true;
            }
        }
    }

    @Override
    protected boolean tryRelease(int arg) {
        setState(0);
        return true;
    }
}
