package cn.edw.mynetty.sample;


import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Writable;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 自定义handler，用于处理业务逻辑
 * @author taoxu.xu
 * @date 9/3/2021 12:25 PM
 */
public class SampleHandler implements WorkHandler {
    @Override
    public void handle(SelectionKey key, Readable readable, Writable writable) throws Exception {
        // 先取消注册ACCEPT事件，避免再次连接
        key.interestOps(0);

        final SocketChannel socketChannel = (SocketChannel) key.channel();
        final Req req = (Req) readable.read(socketChannel);
        System.out.println("server red: "+req);
        req.setName("This is your new name");
        writable.write(socketChannel, req);

        // 使用短连接：发送完数据后把连接关掉
        socketChannel.close();
    }
}