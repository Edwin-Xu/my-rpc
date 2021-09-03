package cn.edw.mynetty.core.server;

import cn.edw.mynetty.core.handler.WorkHandler;
import cn.edw.mynetty.nio.Readable;
import cn.edw.mynetty.nio.Reader;
import cn.edw.mynetty.nio.Writable;
import cn.edw.mynetty.nio.Writer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.List;

/**
 * @author taoxu.xu
 * @date 9/1/2021 4:00 PM
 */
public class Worker extends AbstractWorker {
    /**
     * 可以有多个handler
     */
    private final List<WorkHandler> workHandlers ;

    protected Worker(SelectionKey c, List<WorkHandler> workHandlers) throws IOException {
        this(c, new Reader(), new Writer(), workHandlers);
    }

    protected Worker(SelectionKey c, Readable readable, Writable writable, List<WorkHandler> workHandlers) throws IOException {
        super(c, readable, writable);
        this.workHandlers = workHandlers;
    }

    @Override
    public void run() {
        // System.out.println("worker is running ... ");
        try {
            // 该任务的目标就是 顺序执行所有的handler
            for (WorkHandler handler : workHandlers) {
                handler.handle(selectionKey, readable, writable);
            }
            
        }catch (Exception e) {
            System.out.println(e.getMessage());

            // 如果异常，则关闭 socketChannel
            if (socketChannel != null){
                try {
                    socketChannel.close();
                } catch (IOException ioException) {

                }
            }
        }
    }

}
