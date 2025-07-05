package net.shuyanmc.mpem.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class MixinBoat {

    /**
     * 注入到checkFallDamage方法头部，在计算摔落伤害前重置摔落距离
     *
     */
    @Inject(
        method = "fall",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onCheckFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        BoatEntity boat = (BoatEntity) (Object)this;
        // 重置船的摔落距离为0
        boat.fallDistance = 0.0F;
        
        // 可选：完全取消后续伤害计算（更彻底）
        ci.cancel();
    }
}