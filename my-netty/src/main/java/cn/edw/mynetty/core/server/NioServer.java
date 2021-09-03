package cn.edw.mynetty.core.server;

import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.util.NetUtil;

import java.io.IOException;
import java.util.List;

/**
 * Server 端
 * @author taoxu.xu
 * @date 9/1/2021 3:39 PM
 */
public class NioServer {

    /**
     * 端口
     * */
    private int port;

    /**
     * Reactor模式
     * */
    private Reactor reactor;

    /**
     * 需要使用的handler列表，这些handler会被依次执行
     * */
    private final List<WorkHandler> workHandlers;

    /**
     * @param workHandlers handlers
     * @param port port
     * */
    public NioServer(int port, List<WorkHandler> workHandlers) throws IOException {
        // 端口检查
        NetUtil.validatePort(port);

        this.port = port;
        this.workHandlers = workHandlers;

        // 启动Reactor线程
        reactor = new Reactor(this.port, workHandlers);
        final Thread reactorThread = new Thread(reactor);
        reactorThread.start();
    }

    public List<WorkHandler> getWorkHandlers() {
        return workHandlers;
    }
}
