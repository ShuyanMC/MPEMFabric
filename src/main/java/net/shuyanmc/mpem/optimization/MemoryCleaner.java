package net.shuyanmc.mpem.optimization;

import net.minecraft.client.MinecraftClient;
import net.shuyanmc.mpem.config.CoolConfig;

public class MemoryCleaner {
    private long lastCleanTime = 0;

    public void onServerTick() {

        
        long currentTime = System.currentTimeMillis();
        long interval = CoolConfig.MEMORY_CLEAN_INTERVAL.get() * 1000;
        
        if (currentTime - lastCleanTime > interval) {
            cleanupResources();
            lastCleanTime = currentTime;
        }
    }

    private void cleanupResources() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        // 清理实体缓存
        if (CoolConfig.OPTIMIZE_ENTITY_CLEANUP.get()) {
            //for (ServerWorld level : mc.world.) {
                // 修正后的实体清理方式
                mc.world.getEntities().forEach(entity -> {
                    if (!entity.isAlive() && entity.timeUntilRegen > 600) {
                        entity.discard();
                    }
                });
            //}
        }

        if (CoolConfig.MEMORY_CLEAN_INTERVAL.get() > lastCleanTime) {
            System.gc();
            cleanupResources();
            System.out.println("[MPEM] Memory cleaning completed!");
        }
    }}
