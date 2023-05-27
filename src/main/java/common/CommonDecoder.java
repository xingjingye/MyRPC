package common;

import common.serializer.CommonSerializer;
import exception.RpcError;
import exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import vo.RpcRequest;
import vo.RpcResponse;

import java.util.List;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description: 解码器
 * @author: XingJingYe
 * @create: 2022-05-23 18:11
 **/
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = Logger.getLogger("CommonDecoder");
    private static final int SIGN = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int sign = byteBuf.readInt();
        if (sign != SIGN) {
            logger.severe("不识别的协议包：" + sign);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.severe("不识别的数据包：" + packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.severe("不识别的反序列化器");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = serializer.deserialize(bytes,packageClass);
        out.add(obj);
    }
}
