package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.gen.GenerationStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow public abstract ItemStack getStack();
    @Shadow public abstract int getMaxItemCount(ItemStack stack);
    @Shadow public abstract void setStack(ItemStack stack);
    @Shadow public abstract boolean canInsert(ItemStack stack);

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
        boolean slotIsClump = ClumpItem.isClump(itemStack.getItem());
        boolean insertIsClump = ClumpItem.isClump(stack.getItem());
        Item clumpInCommon;
        int splitAmount = Math.min(Math.min(count, stack.getCount()), this.getMaxItemCount(stack) - itemStack.getCount());
        if (itemStack.isEmpty()) {
            if (insertIsClump) {
                splitAmount = Math.max(splitAmount, Math.min(ClumpItem.getTopStack(stack).getCount(), this.getMaxItemCount(stack) - itemStack.getCount()));
                this.setStack(stack.split(splitAmount));
                stack = ClumpItem.reduceClump(stack);
            } else this.setStack(stack.split(splitAmount));
        } else if (ItemStack.canCombine(itemStack, stack) && !slotIsClump && !insertIsClump) {
            stack.decrement(splitAmount);
            itemStack.increment(splitAmount);
            this.setStack(itemStack);
        } else if ((clumpInCommon = ClumpItem.getCommonClumpType(itemStack, stack)) != null) {
            if (insertIsClump) splitAmount = Math.max(splitAmount, Math.min(ClumpItem.getTopStack(stack).getCount(), this.getMaxItemCount(stack) - itemStack.getCount()));
            ItemStack splitStack = stack.split(splitAmount);
            ItemStack tempStack = itemStack.copy();
            itemStack = new ItemStack(clumpInCommon, tempStack.getCount() + splitStack.getCount());
            ClumpItem.addToClump(itemStack, tempStack);
            ClumpItem.addToClump(itemStack, splitStack);
            this.setStack(ClumpItem.reduceClump(itemStack));
            if (insertIsClump) stack = ClumpItem.reduceClump(stack);
        }
        return stack;
    }
}
