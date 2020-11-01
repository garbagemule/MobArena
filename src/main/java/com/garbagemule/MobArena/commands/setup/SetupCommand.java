package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.region.ArenaRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "setup",
    pattern = "setup",
    usage   = "/ma setup <arena>",
    desc    = "enter setup mode for an arena",
    permission = "mobarena.setup.setup"
)
public class SetupCommand implements Command, Listener {
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Get the arena
        Arena arena;
        if (args.length == 0) {
            List<Arena> arenas = am.getArenas();
            if (arenas.size() > 1) {
                return false;
            }
            arena = arenas.get(0);
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, "There is no arena with the name " + ChatColor.RED + args[0] + ChatColor.RESET + ".");
                am.getGlobalMessenger().tell(sender, "Type " + ChatColor.YELLOW + "/ma addarena " + args[0] + ChatColor.RESET + " to create it!");
                return true;
            }
        }
        Player player = Commands.unwrap(sender);

        // Create the setup object
        Setup setup = new Setup(player, arena);

        // Register it as an event listener
        am.getPlugin().getServer().getPluginManager().registerEvents(setup, am.getPlugin());

        // Set up the conversation
        Conversation convo = new Conversation(am.getPlugin(), player, setup);
        setup.convo = convo;
        convo.addConversationAbandonedListener(setup);
        convo.setLocalEchoEnabled(false);
        convo.begin();
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Arena> arenas = am.getArenas();

        return arenas.stream()
            .filter(arena -> arena.getSlug().startsWith(prefix))
            .map(Arena::getSlug)
            .collect(Collectors.toList());
    }

    /**
     * The internal Setup class has three roles; it is the prompt and the
     * abandon listener for the Conversation initiated by the setup command,
     * but it is also an event listener for the interact event, to handle
     * the Toolbox events.
     */
    private class Setup implements Prompt, ConversationAbandonedListener, Listener {
        private Player player;
        private Arena arena;
        private Conversation convo;

        private boolean enabled;
        private boolean allowFlight;
        private boolean flying;
        private ItemStack[] armor;
        private ItemStack[] items;

        private List<String> missing;
        private String next;

        public Setup(Player player, Arena arena) {
            this.player  = player;
            this.arena   = arena;

            // Store player and arena state
            this.enabled     = arena.isEnabled();
            this.allowFlight = player.getAllowFlight();
            this.flying      = player.isFlying();
            this.armor       = player.getInventory().getArmorContents();
            this.items       = player.getInventory().getContents();

            // Change state
            arena.setEnabled(false);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
            player.getInventory().setContents(getToolbox());
            player.getInventory().setHeldItemSlot(0);

            this.missing = new ArrayList<>();
            this.next = color(String.format(
                    "Setup Mode for arena &a%s&r. Type &e?&r for help.",
                    "&a" + arena.configName() + "&r"
            ));

            ArenaRegion region = arena.getRegion();
            if (!region.isSetup()){
                // Region points
                if (!region.isDefined()) {
                    missing.add("p1");
                    missing.add("p2");
                }

                // Arena, lobby, and spectator warps
                if (region.getArenaWarp() == null) missing.add("arena");
                if (region.getLobbyWarp() == null) missing.add("lobby");
                if (region.getSpecWarp()  == null) missing.add("spectator");

                // Spawnpoints
                if (region.getSpawnpoints().isEmpty()) {
                    missing.add("spawnpoints");
                }
            }
        }


        // ====================================================================
        //  Toolbox handler
        // ====================================================================

        private ItemStack[] getToolbox() {
            // Arena region tool
            ItemStack areg = makeTool(
                Material.GOLDEN_AXE, AREG_NAME,
                color("Set &ep1"),
                color("Set &ep2")
            );
            // Warps tool
            ItemStack warps = makeTool(
                Material.GOLDEN_HOE, WARPS_NAME,
                color("&eSet &rselected warp"),
                color("&eCycle &rbetween warps")
            );
            // Spawns tool
            ItemStack spawns = makeTool(
                Material.GOLDEN_SWORD, SPAWNS_NAME,
                color("&eAdd &rspawnpoint on block"),
                color("&eRemove &rspawnpoint on block")
            );
            // Chests tool
            ItemStack chests = makeTool(
                Material.GOLDEN_SHOVEL, CHESTS_NAME,
                color("&eAdd &rcontainer"),
                color("&eRemove &rcontainer")
            );
            // Lobby region tool
            ItemStack lreg = makeTool(
                Material.GOLDEN_AXE, LREG_NAME,
                color("Set &el1"),
                color("Set &el2")
            );
            // Round 'em up.
            return new ItemStack[] {
                null, areg, warps, spawns, chests, null, lreg
            };
        }

        private ItemStack makeTool(Material mat, String name, String left, String right) {
            ItemStack tool = new ItemStack(mat);
            ItemMeta meta = tool.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(
                    color("&9Left&r: &r" + left),
                    color("&cRight&r: &r" + right)
            ));
            meta.setUnbreakable(true);
            tool.setItemMeta(meta);
            return tool;
        }

        private boolean isTool(ItemStack item) {
            if (item == null || item.getType() == Material.AIR) return false;

            String name = item.getItemMeta().getDisplayName();
            if (name == null) return false;

            // Just check the names of each tool
            return name.equals(AREG_NAME)
                || name.equals(LREG_NAME)
                || name.equals(WARPS_NAME)
                || name.equals(SPAWNS_NAME)
                || name.equals(CHESTS_NAME)
                || name.equals(MANUAL_NAME);
        }

        @EventHandler
        public void onDisable(PluginDisableEvent event) {
            if (event.getPlugin().getName().equals(arena.getPlugin().getName()) && player.isConversing()) {
                player.abandonConversation(convo);
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            if (event.getPlayer().equals(player) && player.isConversing()) {
                player.abandonConversation(convo);
            }
        }

        @EventHandler
        public void onBreak(BlockBreakEvent event) {
            Player p = event.getPlayer();
            if (!p.equals(player)) return;

            ItemStack tool = p.getInventory().getItemInMainHand();
            if (!isTool(tool)) return;

            event.setCancelled(true);
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            Player p = event.getPlayer();
            if (!p.equals(player)) return;

            event.setCancelled(true);
            tell(p, "You can't drop the toolbox items.");
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            Player p = event.getPlayer();
            if (!p.equals(player)) return;

            if (event.getHand() == EquipmentSlot.OFF_HAND) return;

            ItemStack tool = p.getInventory().getItemInMainHand();
            if (!isTool(tool)) return;

            String name = tool.getItemMeta().getDisplayName();
            if (name.equals(AREG_NAME)) {
                if (!arena(event)) return;
            } else if (name.equals(LREG_NAME)) {
                if (!lobby(event)) return;
            } else if (name.equals(WARPS_NAME)) {
                if (!warps(event)) return;
            } else if (name.equals(SPAWNS_NAME)) {
                if (!spawns(event)) return;
            } else if (name.equals(CHESTS_NAME)) {
                if (!chests(event)) return;
            }

            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);

            player.sendRawMessage(getPromptText(null));
        }

        private boolean arena(PlayerInteractEvent event) {
            if (!event.hasBlock()) {
                return false;
            }

            Location loc = event.getClickedBlock().getLocation();
            region(event.getAction(), "p1", "p2", loc);
            return true;
        }

        private boolean lobby(PlayerInteractEvent event) {
            if (!event.hasBlock()) {
                return false;
            }

            Location loc = event.getClickedBlock().getLocation();
            region(event.getAction(), "l1", "l2", loc);
            return true;
        }

        private boolean region(Action action, String lower, String upper, Location loc) {
            switch (action) {
                case LEFT_CLICK_BLOCK:  regions(lower, loc); return true;
                case RIGHT_CLICK_BLOCK: regions(upper, loc); return true;
            }
            return false;
        }

        private boolean warps(PlayerInteractEvent event) {
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK:
                    Location loc = event.getClickedBlock().getLocation();
                    loc.setYaw(player.getLocation().getYaw());
                    loc.setPitch(0);
                    fix(loc);
                    String warp = warpArray[warpIndex];
                    warps(warp, loc);
                    return true;
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR:
                    warpIndex++;
                    if (warpIndex == warpArray.length) {
                        warpIndex = 0;
                    }
                    next = formatYellow("Current warp: %s", warpArray[warpIndex]);
                    return true;
            }
            return false;
        }

        private boolean spawns(PlayerInteractEvent event) {
            if (!event.hasBlock()) {
                return false;
            }

            Location l = event.getClickedBlock().getLocation();
            fix(l);
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK:  spawns(l, true);  return true;
                case RIGHT_CLICK_BLOCK: spawns(l, false); return true;
            }
            return false;
        }

        private boolean chests(PlayerInteractEvent event) {
            if (!event.hasBlock()) {
                return false;
            }

            Block b = event.getClickedBlock();
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK:  chests(b, true);  return true;
                case RIGHT_CLICK_BLOCK: chests(b, false); return true;
            }
            return false;
        }

        private void fix(Location loc) {
            loc.setX(loc.getBlockX() + 0.5D);
            loc.setY(loc.getBlockY() + 1);
            loc.setZ(loc.getBlockZ() + 0.5D);
        }

        private int warpIndex = 0;
        private String[] warpArray = new String[] {"arena", "lobby", "spectator", "exit"};

        private static final String AREG_NAME   = "Arena Region";
        private static final String LREG_NAME   = "Lobby Region";
        private static final String WARPS_NAME  = "Warps";
        private static final String SPAWNS_NAME = "Spawnpoints";
        private static final String CHESTS_NAME = "Containers";
        private static final String MANUAL_NAME = "Manual";


        // ====================================================================
        //  Conversation end handler (items, state, etc.)
        // ====================================================================

        @Override
        public void conversationAbandoned(ConversationAbandonedEvent event) {
            // Unregister listener
            HandlerList.unregisterAll(this);

            // Restore player and arena state
            arena.setEnabled(enabled);
            arena.getRegion().save();
            arena.getRegion().reloadAll();
            player.getInventory().setContents(items);
            player.getInventory().setArmorContents(armor);

            // setAllowFlight(false) also handles setFlying(false)
            player.setAllowFlight(allowFlight);
            if (allowFlight) {
                player.setFlying(flying);
            }
        }


        // ====================================================================
        //  Prompt methods
        // ====================================================================

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.GREEN + "[MobArena] " + ChatColor.RESET + next;
        }

        @Override
        public boolean blocksForInput(ConversationContext context) {
            return true;
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String s) {
            // Check regexes at the bottom of the file
            return s.matches(HELP)     ? help()
                 : s.matches(MISSING)  ? missing()
                 : s.matches(EXPAND)   ? expand(s)
                 : s.matches(EXPHELP)  ? expandOptions()
                 : s.matches(SHOW)     ? show(context, s)
                 : s.matches(SHOWHELP) ? showOptions()
                 : s.matches(DONE)     ? done()
                 : invalidInput();
        }


        // ====================================================================
        //  Input handlers
        // ====================================================================

        /**
         * Help
         */
        private Prompt help() {
            StringBuilder buffy = new StringBuilder();
            buffy.append("\nAvailable input:");
            buffy.append("\n&r&e exp   &7expand a region");
            buffy.append("\n&r&e show   &7show a region, warp, or point");
            buffy.append("\n&r&e miss   &7show missing warps and points");
            buffy.append("\n&r&e done   &7exit out of Setup Mode");
            buffy.append("\n&r&7Read &bitem tooltips&r&7 for info about each tool.");
            next = color(buffy.toString());
            return this;
        }

        /**
         * Regions
         */
        private Prompt regions(String s, Location loc) {
            // Change worlds if needed
            if (!inArenaWorld()) {
                String msg = String.format(
                        "Changed world of arena %s from %s to %s.",
                        ChatColor.GREEN + arena.configName() + ChatColor.RESET,
                        ChatColor.YELLOW + arena.getWorld().getName() + ChatColor.RESET,
                        ChatColor.YELLOW + loc.getWorld().getName() + ChatColor.RESET
                );
                arena.setWorld(loc.getWorld());
                tell(player, msg);
            }
            arena.getRegion().set(s, loc);
            next = formatYellow("Region point %s was set.", s);
            missing.remove(s);
            return this;
        }

        /**
         * Expand
         */
        private Prompt expand(String s) {
            String[] parts = s.split(" ");

            boolean lobby = parts[1].equalsIgnoreCase("lr");
            int amount = Integer.parseInt(parts[2]);

            if (parts[3].equalsIgnoreCase("up")) {
                if (lobby) {
                    arena.getRegion().expandLobbyUp(amount);
                } else {
                    arena.getRegion().expandUp(amount);
                }
            } else if (parts[3].equalsIgnoreCase("down")) {
                if (lobby) {
                    arena.getRegion().expandLobbyDown(amount);
                } else {
                    arena.getRegion().expandDown(amount);
                }
            } else {
                if (lobby) {
                    arena.getRegion().expandLobbyOut(amount);
                } else {
                    arena.getRegion().expandOut(amount);
                }
            }
            next = color(String.format("Expanded &e%s&r region &e%s&r by &e%s&r blocks.", (lobby ? "lobby" : "arena"), parts[3], parts[2]));
            return this;
        }

        /**
         * Warps
         */
        private Prompt warps(String s, Location loc) {
            if (s.equals("spec")) s = "spectator";

            // World change stuff for the arena warp
            if (s.equals("arena") && !arena.getRegion().contains(loc)) {
                if (!arena.getWorld().getName().equals(loc.getWorld().getName())) {
                    World tmp = arena.getWorld();
                    arena.setWorld(loc.getWorld());
                    if (arena.getRegion().contains(loc)) {
                        String msg = String.format(
                                "Changed world of arena %s from %s to %s.",
                                ChatColor.GREEN + arena.configName() + ChatColor.RESET,
                                ChatColor.YELLOW + tmp.getName() + ChatColor.RESET,
                                ChatColor.YELLOW + loc.getWorld().getName() + ChatColor.RESET
                        );
                        tell(player, msg);
                    } else {
                        arena.setWorld(tmp);
                        next = "You must be inside the arena region.";
                        return this;
                    }
                } else {
                    next = "You must be inside the arena region.";
                    return this;
                }
            }
            missing.remove(s);
            arena.getRegion().set(s, loc);
            next = formatYellow("Warp point %s was set.", s);
            return this;
        }

        /**
         * Spawns
         */
        private Prompt spawns(Location l, boolean add) {
            String point = getName(l);
            if (add) {
                if (!arena.getRegion().contains(l)) {
                    next = "You must be inside the arena region.";
                } else {
                    arena.getRegion().addSpawn(point, l);
                    next = formatYellow("Spawnpoint %s added.", point);
                    missing.remove("spawnpoints");
                }
            } else {
                if (arena.getRegion().removeSpawn(point)) {
                    next = formatYellow("Spawnpoint %s removed.", point);
                    if (arena.getRegion().getSpawnpoints().size() == 0) {
                        missing.add("spawnpoints");
                    }
                } else {
                    next = formatYellow("No spawnpoint named %s.", point);
                }
            }
            return this;
        }

        /**
         * Chests
         */
        private Prompt chests(Block b, boolean add) {
            if (b != null) {
                if (!(b.getState() instanceof InventoryHolder)) {
                    next = "You must be looking at a container.";
                } else if (!arena.getRegion().contains(b.getLocation())) {
                    next = "You must be inside the arena region.";
                } else {
                    String point = getName(b.getLocation());
                    if (add) {
                        arena.getRegion().addChest(point, b.getLocation());
                        next = formatYellow("Container %s added.", point);
                    } else if (arena.getRegion().removeChest(point)) {
                        next = formatYellow("Container %s removed.", point);
                    } else {
                        next = formatYellow("No container named %s.", point);
                    }
                }
            }
            return this;
        }

        /**
         * Show things.
         */
        private Prompt show(ConversationContext context, String s) {
            ArenaRegion region = arena.getRegion();
            String toShow = s.split(" ")[1].trim();

            // Regions
            if (toShow.equalsIgnoreCase("r") || toShow.equalsIgnoreCase("regions")) {
                if (region.isDefined()) {
                    region.showRegion(player);
                    if (region.isLobbyDefined()) {
                        region.showLobbyRegion(player);
                        next = formatYellow("Showing both %s.", "regions");
                    } else {
                        next = formatYellow("Showing %s (lobby region not defined).", "arena region");
                    }
                } else if (region.isLobbyDefined()) {
                    region.showLobbyRegion(player);
                    next = formatYellow("Showing %s (arena region not defined).", "lobby region");
                } else {
                    next = "No regions have been defined yet.";
                }
                return this;
            } else if (toShow.equalsIgnoreCase("ar")) {
                if (region.isDefined()) {
                    next = formatYellow("Showing %s.", "arena region");
                    region.showRegion(player);
                } else {
                    next = "The region has not been defined yet.";
                }
                return this;
            } else if (toShow.equalsIgnoreCase("lr")) {
                if (region.isLobbyDefined()) {
                    next = formatYellow("Showing %s.", "lobby region");
                    region.showLobbyRegion(player);
                } else {
                    next = "The lobby region has not been defined yet.";
                }
                return this;
            }

            // Warps
            if (toShow.matches("arena|lobby|spec(tator)?|exit")) {
                next = formatYellow("Showing %s warp.", toShow);
                Location loc;
                loc = toShow.equals("arena")     ? region.getArenaWarp() :
                      toShow.equals("lobby")     ? region.getLobbyWarp() :
                      toShow.equals("spec")      ? region.getSpecWarp()  :
                      toShow.equals("spectator") ? region.getSpecWarp()  :
                      toShow.equals("exit")      ? region.getExitWarp()  : null;
                region.showBlock(player, loc);
                return this;
            }

            // Spawnpoints
            if (toShow.matches("sp(awn(point)?s?)?")) {
                next = formatYellow("Showing %s.", "spawnpoints");
                region.showSpawns(player);
                return this;
            }

            // Chests
            if (toShow.matches("c((hest(s)?)?|on(tainer(s)?)?)")) {
                next = formatYellow("Showing %s.", "containers");
                region.showChests(player);
                return this;
            }

            // Show the "show help", if invalid thing
            return acceptInput(context, "show ?");
        }

        /**
         * Missing points and warps
         */
        private Prompt missing() {
            if (missing.isEmpty()) {
                next = "All required points and warps have been set!";
            } else {
                next = "Missing points and warps: " + getMissing();
            }
            return this;
        }

        /**
         * Expand options
         */
        private Prompt expandOptions() {
            StringBuilder buffy = new StringBuilder();
            buffy.append("\nUsage: &eexp <region> <amount> <direction>");

            buffy.append("\n\n&r&7Variable details:");
            buffy.append("\n&r&7 region: &rar&7 (arena region) or &rlr&7 (lobby region)");
            buffy.append("\n&r&7 amount: number of blocks to expand by");
            buffy.append("\n&r&7 direction: &rup&7, &rdown&7, or &routs&7");

            buffy.append("\n\n&r&7Examples:");
            buffy.append("\n&r exp ar 5 up   &7expand arena region up by 5");
            buffy.append("\n&r exp lr 10 out   &7expand lobby region out by 10");
            next = color(buffy.toString());
            return this;
        }

        /**
         * Show options
         */
        private Prompt showOptions() {
            StringBuilder buffy = new StringBuilder();
            buffy.append("\nUsage: &eshow <thing>");

            buffy.append("\n\n&r&7Possible things to show:");
            buffy.append("\n&r&7 regions: &rar&7 (arena region) or &rlr&7 (lobby region) or &rr&7 (both)");
            buffy.append("\n&r&7 warps: &rarena&7, &rlobby&7, &rspec&7, or &rexit");
            buffy.append("\n&r&7 points: &rspawns&7 or &rchests&7");

            buffy.append("\n\n&r&7Examples:");
            buffy.append("\n&r show spawns   &7show spawnpoints");
            buffy.append("\n&r show ar   &7show arena region");
            next = color(buffy.toString());
            return this;
        }

        /**
         * Done!
         */
        private Prompt done() {
            if (missing.isEmpty()) {
                tell(player, "Setup complete! Arena is ready to be used!");
            } else {
                tell(player, "Setup incomplete. Missing points and warps: " + getMissing());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        /**
         * Invalid input
         */
        private Prompt invalidInput() {
            next = formatYellow("Invalid input. Type %s for help", "?");
            return this;
        }


        // ====================================================================
        //  Auxiliary methods
        // ====================================================================

        private String getMissing() {
            StringBuilder buffy = new StringBuilder();
            for (String m : missing) {
                buffy.append("\n").append(m);
            }
            return buffy.toString();
        }

        private String color(String s) {
            return ChatColor.translateAlternateColorCodes('&', s);
        }

        private boolean inArenaWorld() {
            return player.getWorld().getName().equals(arena.getWorld().getName());
        }

        private void tell(Conversable whom, String msg) {
            whom.sendRawMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.RESET + msg);
        }

        private String formatYellow(String msg, String arg) {
            return String.format(msg, ChatColor.YELLOW + arg + ChatColor.RESET);
        }

        private String getName(Location l) {
            return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
        }


        // ====================================================================
        //  Regular expressions for the input
        // ====================================================================

        private static final String HELP     = "[?]|h(elp)?";
        private static final String MISSING  = "miss(ing)?";
        private static final String EXPAND   = "exp(and)? (a|l)r [1-9][0-9]* (up|down|out)";
        private static final String EXPHELP  = "exp(and)?";
        private static final String SHOW     = "show (r|ar|lr|arena|lobby|spec(tator)?|exit|sp(awn(point)?s?)?|c((hest(s)?)?|on(tainer(s)?)?))";
        private static final String SHOWHELP = "show";
        private static final String DONE     = "done|quit|stop|end";
    }
}
