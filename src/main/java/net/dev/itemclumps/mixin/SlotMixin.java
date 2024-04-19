package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.item.ClumpItems;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow public abstract ItemStack getStack();
    @Shadow public abstract int getMaxItemCount(ItemStack stack);
    @Shadow public abstract void setStack(ItemStack stack);

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public boolean canInsert(ItemStack stack) {
        return true;
    }

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public ItemStack insertStack(ItemStack stack, int count) {
        if (stack.isEmpty() || !this.canInsert(stack)) {
            return stack;
        }
        ItemStack itemStack = this.getStack();
        Item clumpInCommon;
        int i = Math.min(Math.min(count, stack.getCount()), this.getMaxItemCount(stack) - itemStack.getCount());
        if (itemStack.isEmpty()) {
            this.setStack(stack.split(i));
        } else if (ItemStack.canCombine(itemStack, stack)) {
            if (ClumpItemUtil.isClump(itemStack.getItem())) {
                ClumpItem.addToClump(itemStack, stack.copy());
            }
            stack.decrement(i);
            itemStack.increment(i);
            this.setStack(itemStack);
        } else if ((clumpInCommon = ClumpItemUtil.getCommonClumpType(itemStack, stack)) != null) {
            ItemStack tempStack = itemStack.copy();
            itemStack = new ItemStack(clumpInCommon, itemStack.getCount() + i);
            ClumpItem.addToClump(itemStack, tempStack);
            ClumpItem.addToClump(itemStack, stack.copy());
            stack.decrement(i);
            this.setStack(itemStack);
        }
        return stack;
    }
}
