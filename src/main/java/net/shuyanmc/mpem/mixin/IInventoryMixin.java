package net.shuyanmc.mpem.mixin;

import net.minecraft.inventory.Inventory;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Inventory.class)
public interface IInventoryMixin {
    /**
     * @author KSmc_brigade
     * @reason nothing
     */
    @Overwrite
    default int getMaxCountPerStack() {
        if(CoolConfig.ENABLED.get()){
            return CoolConfig.MAX_STACK_SIZE.get();
        }
        return 64;
    }
}
