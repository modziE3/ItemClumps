package net.dev.itemclumps.mixin;

import net.dev.itemclumps.item.ClumpItem;
import net.dev.itemclumps.item.ClumpItems;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Final @Shadow private Item item;
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract boolean canPlaceOn(Registry<Block> blockRegistry, CachedBlockPosition pos);
    @Shadow public abstract Item getItem();

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
                ClumpItem.decrementClump((ItemStack) (Object) this, 1);
            }
        }
        return actionResult;
    }

    @Unique
    public Item getItemOrClump() {
       return this.isEmpty() ? Items.AIR : (ClumpItemUtil.isClump(this.item) ? ClumpItem.getTopClump((ItemStack)(Object)this).getItem() : this.item);
   }
}
