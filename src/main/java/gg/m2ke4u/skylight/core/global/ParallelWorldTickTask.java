package gg.m2ke4u.skylight.core.global;

import catserver.server.CatServer;
import gg.m2ke4u.lutils.threading.traversing.CollectionConcurrentUtils;
import gg.m2ke4u.skylight.core.TickTask;
import gg.m2ke4u.skylight.core.profiler.SinglePartProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.Arrays;
public class ParallelWorldTickTask implements TickTask<MinecraftServer> {
    private volatile boolean finished = false;
    private final SinglePartProfiler profiler = new SinglePartProfiler("worlds");

    @Override
    public void call(MinecraftServer input) {
        try{
            profiler.postSection();
            Integer[] ids = net.minecraftforge.common.DimensionManager.getIDs(input.getTickCounter() % 200 == 0);
            CollectionConcurrentUtils.traverseConcurrent(Arrays.stream(ids), id->{
                long i = System.nanoTime();
                WorldServer worldserver = net.minecraftforge.common.DimensionManager.getWorld(id);
                net.minecraft.tileentity.TileEntityHopper.skipHopperEvents = org.bukkit.event.inventory.InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length == 0 || CatServer.getConfig().disableHopperMoveEventWorlds.contains("*") || CatServer.getConfig().disableHopperMoveEventWorlds.contains(worldserver.getWorld().getName()); // CatServer
                net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreWorldTick(worldserver);
                try
                {
                    worldserver.tick();
                    worldserver.tickPlayers();
                    worldserver.updateEntities();
                }
                catch (Throwable throwable1)
                {
                    throwable1.printStackTrace();
                }
                net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostWorldTick(worldserver);
                worldserver.getEntityTracker().tick();
                worldserver.explosionDensityCache.clear();
                if (input.worldTickTimes.containsKey(id)) input.worldTickTimes.get(id)[input.tickCounter % 100] = System.nanoTime() - i; // CatServer - check world in tickTime list, prevent plugin unload world from causing NPE
            },MinecraftServer.GLOBAL_EXECUTOR);
        }finally {
            finished = true;
            profiler.finishedTask();
        }
    }

    public SinglePartProfiler getProfiler(){
        return this.profiler;
    }

    @Override
    public boolean finished() {
        return this.finished;
    }

    @Override
    public boolean terminate() {
        return false;
    }

    @Override
    public void forceTerminate() {

    }
}
