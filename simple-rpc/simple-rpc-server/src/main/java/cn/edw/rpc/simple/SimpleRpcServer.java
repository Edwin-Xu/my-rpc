package cn.edw.rpc.simple;

import cn.edw.rpc.simple.proxy.RequestHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author taoxu.xu
 * @date 8/15/2021 4:26 PM
 */
public class SimpleRpcServer {
    private ExecutorService executorService;
    private int port;

    public SimpleRpcServer(int port) {
        this.executorService = Executors.newCachedThreadPool();
        this.port = port;
    }

    public void run() {
        try {
            final ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server is running");

            while (true){
                final Socket accept = serverSocket.accept();
                executorService.submit(new RequestHandler(accept));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final SimpleRpcServer simpleRpcServer = new SimpleRpcServer(8888);
        simpleRpcServer.run();
    }
}
