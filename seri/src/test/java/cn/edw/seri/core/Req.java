package cn.edw.seri.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxu.xu
 * @date 8/25/2021 10:26 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Req {
    public String interfaceName;
    public String methodName;
    public Object[] params;
}
