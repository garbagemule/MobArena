########
Commands
########

This page documents the commands available in MobArena, both for players and
administrators.

*Note*: Parentheses (``()``) around an parameter means it's optional (i.e. not
required to work).


***************
Player commands
***************

Any player on the server can use these commands by default.

+---------------------------+----------------------------+---------------------+
| Command                   | Description                | Alias               |
+===========================+============================+=====================+
| ``/ma join (<arena>)``    | Join arena with given name | ``/ma j (<arena>)`` |
+---------------------------+----------------------------+---------------------+
| ``/ma leave``             | Leave current arena or     | ``/ma l``           |
|                           | spectator area             |                     |
+---------------------------+----------------------------+---------------------+
| ``/ma notready``          | List players who are not   |                     |
|                           | ready in arena lobby       |                     |
+---------------------------+----------------------------+---------------------+
| ``/ma spec (<arena>)``    | Enter an arena's spectator | ``/ma s (<arena>)`` |
+---------------------------+----------------------------+---------------------+
| ``/ma arenas``            | List all arenas. Green     |                     |
|                           | names are enabled, gray    |                     |
|                           | names are disabled         |                     |
+---------------------------+----------------------------+---------------------+
| ``/ma players (<arena>)`` | List all players in an     |                     |
|                           | area                       |                     |
+---------------------------+----------------------------+---------------------+
| ``/ma class <class>``     | Manually choose a class in |                     |
|                           | an arena lobby (instead of |                     |
|                           | punching sign)             |                     |
+---------------------------+----------------------------+---------------------+


**************
Admin commands
**************

Players with OP privileges or assigned permissions can use these commands.

+-----------------------------+------------------------------------------------+
| Command                     | Description                                    |
+=============================+================================================+
| ``/ma enable (<arena>)``    | Enable MobArena (optionally a specific arena)  |
+-----------------------------+------------------------------------------------+
| ``/ma disable``             | Disable MobArena (optionally a specific arena) |
+-----------------------------+------------------------------------------------+
| ``/ma force end (<arena>)`` | Forcefully end all arenas or a specific arena  |
+-----------------------------+------------------------------------------------+
| ``/ma force start <arena>`` | Forcefully start an arena (players that aren't |
|                             | ready are removed from arena)                  |
+-----------------------------+------------------------------------------------+
| ``/ma notready <arena>``    | List all players in an arena that aren't ready |
+-----------------------------+------------------------------------------------+
| ``/ma restore <player>``    | Restore a player's inventory (if possible)     |
+-----------------------------+------------------------------------------------+
| ``/ma config reload``       | Reload config file into memory                 |
+-----------------------------+------------------------------------------------+


**************
Setup commands
**************

Players with OP privileges or assigned permissions can use these commands.

+-----------------------------+------------------------------------------------+
| Command                     | Description                                    |
+=============================+================================================+
| ``/ma setup <arena>``       | Enter Setup Mode for an arena (see             |
|                             | :doc:`arena-setup` for more info)              |
+-----------------------------+------------------------------------------------+
| ``/ma addarena <arena>``    | Create new arena node in current world         |
+-----------------------------+------------------------------------------------+
| ``/ma delarena <arena>``    | Delete arena with given name                   |
+-----------------------------+------------------------------------------------+
| ``/ma editarena <arena>``   | Toggle Edit Mode for an arena                  |
+-----------------------------+------------------------------------------------+
| ``/ma editarena <arena>``   | Turn Edit Mode on or off for an arena          |
| ``[true|false]``            |                                                |
+-----------------------------+------------------------------------------------+
| ``/ma setting <arena>``     | List per-arena settings for an arena           |
+-----------------------------+------------------------------------------------+
| ``/ma setting <arena>``     | Check current value of a setting for an arena  |
| ``<setting>``               |                                                |
+-----------------------------+------------------------------------------------+
| ``/ma setting <arena>``     | Change a setting for an arena to given value   |
| ``<setting> <value>``       |                                                |
+-----------------------------+------------------------------------------------+
| ``/ma checkspawns``         | Show all spawnpoints in arena you are standing |
|                             | in as red wool blocks (helpful to check        |
|                             | spawnpoint coverage)                           |
+-----------------------------+------------------------------------------------+
| ``/ma classchest <class>``  | Create a linked class chest for a class (see   |
|                             | :doc:`class-chests` for more info)             |
+-----------------------------+------------------------------------------------+
| ``/ma auto-generate``       | Auto-generate new arena with given name        |
| ``<arena>``                 | (generated directly below player)              |
+-----------------------------+------------------------------------------------+
| ``/ma auto-degenerate``     | Degenerate an auto-generated arena with given  |
| ``<arena>``                 | name                                           |
+-----------------------------+------------------------------------------------+


***********
Permissions
***********

See :doc:`permissions`.
