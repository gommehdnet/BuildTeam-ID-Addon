package net.gommehd.buildteam.addon.updater;

import net.gommehd.buildteam.addon.BuildTeamAddon;
import net.gommehd.buildteam.addon.registry.ItemRegistry;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Connection class for updating the id list provided by GommeHD.net
 */
public class BlockIdUpdater {

    private static URL url;

    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    public BlockIdUpdater() {
        try {
            url = new URL("https://id.fantaflash.de/assets/blocks.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to download the new list of block ids, parse it, and update the current ItemRegistry with the new data
     *
     * @param result Contains {@code true} if the update succeeded, {@code false} otherwise.
     */
    public void update(Consumer<Boolean> result) {
        inProgress.set(true);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.connect();
            try (InputStream inputStream = connection.getInputStream();
                 OutputStream outputStream = new FileOutputStream(BuildTeamAddon.getAddon().getBlocksFile())) {
                IOUtils.copy(inputStream, outputStream);
            }
            connection.disconnect();
            BuildTeamAddon.getAddon().setItemRegistry(new ItemRegistry());
            result.accept(true);
        } catch (IOException e) {
            e.printStackTrace();
            result.accept(false);
        }
        inProgress.set(false);
    }

    public AtomicBoolean getInProgress() {
        return inProgress;
    }
}
