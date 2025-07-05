package net.shuyanmc.mpem.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(
        method = "tryTakeStackRange",
        at = @At("HEAD")
    )
    private void onTryRemove(int amount, int decrement, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (player.getAbilities().invulnerable) {
            int configMax = CoolConfig.maxStackSize.get();
            if (configMax > 0) {
                Slot slot = (Slot)(Object)this;
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty() && stack.getCount() > configMax) {
                    stack.setCount(configMax);
                }
            }
        }
    }
}