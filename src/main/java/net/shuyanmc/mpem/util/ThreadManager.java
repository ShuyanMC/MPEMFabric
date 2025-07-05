package net.shuyanmc.mpem.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
public class ThreadManager {
    private static final ExecutorService executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        new ThreadFactory() {
            private final ThreadFactory factory = Executors.defaultThreadFactory();
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = factory.newThread(r);
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        }
    );
    
    private static final ExecutorService gameLogicExecutor = Executors.newFixedThreadPool(
        Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
        new ThreadFactory() {
            private final ThreadFactory factory = Executors.defaultThreadFactory();
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = factory.newThread(r);
                thread.setDaemon(true);
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.setName("GameLogic-" + thread.getId());
                return thread;
            }
        }
    );
    
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(
        Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
        new ThreadFactory() {
            private final ThreadFactory factory = Executors.defaultThreadFactory();
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = factory.newThread(r);
                thread.setDaemon(true);
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.setName("Render-" + thread.getId());
                return thread;
            }
        }
    );
    
    private static final ExecutorService networkExecutor = Executors.newFixedThreadPool(
        4,
        new ThreadFactory() {
            private final ThreadFactory factory = Executors.defaultThreadFactory();
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = factory.newThread(r);
                thread.setDaemon(true);
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.setName("Network-" + thread.getId());
                return thread;
            }
        }
    );
    
    private static final ExecutorService computeExecutor = Executors.newWorkStealingPool();
    private static volatile boolean enabled = true;
    private static final long MEMORY_CHECK_INTERVAL = 30000; // 内存检查间隔30秒
    private static final long DISK_CACHE_CHECK_INTERVAL = 60000; // 磁盘缓存检查间隔60秒
    private static volatile long lastMemoryCheckTime = 0;
    private static volatile long lastDiskCacheCheckTime = 0;
    private static volatile long memoryThreshold = Runtime.getRuntime().maxMemory() * 70 / 100; // 70%内存阈值
    private static volatile boolean memoryOverloaded = false;
    
    /**
     * 获取当前内存是否过载状态
     * @return 内存是否过载
     */
    public static boolean isMemoryOverloaded() {
        return memoryOverloaded;
    }
    
    /**
     * 提交普通任务
     * @param task 要执行的任务
     */
    public static void submitTask(Runnable task) {
        if (enabled) {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handleThreadException(e);
                }
            });
        } else {
            task.run();
        }
    }
    
    /**
     * 提交游戏逻辑任务
     * @param task 游戏逻辑任务
     */
    public static void submitGameLogicTask(Runnable task) {
        if (enabled) {
            gameLogicExecutor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handleThreadException(e);
                }
            });
        } else {
            task.run();
        }
    }
    
    /**
     * 提交渲染任务
     * @param task 渲染任务
     */
    public static void submitRenderTask(Runnable task) {
        if (enabled) {
            renderExecutor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handleThreadException(e);
                }
            });
        } else {
            task.run();
        }
    }
    
    /**
     * 提交网络任务
     * @param task 网络任务
     */
    public static void submitNetworkTask(Runnable task) {
        if (enabled) {
            networkExecutor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handleThreadException(e);
                }
            });
        } else {
            task.run();
        }
    }
    
    /**
     * 提交计算密集型任务(世界生成、区块加载等)
     * @param task 计算密集型任务
     */
    public static void submitComputeTask(Runnable task) {
        if (enabled) {
            computeExecutor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    handleThreadException(e);
                }
            });
        } else {
            task.run();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static void submitClientTask(Runnable task) {
            submitTask(task);
    }
    @Environment(EnvType.SERVER)
    public static void submitServerTask(Runnable task) {
            submitTask(task);
    }
    
    public static void setEnabled(boolean enabled) {
        ThreadManager.enabled = enabled;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 检查并清理磁盘缓存
     */
    public static void checkAndCleanDiskCache() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDiskCacheCheckTime > DISK_CACHE_CHECK_INTERVAL) {
            lastDiskCacheCheckTime = currentTime;
            
            // 清理临时文件
            try {
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                File[] tempFiles = tempDir.listFiles();
                if (tempFiles != null) {
                    for (File file : tempFiles) {
                        if (file.getName().startsWith("mc-") && file.lastModified() < currentTime - 86400000) { // 清理超过1天的Minecraft临时文件
                            file.delete();
                        }
                    }
                }
// 由于 MpemMod.LOGGER 不可见，使用 System.out 输出信息
System.out.println("The expired disk cache has been cleared");
            } catch (Exception e) {
// 由于 MpemMod.LOGGER 不可见，假设使用 System.err 输出错误信息
System.err.println("Disk cache cleanup failed");
e.printStackTrace();
            }
        }
    }
    
    /**
     * 检查内存使用情况，如果超过阈值则触发清理
     */
    public static void checkMemoryUsage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMemoryCheckTime > MEMORY_CHECK_INTERVAL) {
            lastMemoryCheckTime = currentTime;
            
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            
            if (usedMemory > memoryThreshold) {
                memoryOverloaded = true;
                System.gc(); // 触发垃圾回收
// 由于 MpemMod.LOGGER 不可见，使用 System.out 输出警告信息
System.out.println("Warning: Memory usage exceeds the threshold, triggering GC cleanup");
            } else {
                memoryOverloaded = false;
            }
        }
    }
    
    /**
     * 设置内存使用阈值百分比
     * @param percent 阈值百分比(1-100)
     */
    public static void setMemoryThreshold(int percent) {
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("The memory threshold percentage must be between 1 and 100");
        }
        memoryThreshold = Runtime.getRuntime().maxMemory() * percent / 100;
    }
    
    /**
     * 获取当前内存使用情况
     * @return 内存使用信息字符串
     */
    public static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        
        return String.format("内存使用: %.2f/%.2fMB (%.1f%%)", 
            used / (1024.0 * 1024.0), 
            max / (1024.0 * 1024.0),
            (used * 100.0) / max);
    }
    
    /**
     * 处理线程任务执行过程中的异常
     * @param e 捕获到的异常
     */
    private static void handleThreadException(Exception e) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
// 由于 MpemMod.LOGGER 不可见，使用 System.err 输出错误信息
System.err.println("The client thread task execution is abnormal:");
e.printStackTrace();
        } else {
            System.err.println("Server thread task execution exception: " + e.getMessage());
        }
    }
}