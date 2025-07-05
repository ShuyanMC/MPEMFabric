package net.shuyanmc.mpem.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashOverlay.class)
public class ResourceOverlayMixin {

    @Shadow @Final private boolean reloading;

    @Inject(method = "pausesGame",at = @At("RETURN"),cancellable = true)
    public void no_pause(CallbackInfoReturnable<Boolean> cir){
        if(this.reloading && CoolConfig.reloadResourcesAsync.get()){
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V"))
    public void renderTex(DrawContext instance, Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight){
        if(!this.reloading || !CoolConfig.reloadResourcesAsync.get()){
            instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
        }
    }

    @Redirect(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"))
    public void fill(DrawContext instance, RenderLayer layer, int x1, int y1, int x2, int y2, int color){
        if(!this.reloading || !CoolConfig.reloadResourcesAsync.get()){
            instance.fill(layer,x1,y1,x2,y2,color);
        }
    }
}
