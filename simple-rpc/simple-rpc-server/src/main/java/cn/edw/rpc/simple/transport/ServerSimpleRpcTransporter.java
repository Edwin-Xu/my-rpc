package cn.edw.rpc.simple.transport;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;

import java.io.*;
import java.net.Socket;

/**
 * 传输层
 * @author taoxu.xu
 * @date 8/15/2021 4:27 PM
 */
public class ServerSimpleRpcTransporter {

    public static SimpleRpcRequest request(Socket socket){
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            return (SimpleRpcRequest) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void response(Socket socket, Object res){
        OutputStream outputStream = null;
        ObjectOutputStream objOutputStream = null;
        try {
            outputStream = socket.getOutputStream();
            objOutputStream = new ObjectOutputStream(outputStream);
            objOutputStream.writeObject(res);
            objOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
