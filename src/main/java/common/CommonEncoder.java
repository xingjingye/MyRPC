package common;

import common.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import vo.RpcRequest;

/**
 * @program: MyDubbo
 * @description: 编码器
 * @author: XingJingYe
 * @create: 2022-05-23 18:12
 **/
public class CommonEncoder extends MessageToByteEncoder {

    private static final int SIGN = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(SIGN);
        if (obj instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(obj);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
