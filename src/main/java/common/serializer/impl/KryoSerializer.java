package common.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import common.serializer.CommonSerializer;
import common.serializer.SerializerCode;
import vo.RpcRequest;
import vo.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-25 16:34
 **/
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = Logger.getLogger("KryoSerializer");
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.severe("序列化时有错误发生：" + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input,clazz);
            kryoThreadLocal.remove();
            return obj;
        } catch (Exception e) {
            logger.severe("反序列化时有错误发生：" + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
