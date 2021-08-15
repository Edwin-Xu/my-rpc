package cn.edw.rpc.simple.service;

import cn.edw.myrpc.api.HelloService;

/**
 * @author taoxu.xu
 * @date 8/15/2021 4:35 PM
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hi(String name) {
        return "hi, "+name;
    }
}
