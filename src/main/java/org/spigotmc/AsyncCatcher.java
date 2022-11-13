package org.spigotmc;

import gg.m2ke4u.skylight.WorkerWrapper;
import gg.m2ke4u.skylight.config.WorkerConfig;
import net.minecraft.server.MinecraftServer;

public class AsyncCatcher
{

    public static boolean enabled = true;

    public static void catchOp(String reason)
    {
        if (!WorkerConfig.ASYNC_CATCHER_DISABLED && enabled && Thread.currentThread() != MinecraftServer.getServerInst().primaryThread && !WorkerWrapper.isWorker())
        {
            throw new IllegalStateException( "Asynchronous " + reason + "!" );
        }
    }
}
