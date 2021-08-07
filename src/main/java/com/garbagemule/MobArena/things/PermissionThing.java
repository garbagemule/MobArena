package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionThing implements Thing {

    private final String perm;
    private final boolean value;
    private final MobArena plugin;

    public PermissionThing(String perm, boolean value, MobArena plugin) {
        this.perm = perm;
        this.value = value;
        this.plugin = plugin;
    }

    @Override
    public boolean giveTo(Player player) {
        removeExistingAttachment(player);
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachment.setPermission(perm, value);
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        removeExistingAttachment(player);
        return true;
    }

    private void removeExistingAttachment(Player player) {
        player.getEffectivePermissions().stream()
            .filter(info -> info.getAttachment() != null)
            .filter(info -> info.getAttachment().getPlugin().equals(plugin))
            .filter(info -> info.getPermission().equals(perm))
            .map(PermissionAttachmentInfo::getAttachment)
            .findAny()
            .ifPresent(PermissionAttachment::remove);
    }

    String getPermission() {
        return perm;
    }

    boolean getValue() {
        return value;
    }

    @Override
    public boolean heldBy(Player player) {
        return player.hasPermission(perm);
    }

    @Override
    public String toString() {
        if (value) {
            return perm;
        }
        return "-" + perm;
    }

}
