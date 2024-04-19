package net.dev.itemclumps.item;

import net.dev.itemclumps.util.ClumpItemUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class ClumpItem extends Item {

    private static final String CLUMP_KEY = "Clump";
    private static final String ID_KEY = "id";
    private static final String COUNT_KEY = "count";

    public ClumpItem(Settings settings) {
        super(settings);
    }

    public static void addToClump(ItemStack clump, ItemStack item) {
        if (ClumpItemUtil.isClump(item.getItem())) {
            NbtList addedClumpList = getClump(item);
            for (int i = 0 ; i < addedClumpList.size() ; i++) {
                NbtCompound itemNBT = (NbtCompound) addedClumpList.get(i);
                addToClump(clump, getItemStack(itemNBT));
            } return;
        }
        NbtList clumpList = getClump(clump);
        NbtCompound newItemCompound = new NbtCompound();
        assert item.getNbt() != null;
        newItemCompound.putString(ID_KEY, Registries.ITEM.getId(item.getItem()).toString());
        newItemCompound.putByte(COUNT_KEY, (byte) item.getCount());
        clumpList.add(newItemCompound);
    }

    public static ItemStack getTopClump(ItemStack clump) {
        NbtList clumpList = getClump(clump);
        int size;
        if ((size = clumpList.size() - 1) >= 0) {
            NbtCompound itemNBT = (NbtCompound) clumpList.get(size);
            return getItemStack(itemNBT);
        }
        return new ItemStack(Items.AIR, 0);
    }

    public static int decrementClump(ItemStack clump, int amount) {
        NbtList clumpList = getClump(clump);
        int size;
        if ((size = clumpList.size() - 1) >= 0) {
            NbtCompound itemNBT = (NbtCompound) clumpList.get(size);
            int newCount = itemNBT.getByte(COUNT_KEY) - amount;
            clumpList.remove(size);
            if (newCount > 0) {
                itemNBT.putByte(COUNT_KEY, (byte) newCount);
                clumpList.add(itemNBT);
            } return newCount;
        } return 0;
    }

    public static NbtList getClump(ItemStack clump) {
        NbtCompound clumpNBT = clump.getOrCreateNbt();
        if (!clumpNBT.contains(CLUMP_KEY)) {
            clumpNBT.put(CLUMP_KEY, new NbtList());
        }
        return clumpNBT.getList(CLUMP_KEY, NbtElement.COMPOUND_TYPE);
    }

    public static ItemStack getItemStack(NbtCompound itemNBT) {
        Pair<String,String> id = ClumpItemUtil.idSplit(itemNBT.getString(ID_KEY));
        return new ItemStack(Registries.ITEM.get(new Identifier(id.getLeft(), id.getRight())), itemNBT.getByte(COUNT_KEY));
    }
}
