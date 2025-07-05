package net.shuyanmc.mpem.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public abstract class MixinBlockUpdatePacket {
    @Shadow private BlockPos pos;
    private static BlockPos lastSentPos;

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    private void onWrite(PacketByteBuf buf, CallbackInfo ci) {
        if (CoolConfig.filterRedundantBlockUpdates.get()) {
            // 过滤相同位置的重复方块更新
            if (pos.equals(lastSentPos)) {
                ci.cancel();
            }
            lastSentPos = pos;
        }
    }
}