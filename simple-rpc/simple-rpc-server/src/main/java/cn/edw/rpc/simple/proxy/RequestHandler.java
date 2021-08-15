package cn.edw.rpc.simple.proxy;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;
import cn.edw.rpc.simple.transport.SimpleRpcTransporter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 请求处理器。对一个请求进行处理，主要是获取请求，协议解析，
 * 然后实例化一个实现类，调用，并返回结果
 * @author taoxu.xu
 * @date 8/15/2021 6:11 PM
 */
@AllArgsConstructor
public class RequestHandler implements Runnable{

    private final Socket socket;

    @Override
    public void run() {
        final SimpleRpcRequest request = SimpleRpcTransporter.request(socket);
        Object res = null;
        System.out.println(request);
        if (request != null){
            final Class<?> serviceClass = ServiceMap.getServiceClass(request.getInterfaceName());
            if (serviceClass != null){
                try {
                    // 获取参数Class，用于获取Method
                    final Object[] params = request.getParams();
                    int len = params.length;
                    final Class<?>[] classes = new Class[len];
                    for (int i = 0; i < len; i++) {
                        classes[i] = params[i].getClass();
                    }
                    // 获取方法，实例化一个实现类(这里有点问题，实例化对象时不知道这个对象是否需要通过构造函数初始化特殊的成员属性)
                    final Method method = serviceClass.getMethod(request.getMethodName(), classes);
                    final Object instance = serviceClass.newInstance();
                    res = method.invoke(instance, request.getParams());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            SimpleRpcTransporter.response(socket, res);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
