package net.shuyanmc.mpem.util;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.*;

public class EntityCleaner {
    private static long lastCleanTime = 0;

    public void onServerTick(MinecraftServer server) {
            long gameTime = server.getOverworld().getTime();
            if (gameTime - lastCleanTime > 10000) {
                lastCleanTime = gameTime;
cleanMostFrequentMonsters(server);
            }
    }

    private void cleanMostFrequentMonsters(MinecraftServer server) {
        ServerWorld level = server.getOverworld();
        long gameTime = server.getTicks();
        Map<Class<? extends Monster>, List<Monster>> monsterMap = new HashMap<>();
        
// 为避免变量重名问题，将 lambda 表达式中的参数名改为 newLevel
server.getWorlds().forEach(newLevel -> {
java.util.stream.StreamSupport.stream(newLevel.getEntityLookup().iterate().spliterator(), false)
                .filter(e -> e instanceof Monster)
                .map(e -> (Monster)e)
                .forEach(monster -> {
                    monsterMap.computeIfAbsent(monster.getClass(), k -> new ArrayList<>()).add(monster);
                });
        });

        monsterMap.entrySet().stream()
            .max(Comparator.comparingInt(entry -> entry.getValue().size()))
            .ifPresent(entry -> {
                entry.getValue().stream()
                    .skip(50) // 保留前50个
                    .forEach(m -> ((Entity)m).discard());
            });
    }

    public <T> void onCommandRegister(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("killmobs")
.requires(source -> source.hasPermissionLevel(2) || (source.getPlayer() != null
// 检查玩家是否存在，避免空指针异常
&& source.getPlayer() != null && source.getPlayer().getWorld() != null) )
            .executes(context -> {
                int[] count = {0};
                /*context.getSource().getServer().getWorlds().forEach(level -> {
java.util.stream.StreamSupport.stream(level.getEntityLookup().spliterator(), false)
                        .filter(e -> e instanceof Monster)
                        .forEach(e -> {
                            ((Monster)e).discard();
                            count[0]++;
                        });*/
                context.getSource().getServer().getWorlds().forEach(level ->{
                    for (Entity t : level.getEntityLookup().iterate()){
                        if(t instanceof Monster && t instanceof LivingEntity livingEntity){
                            livingEntity.discard();
                            count[0]++;
                        }
                    }
                });
                context.getSource().sendFeedback(() -> Text.literal("已清除 " + count[0] + " 个怪物实体"), true);
                return count[0];
                }));
// 根据要求删除多余的右括号，此处不添加额外代码
    }
}