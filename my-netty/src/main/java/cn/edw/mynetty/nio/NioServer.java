package cn.edw.mynetty.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;


/**
 * @author taoxu.xu
 * @date 8/29/2021 3:34 PM
 */
public class NioServer {

    private int port;

    private static Selector selector = null;

    /**
     * 指定端口号启动服务
     * */
    public boolean startServer(int port){
        try {
            this.port = port;
            selector = Selector.open();
            //打开监听通道
            ServerSocketChannel server = ServerSocketChannel.open();
            //绑定端口
            server.bind(new InetSocketAddress(this.port));
            //默认configureBlocking为true,如果为 true,此通道将被置于阻塞模式；如果为 false.则此通道将被置于非阻塞模式
            server.configureBlocking(false);
            //创建选择器
            selector = Selector.open();
            //监听客户端连接请求
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务端启动成功，监听端口：" + port);
        }catch (Exception e){
            System.out.println("服务器启动失败");
            return  false;
        }
        return  true;
    }

    public void listen() throws IOException {
        while(true){
            //阻塞方法，轮询注册的channel,当至少一个channel就绪的时候才会继续往下执行
            int keyCount = selector.select();
            System.out.println("当前有 "+keyCount+" 个channel有事件就绪");
            //获取就绪的SelectionKey
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            SelectionKey key = null;
            //迭代就绪的key
            while(it.hasNext()){
                key = it.next();
                it.remove();
                //SelectionKey相当于是一个Channel的表示，标记当前channel处于什么状态
                // 按照channel的不同状态处理数据
                process(key);
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        //该channel已就绪，可接收消息
        if(key.isAcceptable()){
            System.out.println("accept事件就绪...");
            doAccept(key);
        }else if(key.isReadable()){
            System.out.println("read事件就绪...");
            doRead(key);
        }else if(key.isWritable()){
            System.out.println("write事件就绪...");
            doWrite(key);
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        //获取对应的socket
        SocketChannel socket = (SocketChannel)key.channel();
        //获取key上的附件
        String content = (String)key.attachment();
        System.out.println("write:" + content);
        socket.write(ByteBuffer.wrap(content.getBytes()));
        socket.close();
    }

    private void doRead(SelectionKey key) throws IOException {
        //获取对应的socket
        SocketChannel socket = (SocketChannel)key.channel();
        //设置一个读取数据的Buffer 大小为1024
        ByteBuffer buff = ByteBuffer.allocate(1024);
        StringBuilder content = new StringBuilder();

        // 和-1没关系，-1表示没有连接上
        while(socket.read(buff) > 0) {
            // read 是写到buff中，buff.array()是读的方式返回？所以需要flip
            buff.flip();
            content.append(new String(buff.array(), StandardCharsets.UTF_8));
        }
        //注册selector，并设置为可写模式
        key = socket.register(selector,SelectionKey.OP_WRITE);
        //在key上携带一个附件(附近就是之后要写的内容)
        key.attach("服务端已收到:"+content);
        System.out.println("读取内容：" + content);
    }

    private void doAccept(SelectionKey key) throws IOException {
        //获取对应的channel
        ServerSocketChannel server = (ServerSocketChannel)key.channel();
        //从channel中获取socket信息
        SocketChannel socket = server.accept();
        //设置为非阻塞模式
        socket.configureBlocking(false);
        //注册selector，并设置为可读模式
        socket.register(selector, SelectionKey.OP_READ);
    }


    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.startServer(6666);
        nioServer.listen();
    }
}