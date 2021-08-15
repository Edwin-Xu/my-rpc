package cn.edw.rpc.simple.protocol;

import lombok.*;

import java.io.Serializable;

/**
 * @author taoxu.xu
 * @date 8/15/2021 2:55 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SimpleRpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] params;
}
