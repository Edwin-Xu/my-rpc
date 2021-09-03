package cn.edw.mynetty.nio;

import java.nio.channels.SocketChannel;

/**
 * NIO Write
 * @author taoxu.xu
 * @date 9/1/2021 5:06 PM
 */
public interface Writable {
    /**
     * write channel
     * @param socketChannel channel
     * @param object 需要写入的对象
     * */
    void write(SocketChannel socketChannel, Object object) throws Exception;
}
