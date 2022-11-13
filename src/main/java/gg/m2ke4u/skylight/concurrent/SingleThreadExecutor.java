package gg.m2ke4u.skylight.concurrent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class SingleThreadExecutor implements Executor {
    private final Queue<TaskEntry> tasks = new ConcurrentLinkedQueue<>();
    private static final Logger logger = LogManager.getLogger();

    public SingleThreadExecutor(ThreadFactory factory){
        Thread worker = factory.newThread(()->{
            for (;;){
                try {
                    if (!this.tasks.isEmpty()){
                        tasks.poll().runTask();
                        continue;
                    }
                    Thread.sleep(0,1);
                }catch (Exception e){
                    logger.error(e);
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
        Thread cleaner = factory.newThread(()->{
            for (;;){
                try {
                    this.tasks.removeIf(task -> (System.currentTimeMillis() - task.getCommitTime()) > 50);
                    Thread.sleep(50);
                }catch (Exception e){
                    logger.error(e);
                }
            }
        });
        cleaner.setName(cleaner.getName()+"@Worker-Cleaner");
        cleaner.start();
    }

    @Override
    public void execute(Runnable runnable) {
        this.tasks.add(new TaskEntry(runnable));
    }

    private static final class TaskEntry{
        private final Runnable task;
        private final long commitTime;

        public TaskEntry(Runnable task){
            this.task = task;
            this.commitTime = System.currentTimeMillis();
        }

        public void runTask(){
            this.task.run();
        }

        public long getCommitTime(){
            return this.commitTime;
        }
    }
}
