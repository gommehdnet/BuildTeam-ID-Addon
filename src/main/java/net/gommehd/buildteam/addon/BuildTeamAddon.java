package net.gommehd.buildteam.addon;

import net.gommehd.buildteam.addon.registry.ItemRegistry;
import net.gommehd.buildteam.addon.updater.BlockIdUpdater;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.List;

public class BuildTeamAddon extends LabyModAddon {

    private static BuildTeamAddon addon;
    private final BlockIdUpdater updater = new BlockIdUpdater();
    private File blocksFile;
    private ItemRegistry itemRegistry;

    @Override
    public void onEnable() {
        addon = this;
        blocksFile = new File(Minecraft.getInstance().gameDir + File.separator + "LabyMod" + File.separator + "blockIds.json");
        if (!blocksFile.exists()) {
            updater.update(aBoolean -> {
            });
        }
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

    }

    public static BuildTeamAddon getAddon() {
        return addon;
    }

    public BlockIdUpdater getUpdater() {
        return updater;
    }

    public File getBlocksFile() {
        return blocksFile;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }
}