package net.shuyanmc.mpem.mixin;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @Redirect(method = "writeItemStack",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;"))
    public ByteBuf write(PacketByteBuf instance, int value){
        return instance.writeInt(value);
    }

    @Redirect(method = "readItemStack",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readByte()B"))
    public byte readByte(PacketByteBuf instance){
        return 0;
    }

    @ModifyVariable(method = "readItemStack", at = @At("STORE"), ordinal = 0)
    private int read(int value)
    {
        return ((PacketByteBuf) (Object) this).readInt();
    }
}
