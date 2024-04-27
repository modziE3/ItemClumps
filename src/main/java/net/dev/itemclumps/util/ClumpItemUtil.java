package net.dev.itemclumps.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.dev.itemclumps.ItemClumps;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClumpItemUtil {

    private static final String LANG_PATH = (new File("../src/main/resources/assets/itemclumps/lang/en_us.json")).getAbsolutePath();

    public static List<Clump> readFile(String path) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(path)) {
            Type listType = new TypeToken<List<Clump>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeToLang(String name) {
        String id = "item." + ItemClumps.MOD_ID + "." + nameToId(name);
        Gson gson = new Gson();
        try (Reader reader = new FileReader(LANG_PATH)) {
            Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
            Map<String, String> langMap = gson.fromJson(reader, mapType);
            if (langMap == null) {
                langMap = new HashMap<>();
            }
            langMap.put(id, name);
            String updatedJsonString = gson.toJson(langMap);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LANG_PATH))) {
                writer.write(updatedJsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String nameToId(String name) {
        return name.replaceAll(" ", "_").toLowerCase();
    }

    public static Pair<String,String> tagSplit(String tag) {
        Pair<String,String> id = idSplit(tag);
        return new Pair<>(id.getLeft().substring(1), id.getRight());
    }

    public static Pair<String,String> idSplit(String id) {
        String[] parts = id.split(":");
        String namespace, path;
        if (parts.length == 2) {
            namespace = parts[0];
            path = parts[1];
        } else {
            throw new IndexOutOfBoundsException("Invalid Item Tag/ID Format");
        }
        return new Pair<>(namespace, path);
    }

    public static TagKey<Item> idToTag(String id) {
        Pair<String, String> tag_id = ClumpItemUtil.tagSplit(id);
        return TagKey.of(RegistryKeys.ITEM, new Identifier(tag_id.getLeft(), tag_id.getRight()));
    }
}
