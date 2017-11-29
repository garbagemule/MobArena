########
Commands
########

Note: All MobArena commands start with ``/ma``, and most of them can be
disabled using [[Permissions]].

If you are looking for ways to disable non-MobArena commands, look in
the ``global-settings``-section of the [[config-file setup page\|setting
up the config-file]].

Player commands
~~~~~~~~~~~~~~~

These commands can be issued by all players. They include joining,
leaving, spectating and listing arenas as well as listing players.

-  ``/ma join (<arena>)`` or ``/ma j (<arena>)`` - Join the arena with
   the given name.
-  ``/ma leave`` or ``/ma l`` - Leave the current arena, or the
   spectator area.
-  ``/ma notready`` - Get a list of all players who aren't ready.
-  ``/ma spec (<arena>)`` or ``/ma s (<arena>)`` - Spectate the arena
   with the given name.
-  ``/ma arenas`` - Get a list of all arenas. Green names are enabled,
   gray names are disabled.
-  ``/ma players`` - Get a list of all arena players.
-  ``/ma players <arena>`` - Get a list of arena players in the
   specified arena.
-  ``/ma class <class>`` - While in the lobby, manually pick the given
   class instead of punching signs.

Admin commands
~~~~~~~~~~~~~~

Only ops and the console can issue these commands. They include
forcefully starting or ending arenas, enabling/disabling individual
arenas or MobArena entirely.

-  ``/ma enable`` - Enable MobArena.
-  ``/ma disable`` - Disable MobArena.
-  ``/ma enable <arena>`` - Enable the arena with the specified name.
-  ``/ma disable <arena>`` - Disable the arena with the specified name.
-  ``/ma force end`` - Forcefully end all arenas.
-  ``/ma force end <arena>`` - Forcefully end the arena with the
   specified name; forces all players to leave.
-  ``/ma force start <arena>`` - Forcefully start the arena with the
   specified name; forces all players who aren't ready to leave.
-  ``/ma notready <arena>`` - Get a list of all players in the given
   arena who aren't ready.
-  ``/ma restore <player>`` - Restore the inventory of the player with
   the given name, if possible.
-  ``/ma config reload`` - Reload the config-file into memory. This is
   useful if changes are made in the config-file while the server is
   running.

Setup commands
~~~~~~~~~~~~~~

Only ops (and the console, if it makes sense) can issue these commands.
They include setting warp points, spawnpoints and region points.

-  ``/ma setup <arena>`` - Enter [[Setup Mode\|Arena Setup]] for the
   given arena.
-  ``/ma addarena <arena>`` - Create a new arena-node in the current
   world.
-  ``/ma delarena <arena>`` - Delete the arena with the given name.
-  ``/ma editarena <arena>`` - Toggle Edit Mode for the given arena.
-  ``/ma editarena <arena> [true|false]`` - Turn on or off Edit Mode for
   the given arena.
-  ``/ma setting <arena>`` - List all per-arena settings for the given
   arena.
-  ``/ma setting <arena> <setting>`` - Check the current value of the
   given setting for the given arena.
-  ``/ma setting <arena> <setting> <value>`` - Set the value of the
   given setting for the given arena to the given value.
-  ``/ma checkspawns`` - Shows the spawnpoints (of the arena you are
   currently standing in) which cover your current location as red wool
   blocks. This command can be used to check if specific points in your
   arena are actually covered by spawnpoints or not.
-  ``/ma classchest <class>`` - While looking at a chest, link the chest
   to the given class as a [[linked class chest\|Class Chests]]. Linking
   a class chest means MobArena will always copy the items from the
   linked chest, regardless of any local class chests in arena lobbies.
   This is useful if you want a global class chest for a class.
-  ``/ma auto-generate <arena>`` - Auto-generate a new arena with the
   given name. The arena will be generated just below the player in the
   world they are standing in.
-  ``/ma auto-degenerate <arena>`` - Degenerate the arena with the given
   name, effectively restoring the patch that was "bulldozed" with the
   auto-generator.
