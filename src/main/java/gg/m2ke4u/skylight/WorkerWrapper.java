package gg.m2ke4u.skylight;

import com.google.common.collect.Sets;
import gg.m2ke4u.skylight.config.WorkerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerWrapper {
    private static final AtomicInteger threadId = new AtomicInteger();
    private static final Set<Thread> workers = Sets.newConcurrentHashSet();

    public static final ForkJoinPool.ForkJoinWorkerThreadFactory FORK_JOIN_WORKER_THREAD_FACTORY = pool -> {
        ForkJoinWorkerThread workerThread = new ForkJoinWorkerThread(pool){
            protected void onStart() {
                workers.add(Thread.currentThread());
                super.onStart();
            }
            protected void onTermination(Throwable exception) {
                if (WorkerConfig.AUTO_CLEAR_WORKERS){
                    workers.remove(Thread.currentThread());
                }
                super.onTermination(exception);
            }
        };
        workerThread.setContextClassLoader(MinecraftServer.class.getClassLoader());
        workerThread.setPriority(5);
        workerThread.setDaemon(true);
        workerThread.setName("Lettuce-Fork/Join-Worker # "+threadId.getAndIncrement());
        return workerThread;
    };

    public static final ThreadFactory THREAD_FACTORY = task -> {
        Thread worker = new Thread(()->{
            try{
                workers.add(Thread.currentThread());
                task.run();
            }finally {
                workers.remove(Thread.currentThread());
            }
        },"Lettuce-Pool-Worker # "+threadId.getAndIncrement());
        worker.setDaemon(true);
        worker.setPriority(5);
        worker.setContextClassLoader(MinecraftServer.class.getClassLoader());
        return worker;
    };

    public static ForkJoinPool getNewPool(int threads){
        return new ForkJoinPool(threads,FORK_JOIN_WORKER_THREAD_FACTORY,null,true);
    }

    public static boolean isWorker(){
        return workers.contains(Thread.currentThread());
    }
}
