package common.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public enum SerializerCode {
    KRYO(0),
    JSON(1);

    private final int code;
}
