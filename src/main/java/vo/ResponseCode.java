package vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200,"success"),
    ERROR(500,"出现错误"),
    METHOD_NOT_FOUND(505,"方法不存在");

    private final Integer code;
    private final String message;
}
