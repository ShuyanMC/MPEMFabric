package net.shuyanmc.mpem.optimization;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.shuyanmc.mpem.config.CoolConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class EntityOptimizer {
    private static final Map<Entity, Long> inactiveEntities = new WeakHashMap<>();

    public void onEntityJoin(Entity entity) {
        if (!CoolConfig.disableEntityCollisions.get()) return;

        
        // 禁用新生成实体的碰撞
        entity.setNoGravity(false);
        entity.noClip = false;
        inactiveEntities.put(entity, System.currentTimeMillis());
    }

    public void onEntityLeave(Entity entity) {
        inactiveEntities.remove(entity);
    }
    
    public static void processInactiveEntities() {
        if (!CoolConfig.disableEntityCollisions.get()) return;
        
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Entity, Long>> iterator = inactiveEntities.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<Entity, Long> entry = iterator.next();
            Entity entity = entry.getKey();
            
            // 检查实体是否仍然有效
            if (!entity.isAlive()) {
                iterator.remove();
                continue;
            }
            
            // 10秒无活动则冻结
            if (now - entry.getValue() > 10000) {
                entity.setVelocity(Vec3d.ZERO);
                entity.setPos(entity.getX(), entity.getY(), entity.getZ());
            }
        }
    }
}