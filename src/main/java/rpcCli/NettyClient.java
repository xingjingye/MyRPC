package rpcCli;

import common.serializer.CommonSerializer;
import exception.RpcError;
import exception.RpcException;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import registry.LoadBalancer;
import registry.ServiceRegistry;
import registry.balancerImpl.RandomLoadBalancer;
import registry.registryImpl.NacosServiceRegistry;
import vo.RpcRequest;
import vo.RpcResponse;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-24 11:43
 **/
public class NettyClient implements RpcClient{

    private static final Logger logger = Logger.getLogger("NettyClient");

    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public NettyClient(LoadBalancer loadBalancer) {
        if (loadBalancer.getCode() == 0) {
            logger.info("本次调用采用的负载均衡策略为随机");
        } else {
            logger.info("本次调用采用的负载均衡策略为轮转");
        }
        this.serviceRegistry = new NacosServiceRegistry(loadBalancer);
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        if (serializer.getCode() == 0) {
            logger.info("本次调用采用的序列化方式为Kryo序列化");
        } else {
            logger.info("本次调用采用的序列化方式为Json序列化");
        }
        this.serializer = serializer;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.severe("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
            if (channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息：%s",rpcRequest));
                    } else {
                        logger.severe("发送消息时有错误发生：" + future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse;
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.severe("发送消息时有错误发生：" + e);
            e.printStackTrace();
        }
        return null;
    }
}
