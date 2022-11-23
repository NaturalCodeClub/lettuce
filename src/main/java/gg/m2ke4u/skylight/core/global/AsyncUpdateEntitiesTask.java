package gg.m2ke4u.skylight.core.global;

import gg.m2ke4u.skylight.core.TickTask;
import gg.m2ke4u.skylight.core.profiler.SinglePartProfiler;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class AsyncUpdateEntitiesTask implements TickTask<World> {
    private final Executor executorToTickEntities;
    private volatile boolean finished = false;
    private volatile UpdateEntitiesTask currentTask;
    private final SinglePartProfiler profiler = new SinglePartProfiler("AsyncEntities");
    private final Consumer<AsyncUpdateEntitiesTask> callBackTask;

    public AsyncUpdateEntitiesTask(Executor entitiesExecutor, Consumer<AsyncUpdateEntitiesTask> callBackTask) {
        this.executorToTickEntities = entitiesExecutor;
        this.callBackTask = callBackTask;
    }

    @Override
    public void call(World input) {
        this.finished = false;
        profiler.postSection();
        input.asyncEntitiesExecutor.execute(() -> {
            try {
                final UpdateEntitiesTask task = new UpdateEntitiesTask(this.executorToTickEntities);
                this.currentTask = task;
                task.call(input);
            } finally {
                this.finished = true;
                try {
                    this.callBackTask.accept(this);
                } catch (Exception e) {
                    LogManager.getLogger().error("Failed to execute call back task!", e);
                }
            }
        });
        profiler.finishedTask();
    }

    @Override
    public boolean finished() {
        return this.finished;
    }

    public SinglePartProfiler getProfiler() {
        return this.profiler;
    }

    @Override
    public boolean terminate() {
        if (this.currentTask == null) {
            return false;
        }
        return this.currentTask.terminate();
    }

    @Override
    public void forceTerminate() {
        this.currentTask.forceTerminate();
    }

    private final Object waitLock = new Object();

    @Override
    public void awaitFinish(long nanosTimeOut) {
        long counter = nanosTimeOut;
        try {
            synchronized (this.waitLock) {
                while (!this.finished() && counter > 0) {
                    this.waitLock.wait(0, 1);
                    counter--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void awaitFinish() {
        try {
            synchronized (this.waitLock) {
                while (!this.finished()) {
                    this.waitLock.wait(0, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
