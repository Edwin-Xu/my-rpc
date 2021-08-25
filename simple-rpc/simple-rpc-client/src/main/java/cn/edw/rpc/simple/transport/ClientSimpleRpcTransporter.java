package cn.edw.rpc.simple.transport;

import cn.edw.rpc.simple.protocol.SimpleRpcRequest;
import cn.edw.seri.core.Deseri;
import cn.edw.seri.core.Seri;
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
        InputStream inputStream = null;

        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();

            final Seri seri = new Seri();
            seri.write(request);
            final byte[] reqBytes = seri.getBytes();

            outputStream.write(reqBytes);

            inputStream = socket.getInputStream();

            final byte[] bytes = new byte[1024 * 5];
            final int read = inputStream.read(bytes);

            final Deseri deseri = new Deseri(bytes);

            return deseri.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
