package cn.edw.seri.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxu.xu
 * @date 8/24/2021 10:43 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyInterfaceImpl implements MyInterface{
    private String msg;
}
