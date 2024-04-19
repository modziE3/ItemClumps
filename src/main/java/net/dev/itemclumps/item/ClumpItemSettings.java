package net.dev.itemclumps.item;

import net.minecraft.item.Item;

import java.lang.reflect.Field;

public class ClumpItemSettings extends Item.Settings {

    @Override
    public Item.Settings maxCount(int maxCount) {
        try {
            Field field = Item.Settings.class.getDeclaredField("maxCount");
            field.setAccessible(true);
            field.set(this, maxCount);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        return this;
    }
}
