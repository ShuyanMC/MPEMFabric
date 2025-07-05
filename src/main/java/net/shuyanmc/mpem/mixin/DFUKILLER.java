package net.shuyanmc.mpem.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.datafixer.Schemas;
import net.shuyanmc.mpem.LazyDataFixerBuilder;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = Schemas.class)
public class DFUKILLER {
    /**
     * @return LazyDataFixerBuilder
     * @author KSmc_brigade
     * @reason to fabric version
     */
    @Redirect(method = "create",at = @At(value = "NEW", target = "(I)Lcom/mojang/datafixers/DataFixerBuilder;"))
    private static DataFixerBuilder create(int dataVersion){
        return new LazyDataFixerBuilder(dataVersion);
    }
}