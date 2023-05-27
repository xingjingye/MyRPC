package registry.registryImpl;

import exception.RpcError;
import exception.RpcException;
import registry.ServiceProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-16 18:50
 **/
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = Logger.getLogger("ServiceProviderImpl");
    private final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("向接口：" + interfaces + " 注册服务：" + serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service ==  null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
