package net.shuyanmc.mpem.mixin;
import net.minecraft.server.world.ServerWorld;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(ServerWorld.class)
public abstract class MixinServerLevel {
    @ModifyVariable(method = "tickChunk", at = @At("HEAD"), argsOnly = true)
    private int modifyChunkLoadRate(int chunks) {
        return Math.min(chunks, CoolConfig.chunkUnloadDelay.get());
    }
}