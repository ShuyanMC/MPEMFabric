package net.shuyanmc.mpem.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketMixin {
    @Shadow @Final private Fluid fluid;

    @Inject(method = "getEmptiedStack",at = @At("HEAD"),cancellable = true)
    private static void get(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir){
        Fluid fluid1 = null;
        if(stack.getItem() instanceof BucketItem bucketItem) fluid1 = bucketItem.fluid;
        if(fluid1 != Fluids.EMPTY && stack.getCount()>=2){
            cir.setReturnValue(stack.copyWithCount(stack.getCount()-1));
            cir.cancel();
        }
    }

    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;success(Ljava/lang/Object;Z)Lnet/minecraft/util/TypedActionResult;",shift = At.Shift.BEFORE))
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        if(this.fluid==null || this.fluid == Fluids.EMPTY) return;
        ItemStack stack = user.getStackInHand(hand);
        if(!user.isCreative() && stack.getCount()>=2){
            user.giveItemStack(new ItemStack(Items.BUCKET));
        }
    }
}
