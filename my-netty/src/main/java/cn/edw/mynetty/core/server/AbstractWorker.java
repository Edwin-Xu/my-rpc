package cn.edw.mynetty.core.server;

import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Writable;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author taoxu.xu
 * @date 9/1/2021 2:39 PM
 */
public abstract class AbstractWorker implements Runnable {
    /**
     * worker绑定一个SocketChannel
     * */
    protected final SocketChannel socketChannel;

    protected SelectionKey selectionKey;

    protected Readable readable;

    protected Writable writable;

    protected AbstractWorker(SelectionKey key, Readable readable, Writable writable) throws IOException {
        this.selectionKey = key;
        this.socketChannel = (SocketChannel) key.channel();
        socketChannel.configureBlocking(false);

        this.readable = readable;
        this.writable = writable;

        // Optionally try first read now
        // TODO 这里是干嘛？ ops=0表示什么？ 0表示NOTHING？即不listening任何事件，这里只是为了获取SelectionKey
        // sk = socketChannel.register(sel, 0);
        // TODO 把这个handler本生作为attachment？
        // sk.attach(this);
        // TODO 这里才是真正的interest事件
        // sk.interestOps(SelectionKey.OP_READ );
        // TODO ??? 唤醒selector？  为何需要唤醒
        // sel.wakeup();
    }

}
