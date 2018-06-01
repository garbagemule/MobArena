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
        listeners.add(clicks(bootstrap));
        listeners.add(creation(bootstrap));
        listeners.add(destruction(bootstrap));
        listeners.add(redraw(bootstrap));

        listeners.forEach(listener -> register(listener, bootstrap));
    }

    private HandlesSignClicks clicks(SignBootstrap bootstrap) {
        return new HandlesSignClicks(
            bootstrap.getSignStore(),
            bootstrap.getInvokesSignAction()
        );
    }

    private HandlesSignCreation creation(SignBootstrap bootstrap) {
        return new HandlesSignCreation(
            bootstrap.getStoresNewSign(),
            bootstrap.getRendersTemplateById(),
            bootstrap.getPlugin().getGlobalMessenger()
        );
    }

    private HandlesSignDestruction destruction(SignBootstrap bootstrap) {
        return new HandlesSignDestruction(
            bootstrap.getRemovesSignAtLocation(),
            bootstrap.getPlugin().getGlobalMessenger()
        );
    }

    private RedrawsSignsOnUpdates redraw(SignBootstrap bootstrap) {
        return new RedrawsSignsOnUpdates(
            bootstrap.getRedrawsArenaSigns(),
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
