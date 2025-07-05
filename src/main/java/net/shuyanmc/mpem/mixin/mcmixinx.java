package net.shuyanmc.mpem.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.slot.Slot;
import net.shuyanmc.mpem.config.CoolConfig;
import net.shuyanmc.mpem.flang;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class mcmixinx {
    @Shadow @Final private LanguageManager languageManager;

    @Shadow public abstract ResourceManager getResourceManager();

    @Shadow @Nullable public Screen currentScreen;

    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",at = @At("HEAD"),cancellable = true)
    public void reloadRes(CallbackInfoReturnable<CompletableFuture<Void>> cir){
        if(flang.langReload){
            this.languageManager.reload(this.getResourceManager());
            flang.langReload = false;
            cir.setReturnValue(null);
            cir.cancel();
        }
        else{
            CoolConfig.SPEC.afterReload();
        }
    }

    @Inject(method = "setOverlay",at = @At("HEAD"),cancellable = true)
    public void reloadRes(Overlay p_91151_, CallbackInfo ci){
        if(this.currentScreen instanceof LanguageOptionsScreen || this.currentScreen instanceof CraftingScreen) ci.cancel();
    }
}
