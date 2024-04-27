package net.dev.itemclumps.item;

import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class ClumpItem extends Item {

    private static final String CLUMP_KEY = "Clump";
    private static final String ID_KEY = "id";
    private static final String COUNT_KEY = "count";
    private static final ItemStack AIR = new ItemStack(Items.AIR, 0);

    public ClumpItem(Settings settings) {
        super(settings);
    }

    public static void addToClump(ItemStack clump, ItemStack item) {
        if (item.getItem() == AIR.getItem()) return;
        if (isClump(item.getItem())) {
            NbtList addedClumpList = getClump(item);
            for (NbtElement nbtElement : addedClumpList) {
                NbtCompound itemNBT = (NbtCompound) nbtElement;
                addToClump(clump, nbtCompoundToStack(itemNBT));
            }
            return;
        }
        NbtList clumpList = getClump(clump);
        NbtCompound newItemCompound = stackToNbtCompound(item);
        clumpList.add(newItemCompound);
    }

    public static ItemStack getTopStack(ItemStack clump) {
        NbtList clumpList = getClump(clump);
        int index;
        if ((index = clumpList.size() - 1) >= 0) {
            NbtCompound itemNBT = (NbtCompound) clumpList.get(index);
            return nbtCompoundToStack(itemNBT);
        }
        return AIR;
    }

    public static ItemStack removeTopStack(ItemStack clump, int amount) {
        NbtList clumpList = getClump(clump);
        int index;
        if ((index = clumpList.size() - 1) >= 0) {
            ItemStack topStack = nbtCompoundToStack((NbtCompound) clumpList.remove(index));
            if (amount >= topStack.getCount() || amount == -1) {
                clump.decrement(topStack.getCount());
            } else {
                ItemStack remainderStack = topStack.split(amount);
                addToClump(clump, remainderStack);
            }
            return topStack;
        }
        return AIR;
    }

    public static ItemStack getNextStack(ItemStack clump) {
        NbtList clumpList = getClump(clump);
        int index;
        if ((index = clumpList.size() - 2) >= 0) {
            NbtCompound itemNBT = (NbtCompound) clumpList.get(index);
            return nbtCompoundToStack(itemNBT);
        }
        return AIR;
    }

    public static ItemStack decrementClump(ItemStack clump, int amount) {
        NbtList clumpList = getClump(clump);
        int index;
        if ((index = clumpList.size() - 1) > 0) {
            NbtCompound itemNBT = (NbtCompound) clumpList.get(index);
            int newCount = itemNBT.getByte(COUNT_KEY) - amount;
            clumpList.remove(index);
            if (newCount > 0) {
                itemNBT.putByte(COUNT_KEY, (byte) newCount);
                clumpList.add(itemNBT);
            }
        }
        return reduceClump(clump);
    }

    public static ItemStack reduceClump(ItemStack clump) {
        if (!isClump(clump.getItem())) return clump;
        NbtList clumpList = getClump(clump);
        reduce(-1, clumpList);
        return switch (getClump(clump).size()) {
            case 0 -> AIR;
            case 1 -> getTopStack(clump);
            default -> clump;
        };
    }

    public static void reduce(int index, NbtList clumpList) {
        if (index < 0) index = index + clumpList.size();
        try {
            ItemStack topStack = nbtCompoundToStack((NbtCompound) clumpList.get(index));
            ItemStack nextStack = nbtCompoundToStack((NbtCompound) clumpList.get(index - 1));
            if (topStack.getItem() == nextStack.getItem()) {
                nextStack.increment(topStack.getCount());
                clumpList.remove(index);
                clumpList.setElement(index - 1, stackToNbtCompound(nextStack));
            }
        } catch (IndexOutOfBoundsException ignored) {}
    }

    public static NbtList getClump(ItemStack clump) {
        NbtCompound clumpNBT = clump.getOrCreateNbt();
        if (!clumpNBT.contains(CLUMP_KEY)) {
            clumpNBT.put(CLUMP_KEY, new NbtList());
        }
        return clumpNBT.getList(CLUMP_KEY, NbtElement.COMPOUND_TYPE);
    }

    public static ItemStack nbtCompoundToStack(NbtCompound itemNBT) {
        Pair<String,String> id = ClumpItemUtil.idSplit(itemNBT.getString(ID_KEY));
        return new ItemStack(Registries.ITEM.get(new Identifier(id.getLeft(), id.getRight())), itemNBT.getByte(COUNT_KEY));
    }

    public static NbtCompound stackToNbtCompound(ItemStack stack) {
        NbtCompound newItemCompound = new NbtCompound();
        newItemCompound.putString(ID_KEY, Registries.ITEM.getId(stack.getItem()).toString());
        newItemCompound.putByte(COUNT_KEY, (byte) stack.getCount());
        return newItemCompound;
    }

    public static Item getCommonClumpType(ItemStack stack1, ItemStack stack2) {
        if (isClump(stack1.getItem()) && (stack2.isIn(ClumpItemUtil.idToTag(ClumpItems.CLUMP_ITEMS.inverse().get(stack1.getItem()))) || stack1.getItem() == stack2.getItem())) {
            return stack1.getItem();

        } else if (isClump(stack2.getItem()) && stack1.isIn(ClumpItemUtil.idToTag(ClumpItems.CLUMP_ITEMS.inverse().get(stack2.getItem())))) {
            return stack2.getItem();
        }

        for (String id : ClumpItems.CLUMP_ITEMS.keySet()) {
            TagKey<Item> tag = ClumpItemUtil.idToTag(id);
            if (stack1.isIn(tag) && stack2.isIn(tag)) {
                return ClumpItems.CLUMP_ITEMS.get(id);
            }
        }
        return null;
    }

    public static boolean isClump(Item item) {
        return ClumpItems.CLUMP_ITEMS.containsValue(item);
    }

    public static boolean canClump(ItemStack stack1, ItemStack stack2) {
        return getCommonClumpType(stack1, stack2) != null;
    }
}
