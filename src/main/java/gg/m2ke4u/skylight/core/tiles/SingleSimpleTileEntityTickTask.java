package gg.m2ke4u.skylight.core.tiles;

import gg.m2ke4u.skylight.core.TickTask;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class SingleSimpleTileEntityTickTask implements TickTask<TileEntity> {
    private volatile boolean finished = false;

    @Override
    public void call(TileEntity input) {
        try {
            final World world = input.world;
            if (!input.isInvalid()) {
                if (!world.loadedTileEntityList.contains(input)) {world.addTileEntity(input);}
                if (world.isBlockLoaded(input.getPos())) {
                    Chunk chunk = world.getChunkFromBlockCoords(input.getPos());
                    IBlockState iblockstate = chunk.getBlockState(input.getPos());
                    chunk.addTileEntity(input.getPos(), input);
                    world.notifyBlockUpdate(input.getPos(), iblockstate, iblockstate, 3);
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
