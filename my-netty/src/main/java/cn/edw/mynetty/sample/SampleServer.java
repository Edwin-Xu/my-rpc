package cn.edw.mynetty.sample;

import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.core.server.NioServer;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author taoxu.xu
 * @date 9/3/2021 4:41 PM
 */
public class SampleServer {
    public static void main(String[] args) throws IOException {
        final LinkedList<WorkHandler> workHandlers = new LinkedList<WorkHandler>() {{
            add(new SampleHandler());
        }};
        final NioServer nioServer = new NioServer(6666, workHandlers);
    }
}
