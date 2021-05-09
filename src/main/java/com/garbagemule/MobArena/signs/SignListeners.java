package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class SignListeners {

    private final List<Listener> listeners;

    public SignListeners() {
        this.listeners = new ArrayList<>();
    }

    public void register(SignBootstrap bootstrap) {
        listeners.add(load(bootstrap));
        listeners.add(unload(bootstrap));
        listeners.add(clicks(bootstrap));
        listeners.add(creation(bootstrap));
        listeners.add(destruction(bootstrap));
        listeners.add(updates(bootstrap));

        listeners.forEach(listener -> register(listener, bootstrap));
    }

    private HandlesWorldLoad load(SignBootstrap bootstrap) {
        return new HandlesWorldLoad(
            bootstrap.getSignDataMigrator(),
            bootstrap.getSignReader(),
            bootstrap.getSignStore(),
            bootstrap.getPlugin().getLogger()
        );
    }

    private HandlesWorldUnload unload(SignBootstrap bootstrap) {
        return new HandlesWorldUnload(
            bootstrap.getSignStore(),
            bootstrap.getPlugin().getLogger()
        );
    }

    private HandlesSignClicks clicks(SignBootstrap bootstrap) {
        return new HandlesSignClicks(
            bootstrap.getSignStore(),
            bootstrap.getInvokesSignAction()
        );
    }

    private HandlesSignCreation creation(SignBootstrap bootstrap) {
        return new HandlesSignCreation(
            bootstrap.getSignCreator(),
            bootstrap.getSignWriter(),
            bootstrap.getSignStore(),
            bootstrap.getSignRenderer(),
            bootstrap.getPlugin().getGlobalMessenger(),
            bootstrap.getPlugin().getLogger()
        );
    }

    private HandlesSignDestruction destruction(SignBootstrap bootstrap) {
        return new HandlesSignDestruction(
            bootstrap.getSignStore(),
            bootstrap.getSignWriter(),
            bootstrap.getPlugin().getGlobalMessenger(),
            bootstrap.getPlugin().getLogger()
        );
    }

    private HandlesArenaUpdates updates(SignBootstrap bootstrap) {
        return new HandlesArenaUpdates(
            bootstrap.getSignStore(),
            bootstrap.getSignRenderer(),
            bootstrap.getPlugin()
        );
    }

    private void register(Listener listener, SignBootstrap bootstrap) {
        MobArena plugin = bootstrap.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void unregister() {
        listeners.forEach(HandlerList::unregisterAll);
        listeners.clear();
    }

}
