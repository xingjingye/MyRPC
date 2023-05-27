package rpcCli;

import common.CommonDecoder;
import common.CommonEncoder;
import common.serializer.CommonSerializer;
import exception.RpcError;
import exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-26 11:21
 **/
public class ChannelProvider {
    private static final Logger logger = Logger.getLogger("ChannelProvider");
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap,inetSocketAddress,MAX_RETRY_COUNT,countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.severe("获取channel时有错误发生：" + e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress,int retry,CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功！");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if (retry == 0) {
                logger.severe("客户端连接失败：重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }

            int order = (MAX_RETRY_COUNT - retry) + 1;
            int delay = 1 << order;
            logger.severe(new Date() + "：连接失败，第" + order + "次重连。。。");
            bootstrap.config().group().schedule(() ->connect(bootstrap,inetSocketAddress,retry - 1,countDownLatch),delay, TimeUnit.SECONDS);
        });
    }
}
