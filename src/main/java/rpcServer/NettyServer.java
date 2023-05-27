package rpcServer;

import common.CommonDecoder;
import common.CommonEncoder;
import common.serializer.CommonSerializer;
import exception.RpcError;
import exception.RpcException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import registry.ServiceProvider;
import registry.ServiceRegistry;
import registry.ShutdownHook;
import registry.balancerImpl.RandomLoadBalancer;
import registry.registryImpl.NacosServiceRegistry;
import registry.registryImpl.ServiceProviderImpl;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-24 11:20
 **/
public class NettyServer implements RpcServer{

    private static final Logger logger = Logger.getLogger("NettyServer");
    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;
    private final String host;
    private final int port;

    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry(new RandomLoadBalancer());
        serviceProvider = new ServiceProviderImpl();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.severe("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.registerService(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler(serviceProvider));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            ShutdownHook.getShutdownHook().addClearAllHook();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.severe("启动服务器时有错误发生：" + e);
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
