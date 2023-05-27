package exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vo.ResponseCode;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-16 19:54
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcException extends RuntimeException{
    private RpcError rpcError;
}
