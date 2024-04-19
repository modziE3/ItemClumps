package net.dev.itemclumps;

import net.dev.itemclumps.item.ClumpItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemClumps implements ModInitializer {

	public static final String MOD_ID = "itemclumps";
    public static final Logger LOGGER = LoggerFactory.getLogger("itemclumps");

	@Override
	public void onInitialize() {
		ClumpItems.registerModItems();
	}
}