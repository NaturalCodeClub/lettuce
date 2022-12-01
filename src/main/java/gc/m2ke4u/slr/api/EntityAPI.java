package gc.m2ke4u.slr.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class EntityAPI {
    private static final MinecraftServer SERVER = MinecraftServer.getServerInst();

    public static Entity getEntity(int entityId){
        for (WorldServer worldServer : SERVER.worlds){
            for (Entity entity : worldServer.loadedEntityList){
                if (entity.getEntityId() == entityId){
                    return entity;
                }
            }
        }
        return null;
    }

    public static EntityPlayerMP getPlayer(String name){
        return SERVER.getPlayerList().getPlayerByUsername(name);
    }

    public static void sendPacketToPlayer(Packet packet,String name){
        getPlayer(name).connection.sendPacket(packet);
    }

    public static void sendPacketToPlayer(Packet packet,String name,Runnable whenSended){
        getPlayer(name).connection.getNetworkManager().sendPacket(packet, future -> {
            try {
                whenSended.run();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public static void forceKillPlayer(String name){
        EntityPlayerMP playerMP = getPlayer(name);
        playerMP.unkillable = false;
        playerMP.setHealth(0);
        playerMP.setDead();
        playerMP.isDead = true;
        playerMP.world.loadedEntityList.remove(playerMP);
        playerMP.world.playerEntities.remove(playerMP);
    }

    public static void forceKillEntity(){}
}
