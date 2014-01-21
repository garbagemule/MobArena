package com.garbagemule.MobArena.util;

import org.bukkit.entity.Player;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.Updater.UpdateResult;
import com.garbagemule.MobArena.util.Updater.UpdateType;

public class VersionChecker
{
	static Updater updater;
    public static void checkForUpdates(final MobArena plugin, final Player player) {
    	if (updater == null) {
    		updater = new Updater(plugin, 31265, plugin.getPluginFile(), UpdateType.NO_DOWNLOAD, false);
    	}
    	
    	if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
    		if (player == null) {
                Messenger.info(updater.getLatestName() + " is now available!");
                Messenger.info("Your version: v" + plugin.getDescription().getVersion());
            } else if (player.isOnline()) {
                Messenger.tell(player, updater.getLatestName() + " is now available!");
                Messenger.tell(player, "Your version: v" + plugin.getDescription().getVersion());
            }
    	}
    }
    public static void shutDown() {
    	updater = null;
    }
}
