package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Mutable @Final @Shadow private Item item;
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract boolean canPlaceOn(Registry<Block> blockRegistry, CachedBlockPosition pos);
    @Shadow public abstract Item getItem();
    @Shadow private int count;
    @Shadow private @Nullable NbtCompound nbt;
    @Shadow public abstract int getCount();
    @Shadow public abstract ItemStack copyWithCount(int count);
    @Shadow public abstract void decrement(int amount);

    @Shadow public abstract ItemStack copy();

    @Shadow public abstract void setCount(int count);

    @Shadow public abstract void increment(int amount);

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
        if (playerEntity != null && !playerEntity.getAbilities().allowModifyWorld && !this.canPlaceOn(context.getWorld().getRegistryManager().get(RegistryKeys.BLOCK), cachedBlockPosition)) {
            return ActionResult.PASS;
        }
        Item item = this.getItemOrClump();
        ActionResult actionResult = item.useOnBlock(context);
        if (playerEntity != null && actionResult.shouldIncrementStat()) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
            if (ClumpItemUtil.isClump(this.getItem()) && !playerEntity.isCreative()) {
                this.setStack(ClumpItem.decrementClump((ItemStack) (Object) this, 1));
            }
        }
        return actionResult;
    }

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    public ItemStack split(int amount) {
        int i = Math.min(amount, this.getCount());
        if (ClumpItem.isClump(this.getItem())) {
            if (i == this.getCount()) {
                ItemStack itemStack = this.copy();
                this.setCount(0);
                return itemStack;
            }
            ItemStack topStack = ClumpItem.removeTopStack((ItemStack) (Object) this, -1);
            if (i >= topStack.getCount()) {
                this.setStack(ClumpItem.reduceClump((ItemStack) (Object) this));
                return ClumpItem.reduceClump(topStack);
            } else {
                ItemStack splitStack = topStack.split(i);
                this.increment(topStack.getCount());
                ClumpItem.addToClump((ItemStack) (Object) this, topStack);
                this.setStack(ClumpItem.reduceClump((ItemStack) (Object) this));
                return ClumpItem.reduceClump(splitStack);
            }
        }
        ItemStack itemStack = this.copyWithCount(i);
        this.decrement(i);
        return itemStack;
    }

    @Unique
    public void setStack(ItemStack stack) {
        this.item = stack.getItem();
        this.count = stack.getCount();
        this.nbt = stack.getNbt();
    }

    @Unique
    public Item getItemOrClump() {
       return this.isEmpty() ? Items.AIR : (ClumpItemUtil.isClump(this.item) ? ClumpItem.getTopStack((ItemStack)(Object)this).getItem() : this.item);
   }
}
