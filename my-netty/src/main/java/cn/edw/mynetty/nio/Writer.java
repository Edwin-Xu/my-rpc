package cn.edw.mynetty.nio;

import cn.edw.seri.core.Seri;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author taoxu.xu
 * @date 9/3/2021 11:34 AM
 */
public class Writer implements Writable {
    @Override
    public void write(SocketChannel channel, Object object) throws Exception {
        //序列化/编码
        Seri seri = new Seri();

        // 把对象写到seri
        seri.write(object);
        // 取出bytes
        final byte[] bytes = seri.getBytes();

        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        // 写到Channel
        channel.write(buffer);

    }
}
