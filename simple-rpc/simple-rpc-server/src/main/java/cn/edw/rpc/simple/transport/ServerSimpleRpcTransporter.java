package cn.edw.rpc.simple.transport;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;
import cn.edw.seri.core.Deseri;
import cn.edw.seri.core.Seri;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 传输层
 *
 * @author taoxu.xu
 * @date 8/15/2021 4:27 PM
 */
public class ServerSimpleRpcTransporter {

    public static SimpleRpcRequest request(Socket socket) {
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = socket.getInputStream();
//            objectInputStream = new ObjectInputStream(inputStream);
//            return (SimpleRpcRequest) objectInputStream.readObject();

            // TODO 还有一些问题
            final byte[] bytes = new byte[1024 * 5];
            final int read = inputStream.read(bytes);
            final Deseri deseri = new Deseri(bytes);
            final Object object = deseri.read();

            return (SimpleRpcRequest) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void response(Socket socket, Object res) {
        /*
        * OutputStream outputStream = null;
        * ObjectOutputStream objOutputStream = null;
        * try {
        *     outputStream = socket.getOutputStream();
        *     objOutputStream = new ObjectOutputStream(outputStream);
        *     objOutputStream.writeObject(res);
        *     objOutputStream.flush();
        * } catch (IOException e) {
        *     e.printStackTrace();
        * }*/

        try {
            final OutputStream outputStream = socket.getOutputStream();
            final Seri seri = new Seri();
            seri.write(res);
            outputStream.write(seri.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
