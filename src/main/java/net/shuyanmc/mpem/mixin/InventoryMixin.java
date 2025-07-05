package net.shuyanmc.mpem.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerInventory.class)
public abstract class InventoryMixin {

    @ModifyVariable(
        method = "addStack(Lnet/minecraft/item/ItemStack;)I",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack onAddStack(ItemStack stack) {
        int configMax = CoolConfig.maxStackSize.get();
        if(configMax<0) return stack;
        if (configMax > 0 && stack.getCount() > configMax) {
            stack.setCount(configMax);
        }
        return stack;
    }
}