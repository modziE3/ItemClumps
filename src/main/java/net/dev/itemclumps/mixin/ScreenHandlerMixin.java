package net.dev.itemclumps.mixin;

import com.google.common.collect.Sets;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.Set;

import static net.minecraft.screen.ScreenHandler.EMPTY_SPACE_SLOT_INDEX;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    private final Set<Slot> quickCraftSlots = Sets.newHashSet();

    @Shadow private int quickCraftStage;
    @Shadow protected abstract void endQuickCraft();
    @Shadow public abstract ItemStack getCursorStack();
    @Shadow private int quickCraftButton;
    @Shadow @Final public DefaultedList<Slot> slots;
    @Shadow public abstract void setCursorStack(ItemStack stack);
    @Shadow public abstract ItemStack quickMove(PlayerEntity var1, int var2);
    @Shadow public abstract boolean canInsertIntoSlot(ItemStack stack, Slot slot);
    @Shadow public abstract boolean canInsertIntoSlot(Slot slot);
    @Shadow protected abstract boolean handleSlotClick(PlayerEntity player, ClickType clickType, Slot slot4, ItemStack itemStack4, ItemStack itemStack42);

    /**
     * @author Modzyyy
     * @reason ItemClumps
     */
    @Overwrite
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        block39: {
            block50: {
                block46: {
                    ItemStack itemStack;
                    Slot slot;
                    ItemStack itemStack5;
                    PlayerInventory playerInventory;
                    block49: {
                        block48: {
                            block47: {
                                block44: {
                                    ClickType clickType;
                                    block45: {
                                        block43: {
                                            block37: {
                                                block42: {
                                                    ItemStack itemStack2;
                                                    block41: {
                                                        block40: {
                                                            block38: {
                                                                playerInventory = player.getInventory();
                                                                if (actionType != SlotActionType.QUICK_CRAFT) break block37;
                                                                int i = this.quickCraftStage;
                                                                this.quickCraftStage = ScreenHandler.unpackQuickCraftStage(button);
                                                                if (i == 1 && this.quickCraftStage == 2 || i == this.quickCraftStage) break block38;
                                                                this.endQuickCraft();
                                                                break block39;
                                                            }
                                                            if (!this.getCursorStack().isEmpty()) break block40;
                                                            this.endQuickCraft();
                                                            break block39;
                                                        }
                                                        if (this.quickCraftStage != 0) break block41;
                                                        this.quickCraftButton = ScreenHandler.unpackQuickCraftButton(button);
                                                        if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftButton, player)) {
                                                            this.quickCraftStage = 1;
                                                            this.quickCraftSlots.clear();
                                                        } else {
                                                            this.endQuickCraft();
                                                        }
                                                        break block39;
                                                    }
                                                    if (this.quickCraftStage != 1) break block42;
                                                    Slot slot2 = this.slots.get(slotIndex);
                                                    if (!ScreenHandler.canInsertItemIntoSlot(slot2, itemStack2 = this.getCursorStack(), true) || !slot2.canInsert(itemStack2) || this.quickCraftButton != 2 && itemStack2.getCount() <= this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) break block39;
                                                    this.quickCraftSlots.add(slot2);
                                                    break block39;
                                                }
                                                if (this.quickCraftStage == 2) {
                                                    if (!this.quickCraftSlots.isEmpty()) {
                                                        if (this.quickCraftSlots.size() == 1) {
                                                            int j = this.quickCraftSlots.iterator().next().id;
                                                            this.endQuickCraft();
                                                            this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
                                                            return;
                                                        }
                                                        ItemStack itemStack2 = this.getCursorStack().copy();
                                                        if (itemStack2.isEmpty()) {
                                                            this.endQuickCraft();
                                                            return;
                                                        }
                                                        int k = this.getCursorStack().getCount();
                                                        for (Slot slot2 : this.quickCraftSlots) {
                                                            ItemStack itemStack3 = this.getCursorStack();
                                                            if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack3, true) || !slot2.canInsert(itemStack3) || this.quickCraftButton != 2 && itemStack3.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                                                            int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                                                            int m = Math.min(itemStack2.getMaxCount(), slot2.getMaxItemCount(itemStack2));
                                                            int n = Math.min(ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack2) + l, m);
                                                            k -= n - l;
                                                            slot2.setStack(itemStack2.copyWithCount(n));
                                                        }
                                                        itemStack2.setCount(k);
                                                        this.setCursorStack(itemStack2);
                                                    }
                                                    this.endQuickCraft();
                                                } else {
                                                    this.endQuickCraft();
                                                }
                                                break block39;
                                            }
                                            if (this.quickCraftStage == 0) break block43;
                                            this.endQuickCraft();
                                            break block39;
                                        }
                                        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE || button != 0 && button != 1) break block44;
                                        ClickType clickType2 = clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                                        if (slotIndex != EMPTY_SPACE_SLOT_INDEX) break block45;
                                        if (this.getCursorStack().isEmpty()) break block39;
                                        if (clickType == ClickType.LEFT) {
                                            player.dropItem(this.getCursorStack(), true);
                                            this.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            player.dropItem(this.getCursorStack().split(1), true);
                                        }
                                        break block39;
                                    }
                                    if (actionType == SlotActionType.QUICK_MOVE) {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot3 = this.slots.get(slotIndex);
                                        if (!slot3.canTakeItems(player)) {
                                            return;
                                        }
                                        ItemStack itemStack3 = this.quickMove(player, slotIndex);
                                        while (!itemStack3.isEmpty() && ItemStack.areItemsEqual(slot3.getStack(), itemStack3)) {
                                            itemStack3 = this.quickMove(player, slotIndex);
                                        }
                                    } else {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot4 = this.slots.get(slotIndex);
                                        ItemStack itemStack4 = slot4.getStack();
                                        ItemStack itemStack42 = this.getCursorStack();
                                        player.onPickupSlotClick(itemStack42, slot4.getStack(), clickType);
                                        if (!this.handleSlotClick(player, clickType, slot4, itemStack4, itemStack42)) {
                                            if (itemStack4.isEmpty()) {
                                                if (!itemStack42.isEmpty()) {
                                                    int o = clickType == ClickType.LEFT ? itemStack42.getCount() : 1;
                                                    this.setCursorStack(slot4.insertStack(itemStack42, o));
                                                }
                                            } else if (slot4.canTakeItems(player)) {
                                                if (itemStack42.isEmpty()) {
                                                    int o = clickType == ClickType.LEFT ? itemStack4.getCount() : (itemStack4.getCount() + 1) / 2;
                                                    Optional<ItemStack> optional = slot4.tryTakeStackRange(o, Integer.MAX_VALUE, player);
                                                    optional.ifPresent(stack -> {
                                                        this.setCursorStack((ItemStack)stack);
                                                        slot4.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                } else if (slot4.canInsert(itemStack42) || ClumpItemUtil.canClump(slot4.getStack(), itemStack42)) {
                                                    if (ItemStack.canCombine(itemStack4, itemStack42) || ClumpItemUtil.canClump(slot4.getStack(), itemStack42)) {
                                                        int o = clickType == ClickType.LEFT ? itemStack42.getCount() : 1;
                                                        this.setCursorStack(slot4.insertStack(itemStack42, o));
                                                    } else if (itemStack42.getCount() <= slot4.getMaxItemCount(itemStack42)) {
                                                        this.setCursorStack(itemStack4);
                                                        slot4.setStack(itemStack42);
                                                    }
                                                } else if (ItemStack.canCombine(itemStack4, itemStack42)) {
                                                    Optional<ItemStack> optional2 = slot4.tryTakeStackRange(itemStack4.getCount(), itemStack42.getMaxCount() - itemStack42.getCount(), player);
                                                    optional2.ifPresent(stack -> {
                                                        itemStack42.increment(stack.getCount());
                                                        slot4.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                }
                                            }
                                        }
                                        slot4.markDirty();
                                    }
                                    break block39;
                                }
                                if (actionType != SlotActionType.SWAP || (button < 0 || button >= 9) && button != 40) break block46;
                                itemStack5 = playerInventory.getStack(button);
                                slot = this.slots.get(slotIndex);
                                itemStack = slot.getStack();
                                if (itemStack5.isEmpty() && itemStack.isEmpty()) break block39;
                                if (!itemStack5.isEmpty()) break block47;
                                if (!slot.canTakeItems(player)) break block39;
                                playerInventory.setStack(button, itemStack);
                                slot.setStack(ItemStack.EMPTY);
                                slot.onTakeItem(player, itemStack);
                                break block39;
                            }
                            if (!itemStack.isEmpty()) break block48;
                            if (!slot.canInsert(itemStack5)) break block39;
                            int p = slot.getMaxItemCount(itemStack5);
                            if (itemStack5.getCount() > p) {
                                slot.setStack(itemStack5.split(p));
                            } else {
                                playerInventory.setStack(button, ItemStack.EMPTY);
                                slot.setStack(itemStack5);
                            }
                            break block39;
                        }
                        if (!slot.canTakeItems(player) || !slot.canInsert(itemStack5)) break block39;
                        int p = slot.getMaxItemCount(itemStack5);
                        if (itemStack5.getCount() <= p) break block49;
                        slot.setStack(itemStack5.split(p));
                        slot.onTakeItem(player, itemStack);
                        if (playerInventory.insertStack(itemStack)) break block39;
                        player.dropItem(itemStack, true);
                        break block39;
                    }
                    playerInventory.setStack(button, itemStack);
                    slot.setStack(itemStack5);
                    slot.onTakeItem(player, itemStack);
                    break block39;
                }
                if (actionType != SlotActionType.CLONE || !player.getAbilities().creativeMode || !this.getCursorStack().isEmpty() || slotIndex < 0) break block50;
                Slot slot3 = this.slots.get(slotIndex);
                if (!slot3.hasStack()) break block39;
                ItemStack itemStack2 = slot3.getStack();
                this.setCursorStack(itemStack2.copyWithCount(itemStack2.getMaxCount()));
                break block39;
            }
            if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                int j = button == 0 ? 1 : slot3.getStack().getCount();
                ItemStack itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
                player.dropItem(itemStack, true);
            } else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                ItemStack itemStack2 = this.getCursorStack();
                if (!(itemStack2.isEmpty() || slot3.hasStack() && slot3.canTakeItems(player))) {
                    int k = button == 0 ? 0 : this.slots.size() - 1;
                    int p = button == 0 ? 1 : -1;
                    for (int o = 0; o < 2; ++o) {
                        for (int q = k; q >= 0 && q < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxCount(); q += p) {
                            Slot slot4 = this.slots.get(q);
                            if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack2, true) || !slot4.canTakeItems(player) || !this.canInsertIntoSlot(itemStack2, slot4)) continue;
                            ItemStack itemStack6 = slot4.getStack();
                            if (o == 0 && itemStack6.getCount() == itemStack6.getMaxCount()) continue;
                            ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), itemStack2.getMaxCount() - itemStack2.getCount(), player);
                            itemStack2.increment(itemStack7.getCount());
                        }
                    }
                }
            }
        }
    }
}
