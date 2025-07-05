package net.shuyanmc.mpem.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.shuyanmc.mpem.client.ItemCountRenderer;
import net.shuyanmc.mpem.client.TextR;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @ModifyArgs(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void modifyText(Args args){
        TextR textR = new TextR(args.get(1));
        ItemCountRenderer.onNameTagRender(args.get(0),textR);
        args.set(1,textR.text);
    }
}
