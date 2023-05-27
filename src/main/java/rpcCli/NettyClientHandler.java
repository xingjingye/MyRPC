package rpcCli;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import vo.RpcResponse;

import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description: 客户端责任链尾部
 * @author: XingJingYe
 * @create: 2022-05-24 11:04
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = Logger.getLogger("NettyClientHandler");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息：%s",rpcResponse));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(rpcResponse);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.severe("过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}
