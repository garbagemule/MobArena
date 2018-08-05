# Changelog
All notable changes to the project are recorded in this document in a user-friendly format.
The format is loosely based on [Keep a Changelog](https://keepachangelog.com/).

Each version in the document contains only user-facing changes (including API changes).
However, each version in the document is also a link, and clicking a version link reveals all commits that went into the given version.
The release notes published on Spigot and DBO are derived from this document.

The "Unreleased" section contains changes that have not yet been released.
These changes will (most likely) be included in the next version.


## [Unreleased]
- It is now possible to add a fixed delay (in seconds) between waves with the new per-arena setting `next-wave-delay`.
- Right-clicking is now allowed in the lobby. This makes it possible to activate blocks like buttons and levers.
- Snow and ice no longer melts in arenas.

Thanks to:
- PrinceIonia and Nesseley for help with test of dev builds

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

[Unreleased]: https://github.com/garbagemule/MobArena/compare/0.102...HEAD
[0.102]: https://github.com/garbagemule/MobArena/compare/0.101...0.102
[0.101]: https://github.com/garbagemule/MobArena/compare/0.100.2...0.101
[0.100.2]: https://github.com/garbagemule/MobArena/compare/0.100.1...0.100.2
[0.100.1]: https://github.com/garbagemule/MobArena/compare/0.100...0.100.1
[0.100]: https://github.com/garbagemule/MobArena/compare/0.99...0.100
