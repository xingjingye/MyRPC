package rpcServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import registry.ServiceProvider;
import vo.RpcRequest;
import vo.RpcResponse;

import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description: 服务端责任链尾部
 * @author: XingJingYe
 * @create: 2022-05-24 10:53
 **/

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = Logger.getLogger("NettyServerHandler");
    private static final RequestHandler requestHandler;
    private final ServiceProvider serviceProvider;

    static {
        requestHandler = new RequestHandler();
    }

    public NettyServerHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器接收到请求：" + rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(interfaceName);
            Object result = requestHandler.handle(rpcRequest,service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.severe("处理过程调用时有错误发生");
        cause.printStackTrace();
        ctx.close();
    }
}
