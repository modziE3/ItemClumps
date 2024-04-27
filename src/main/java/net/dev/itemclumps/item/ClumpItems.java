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
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.List;

public class ClumpItems {

    public static final String CLUMP_PATH = (new File("../src/main/resources/data/itemclumps/clump/clumps.json")).getAbsolutePath();
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

    public static void registerModClumpItems() {
        ItemClumps.LOGGER.info("Registering Clumps for "+ItemClumps.MOD_ID);
    }
}
