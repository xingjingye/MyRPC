package rpcCli;

import common.serializer.CommonSerializer;
import vo.RpcRequest;

public interface RpcClient {

    void setSerializer(CommonSerializer serializer);
    Object sendRequest(RpcRequest rpcRequest);
}
