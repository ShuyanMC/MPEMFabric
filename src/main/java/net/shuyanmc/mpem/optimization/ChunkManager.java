package net.shuyanmc.mpem.optimization;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.shuyanmc.mpem.config.CoolConfig;

import java.util.HashMap;
import java.util.Map;

public class ChunkManager {
    private static final Map<ChunkPos, Long> chunkAccessTimes = new HashMap<>();

    public void onServerTick(MinecraftServer server) {
        if (!CoolConfig.aggressiveChunkUnloading.get()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long unloadThreshold = CoolConfig.chunkUnloadDelay.get() * 1000L;
        
        // 更新所有活跃区块
        for (ServerWorld level : server.getWorlds()) {
            level.getPlayers(player -> {
                ChunkPos pos = new ChunkPos(player.getChunkPos().x, player.getChunkPos().z);
                chunkAccessTimes.put(pos, currentTime);
                return false;
            });
        }
        
        // 卸载非活跃区块
        chunkAccessTimes.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > unloadThreshold) {
                for (ServerWorld level : server.getWorlds()) {
                    if (level.getChunkManager().isChunkLoaded(entry.getKey().x, entry.getKey().z)) {
                        // 使用正确的区块卸载方法
                        level.getChunkManager().tick(() -> true, true);
                    }
                }
                return true;
            }
            return false;
        });
    }
}