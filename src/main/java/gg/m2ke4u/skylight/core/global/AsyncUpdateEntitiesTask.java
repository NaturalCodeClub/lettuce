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

    public AsyncUpdateEntitiesTask(Executor entitiesExecutor, Consumer<AsyncUpdateEntitiesTask> callBackTask){
        this.executorToTickEntities = entitiesExecutor;
        this.callBackTask = callBackTask;
    }

    @Override
    public void call(World input) {
        profiler.postSection();
        input.asyncEntitiesExecutor.execute(()->{
            try{
                final UpdateEntitiesTask task = new UpdateEntitiesTask(this.executorToTickEntities);
                this.currentTask = task;
                task.call(input);
            }finally {
                this.finished = true;
                try{
                    this.callBackTask.accept(this);
                }catch (Exception e){
                    LogManager.getLogger().error("Failed to execute call back task!",e);
                }
            }
        });
        profiler.finishedTask();
    }

    @Override
    public boolean finished() {
        return this.finished;
    }

    public SinglePartProfiler getProfiler(){
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
}
