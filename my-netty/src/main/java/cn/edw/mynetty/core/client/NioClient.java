package cn.edw.mynetty.core.client;

import cn.edw.mynetty.util.NetUtil;
import cn.edw.seri.core.Deseri;
import cn.edw.seri.core.Seri;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author taoxu.xu
 * @date 9/3/2021 4:39 PM
 */
public class NioClient {
    /**
     * host
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * SocketChannel
     */
    private Socket socket;

    /**
     * 序列化
     */
    private final Seri seri;

    /**
     * 反序列化
     */
    private final Deseri deseri;

    private byte[] bytes = new byte[1024];


    public NioClient(String host, int port) throws IOException {
        NetUtil.validatePort(port);
        this.host = host;
        this.port = port;

        seri = new Seri();
        deseri = new Deseri();

        // connect server
        connect();
    }

    /**
     * 建立连接
     */
    private void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
    }

    /**
     * 请求并返回
     *
     * @param request 请求的数据
     * @return 返回的对象
     */
    public Object request(Object request) {
        try {
            // write
            write(request);
            // read
            return read();
        } catch (Exception e) {
            System.out.println("SocketChannel Closed Because :" + e.getMessage());
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    System.out.println(ioException.getMessage());
                }
            }
            return null;
        }finally {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * write channel
     */
    private void write(Object request) throws Exception {
        // 先重置Seri
        seri.reset();
        // 写入对象
        seri.write(request);

        final byte[] bytes = seri.getBytes();
        // 写入socket
        socket.getOutputStream().write(bytes);
    }


    /**
     * 读取
     */
    private Object read() throws Exception {
        // 重置
        deseri.reset();

        int read = -2;

        final InputStream inputStream = socket.getInputStream();
        do {
            // inputStream.read()本身是阻塞的方法，只有读到数据才会返回
            read = inputStream.read(bytes);
            // 读取该部分bytes
            deseri.appendBytes(bytes, 0, read);
            // available()方法表示还未读的字节数，用来判断后再读。
        } while (inputStream.available() > 0);

        return deseri.read();
    }

}
