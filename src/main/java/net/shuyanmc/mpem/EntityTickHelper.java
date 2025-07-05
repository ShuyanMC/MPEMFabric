package net.shuyanmc.mpem;

import com.google.common.base.Suppliers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.shuyanmc.mpem.api.IOptimizableEntity;
import net.shuyanmc.mpem.config.CoolConfig;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class EntityTickHelper {
    // 缓存物品白名单配置
    private static final Supplier<Set<Item>> ITEM_WHITELIST_CACHE = Suppliers.memoize(() ->
            CoolConfig.itemWhitelist.get().stream()
                    .map(s -> Registries.ITEM.get(new Identifier(s)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet())
    );

    /**
     * 判断实体是否需要取消Tick
     */
    public static boolean shouldCancelTick(Entity entity) {
        // 基础检查
        if (entity == null || entity.getWorld() == null || !CoolConfig.optimizeEntities.get()) {
            return false;
        }

        // 白名单实体直接放行
        if (isAlwaysTicking(entity)) {
            return false;
        }

        // 死亡实体特殊处理
        if (isDeadEntity(entity)) {
            return false;
        }

        // 物品实体特殊逻辑
        if (entity instanceof ItemEntity itemEntity) {
            return shouldOptimizeItemEntity(itemEntity);
        }

        BlockPos pos = entity.getBlockPos();
        World level = entity.getWorld();

        // 袭击事件特殊处理
        if (shouldBypassForRaid(entity, level, pos)) {
            return false;
        }

        // 最终玩家距离检查
        return !isPlayerNearby(level, pos);
    }

    // ========== 私有工具方法 ==========

    private static boolean isAlwaysTicking(Entity entity) {
        return ((IOptimizableEntity) entity.getType()).shouldAlwaysTick();
    }

    private static boolean isDeadEntity(Entity entity) {
        return !CoolConfig.ignoreDeadEntities.get() &&
                entity instanceof LivingEntity living &&
                living.isDead();
    }

    private static boolean shouldOptimizeItemEntity(ItemEntity itemEntity) {
        return CoolConfig.optimizeItems.get() &&
                !ITEM_WHITELIST_CACHE.get().contains(itemEntity.getStack().getItem());
    }

    private static boolean shouldBypassForRaid(Entity entity, World level, BlockPos pos) {
        return CoolConfig.tickRaidersInRaid.get() &&
                level instanceof ServerWorld serverLevel &&
                serverLevel.hasRaidAt(pos) &&
                (entity instanceof RaiderEntity ||
                        ((IOptimizableEntity) entity.getType()).shouldTickInRaid());
    }

    private static boolean isPlayerNearby(World level, BlockPos pos) {
        int horizontalRange = CoolConfig.horizontalRange.get();
        int verticalRange = CoolConfig.verticalRange.get();
        double rangeSq = horizontalRange * horizontalRange;

        Box checkArea = new Box(
                pos.getX() - horizontalRange,
                pos.getY() - verticalRange,
                pos.getZ() - horizontalRange,
                pos.getX() + horizontalRange,
                pos.getY() + verticalRange,
                pos.getZ() + horizontalRange
        );

        return !level.getEntitiesByClass(
                PlayerEntity.class,
                checkArea,
                player -> player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < rangeSq
        ).isEmpty();
    }

    public static boolean shouldCancelTick(World level) {
        return true;
    }
}