package net.gommehd.buildteam.addon;

import net.gommehd.buildteam.addon.registry.ItemRegistry;
import net.gommehd.buildteam.addon.updater.BlockIdUpdater;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.io.File;
import java.util.List;

public class BuildTeamAddon extends LabyModAddon {

    private static BuildTeamAddon addon;
    private BlockIdUpdater updater;
    private File dataDirectory;
    private File blocksFile;
    private ItemRegistry itemRegistry;

    @Override
    public void onEnable() {
        addon = this;
        this.dataDirectory = new File(AddonLoader.getConfigDirectory() + File.separator + "LegacyIDs");
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        this.blocksFile = new File(dataDirectory, "blockIDs.json");
        this.updater = new BlockIdUpdater();
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

    public File getDataDirectory() {
        return dataDirectory;
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