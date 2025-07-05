package net.shuyanmc.mpem.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoolConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // ==================== 实体优化 | Entity Optimization ====================
    public static ForgeConfigSpec.BooleanValue disableEntityCollisions;
    public static ForgeConfigSpec.BooleanValue optimizeEntityAI;
    public static ForgeConfigSpec.IntValue entityActivationRange;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_ENTITY_CLEANUP;
    public static final ForgeConfigSpec.BooleanValue reduceEntityUpdates;

    // ==================== 物品优化 | Item Optimization ====================
    public static ForgeConfigSpec.IntValue maxStackSize;
    public static ForgeConfigSpec.DoubleValue mergeDistance;
    public static ForgeConfigSpec.IntValue listMode;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemList;
    public static ForgeConfigSpec.BooleanValue showStackCount;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue MAX_STACK_SIZE;
    //public static ForgeConfigSpec.BooleanValue MAX_INV_STACK_SIZE_SUPPORT;

    // ==================== 内存优化 | Memory Optimization ====================
    public static final ForgeConfigSpec.IntValue MEMORY_CLEAN_INTERVAL;
    public static final ForgeConfigSpec.BooleanValue ENABLE_GC;

    // ==================== 调试选项 | Debug Options ====================
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;
    public static final ForgeConfigSpec.BooleanValue LOG_BLOCK_EVENTS;

    // ==================== 区块优化 | Chunk Optimization ====================
    public static ForgeConfigSpec.BooleanValue aggressiveChunkUnloading;
    public static ForgeConfigSpec.IntValue chunkUnloadDelay;
    public static final ForgeConfigSpec.BooleanValue reduceChunkUpdates;
    public static final ForgeConfigSpec.BooleanValue filterRedundantBlockUpdates;
    public static final ForgeConfigSpec.IntValue CHUNK_GEN_THREADS;

    // ==================== 异步优化 | Async Optimization ====================
    public static final ForgeConfigSpec.BooleanValue ASYNC_PARTICLES;
    public static final ForgeConfigSpec.IntValue MAX_ASYNC_OPERATIONS_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue DISABLE_ASYNC_ON_ERROR;
    public static final ForgeConfigSpec.IntValue ASYNC_EVENT_TIMEOUT;
    public static final ForgeConfigSpec.BooleanValue WAIT_FOR_ASYNC_EVENTS;
    public static ForgeConfigSpec.IntValue maxCPUPro;
    public static ForgeConfigSpec.IntValue maxthreads;

    // ==================== 事件系统 | Event System ====================
    public static ForgeConfigSpec.BooleanValue FEATURE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ASYNC_EVENT_CLASS_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ASYNC_EVENT_MOD_BLACKLIST;
    public static final ForgeConfigSpec.BooleanValue STRICT_CLASS_CHECKING;

    // 渐进加载配置
    public static final ForgeConfigSpec.IntValue ENTITY_LOAD_BATCH_SIZE;
    public static final ForgeConfigSpec.IntValue TILE_ENTITY_LOAD_BATCH_SIZE;
    public static final ForgeConfigSpec.IntValue MIN_TICK_INTERVAL;
    public static final ForgeConfigSpec.IntValue MAX_TICK_INTERVAL;
    public static final ForgeConfigSpec.DoubleValue TPS_ADJUST_THRESHOLD;
    // 实体优化配置
    public static ForgeConfigSpec.BooleanValue optimizeEntities;
    public static ForgeConfigSpec.IntValue horizontalRange;
    public static ForgeConfigSpec.IntValue verticalRange;
    public static ForgeConfigSpec.BooleanValue ignoreDeadEntities;

    // 物品实体配置
    public static ForgeConfigSpec.BooleanValue optimizeItems;

    // 袭击事件配置
    public static ForgeConfigSpec.BooleanValue tickRaidersInRaid;

    // 维度白名单
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionWhitelist;
 //itemtick

    public static ForgeConfigSpec.BooleanValue sendLoginMessage;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> entityWhitelist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> entityModWhitelist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> raidEntityWhitelist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemWhitelist;

    public static ForgeConfigSpec.BooleanValue reloadResourcesAsync;
    static {
        // 实体设置
        BUILDER.push("实体优化");
        optimizeEntities = BUILDER
                .comment("启用实体tick优化")
                .define("enable", true);
        horizontalRange = BUILDER
                .comment("水平检测范围(方块)")
                .defineInRange("horizontalRange", 64, 1, 256);
        verticalRange = BUILDER
                .comment("垂直检测范围(方块)")
                .defineInRange("verticalRange", 32, 1, 256);
        ignoreDeadEntities = BUILDER
                .comment("忽略已死亡的实体")
                .define("ignoreDead", false);
        BUILDER.pop();

        // 物品设置
        BUILDER.push("物品实体");
        optimizeItems = BUILDER
                .comment("优化物品实体tick")
                .define("enable", false);
        BUILDER.pop();

        // 袭击设置
        BUILDER.push("袭击事件");
        tickRaidersInRaid = BUILDER
                .comment("在袭击中保持袭击者tick")
                .define("enable", true);
        BUILDER.pop();


        // 物品设置
        BUILDER.push("Entities");
        optimizeEntities = BUILDER.define("optimizeEntities", true);
        horizontalRange = BUILDER.defineInRange("horizontalRange", 64, 1, 256);
        verticalRange = BUILDER.defineInRange("verticalRange", 32, 1, 256);
        ignoreDeadEntities = BUILDER.define("ignoreDeadEntities", false);
        entityWhitelist = BUILDER.defineList("entityWhitelist", List.of("minecraft:ender_dragon"), o -> true);
        entityModWhitelist =BUILDER.defineList("entityModWhitelist", List.of("create"), o -> true);
        tickRaidersInRaid = BUILDER.define("tickRaidersInRaid", true);
        raidEntityWhitelist = BUILDER.defineList("raidEntityWhitelist", List.of("minecraft:witch"), o -> true);
        BUILDER.pop();

        BUILDER.push("Items");
        optimizeItems = BUILDER.define("optimizeItems", false);
        itemWhitelist = BUILDER.defineList("itemWhitelist", List.of("minecraft:diamond"), o -> true);
        BUILDER.pop();



        BUILDER.push("Messages");
        sendLoginMessage = BUILDER.define("sendLoginMessage", true);
        BUILDER.pop();

        BUILDER.push("Progressive Entity Loading Settings");

        ENTITY_LOAD_BATCH_SIZE = BUILDER
                .comment("Number of entities to load per tick")
                .defineInRange("entityLoadBatchSize", 10, 1, 100);

        TILE_ENTITY_LOAD_BATCH_SIZE = BUILDER
                .comment("Number of tile entities to load per tick")
                .defineInRange("tileEntityLoadBatchSize", 5, 1, 50);

        MIN_TICK_INTERVAL = BUILDER
                .comment("Minimum tick interval (ms) between entity updates")
                .defineInRange("minTickInterval", 10, 1, 100);

        MAX_TICK_INTERVAL = BUILDER
                .comment("Maximum tick interval (ms) between entity updates")
                .defineInRange("maxTickInterval", 100, 10, 1000);

        TPS_ADJUST_THRESHOLD = BUILDER
                .comment("TPS threshold for adjusting tick rate (percentage of 20)")
                .defineInRange("tpsAdjustThreshold", 0.85, 0.1, 1.0);

        BUILDER.pop();
        // ==================== 实体优化设置 | Entity Optimization Settings ====================
        BUILDER.comment("实体优化设置 | Entity Optimization Settings").push("entity_optimization");

        disableEntityCollisions = BUILDER
                .comment("优化实体碰撞检测 | Optimize entity collision detection")
                .define("disableEntityCollisions", true);

        optimizeEntityAI = BUILDER
                .comment("优化实体AI计算 | Optimize entity AI calculations")
                .define("optimizeEntityAI", true);

        entityActivationRange = BUILDER
                .comment("实体激活范围 (方块) | Entity activation range (blocks)")
                .defineInRange("entityActivationRange", 32, 16, 128);

        OPTIMIZE_ENTITY_CLEANUP = BUILDER
                .comment("启用死亡实体清理 | Enable dead entity cleanup")
                .define("entityCleanup", true);

        reduceEntityUpdates = BUILDER
                .comment("减少远处实体的同步频率 | Reduce entity sync frequency for distant entities")
                .define("reduceEntityUpdates", true);

        BUILDER.pop();

        // ==================== 物品优化设置 | Item Optimization Settings ====================
        BUILDER.comment("物品优化设置 | Item Optimization Settings").push("item_optimization");

        maxStackSize = BUILDER
                .comment("合并物品的最大堆叠数量（0表示无限制,-1 表 原版逻辑）| Maximum stack size for merged items (0 = no limit)")
                .defineInRange("maxStackSize", -1, -1, Integer.MAX_VALUE);

        mergeDistance = BUILDER
                .comment("物品合并检测半径（单位：方块）| Item merge detection radius in blocks")
                .defineInRange("mergeDistance", 0.5, 0.1, 10.0);

        listMode = BUILDER
                .comment("0: 禁用 1: 白名单模式 2: 黑名单模式 | 0: Disabled, 1: Whitelist, 2: Blacklist")
                .defineInRange("listMode", 0, 0, 2);

        itemList = BUILDER
                .comment("白名单/黑名单中的物品注册名列表 | Item registry names for whitelist/blacklist")
                .defineList("itemList", Collections.emptyList(), o -> o instanceof String);

        showStackCount = BUILDER
                .comment("是否在合并后的物品上显示堆叠数量 | Whether to show stack count on merged items")
                .define("showStackCount", true);

        BUILDER.push("stack_size");

        ENABLED = BUILDER
                .comment("启用自定义堆叠大小 | Enable custom stack sizes")
                .define("enabled", true);

        MAX_STACK_SIZE = BUILDER
                .comment("最大物品堆叠大小 (1-9999) | Maximum item stack size (1-9999)")
                .defineInRange("maxStackSize", 64, 1, 9999);

        BUILDER.pop();
        BUILDER.pop();

        // ==================== 内存优化设置 | Memory Optimization Settings ====================
        BUILDER.comment("内存优化设置 | Memory Optimization Settings").push("memory_optimization");

        MEMORY_CLEAN_INTERVAL = BUILDER
                .comment("内存清理间隔(秒) | Memory cleanup interval (seconds)")
                .defineInRange("cleanInterval", 600, 60, 3600);

        ENABLE_GC = BUILDER
                .comment("是否在清理时触发垃圾回收 | Whether to trigger garbage collection during cleanup")
                .define("enableGC", false);

        BUILDER.pop();

        // ==================== 调试设置 | Debug Settings ====================
        BUILDER.comment("调试设置 | Debug Settings").push("debug");

        DEBUG_LOGGING = BUILDER
                .comment("启用调试日志 | Enable debug logging")
                .define("debug", false);

        LOG_BLOCK_EVENTS = BUILDER
                .comment("记录方块相关事件 | Log block related events")
                .define("logBlockEvents", false);

        BUILDER.pop();

        // ==================== 区块优化设置 | Chunk Optimization Settings ====================
        BUILDER.comment("区块优化设置 | Chunk Optimization Settings").push("chunk_optimization");

        aggressiveChunkUnloading = BUILDER
                .comment("主动卸载非活动区块 | Aggressively unload inactive chunks")
                .define("aggressiveChunkUnloading", true);

        chunkUnloadDelay = BUILDER
                .comment("区块卸载延迟 (秒) | Chunk unload delay (seconds)")
                .defineInRange("chunkUnloadDelay", 60, 10, 600);

        reduceChunkUpdates = BUILDER
                .comment("当玩家移动短距离时减少区块更新频率 | Reduce chunk update frequency when player moves short distances")
                .define("reduceChunkUpdates", true);

        filterRedundantBlockUpdates = BUILDER
                .comment("过滤冗余的方块更新数据包 | Filter redundant block update packets")
                .define("filterRedundantBlockUpdates", true);

        CHUNK_GEN_THREADS = BUILDER
                .comment("异步区块生成的线程数 | Number of threads for async chunk generation")
                .defineInRange("chunkGenThreads", 2, 1, 8);

        BUILDER.pop();

        // ==================== 异步优化设置 | Async Optimization Settings ====================
        BUILDER.comment("异步优化设置 | Async Optimization Settings").push("async_optimization");

        ASYNC_PARTICLES = BUILDER
                .comment("启用异步粒子处理 | Enable asynchronous particle processing")
                .define("asyncParticles", true);

        MAX_ASYNC_OPERATIONS_PER_TICK = BUILDER
                .comment("每tick处理的最大异步操作数 | Max async operations processed per tick")
                .defineInRange("maxAsyncOpsPerTick", 1000, 100, 10000);

        DISABLE_ASYNC_ON_ERROR = BUILDER
                .comment("出错后禁用该事件类型的异步处理 | Disable async for event type after errors")
                .define("disableAsyncOnError", true);

        ASYNC_EVENT_TIMEOUT = BUILDER
                .comment("异步事件超时时间(秒) | Timeout in seconds for async events")
                .defineInRange("asyncEventTimeout", 2, 1, 10);

        WAIT_FOR_ASYNC_EVENTS = BUILDER
                .comment("等待异步事件完成 | Wait for async events to complete")
                .define("waitForAsyncEvents", false);

        BUILDER.push("async_cpu_config");

        maxCPUPro = BUILDER
                .comment("异步系统最大CPU核心数 | Max CPU Cores for async system (only for async threads, not world async)")
                .defineInRange("maxCPUPro", 2, 2, 9999);

        maxthreads = BUILDER
                .comment("最大线程数 | Max Threads (only for general async threads, not dedicated async threads)")
                .defineInRange("maxthreads", 2, 2, 9999);

        BUILDER.pop();
        BUILDER.pop();

        // ==================== 事件系统设置 | Event System Settings ====================
        BUILDER.comment("事件系统设置 | Event System Settings").push("event_system");

        FEATURE_ENABLED = BUILDER
                .comment("启用高性能异步事件功能")
                .define("featureEnabled", true);
        ASYNC_EVENT_CLASS_BLACKLIST = BUILDER
                .comment("不应异步处理的事件类列表 | List of event classes that should NOT be processed asynchronously",
                        "支持通配符 (如 'net.minecraftforge.event.entity.living.*') | Supports wildcards (e.g. 'net.minecraftforge.event.entity.living.*')")
                .defineList("classBlacklist",
                        List.of(
                                "net.minecraftforge.event.TickEvent",
                                "net.minecraftforge.event.level.LevelTickEvent",
                                "net.minecraftforge.event.entity.living.*"
                        ),
                        o -> o instanceof String);

        ASYNC_EVENT_MOD_BLACKLIST = BUILDER
                .comment("不应异步处理的模组ID列表 | List of mod IDs whose events should NOT be processed asynchronously")
                .defineList("modBlacklist", Collections.emptyList(), o -> o instanceof String);

        STRICT_CLASS_CHECKING = BUILDER
                .comment("启用严格的类存在检查 (推荐为稳定性) | Enable strict class existence checking (recommended for stability)")
                .define("strictClassChecking", true);

        BUILDER.pop();

        reloadResourcesAsync = BUILDER.define("reload_resources_no_lag",false);


        SPEC = BUILDER.build();
    }

    // ==================== 工具方法 | Utility Methods ====================
    public static Set<String> getAsyncEventClassBlacklist() {
        return new HashSet<>(ASYNC_EVENT_CLASS_BLACKLIST.get());
    }

    // 检查开关状态的快捷方法
    public static boolean isEnabled() {
        return FEATURE_ENABLED.get();
    }

    public static Set<String> getAsyncEventModBlacklist() {
        return new HashSet<>(ASYNC_EVENT_MOD_BLACKLIST.get());
    }

    public static boolean isStrictClassCheckingEnabled() {
        return STRICT_CLASS_CHECKING.get();
    }


    /*public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "mpem.toml");
    }*/
}