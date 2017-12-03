###########
Arena setup
###########

This page explains how to set up an arena, from defining it to configuring it.


*****************
Building an arena
*****************

There are four key parts to an arena:

#. Lobby
#. Arena floor
#. Spectator area
#. Exit point

Lobby
=====

A lobby is where players choose a class before joining an arena. It's also the
"waiting area" before a new match begins.

Lobbies have two requirements:

- **Class selection signs**: Clicked to select a class
- **Iron block**: Clicked to mark self as "ready"

Class selection signs must have the name of the class on the first line
(case-sensitive). The last three lines are not checked and they can have any
text. You will not receive a confirmation message after making a new sign since
MobArena does not register the creation of class selection signs.

Arena floor
===========

An arena floor is where the action happens. Players fight through mob waves on
the arena floor. How the arena floor looks is up to you, but we recommend these
minimum requirements:

- Closed in by walls on all sides
- Have a ceiling / roof

This prevents players and mobs from escaping and also blocks players from
wandering out of the arena with class items.
ander out of the arena.

Spectator area
==============

A spectator area lets non-players watch an on-going match. The ``/ma spec``
command teleports a player into the spectator area. If configured, [#]_ players
warp into the spectator arena when they die.

Design the area so spectators cannot escape the spectator area, since they
invincible. Spectators should *not* enter the arena floor or exit the spectator
area on foot.

.. [#] Set ``spectate-after-death`` to ``true`` in the config file to force
   players to the spectator area after dying

Exit point
==========

When players leave the arena or when the last player standing dies, arena
players and spectators teleport to the location they joined from. Optionally, an
arena can have an exit warp. This controls where players go after leaving a
match.


****************
Defining regions
****************

When an arena is ready, it must be **defined** into…

#. Regions
#. Warps
#. Spawnpoints
#. Containers

Regions
=======

Arenas **must** have an arena region and optionally a lobby region. Regions are
set using the Regions tools. MobArena uses the arena region to…

- Stop cheating by kicking players if they leave the arena region
- Only spawn MobArena mobs inside arena region

Warps
=====

Players teleport to different warps for different events. There are four warps
used in MobArena:

- **Lobby warp**: Warp location for players joining a new match; leave when all
  players "ready up" or match countdown timer ends
- **Arena warp**: Warp location for players to spawn in the arena floor
- **Spectator warp**: Warp location for spectators to watch an on-going match
- **Exit warp**: Optional warp location for players to teleport to after a match
  finishs or they leave the arena

Spawnpoints
===========

Mobs spawn at the spawnpoint(s) of an area. MobArena only uses spawnpoints in a
*15-block radius* from any player. An arena can have multiple spawnpoints.
Spawnpoints are added using the Spawnpoints tool.

The number of mobs spawned is not determined by the number of spawnpoints, but
actual formulas. See :doc:`wave-formulas` for more information.

Containers
==========

Containers are locations of chests, dispensers, or other containers with
renewable contents. Any containers added to an arena must be registered using
the Chests tool.


**********
Setup Mode
**********

Configure a new arena with *Setup Mode*. Setup Mode is a special mode that
temporarily stores inventory and gives an administrator a set of golden tools.
The golden tools are called the **Toolbox**.

Flying is enabled to simplify arena setup. Talking in server chat is also
disabled because Setup Mode starts an isolated conversation with the
administrator (explained below).

- **Create a new arena**: ``/ma addarena <arena name>``
- **Enter Setup Mode**: ``/ma setup <arena name>`` [#]_
- **Leave Setup Mode**: ``done`` (no slash)
- **Delete an arena**: ``/ma delarena <arena name>`` [#]_

.. [#] If you only have one arena, you don't have to specify the arena name
.. [#] An arena named ``default`` is created on first use. You can remove this
   arena if you want to use an arena with a different name.

Setup Mode commands
===================

Setup Mode is an *isolated conversation*, which means Setup Mode intercepts
everything an administrator types. This makes commands in Setup Mode shorter and
prevents accidental use of other plugins.

Below is a list of all commands in Setup Mode:

+-------------------+-------------------------------------+------------+
| Command           | Description                         | Aliases    |
+===================+=====================================+============+
| done              | Leave Setup Mode                    | end, stop, |
|                   |                                     | done, quit |
+-------------------+-------------------------------------+------------+
| help              | Display help screen                 | ?, h       |
+-------------------+-------------------------------------+------------+
| missing           | Display list of missing (mandatory) | miss       |
|                   | regions, warps, spawnpoints. Useful |            |
|                   | to check what is left to set up.    |            |
+-------------------+-------------------------------------+------------+
| expand            | Expand region by some amount in a   | exp        |
| ``<region>``      | given direction. *Example*:         |            |
| ``<amount>``      | ``expand ar 5 up``                  |            |
| ``<direction>``   |                                     |            | 
+-------------------+-------------------------------------+------------+
| show              | Show a region, warp, spawnpoint(s), | N/A        |
| ``[<region>|``    | or container as red wool blocks.    |            |
| ``<warp>|``       | *Example*: ``show sp``              |            |
| ``<spawnpoint>|`` |                                     |            |
| ``<container>]``  |                                     |            |
+-------------------+-------------------------------------+------------+

- **Valid regions**: ``ar`` (arena region), ``lr`` (lobby region)
- **Valid amounts**: Any positive integer (i.e. whole number)
- **Valid directions**: ``up``, ``down``, ``out``
- **Valid warps**: ``arena``, ``lobby``, ``spec``, ``exit``
- **Valid spawnpoints**: ``spawns`` (or ``sp``)
- **Valid containers**: ``chests`` (or ``c``)

Toolbox
=======

The Toolbox is a set of golden tools. Each tool has a specific functions. We use
them to set up regions, warps, spawnpoints, and containers. Toolbox tools are
used with either a left- or right-click.

Tool functions are also described in the *item tooltip* in your inventory.

Region tools
------------

|r-icon|

Arena and lobby regions are defined with Region tools (golden axes). There are
two golden axes in the Toolbox. One is for *arena setup* and the other is for
*lobby setup*. The tools are named accordingly.

Region tools behave similarly to the WorldEdit wand (wooden axe). If you are
familiar with regions in WorldEdit, Region tools should feel familiar.

- **Left-click**: Sets first point on clicked block
- **Right-click**: Sets second point on clicked block

When both points are set, the region is defined. ``show ar`` (or ``show lr``)
lets you check the region spans the desired area. If the region is too small,
use the ``expand`` command (see above) to make it bigger.

The region must be three-dimensional (like a box) and not two-dimensional (flat
rectangle). Make sure your arena floor is contained in the region selection
(expanding a block or two below the floor is recommended).

Warp tool
---------

|w-icon|

All warps are defined using the Warp tool (golden hoe). The tool defines any of
the four types of warps depending which one is selected.

- **Left-click:** Set selected warp type on top of clicked block
- **Right-click:** Cycle between warp types

A selected warp is placed on top of the clicked block. The direction you are
looking is also taken into account.

Arena, lobby, and spectator warps are required. An exit warp is optional.

Spawnpoint tool 
---------------

|s-icon|

Spawnpoints are set up with the Spawnpoint tool (golden sword). The tool allows
an administrator to set or remove spawnpoints for mobs.

- **Left-click:** Add spawnpoint on top of clicked block
- **Right-click:** Remove spawnpoint on top of clicked block (if one exists)

Many spawnpoints recommended
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A **high number of spawnpoints** is recommended. Mobs only spawn at spawnpoints
within 15 blocks of a player. Every area in the arena should have one or more
spawnpoints in a 15 block radius from each other.

If a player is not within 15 blocks of a spawnpoint, MobArena prints a warning
to the console with coordinates. If no players are within 15 blocks of a
spawnpoint, MobArena uses a random spawnpoint. This means mobs may spawn far
away from players.

Container tool
--------------

|c-icon|

Containers are set up with the Container tool (golden shovel). It works like the
Spawnpoint tool, but checks that the clicked block is a valid container.

- **Left-click:** Register clicked container (if not registered)
- **Right-click:** Unregister clicked container (if registered)

At the end of a match, a container is restored to its contents from the
beginning of the match.

.. |r-icon| image:: http://puu.sh/4wwCH.png
.. |w-icon| image:: http://puu.sh/4wwIB.png
.. |s-icon| image:: http://puu.sh/4wwCJ.png
.. |c-icon| image:: http://puu.sh/4wwIF.png

