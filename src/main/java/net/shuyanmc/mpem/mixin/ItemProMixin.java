package net.shuyanmc.mpem.mixin;

import net.minecraft.item.Item;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemProMixin {

    @Shadow
    int maxCount;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        if(CoolConfig.SPEC.isLoaded() && CoolConfig.ENABLED.get()) this.maxCount = CoolConfig.MAX_STACK_SIZE.get();
    }

    @Inject(method = "maxCount",at = @At("RETURN"))
    public void stackTo(int p_41488_, CallbackInfoReturnable<Item.Settings> cir) {
        if(CoolConfig.SPEC.isLoaded() && CoolConfig.ENABLED.get()) this.maxCount = CoolConfig.MAX_STACK_SIZE.get();
    }
}
