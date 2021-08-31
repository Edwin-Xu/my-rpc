package cn.edw.mynetty.reactor.singlereactormultithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * ReactorServer: 单Reactor单线程模式
 * @author taoxu.xu
 * @date 8/26/2021 5:41 PM
 */
class Reactor implements Runnable {
    /**
     * selector，用于接收请求
     * */
    final Selector selector;
    /**
     * 启动一个socket，使用channel连接
     * */
    final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        // SelectionKey 用于获取在socket上有那些事件发生
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        // SelectionKey 需要一个Acceptor来接收这些事件
        sk.attach(new Acceptor());
    }

    /**
    * Alternatively, use explicit SPI provider:
    * SelectorProvider p = SelectorProvider.provider();
    * selector = p.openSelector();
    * serverSocket = p.openServerSocketChannel();
    */
    @Override
    public void run() { // normally in a new Thread
        try {
            while (!Thread.interrupted()) {
                // 进行一次select， 获取到事件
                selector.select();
                // 遍历目前获取到的事件
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    // 分发到具体的handler，谁来分发？ 当然是Acceptor
                    dispatch(it.next());
                }
                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 分发事件
     * */
    void dispatch(SelectionKey k) {
        // 获取 SelectionKey 绑定的 Acceptor
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            // 直接run？ 是阻塞的 TODO
            r.run();
        }
    }


    /**
     * Acceptor 用于接收请求
     * */
    class Acceptor implements Runnable { // inner
        @Override
        public void run() {
            try {
                // 接收一个请求
                SocketChannel c = serverSocket.accept();
                System.out.println("Acceptor " + hashCode() +" accept a request.");
                if (c != null) {
                    // 接收请求后使用对应的handler处理
                    new Handler(selector, c);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

/**
 * Handler事件处理器。 Handler是运行在具体的线程上的，SocketChannel和线程之间交互，必须通过Buffer
 * */
final class Handler implements Runnable {
    private static final int MAXIN = 1024;
    private static final int MAXOUT = 1024;

    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(MAXIN);
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now  TODO
        sk = socket.register(sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }

    boolean inputIsComplete() {
        return input.array().length > 50;
    }

    boolean outputIsComplete() {
        return output.array().length > 50;
    }

    void process() {
        System.out.println("handler "+ hashCode() +" processing");
        output.put(input.array());
    }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 读取
     * */
    void read() throws IOException {
        System.out.println("reading");
        socket.read(input);
        System.out.println(new String(input.array()));
        if (inputIsComplete()) {
            process();
            state = SENDING;
            // Normally also do first write now
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    /**
     * 发送
     * */
    void send() throws IOException {
        System.out.println("sending");

        socket.write(output);
        if (outputIsComplete()) {
            sk.cancel();
        }
    }

    public static void main(String[] args) throws IOException {
        final Reactor reactor = new Reactor(6666);
        final Thread thread = new Thread(reactor);
        thread.start();
    }
}
