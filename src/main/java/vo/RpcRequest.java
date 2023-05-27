package vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-15 22:35
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    //待调用接口名称
    private String interfaceName;
    //待调用方法名称
    private String methodName;
    //调用方法参数
    private Object[] parameters;
    //调用方法的参数类型
    private Class<?>[] paramTypes;
}
