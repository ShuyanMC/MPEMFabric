package net.shuyanmc.mpem.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class ItemCountRenderer {
    //RenderNameTagEvent event
    public static void onNameTagRender(Entity entity,TextR teXTR) {
        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getStack().getCount() > 1 && itemEntity.hasCustomName()) {
                teXTR.text = itemEntity.getCustomName();
            }
        }
    }
}