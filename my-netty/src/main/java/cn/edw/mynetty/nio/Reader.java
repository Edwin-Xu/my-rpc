package cn.edw.mynetty.nio;

import cn.edw.seri.core.Deseri;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * NIO Reader
 *
 * @author taoxu.xu
 * @date 9/3/2021 11:24 AM
 */
public class Reader implements Readable {
    /**
     * 反序列化(解码)
     */
    private final Deseri deseri = new Deseri();

    /**
     * 读取
     *
     * @param socketChannel channel
     * @throws IOException 如果socket关闭或者其他情况，抛给业务类处理
     */
    @Override
    public Object read(SocketChannel socketChannel) throws Exception {
        // 每次都开空间稍有点不合适，不过为了提高reader的适用性也只能这样了
        final ByteBuffer input = ByteBuffer.allocate(1024);
        int read = 0;
        while (true) {
            // 先清空buffer
            input.clear();
            // 从Channel中写到buffer
            read = socketChannel.read(input);
            // 客户端主动关闭了, 服务端也应该关闭掉
            if (read == -1) {
                throw new ClosedChannelException();
            } else if (read == 0) {
                // 3种情况：1.数据读完了，2.buffer满了，3.客户端数据发送完毕了
                // TODO 第二种情况不能break啊！！！ buffer满了是返回0？而不是读取的数量？？？？
                break;
            } else {
                final byte[] array = input.array();
                // 添加到Deseri中
                deseri.appendBytes(array, 0, read);
            }
        }
        // 所有的bytes都已经写入到Deseri中，然后开始反序列化
        // 序列化和反序列化都是自动识别数据类型
        return deseri.read();
    }
}
