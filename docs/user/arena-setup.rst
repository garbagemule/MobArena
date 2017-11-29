###########
Arena setup
###########

**On this page:** \* `Building an arena <#building-an-arena>`__ \*
`About regions, warps, etc. <#about-regions-warps-etc>`__ \* `Setup
Mode <#setup-mode>`__ \* `Commands <#setup-mode-commands>`__ \* `The
Toolbox <#the-toolbox>`__ \* `Regions <#regions->`__ \*
`Warps <#warps->`__ \* `Spawnpoints <#spawnpoints->`__ \*
`Chests <#chests->`__

Building an arena
=================

A well-built arena consists of a **lobby** for class selection, an
**arena floor**, and either a **spectator area** or an **exit point**.
Let's go over each one...

**Lobby:** The lobby is where your players will be selecting their
classes when they join an arena. A well-formed lobby consists of *a sign
for each class*, which the players will click to choose a given class,
as well as an *iron block*, which the players will click when they are
ready to start. The signs must have the name of the class on the first
line (case-sensitive), but you can put whatever you want on the
remaining three lines. Note that MobArena *does not* "register" class
sign creation, so you *will not* get a confirmation message (if you do,
it is another plugin interfering).

**Arena floor:** This is where your players will be fighting monsters.
The arena floor should be enclosed in walls, and possibly with a roof,
such that the players and monsters have no way of getting out. This
ensures that players won't be able to just wander out of the arena.

**Spectator area:** When players want to spectate a session, they can
use the ``/ma spec`` command to get warped to the spectator area of an
arena. This is also where arena players are warped to when they die, if
``spectate-after-death: true`` in the config-file. The area should be
designed so that it *is not* possible for the spectators to wander out
(into the arena or away from the area), because spectators are
invincible.

**Exit point:** Upon typing ``/ma leave``, arena players and spectators
will be warped to the location they joined from, unless the arena has an
exit warp (optional). Using exit points, it is possible to control
exactly where players go after a session.

About regions, warps, etc.
--------------------------

Once our arena is built, it's time to set up the MobArena regions,
warps, and points. Before we do so, let's take a look at what these
things are and what MobArena uses them for...

**Regions:** An arena needs an *arena region*, and optionally a *lobby
region*. MobArena uses the arena region to make sure that players aren't
cheating (by kicking them from the arena if they move outside the
region), and to make sure that only MobArena's own mobs are spawned
inside of it. MobArena is extremely xenophobic (afraid of strangers), so
it tries its best to keep unwanted mobs out of the sessions. Regions are
set using the Region tools.

**Warps:** When players join a MobArena session, they are teleported to
the *lobby warp* inside the lobby, where they will *pick their class*
and ready up using the *ready block* (block of iron). When everyone is
ready, the players are teleported to the *arena warp* inside of the
arena. Spectators will be teleported to the *spectator warp* in the
spectator area, and when players leave an arena, they will either be
teleported to where they joined from, or to the *exit warp*, if it has
been set up. Warps are set using the Warps tool.

**Spawnpoints:** The *spawnpoints* of an arena are the locations where
monsters can spawn from. MobArena will only spawn monsters from
spawnpoints that have players nearby (in a 15-block radius). Note that
the number of mobs spawned doesn't depend on how many spawnpoints an
arena has - the number of mobs is determined by a formula (see
[[Formulas]]) that only involves the wave number and player count
(unless you use fixed amounts). Spawnpoints are added using the
Spawnpoints tool.

**Containers:** The *containers* of an arena are locations of chests,
dispensers, etc. which contain items that will be renewed when the arena
ends. Only registered containers will have their contents renewed, so it
is not enough to simply put a chest in the arena - it also needs to be
registered using the Chests tool.

Setup Mode
==========

We will set up the arena using *Setup Mode*. When we enter Setup Mode,
MobArena will temporarily store our inventory and give us a set of
golden tools (the *Toolbox*), each with a different function. We will
also be able to *fly*, making moving around the arena a lot easier.
Last, but not least, we will *not* be able to chat or type normal
commands while in Setup Mode, because Setup Mode starts an *isolated
conversation* with us.

**To enter Setup Mode:** Type ``/ma setup <arena>``, where ``<arena>``
is the name of an arena. Note that if you only have one arena, you don't
have to type the arena name. If the arena you want to set up has not yet
been created, first type ``/ma addarena <arena>`` to create it.

**To leave Setup Mode:** Type ``done``.

Note that if you have just installed MobArena, there will be a premade
arena-node called ``default`` in the config-file already. If you want a
different name, create a new arena first, and then remove the default
arena by typing ``/ma delarena default``.

In the next section, we will take a look at the different commands
available in Setup Mode...

Setup Mode Commands
-------------------

Setup Mode is an *isolated conversation*, which means Setup Mode will
intercept everything we type. The reason for this is that it makes the
commands in Setup Mode shorter (e.g. ``exp`` instead of
``/ma expandregion``), and it also prevents us from accidentally typing
commands from other plugins.

Below is a list of all the commands we can use in Setup Mode. Many of
the commands have short-hand aliases which might make them even faster
to type out. As an example, the ``expand`` command has the alias
``exp``. Sometimes it's easier to remember the longer names, but the
short-hand aliases are provided for faster setup.

-  | ``done``
   | Leave Setup Mode.
   | **Aliases:** ``end`` ``stop`` ``done`` ``quit``

-  | ``help``
   | Display help screen.
   | **Aliases:** ``?`` ``h``

-  | ``missing``
   | Display a list of missing (mandatory) regions, warps, and points.
   | This command is useful if you have forgotten how far you are in the
     setup process, and what you still need to set up.
   | **Aliases:** ``miss``

-  | ``expand <region> <amount> <direction>``
   | Expand a region by some amount in some direction.
   | Valid regions: ``ar`` for the arena region, or ``lr`` for the lobby
     region
   | Valid amounts: positive integers (whole numbers)
   | Valid directions: ``up``, ``down``, or ``out``
   | **Example:** ``expand ar 5 up``
   | **Aliases:** ``exp``

-  | ``show [<region>|<warp>|<point>]``
   | Show a region, warp, or point(s) as red wool blocks.
   | Valid regions: ``ar`` for the arena region, or ``lr`` for the lobby
     region
   | Valid warps: ``arena``, ``lobby``, ``spec``, ``exit``
   | Valid points: ``spawns`` (or just ``sp``) for spawnpoints,
     ``chests`` (or just ``c``) for chests
   | **Example:** ``show sp``

The Toolbox
-----------

The Toolbox is a set of golden tools, where each tool has a specific
function. We will use these tools to set up the regions, warps, and
points of our arena. The tools are used by left- or right-clicking a
block while holding them, and the actions vary depending on the specific
tool.

Note that the functions of a tool are described in the *item tooltip*,
which we can see by opening up our inventory and hovering our mouse over
the tools.

Regions |Region Tools|
~~~~~~~~~~~~~~~~~~~~~~

The arena and lobby regions can be set up using the Region tools (axe).
There are two golden axes in the Toolbox, and they both behave the same,
except that one is for the arena region, and the other is for the lobby
region. The tools are named accordingly, and they will display either
"Arena Region" or "Lobby Region" above the quickbar when we select them.

Note that the behavior of the Region tools is similar to that of the
WorldEdit wand (wooden axe), so if you are familiar with defining
regions in WorldEdit, the Region tools should feel familiar.

| **Left-click:** Set the first point to be the location of the target
  block
| **Right-click:** Set the second point to be the location of the target
  block

Upon setting both points, the region will be defined. Type ``show ar``
(or ``show lr``) to check that the region spans the desired area. If the
region is a little bit too small, use the ``expand`` command (see above)
to make it a little bigger.

**Note:** The region MUST look like a box (3D) and not a rectangle (2D).
If the region is just a rectangle, your arena will not work correctly.
The same applies if the arena floor is not fully contained in the box,
so make sure to expand the region down a block or two to be sure.

Warps |Warps Tool|
~~~~~~~~~~~~~~~~~~

The arena, lobby, spectator, and exit warps can be set up using the
Warps tool (hoe). The tool knows about all the warps, and we have to
*cycle through them* to select the warp we want to place. The default
selected warp is the *arena warp*.

| **Left-click:** Set the currently selected warp on top of the target
  block
| **Right-click:** Cycle between available warps

When left-clicking, the selected warp will be set to the top of the
clicked block. The pitch (up and down) will be set to 0, which means
when players are teleported, they will be looking "straight ahead". The
yaw (rotation, left/right) will be set to whatever direction we are
facing, when we set the warp. This means that we need to rotate
ourselves to be looking in the direction we want the players to look in
when they are teleported to the point.

**Note:** The arena, lobby, and spectator warps are all required. The
exit warp is optional.

Spawnpoints |Spawnpoints Tool|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The spawnpoints can be set up using the Spawnpoints tool (sword). The
tool knows about all the spawnpoints of the arena, and allows us to
remove existing ones or add new ones as we please.

| **Left-click:** Add a spawnpoint on top of the target block
| **Right-click:** Remove the spawnpoint on top of the target block (if
  the spawnpoint exists)

When left-clicking, a spawnpoint will be added on top of the clicked
block, if one doesn't already exist. Right-clicking a block will remove
a spawnpoint on that block, if one exists.

**Note:** Due to limitations and "bugs" in Minecraft, it is not possible
(read: viable) to spawn mobs further than 15 blocks away from a player,
and still make it target and attack the player naturally. Therefore, it
is recommended to place many spawnpoints, so that every single location
in the arena is within a 15-block radius of a spawnpoint. If a player is
not within 15 blocks of any spawnpoint, MobArena will print a warning to
the console with the coordinates. If no players are within 15 blocks of
any spawnpoint, MobArena will default to using all spawnpoints, which
may result in mobs spawning far away from players, so they will have to
run around searching for them.

Chests |Chests Tool|
~~~~~~~~~~~~~~~~~~~~

The chests and containers can be set up using the Chests tool (spade).
It works very much like the Spawnpoints tool, but requires that the
clicked block is a valid container.

| **Left-click:** Register the clicked container (if it wasn't
  registered)
| **Right-click:** Unregister the clicked container (if it was
  registered)

When left-clicking a container, MobArena will register the container (if
it wasn't registered already), such that when an arena session ends, the
container will be restored to contain whatever was in it when the arena
began. Right-clicking a container will unregister it.

.. |Region Tools| image:: http://puu.sh/4wwCH.png
.. |Warps Tool| image:: http://puu.sh/4wwIB.png
.. |Spawnpoints Tool| image:: http://puu.sh/4wwCJ.png
.. |Chests Tool| image:: http://puu.sh/4wwIF.png

