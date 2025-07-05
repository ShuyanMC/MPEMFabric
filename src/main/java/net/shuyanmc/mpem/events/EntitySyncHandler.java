package net.shuyanmc.mpem.events;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shuyanmc.mpem.config.CoolConfig;

public class EntitySyncHandler {

    public void onEntityJoinLevel(Entity entity) {
        if (!CoolConfig.reduceEntityUpdates.get()) return;
        
        if (entity instanceof ServerPlayerEntity player) {
            if (player.squaredDistanceTo(entity) > 4096.0) {
                entity.discard();
            }
        }
    }
}