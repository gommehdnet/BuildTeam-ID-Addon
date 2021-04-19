package net.gommehd.buildteam.addon.updater;

import net.gommehd.buildteam.addon.BuildTeamAddon;
import net.gommehd.buildteam.addon.registry.ItemRegistry;
import net.labymod.utils.request.DownloadServerRequest;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Connection class for updating the id list provided by GommeHD.net
 */
public class BlockIdUpdater {

    private static final String BASE_URL = "https://raw.githubusercontent.com/gommehdnet/BuildTeam-ID-Addon/.data/";
    private static final String VERSION_URL = BASE_URL + "version";
    private static final String ITEMS_URL = BASE_URL + "items.json";

    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final File localVersionFile;

    public BlockIdUpdater() {
        localVersionFile = new File(BuildTeamAddon.getAddon().getDataDirectory(), "version");
        if (!localVersionFile.exists()) {
            try {
                localVersionFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tries to download the new list of block ids, parse it, and update the current ItemRegistry with the new data
     *
     * @param response Contains the response of the update operation.
     */
    public void update(Consumer<UpdateResponse> response) {
        inProgress.set(true);
        try {
            String remoteVersion = DownloadServerRequest.getString(VERSION_URL);
            if (!isNewVersion(remoteVersion)) {
                response.accept(UpdateResponse.SKIPPED);
                return;
            }
            FileUtils.copyURLToFile(new URL(ITEMS_URL), BuildTeamAddon.getAddon().getBlocksFile());
            BuildTeamAddon.getAddon().setItemRegistry(new ItemRegistry());
            FileUtils.writeStringToFile(localVersionFile, remoteVersion, StandardCharsets.UTF_8);
            response.accept(UpdateResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response.accept(UpdateResponse.FAILED);
        }
        inProgress.set(false);
    }


    private boolean isNewVersion(String remoteVersion) throws Exception {
        String localVersion = FileUtils.readFileToString(localVersionFile, StandardCharsets.UTF_8);
        if (localVersion == null || localVersion.isEmpty())
            return true;
        ComparableVersion local = new ComparableVersion(localVersion);
        ComparableVersion remote = new ComparableVersion(remoteVersion);
        return local.compareTo(remote) > 0;
    }

    public AtomicBoolean getInProgress() {
        return inProgress;
    }
}
