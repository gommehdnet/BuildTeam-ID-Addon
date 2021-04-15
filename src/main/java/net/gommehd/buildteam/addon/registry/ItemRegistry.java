package net.gommehd.buildteam.addon.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.gommehd.buildteam.addon.BuildTeamAddon;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private final Map<String, String> keyToId = new HashMap<>();

    public ItemRegistry() {
        File file = BuildTeamAddon.getAddon().getBlocksFile();
        if (file == null || !file.exists())
            return;
        try {
            JsonObject data = new JsonParser().parse(FileUtils.readFileToString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject blocks = data.getAsJsonObject("blocks");
            blocks.entrySet().forEach(entry -> registerBlock(entry.getKey(), entry.getValue().getAsString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerBlock(String id, String namespace) {
        if (namespace.contains("[")) {
            int index = namespace.indexOf('[');
            namespace = namespace.substring(0, index);
        }
        if (!keyToId.containsKey(namespace))
            keyToId.put(namespace, id);
    }

    public String getId(String namespace) {
        if (!namespace.startsWith("minecraft:"))
            namespace = "minecraft:" + namespace;
        return keyToId.get(namespace);
    }

    public String getId(Item item) {
        return getId(Registry.ITEM.getKey(item).toString());
    }

}
