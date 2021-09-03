package cn.edw.mynetty.core.server;

import cn.edw.mynetty.core.handler.WorkHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 单Reactor 多线程 模式
 *
 * @author taoxu.xu
 * @date 9/1/2021 2:38 PM
 */
public class Reactor implements Runnable {

    private int port;

    /**
     * selector，用于接收请求
     */
    final Selector selector;
    /**
     * 启动一个socket，使用channel连接
     */
    final ServerSocketChannel serverSocketChannel;


    private Dispatcher dispatcher;

    public Reactor(int port, List<WorkHandler> workHandlers) throws IOException {
        this.port = port;
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
        this.serverSocketChannel.configureBlocking(false);
        final SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.dispatcher = new Dispatcher(workHandlers);

        // TODO 把server带上吧， 好像没用
        selectionKey.attach(serverSocketChannel);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 进行一次select， 获取到事件
                selector.select();
                // 遍历目前获取到的事件
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    // 分发到具体的handler
                    dispatcher.dispatch(it.next(), selector);
                }
                // 清空
                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
