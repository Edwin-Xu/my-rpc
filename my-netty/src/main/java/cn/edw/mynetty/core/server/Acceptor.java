package cn.edw.mynetty.core.server;

import cn.edw.mynetty.exception.NotAcceptableException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author taoxu.xu
 * @date 9/1/2021 3:39 PM
 */
public class Acceptor{

    public void accept(SelectionKey key, Selector selector) throws IOException {
        if (!key.isAcceptable()){
            throw new NotAcceptableException();
        }
        // 把之前attach的ServerSocketChannel拿出来
        final ServerSocketChannel server = (ServerSocketChannel) key.attachment();
        final SocketChannel accept = server.accept();

        accept.configureBlocking(false);

        // 现在需要注册那种事件。两种设计，一是分客户端和服务端，先读还是先写；二是同时监听读写。
        // 只需要注册读事件，读完后应该是调用业务方法进行处理，然后返回，不用监听写
        accept.register(selector, SelectionKey.OP_READ);
    }

}


