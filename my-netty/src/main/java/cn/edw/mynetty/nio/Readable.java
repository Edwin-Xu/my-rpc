package cn.edw.mynetty.nio;

import java.nio.channels.SocketChannel;

/**
 * 读SocketChannel
 * @author taoxu.xu
 * @date 9/1/2021 5:02 PM
 */
public interface Readable {
    Object read(SocketChannel socketChannel) throws Exception;
}
