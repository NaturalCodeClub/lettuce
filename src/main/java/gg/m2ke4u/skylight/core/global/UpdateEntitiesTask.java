package gg.m2ke4u.skylight.core.global;

import gg.m2ke4u.lutils.threading.traversing.CollectionConcurrentUtils;
import gg.m2ke4u.skylight.core.TickTask;
import gg.m2ke4u.skylight.core.entities.SingleEntityTickTask;
import gg.m2ke4u.skylight.core.profiler.SinglePartProfiler;
import gg.m2ke4u.skylight.core.tiles.SingleSimpleTileEntityTickTask;
import gg.m2ke4u.skylight.core.tiles.SingleTickableTileEntityTickTask;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class UpdateEntitiesTask implements TickTask<World> {
    private volatile boolean finished = false;
    private final Executor executor;
    private volatile boolean terminated = false;
    private final SinglePartProfiler profiler = new SinglePartProfiler("ConcurrentEntities");

    public UpdateEntitiesTask(Executor executor){
        this.executor = executor;
    }

    @Override
    public void call(World input) {
        try {
            this.profiler.postSection();;
            CollectionConcurrentUtils.traverseConcurrent(input.weatherEffects, entity->{
                if (entity == null || this.terminated) {
                    return;
                }
                try
                {
                    if(entity.updateBlocked) return;
                    ++entity.ticksExisted;
                    entity.onUpdate();
                }
                catch (Throwable throwable2)
                {throwable2.printStackTrace();}
                if (entity.isDead) {input.weatherEffects.remove(entity);}
            },this.executor);

            input.loadedEntityList.removeAll(input.unloadedEntityList);
            CollectionConcurrentUtils.traverseConcurrent(input.unloadedEntityList,entity1->{
                int j = entity1.chunkCoordX;
                int k1 = entity1.chunkCoordZ;
                if (entity1.addedToChunk && input.isChunkLoaded(j, k1, true)) {
                    input.getChunkFromChunkCoords(j, k1).removeEntity(entity1);
                }
                input.onEntityRemoved(entity1);
            },this.executor);
            input.unloadedEntityList.clear();

            org.spigotmc.ActivationRange.activateEntities(input); // Spigot
            input.entityLimiter.initTick();
            CollectionConcurrentUtils.traverseConcurrent(input.loadedEntityList,entity2->{
                if (this.terminated){
                    return;
                }
                final SingleEntityTickTask entityTickTask = new SingleEntityTickTask();
                entityTickTask.call(entity2);
            },this.executor);

            input.processingLoadedTiles = true; //FML Move above remove to prevent CMEs

            //Remove tile entities
            if (!input.tileEntitiesToBeRemoved.isEmpty())
            {
                for (TileEntity tile : input.tileEntitiesToBeRemoved) {tile.onChunkUnload();}
                java.util.Set<TileEntity> remove = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
                remove.addAll(input.tileEntitiesToBeRemoved);
                input.tickableTileEntities.removeAll(remove);
                input.loadedTileEntityList.removeAll(remove);
                input.tileEntitiesToBeRemoved.clear();
            }

            input.tileLimiter.initTick();
            CollectionConcurrentUtils.traverseConcurrent(input.tickableTileEntities,tileentity->{
                if (this.terminated){
                    return;
                }
                final SingleTickableTileEntityTickTask tickableTileEntityTickTask = new SingleTickableTileEntityTickTask();
                tickableTileEntityTickTask.call(tileentity);
            },this.executor);
            input.processingLoadedTiles = false;

            if (!input.addedTileEntityList.isEmpty())
            {
                CollectionConcurrentUtils.traverseConcurrent(input.addedTileEntityList,tileentity1->{
                    if (this.terminated){
                        return;
                    }
                    final SingleSimpleTileEntityTickTask task = new SingleSimpleTileEntityTickTask();
                    task.call(tileentity1);
                },this.executor);
                input.addedTileEntityList.clear();
            }
        }finally {
            this.finished = true;
            this.profiler.finishedTask();
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
        this.terminated = true;
        return this.terminated;
    }

    @Override
    public void forceTerminate() {
        this.terminated = true;
    }
}
