package cn.edw.rpc.simple.proxy;

import cn.edw.myrpc.api.HelloService;
import cn.edw.myrpc.api.UserService;
import cn.edw.rpc.simple.service.HelloServiceImpl;
import cn.edw.rpc.simple.service.UserServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来记录接口对应的实现类，这里简化为一个实现类，
 * 同时实际中不应该这样做，应该通过包扫描找到实现类。
 * @author taoxu.xu
 * @date 8/15/2021 6:21 PM
 */
public class ServiceMap {
    private static final Map<String, Class< ? >> SERVICE_MAP = new HashMap<>();

    static {
        SERVICE_MAP.put(HelloService.class.getName(),
                HelloServiceImpl.class);
        SERVICE_MAP.put(UserService.class.getName(),
                UserServiceImpl.class);
    }

    public static Class<? >getServiceClass(String interfaceName) {
        return SERVICE_MAP.get(interfaceName);
    }

}
