package gg.m2ke4u.skylight.core.tiles;

import gg.m2ke4u.skylight.core.TickTask;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class SingleTickableTileEntityTickTask implements TickTask<TileEntity> {
    private volatile boolean finished = false;

    @Override
    public void call(TileEntity input) {
        try {
            final World world = input.world;
            if (input == null || !world.tileLimiter.shouldContinue()) {
                return;
            }
            if (!input.isInvalid() && input.hasWorld())
            {
                BlockPos blockpos = input.getPos();
                if (world.isBlockLoaded(blockpos, false) && world.getWorldBorder().contains(blockpos)) //Forge: Fix TE's getting an extra tick on the client side....
                {
                    ((ITickable)input).update();
                }
            }
            if (input.isInvalid())
            {
                world.tickableTileEntities.remove(input);
                world.loadedTileEntityList.remove(input);
                if (world.isBlockLoaded(input.getPos()))
                {
                    Chunk chunk = world.getChunkFromBlockCoords(input.getPos());
                    if (chunk.getTileEntity(input.getPos(), net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK) == input)
                        chunk.removeTileEntity(input.getPos());
                }
            }
        }finally {
            this.finished = true;
        }
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
    public void forceTerminate() {}
}
