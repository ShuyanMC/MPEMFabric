package net.shuyanmc.mpem.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow private int count;

    @Mutable
    @Shadow @Final public static Codec<ItemStack> CODEC;

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        int configMax = CoolConfig.maxStackSize.get();
        if(CoolConfig.ENABLED.get()){
            cir.setReturnValue(CoolConfig.MAX_STACK_SIZE.get());
            return;
        }
        if(configMax<0) return;
        if (configMax > 0) {
            int vanillaMax = this.getItem().getMaxCount();
            cir.setReturnValue(Math.min(configMax, vanillaMax));
        }
    }

    @Inject(method = "isStackable", at = @At("HEAD"), cancellable = true)
    private void onIsStackable(CallbackInfoReturnable<Boolean> cir) {
        int configMax = CoolConfig.maxStackSize.get();
        if (configMax == 0) {
            cir.setReturnValue(this.getItem().getMaxCount() > 1);
        }
    }

    @Redirect(method = "writeNbt",at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putByte(Ljava/lang/String;B)V"))
    private void write(NbtCompound instance, String key, byte value){
            instance.putByte(key,value);
            instance.putInt("countMod",this.count);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V",at = @At(value = "TAIL"))
    private void read(NbtCompound nbt, CallbackInfo ci){
        if(nbt.contains("countMod") && CoolConfig.ENABLED.get()){
            this.count = nbt.getInt("countMod");
        }
    }

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void clinit(CallbackInfo ci){
        CODEC = RecordCodecBuilder.create((instance) -> instance.group(Registries.ITEM.getCodec().fieldOf("id").forGetter(ItemStack::getItem), Codec.INT.fieldOf("Count").forGetter(ItemStack::getCount), NbtCompound.CODEC.optionalFieldOf("tag").forGetter((stack) -> Optional.ofNullable(stack.getNbt())),Codec.INT.optionalFieldOf("countMod").forGetter((o)-> Optional.of(o.getCount()))).apply(instance, (item, integer, nbtCompound, integer2) -> {
            int d = integer;
            if(integer2.isPresent()){
                d = integer2.get();
            }
            return new ItemStack(item,d);
        }));
    }
}