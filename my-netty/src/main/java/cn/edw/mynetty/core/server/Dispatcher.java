package cn.edw.mynetty.core.server;

import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Reader;
import cn.edw.mynetty.nio.Writable;
import cn.edw.mynetty.nio.Writer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author taoxu.xu
 * @date 9/2/2021 2:20 PM
 */
public class Dispatcher {
    /**
     * Acceptor 接收Accept事件
     */
    private final Acceptor acceptor = new Acceptor();

    /**
     * handlers
     */
    private final List<WorkHandler> workHandlers;

    /**
     * 读写工具类，用于传递给handler
     */
    private final Readable readable;
    private final Writable writable;

    /**
     * workerGroup 放到Dispatcher这里
     */
    private final WorkerGroup workerGroup = new WorkerGroup();

    public Dispatcher(List<WorkHandler> workHandlers) {
        this.workHandlers = workHandlers;
        readable = new Reader();
        writable = new Writer();
    }

    public void dispatch(SelectionKey key, Selector selector) throws IOException {
        if (key.isAcceptable()) {
            acceptor.accept(key, selector);
        } else {
            if (key.isReadable()) {
                // 如果该事件正在处理中，就忽略，不提交到线程池执行
                if (workerGroup.isHandling(key)) {
                    return;
                }

                // 添加到handlingEvents，表示加入正在处理的任务中
                workerGroup.addHandlingEvent(key);

                // 获取Channel
                final SocketChannel channel = (SocketChannel) key.channel();

                // 末尾添加一个额外的handler，用于当event处理完后从handlingEvents中移除
                workHandlers.add((socketChannel, readable, writable) -> workerGroup.removeHandlingEvent(key));

                // 提交任务进行执行
                workerGroup.submit(new Worker(key, readable, writable, workHandlers));
            } else {
                // 监听的事件只有READ、ACCEPT
                throw new IOException("Event type not support!");
            }
        }
    }
}
