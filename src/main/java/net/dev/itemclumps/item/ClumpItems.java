package net.dev.itemclumps.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.dev.itemclumps.ItemClumps;
import net.dev.itemclumps.util.Clump;
import net.dev.itemclumps.util.ClumpItemUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ClumpItems {

    public static final String CLUMP_PATH = "C:\\Users\\User\\Desktop\\CURRENT\\person\\minecraft\\fabric\\item-clumps-1.20.4\\src\\main\\resources\\data\\itemclumps\\clump\\clumps.json";

    public static final BiMap<String, Item> CLUMP_ITEMS = registerClumps();

    public static BiMap<String, Item> registerClumps() {
        List<Clump> clumps = ClumpItemUtil.readFile(CLUMP_PATH);
        BiMap<String, Item> clumpsTable = HashBiMap.create();
        if (clumps == null) return clumpsTable;
        for (Clump clump : clumps) {
            if (clump.isEnabled()) {
                ClumpItemUtil.writeToLang(clump.getName());
                clumpsTable.put(clump.getTagId(), registerClump(ClumpItemUtil.nameToId(clump.getName())));
            }
        }
        return clumpsTable;
    }

    private static Item registerClump(String name) {
        return Registry.register(Registries.ITEM, new Identifier(ItemClumps.MOD_ID, name), new ClumpItem(new FabricItemSettings()));
    }

    public static void registerModItems() {
        ItemClumps.LOGGER.info("Registering Clumps for "+ItemClumps.MOD_ID);
    }
}
