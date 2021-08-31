package cn.edw.mynetty.reactor.singlereactorsignlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author taoxu.xu
 * @date 8/26/2021 3:26 PM
 */
public class ReactorServer {
    private ServerSocketChannel server = null;
    private Selector selector = null;

    /**
     * buffer 默认情况下是写模式吧，读之前需要先flip
     * */
    final ByteBuffer input = ByteBuffer.allocate(1024);
    final ByteBuffer output = ByteBuffer.allocate(1024);

    public ReactorServer(int port) throws IOException {
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(false);

        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("sever is running");
    }

    private void listen() throws IOException {
        System.out.println("listening");
        while (true) {
            try {
                selector.select();

                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
                    process(key);
                    // 移除key
                    iterator.remove();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void process(SelectionKey key) {
        if (key.isAcceptable()) {
            doAccept(key);
        } else if (key.isReadable()) {
            doRead(key);
        } else if (key.isWritable()) {
            doWrite(key);
        }
    }

    private void doAccept(SelectionKey key) {
        System.out.println("accepted " + key.hashCode());
        // 注意：如果是Accept，必须要处理掉，不然这个SelectionKey一直存在，删不掉的
        SocketChannel socketChannel = null;
        try {
            // 获取Channel
            socketChannel = server.accept();
            socketChannel.configureBlocking(false);
            // 读写都监听
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (IOException e) {
            // 如果异常，则关闭socket
            System.out.println(e.getMessage());
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 读的异常不要处理，抛出 TODO
     */
    private void doRead(SelectionKey key) {
        final SocketChannel channel = (SocketChannel) key.channel();

        try {
            int read = 0;
            do {
                // 先清空buffer
                input.clear();
                // 从Channel中写到buffer
                read = channel.read(input);
                // 客户端主动关闭了, 服务端也应该关闭掉
                if (read == -1){
                    throw new ClosedChannelException();
                }else if (read == 0) {
                     // 3种情况：1.数据读完了，2.buffer满了，3.客户端数据发送完毕了
                    break;
                }else{
                    final byte[] array = input.array();
                    System.out.println("read " + read + ":" + new String(array, 0, read));
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Channel Closed Because "+e.getMessage());
            try {
                if (channel != null){
                    channel.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        // TODO 正常情况下应该是不能把Channel关闭的
    }

    /**
     * 待发送的消息
     * */
    private final Deque<String> queue = new LinkedList<>();

    private void doWrite(SelectionKey key)  {
        // 如果有数据，则发送之
        if (queue.size()>0){
            final String msg = queue.removeFirst();
            final SocketChannel channel = (SocketChannel) key.channel();
            final ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            try {
                channel.write(buffer);
            } catch (IOException e) {
                System.out.println("Channel Closed Because "+e.getMessage());
                try {
                    channel.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加信息，会自动发送
     * */
    public void putMessage(String msg) {
        queue.add(msg);
    }


    public static class MsgTask implements Runnable {
        private ReactorServer server;
        public MsgTask(ReactorServer server){
            this.server = server;
        }
        @Override
        public void run() {
            final Scanner scanner = new Scanner(System.in);
            String cmd = "";
            while (!"q".equals(cmd = scanner.nextLine())){
                if (cmd.startsWith("send ")){
                    server.putMessage(cmd.substring(5));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            final ReactorServer reactorServer = new ReactorServer(6666);
            // 单独一个线程用来添加消息
            new Thread(new MsgTask(reactorServer)).start();
            reactorServer.listen();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
