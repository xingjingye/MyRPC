package registry.registryImpl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import exception.RpcError;
import exception.RpcException;
import registry.LoadBalancer;
import registry.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-25 22:34
 **/
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = Logger.getLogger("NacosServiceRegistry");
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;
    private final LoadBalancer loadBalancer;

    public NacosServiceRegistry(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.severe("连接到Nacos时有错误发生：" + e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
            NacosServiceRegistry.address = inetSocketAddress;
            serviceNames.add(serviceName);
        } catch (NacosException e) {
            logger.severe("注册服务时有错误发生：" + e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.severe("获取服务时有错误发生：" + e);
        }
        return null;
    }

    public static void clearRegistry() {
        if(!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.severe("注销服务 " + serviceName + ":失败");
                }
            }
        }
    }
}
