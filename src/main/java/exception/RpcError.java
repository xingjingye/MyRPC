package exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RpcError {

    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE(501,"服务没有实现任何接口"),
    SERVICE_NOT_FOUND(502,"服务不存在"),
    UNKNOWN_PROTOCOL(503,"未知的协议包"),
    UNKNOWN_PACKAGE_TYPE(504,"未知的数据包"),
    UNKNOWN_SERIALIZER(505,"未知的反序列化器"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY(506,"连接注册中心失败"),
    REGISTER_SERVICE_FAILED(507,"注册服务时失败"),
    CLIENT_CONNECT_SERVER_FAILURE(508,"客户端连接失败"),
    SERIALIZER_NOT_FOUND(509,"未设置序列化器");
    Integer code;
    String message;
}
