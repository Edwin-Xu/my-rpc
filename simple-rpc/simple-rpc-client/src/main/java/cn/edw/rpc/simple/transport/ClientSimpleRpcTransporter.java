package cn.edw.rpc.simple.transport;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.Socket;

/**
 * @author taoxu.xu
 * @date 8/15/2021 1:39 AM
 */
@AllArgsConstructor
public class ClientSimpleRpcTransporter {
    private String host;
    private int port;

    public Object request(SimpleRpcRequest request){
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;
        ObjectInputStream objInputStream = null;

        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();

            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            inputStream = socket.getInputStream();

            objInputStream = new ObjectInputStream(inputStream);

            return objInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
