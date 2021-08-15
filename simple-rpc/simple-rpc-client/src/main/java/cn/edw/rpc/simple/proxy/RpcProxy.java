package cn.edw.rpc.simple.proxy;

import java.lang.reflect.Proxy;

/**
 * @author taoxu.xu
 * @date 8/15/2021 3:13 PM
 */
public class RpcProxy {
    public static Object newRpcProxy(Class<?> serviceClass){
        final Class<?>[] classes = new Class<?>[1];
        classes[0] = serviceClass;

        return Proxy.newProxyInstance(serviceClass.getClassLoader(),
                classes,
                new RpcInvocationHandler(serviceClass.getName()));
    }
}
