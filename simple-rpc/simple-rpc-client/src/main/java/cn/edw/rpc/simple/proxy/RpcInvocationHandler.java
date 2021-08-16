package cn.edw.rpc.simple.proxy;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;
import cn.edw.rpc.simple.transport.ClientSimpleRpcTransporter;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author taoxu.xu
 * @date 8/15/2021 3:18 PM
 */
@AllArgsConstructor
public class RpcInvocationHandler implements InvocationHandler {
    private final String interfaceName;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        final SimpleRpcRequest request =
                new SimpleRpcRequest(interfaceName, method.getName(), args);

        final ClientSimpleRpcTransporter transporter = new ClientSimpleRpcTransporter("127.0.0.1",8888);

        return transporter.request(request);
    }
}
