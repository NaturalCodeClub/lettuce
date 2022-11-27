package gg.m2ke4u.lettuce;

import com.google.common.collect.Lists;
import gg.m2ke4u.skylight.TerminateException;
import gg.m2ke4u.skylight.WorkerWrapper;
import gg.m2ke4u.skylight.core.TickTask;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.concurrent.Executor;

public class Server
{
    private final MinecraftServer serverInstance;
    private final List<TickTask<?>> worldTickTasks = Lists.newCopyOnWriteArrayList();
    private final List<Executor> registedExecutors = Lists.newCopyOnWriteArrayList();
    private final String serverCurrentModName;

    public Server(MinecraftServer serverInstance){
        this.serverInstance = serverInstance;
        this.serverCurrentModName = this.serverInstance.getServerModName();
    }

    public boolean isMainThread(){
        return this.serverInstance.isCallingFromMinecraftThread();
    }

    public boolean isWorkerThread(){
        return WorkerWrapper.isWorker();
    }

    public void forceTerminateMainEventLoop(){
        this.serverInstance.processQueue.add(()->{throw new TerminateException();});
    }

    public boolean isServerRunning(){
        return this.serverInstance.isServerRunning();
    }

    public List<Executor> getRegistedExecutors(){
        return this.registedExecutors;
    }

    public List<TickTask<?>> getWorldTickTasks(){
        return this.worldTickTasks;
    }

    public void addExecutor(Executor executor){
        this.registedExecutors.add(executor);
    }

    public void addTickTask(TickTask<?> task){
        this.worldTickTasks.add(task);
    }

    public List<WorldServer> getWorlds(){
        return this.serverInstance.worldServerList;
    }
}
