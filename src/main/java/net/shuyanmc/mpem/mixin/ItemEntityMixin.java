package net.shuyanmc.mpem.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private int lastMergeTick = -1;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        if (self.getWorld().isClient) return;

        // 更新物品堆叠数量显示
        updateStackDisplay(self);

        long gameTime = self.getWorld().getTime();
        if (lastMergeTick == -1 || gameTime - lastMergeTick >= 5) {
            lastMergeTick = (int)gameTime;
            tryMergeItems(self);
        }
    }

    @Unique
    private void tryMergeItems(ItemEntity self) {
        double mergeDistance = CoolConfig.mergeDistance.get();
        int configMaxStack = CoolConfig.maxStackSize.get();
        int listMode = CoolConfig.listMode.get();
        List<? extends String> itemList = CoolConfig.itemList.get();

        if (!isMergeAllowed(self.getStack(), listMode, itemList)) return;

        ItemStack stack = self.getStack();
        // 计算最大堆叠数 - 配置为0则无限制，否则使用配置值
        int maxStack = configMaxStack > 0 ? configMaxStack : Integer.MAX_VALUE - 100; // 防止整数溢出

        // 如果当前堆叠数已经达到或超过最大值，则不进行合并
        if (stack.getCount() >= maxStack) return;

        List<ItemEntity> nearby = self.getWorld().getEntitiesByClass(
                ItemEntity.class,
                self.getBoundingBox().expand(mergeDistance),
                e -> isValidMergeTarget(self, e, listMode, itemList)
        );

        nearby.sort(Comparator.comparingDouble(self::squaredDistanceTo));
        int remainingSpace = maxStack - stack.getCount();

        for (ItemEntity other : nearby) {
            if (remainingSpace <= 0) break;

            ItemStack otherStack = other.getStack();
            int transfer = Math.min(otherStack.getCount(), remainingSpace);

            stack.increment(transfer);
            self.setStack(stack);
            self.resetPickupDelay();

            if (otherStack.getCount() == transfer) {
                other.discard();
            } else {
                otherStack.decrement(transfer);
                other.setStack(otherStack);
                ((ItemEntityMixin)(Object)other).updateStackDisplay(other);
            }

            remainingSpace -= transfer;
        }
    }

    @Unique
    private void updateStackDisplay(ItemEntity entity) {
        if (!CoolConfig.showStackCount.get()) {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
            return;
        }

        ItemStack stack = entity.getStack();
        if (stack.getCount() > 1) {
            // 使用深绿色文本显示堆叠数量
            Text countText = Text.literal("×" + stack.getCount())
                    .formatted(Formatting.DARK_GREEN)
                    .formatted(Formatting.BOLD);

            entity.setCustomName(countText);
            // 确保名称始终可见
            entity.setCustomNameVisible(true);
        } else {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
        }
    }

    @Unique
    private boolean isValidMergeTarget(ItemEntity self, ItemEntity other, int listMode, List<? extends String> itemList) {
        return self != other &&
                !other.isRemoved() &&
                isSameItem(self.getStack(), other.getStack()) &&
                isMergeAllowed(other.getStack(), listMode, itemList);
    }

    @Unique
    private boolean isSameItem(ItemStack a, ItemStack b) {
        return ItemStack.canCombine(a, b);
    }

    @Unique
    private boolean isMergeAllowed(ItemStack stack, int listMode, List<? extends String> itemList) {
        if (listMode == 0) return true;

        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id == null) return false;

        boolean inList = itemList.contains(id.toString());
        return listMode == 1 ? inList : !inList;
    }
}