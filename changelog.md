# Changelog
All notable changes to the project are recorded in this document in a user-friendly format.
The format is loosely based on [Keep a Changelog](https://keepachangelog.com/).

Each version in the document contains only user-facing changes (including API changes).
However, each version in the document is also a link, and clicking a version link reveals all commits that went into the given version.
The release notes published on Spigot and DBO are derived from this document.

The "Unreleased" section contains changes that have not yet been released.
These changes will (most likely) be included in the next version.


## [Unreleased]
- A new `ready` state is now available for arena sign templates. Signs are in this state when all players in the lobby have readied up, but the arena has not yet started due to a start delay timer. Check the wiki for details.
- Arena signs now support dynamic list entry variables for 4 different player lists. As an example, `<notready-1>` results in the name of a player in the lobby who hasn't readied up yet. This is useful for visualizing who is holding up the lobby. Check the wiki for details.
- Elytra are now supported chest pieces in class chests.
- Boss names now support color codes.
- The Root Target ability now uses potion effects (slowness, slow falling, and negative jump boost) instead of repeated teleports. This should make for a smoother root experience.
- Config-files with missing `pet-items` nodes no longer errors. A missing `pet-items` node in `global-settings` is treated as empty, i.e. no pet items will be registered.
- The `player-time-in-arena` setting has been fixed.

## [0.104.2] - 2020-01-03
- The region overlap check now works across both arena and lobby regions, i.e. all four combinations of intersections between two regions (arena-arena, arena-lobby, lobby-arena, and lobby-lobby) are evaluated.
- Arenas with missing regions no longer cause errors in the region overlap check.

## [0.104.1] - 2019-12-31
- It is no longer necessary to have recurrent waves for an arena to work. MobArena automatically creates a "catch all" recurrent wave in case the arena session reaches a wave number that isn't covered by any other wave definitions.
- Entities outside of the arena can no longer target players, pets, or monsters inside of the arena.
- Tab completion for `/ma kick` and `/ma restore` now uses actual player names instead of display names.
- If the world of an exit warp, leaderboard, or linked class chest is not available on (re)load, MobArena now throws a config error instead of failing silently later down the road.
- Overlapping arena regions are now reported as warnings in the server log during arena load, because overlapping regions can result in undefined, buggy behavior.
- MobArena's internal version checker has been rewritten. It now uses the resource API of Spigot instead of DBO. It's also a lot more lightweight and caches results for up to one hour.

## [0.104] - 2019-08-08
- Extended and upgraded potions are now supported in the item syntax by prepending `long_` or `strong_` to the data portion of a potion item (e.g. `potion:strong_instant_heal:1` will yield a Potion of Healing II). Check the wiki for details.
- MobArena now has basic tab completion support for most of the commands that take arguments.
- The `pet-items` node in `global-settings` now supports any living entity as a pet - even zombies! Pets will aggro hostile mobs that damage their owner, but only tameable pets (wolves, cats, etc.) will properly follow their owners around. This should also allow 1.14 servers to replace `ocelot` with `cat` in their `pet-items` node to get cat pets working again.
- Pets now have custom names that denote who their owner is, e.g. "garbagemule's pet".
- Pet items can now be used in Upgrade Waves. When an Upgrade Wave spawns, all pet items are transformed to pets just like when the arena session starts. Note that while only Upgrade Waves trigger this behavior, pet items obtained elsewhere (e.g. from Supply Wave drops) will also be transformed on subsequent Upgrade Waves.
- Potion effects can now be used as upgrades in Upgrade Waves. Check the wiki for details.
- Tridents and crossbows are now considered weapons with regards to the `unbreakable-weapons` flag. All class items that have durability now have their unbreakable flag set to true unless the `unbreakable-weapons` and/or `unbreakable-armor` flags are set to `false`.
- Leaderboards now work again on servers running Minecraft 1.14+.
- Class chests (non-linked) now work again on servers running Minecraft 1.14+.
- MobArena no longer crashes when players try to join with items that lower their max health below the default of 20. Players with lower max health will notice missing health in the lobby, but it will quickly regenerate to full.
- Food levels no longer deplete for players in the lobby and spectator area.
- Wither skeletons now correctly spawn with stone swords.
- Mobs now correctly take damage from player-made iron golems.
- Cat and parrot pets now also sit when their owner joins an arena (although parrots perching on players' shoulders will still follow them into the arena).
- Pig zombies are now angry immediately after they spawn as they should be.
- Vexes summoned by evokers are now kept track of, so they count towards `clear-wave-before-next`, and they are properly removed at arena end.
- Support for denoting potion effects by magic number IDs has been dropped. This means that if your config-file has any such magic numbers in it, MobArena will no longer successfully parse them and will throw an error on startup.
- Support for auto-respawning has been dropped. The hacky way it was implemented is not officially supported by the Bukkit API and is highly discouraged because it is very buggy.

## [0.103.2] - 2019-04-23
- MobArena no longer touches the `flySpeed` player attribute when players join an arena. This should fix issues where a crash would result in players being "locked in the air" when trying to fly outside of the arena. It also introduces compatibility with plugins that use flight to augment player abilities.
- Fixed a bug introduced by a breaking API change in Spigot where a player with a nearly full inventory might cause item rewards to change stack amounts.
- MobArena no longer uncancels teleport events that occur outside of its own context when players have the `mobarena.admin.teleport` permission. This fixes a bug where the permission could override the cancellation of events that weren't related to MobArena.
- When resetting player health, MobArena now uses the player max health attribute base value rather than a fixed value of 20. This fixes crashes associated with max health values lower than 20, and ensures that players always get a full heal with values higher than 20.
- The server version check on the main build (currently for 1.13) now explicitly looks for incompatible versions rather than compatible versions. This brings back the "works unless otherwise specified" nature of the plugin, and thus a MobArena build for Minecraft 1.13 should (knock-on-wood) work on 1.14.

Thanks to:
- minoneer for help with fixing and testing the teleport bug

## [0.103.1] - 2018-12-31
- Like the other user commands, the permission for `/ma ready` now defaults to true. 
- Unbreakable weapons and armor now use the unbreakable item flag instead of item durability and on-hit repairs. This means that MobArena's unbreakable items are now compatible with plugins that depend on special durability values, such as QualityArmory. 
- Spectators can no longer take damage when the arena isn't running.
- Pets can now teleport back to their owners if they get too far away.
- Enchantment names are now case insensitive (i.e. `FIRE_PROTECTION` is the same as `fire_protection`).
- All commands are now case insensitive. This means that typing `/ma join` is the same as typing `/MA JOIN`. This should help reduce the confusion with commands like `/ma l` where the L can be confused with a 1 (one).

## [0.103] - 2018-08-28
- It is now possible to add a fixed delay (in seconds) between waves with the new per-arena setting `next-wave-delay`.
- The new per-arena setting `join-interrupt-timer` makes it possible to add a "delay" to the join and spec commands. If the player moves or takes damage during this delay, the command is interrupted. This should help prevent exploits on PvP servers.
- Right-clicking is now allowed in the lobby. This makes it possible to activate blocks like buttons and levers.
- Snow and ice no longer melts in arenas.
- Much of the parsing logic has been rewritten so that MobArena now logs more user-friendly errors when it encounters invalid values in the config-file.
- If MobArena fails to load due to config-file errors, it now enters a type of "error state". In this state, it responds to all commands (except for the reload command) with a short message explaining why it's disabled.
- It is now possible to reload config-files with `/ma reload`.
- The reload command now also reloads global settings, e.g. the global messenger prefix.
- Armor stands can now be placed in arenas and lobbies.
- The new command `/ma ready` (`/ma rdy` for short) can be used as an alternative to the iron block for readying up.  
- Total experience is now correctly stored, reset, and restored on arena join/leave. This fixes a potential bug where total experience could increase in the arena, but levels and progress would still get reset at arena end.
- The per-arena setting `keep-exp` returns. If enabled, any experience collected during an arena session is added as a reward on death or when the final wave is reached.
- Waves will no longer intermittently progress at double frequency in some arena sessions. This long-standing bug where waves progress at "double speed" has finally been fixed.

Thanks to:
- Sait for adding the /ma ready command
- PrinceIonia, Nesseley, and Diamond\_Cat for help with test of dev builds

## [0.102] - 2018-07-05
- It is now possible to change which items are turned into wolf pets in the global-settings in the config-file.
- Ocelots can now be used as class pets by giving a class a raw fish. The item type can, just like with pet wolves, be configured in the global-settings of the config-file.
- The hellhounds setting has been removed. Sorry, fiery puppy lovers.
- Temporary permissions are now supported in the Things API. This means that temporary permissions can now be used in place of items, money, commands, etc. by using the prefix `perm:`. While this isn't particularly useful in and of itself, it does give way to other neat features...
- Upgrade waves now use the Things API. This means that it is now possible to grant economy money or run commands as upgrades for a class. Note that the previous "weapns upgrade" behavior no longer works - only armor upgrades replace existing items, while weapon upgrades are simply added to the inventory. The weapon replacement was a bit wonky and didn't always work as expected, so now it just works consistently by always adding instead of replacing.
- The give-all-items flag in Upgrade waves has been removed. Upgrade waves now always grant all items listed.
- The MobArena item syntax now supports named potion types and dye colors in addition to the numeric values.
- Using numeric IDs for items and data values now results in a warning from MobArena, encouraging the use of equivalent string IDs instead. This should help prepare a bit for the changes in 1.13.
- In 1.8, splash potions are now denoted as splash_potion just like in 1.11 and up.
- Boss bars are now properly cleared from players that die or leave the arena during a boss fight.
- Scoreboards now correctly restore when leaving an arena that uses scoreboards.
- Potion effects are now correctly removed from players who switch from a class that has potion effects to a class that uses class chests but has no potion effects. This is a very specific edge case that probably affected no one, but at least it's fixed now.
- MobArena will now check the server version upon startup, and if the build is incompatible with the server version, it throws a more human-readable error. This should make it easier for people to spot if they accidentally downloaded the wrong build.
- The five default classes have been modernized with string IDs for all items, and a couple of them have been given starting potion effects. If you want to use the updated classes, you can find them in the github repo or you can let MobArena generate a new config-file and copy them over from there.
- A large portion of old setup have been removed. These commands all had extremely specialized functionality that required some semi-nasty coupling throughout the codebase, but none of them were necessary, since they mostly revolved around "shortcuts" to the config-file. By removing them, we now have a much easier path towards Minecraft 1.13 and awesome reworks in MobArena. The removed commands: `addclassperm`, `removeclassperm`, `listclassperms`, `setclassprice`, `setclass`, `removeclass`.

Thanks to:
- ArthurPendragon and other folks who contributed bug reports for the fixes in this version
- Nesseley for help with test of dev builds

## [0.101] - 2018-06-23
- MobArena now has bStats! This will greatly aid the development efforts in terms of figuring out which features are the most popular and which server implementations MobArena runs on. With this kind of data, the community can be involved in the development of the plugin by simply letting bStats speak for them. This "passive" involvement will allow us to trim away obsolete features and focus development efforts in areas that will benefit the most people.
- Join, leave, and info signs have been added. The signs can be used to display live information about an arena via a small templating engine.
- Bosses can now have their health displayed as a boss bar, a chapter title (on hit), or as a custom name. Note that boss bars are only available in 1.11+.
- Players can now dismount and re-mount their horses. Only the owner of a horse may mount it.
- Potion effects can now be added to classes.
- Potion effects can now be added to all wave types.
- The /ma config reload command now reloads the announcements-file (and the new sign templates file) along with the config-file.
- Player inventories are now correctly cleared when leaving the arena on Minecraft 1.8 servers.

Thanks to:
- NathanWolf for the initial pull request for join and leave signs
- PrinceIonia, Nesseley, and KyleGallacher for testing the dev builds

## [0.100.2] - 2018-05-13
- Vault is now optional again. In the previous patch (v0.100.1), Vault was accidentally made a requirement for MobArena to even start. This has been fixed.
- If Vault is not found, or if a compatible economy plugin is not found, MobArena now informs about this with an INFO message instead of a WARNING. However, if MobArena parses a money thing (like '$10') in this state, it will print an ERROR to the server log.

## [0.100.1] - 2018-05-04
- Fixed the plugin-wide economy bug introduced in v0.100. This bug would result in entry fees and class prices being "too expensive" for everyone regardless of balance, and economy rewards would not get paid out.
- Fixed a bug where entry fees weren't actually taken from players upon joining an arena.
- Fixed a bug where item/block entry fees couldn't be refunded. The items/blocks are now refunded *after* the player inventory has been restored, so the player inventory no longer overwrites the refunded items/blocks.
- Entry fees are now only refunded when leaving the arena while in the lobby. As soon as the arena starts, that price is paid, and there are no refunds.

## [0.100] - 2018-05-03
- MobArena now requires Java 8.
- The long-standing per-world inventory bug, by which players sometimes lose their inventories, has finally been fixed with a full rewrite of the join/leave process. It was discovered, however, that for the plugin PerWorldInventory, gamemode-specific inventories MUST be disabled. If not, PerWorldInventory will restore the gamemode-specific inventory after MobArena has already tried to take a backup of the existing inventory, and as a result, it will wipe it when the player leaves.
- Another long-standing bug, the `/back` command exploit, has also finally been fixed. It is no longer possible to use the `/back` command to get back into the arena after dying (unless the player is op or has the mobarena.admin.teleport permission).
- Class items and armor now support the Things API. This means that other plugins can now register simple parser interfaces to provide armor and items (which can be arbitrary things) to MobArena classes. One such plugin is Magic by Nathanwolf. Try it out!
- Item display names are now supported in reward strings, so custom items or named items will be displayed with the custom name if one is set.
- A hardcoded boss kill message has been split into two new announcments in the announcements-file, wave-boss-killed and wave-boss-reward-earned.
- Boss rewards are now granted again. This bug slipped through the cracks during the Things API rewrite.
- TNT placed from the off-hand now correctly depletes.
- The "My Items" class is now a little restricted to prevent some exploiting: ender pearls, ender chests, shulker boxes, and shulker shells are filtered out of the inventory within the arena (but the items are safely backed up with the rest of the player's items).
- A bug where using the /give command would hang the server and throw an error has been fixed.
- Arena names are now case insensitive in commands, meaning if your arena is called 'FirePit', you can type '/ma j firepit' (or even '/ma j FiRePiT') to join it.
- An error thrown when trying to restore scoreboards for disconnecting players has been fixed.
- Reloading config-files with the /ma config reload command is now MUCH faster (from over 10 seconds to under 100 milliseconds on some setups).
- The Things API has seen quite a few improvements. It is now possible for other plugins to register custom item parsers for use in the built-in item parser, making it even simpler to integrate with classes and rewards.

Thanks to:
- jwflory for porting and cleaning up documentation
- ctmartin for help with wrangling the build-up of github issues
- NathanWolf for help with improvements to the Things API
- Pugabyte for performance and permissions testing
- Nesseley, Sikatsu, and ScuroK for testing the inventory bug fix
- Swatacular for help with testing bug fixes
- Haileykins for contributions to the code base

[Unreleased]: https://github.com/garbagemule/MobArena/compare/0.104.2...HEAD
[0.104.2]: https://github.com/garbagemule/MobArena/compare/0.104.1...0.104.2
[0.104.1]: https://github.com/garbagemule/MobArena/compare/0.104...0.104.1
[0.104]: https://github.com/garbagemule/MobArena/compare/0.103.2...0.104
[0.103.2]: https://github.com/garbagemule/MobArena/compare/0.103.1...0.103.2
[0.103.1]: https://github.com/garbagemule/MobArena/compare/0.103...0.103.1
[0.103]: https://github.com/garbagemule/MobArena/compare/0.102...0.103
[0.102]: https://github.com/garbagemule/MobArena/compare/0.101...0.102
[0.101]: https://github.com/garbagemule/MobArena/compare/0.100.2...0.101
[0.100.2]: https://github.com/garbagemule/MobArena/compare/0.100.1...0.100.2
[0.100.1]: https://github.com/garbagemule/MobArena/compare/0.100...0.100.1
[0.100]: https://github.com/garbagemule/MobArena/compare/0.99...0.100
