package rpcServer;

import vo.ResponseCode;
import vo.RpcRequest;
import vo.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-16 20:25
 **/
public class RequestHandler {

    private static final Logger logger = Logger.getLogger("RequestHandler");

    public Object handle(RpcRequest rpcRequest,Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest,service);
            logger.info("服务：" + rpcRequest.getInterfaceName() + "成功调用方法：" + rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.severe("调用或发送时有错误发生：" + e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }

        return method.invoke(service,rpcRequest.getParameters());
    }
}
