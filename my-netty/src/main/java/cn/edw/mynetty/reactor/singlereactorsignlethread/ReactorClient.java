package cn.edw.mynetty.reactor.singlereactorsignlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author taoxu.xu
 * @date 8/27/2021 2:37 PM
 */
public class ReactorClient {
    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private final ByteBuffer input = ByteBuffer.allocate(1024);
    private final ByteBuffer output = ByteBuffer.allocate(1024);

    public ReactorClient(String host, int port) throws IOException {
        selector = Selector.open();

        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 注册到Selector上，注意监听的事件类型是Connect，即连接事件，而服务端是Accept
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(host, port));

        // finishConnect在连接成功时会消耗一次OP_CONNECT事件, 所以在连接时如果调用消耗了，后面就会select不到

        System.out.println("client is connected to server");
    }

    public void listen() throws IOException {
        while (true){
            // finishConnect在连接成功时会消耗一次OP_CONNECT事件
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


    private void process(SelectionKey key) throws IOException {
        if (key.isConnectable()){
            doConnect(key);
        }else if (key.isReadable()){
            doRead(key);
        }else if (key.isWritable()){
            doWrite(key);
        }
    }


    private void doConnect(SelectionKey key) throws IOException {
        // 获取连接
        final SocketChannel channel = (SocketChannel) key.channel();
        // 如果正在连接，finishConnect，消耗CONNECT事件()
        if (channel.isConnectionPending()){
            channel.finishConnect();
        }

        // 确认连接后注册读写监听到selector
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
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
    }

    /**
     * 待发送的消息
     * */
    private final Deque<String> queue = new LinkedList<>();

    private void doWrite(SelectionKey key) {
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
        private ReactorClient client;
        public MsgTask(ReactorClient client){
            this.client = client;
        }
        @Override
        public void run() {
            final Scanner scanner = new Scanner(System.in);
            String cmd = "";
            while (!"q".equals(cmd = scanner.nextLine())){
                if (cmd.startsWith("send ")){
                    client.putMessage(cmd.substring(5));
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            final ReactorClient client = new ReactorClient("localhost", 6666);
            new Thread(new MsgTask(client)).start();
            client.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
