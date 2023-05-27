package registry;

import registry.registryImpl.NacosServiceRegistry;
import util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description: 钩子
 * @author: XingJingYe
 * @create: 2022-06-24 11:20
 **/
public class ShutdownHook {

    private static final Logger logger = Logger.getLogger("ShutdownHook");
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");

    private static final ShutdownHook shutdownHook = new ShutdownHook();
    private ShutdownHook(){};

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosServiceRegistry.clearRegistry();
            threadPool.shutdown();
        }));
    }
}
