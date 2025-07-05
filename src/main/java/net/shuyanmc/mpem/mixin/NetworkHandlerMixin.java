package net.shuyanmc.mpem.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public class NetworkHandlerMixin {
    @ModifyConstant(method = "onCreativeInventoryAction",constant = @Constant(intValue = 64))
    private int onAction(int constant){
        if(CoolConfig.ENABLED.get()){
            return CoolConfig.MAX_STACK_SIZE.get();
        }
        return 64;
    }
}
