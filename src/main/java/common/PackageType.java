package common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PackageType {

    REQUEST_PACK(0,"request包"),
    RESPONSE_PACK(1,"response包");

    private final Integer code;
    private final String message;
}
