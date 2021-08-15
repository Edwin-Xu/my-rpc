package cn.edw.rpc.simple.transport;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;

import java.io.*;
import java.net.Socket;

/**
 * 传输层
 * @author taoxu.xu
 * @date 8/15/2021 4:27 PM
 */
public class SimpleRpcTransporter {

    public static SimpleRpcRequest request(Socket socket){
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            return (SimpleRpcRequest) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (objectInputStream != null){
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void response(Socket socket, Object res){
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            final ObjectOutputStream objOutputStream = new ObjectOutputStream(outputStream);
            objOutputStream.writeObject(res);
            objOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {


            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
