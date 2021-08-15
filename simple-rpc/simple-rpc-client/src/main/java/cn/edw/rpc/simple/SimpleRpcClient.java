package cn.edw.rpc.simple;

import cn.edw.myrpc.api.HelloService;
import cn.edw.rpc.simple.proxy.RpcProxy;

/**
 * @author taoxu.xu
 * @date 8/15/2021 1:33 AM
 */
public class SimpleRpcClient {
    public static void main(String[] args) {
        final HelloService proxy = (HelloService)
                RpcProxy.newRpcProxy(HelloService.class);
        final String edw = proxy.hi("edw");
        System.out.println(edw);

//        final UserService userService =
//                (UserService) RpcProxy.newRpcProxy(UserService.class);
//        final User user = userService.getUserById(1);
//        System.out.println(user.toString());

    }
}
