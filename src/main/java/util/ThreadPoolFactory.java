package util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-29 20:57
 **/
public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCK_QUEUE_CAPACITY = 100;

    private ThreadPoolFactory() {

    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix,false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix,Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCK_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix,daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE_SIZE,KEEP_ALIVE_TIME,TimeUnit.MINUTES,workQueue,threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix,Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }

        return Executors.defaultThreadFactory();
    }
}
