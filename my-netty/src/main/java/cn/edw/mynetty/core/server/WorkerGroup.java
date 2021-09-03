package cn.edw.mynetty.core.server;

import java.nio.channels.SelectionKey;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author taoxu.xu
 * @date 9/1/2021 2:39 PM
 */
public class WorkerGroup {
    private static final int MAX_THREADS = 128;
    /**
     * TODO 默认线程数，应该是CPU核数*2
     * */
    private static final int DEFAULT_THREADS = 8;

    private int nThreads;

    private final ExecutorService executor;

    /**
     * 正在处理中的事件, 通过记录避免同一个事件频繁触发、提交
     * */
    private final Set<SelectionKey> handlingEvents ;

    public WorkerGroup() {
        this(DEFAULT_THREADS);
    }

    public WorkerGroup(int nThreads) {
        if (nThreads < DEFAULT_THREADS){
            this.nThreads = DEFAULT_THREADS;
        }else {
            this.nThreads = Math.min(nThreads, MAX_THREADS);
        }

        handlingEvents = new HashSet<>();

        executor = new ThreadPoolExecutor(this.nThreads, MAX_THREADS,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>() );
    }

    /**
     * 提交任务
     * */
    public void submit(AbstractWorker r) {
        executor.submit(r);
    }

    public void addHandlingEvent(SelectionKey key){
        this.handlingEvents.add(key);
    }
    public void removeHandlingEvent(SelectionKey key){
        this.handlingEvents.remove(key);
    }
    public boolean isHandling(SelectionKey key){
        return this.handlingEvents.contains(key);
    }
}
