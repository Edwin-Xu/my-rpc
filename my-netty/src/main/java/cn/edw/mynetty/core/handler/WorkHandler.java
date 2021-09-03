package cn.edw.mynetty.core.handler;

import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Writable;

import java.nio.channels.SelectionKey;

/**
 * 使用handler把具体的处理流程交给用户
 * @author taoxu.xu
 * @date 9/3/2021 11:06 AM
 */
public interface WorkHandler {
    /**
     * 处理请求(函数式接口)
     * @param selectionKey socket
     * @param readable 这里把读写的工具也传入
     * @param writable 这里把读写的工具也传入
     * @throws Exception 读写过程中可能的异常
     * */
    void handle(SelectionKey selectionKey, Readable readable, Writable writable) throws Exception;
}
