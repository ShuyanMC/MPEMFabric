package net.shuyanmc.mpem.optimization;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class EntityActivator {
    private static final Map<Entity, Boolean> activeEntities = new HashMap<>();

    public void onServerTick(MinecraftServer server) {
        
        // 每10秒清理一次缓存
        if (server.getTicks() % 200 == 0) {
            activeEntities.keySet().removeIf(e -> !e.isAlive());
        }
    }
    
    public static boolean isEntityActive(Entity entity) {
        if (!activeEntities.containsKey(entity)) {
            updateEntityActivity(entity);
        }
        return activeEntities.get(entity);
    }
    
    private static void updateEntityActivity(Entity entity) {
        boolean active = false;
        
        // 检查附近玩家
        for (PlayerEntity player : entity.getWorld().getPlayers()) {
            if (player.squaredDistanceTo(entity) < 1024) { // 32^2
                active = true;
                break;
            }
        }
        
        // 检查是否在玩家视野内
        if (!active) {
            active = entity.getWorld().getClosestPlayer(entity, 64) != null;
        }
        
        activeEntities.put(entity, active);
    }
}