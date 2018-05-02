##########################
Setting up the config file
##########################

**On this page:** \*
`Overview <./Setting-up-the-config-file#wiki-an-overview>`__ \*
```global-settings`` <./Setting-up-the-config-file#wiki-global-settings>`__
\* ```classes`` <./Setting-up-the-config-file#wiki-classes>`__ \* `Bring
your own items <./Setting-up-the-config-file#bring-your-own-items>`__ \*
`Slot-specific armor
nodes <./Setting-up-the-config-file#slot-specific-armor-nodes>`__ \*
`Price <./Setting-up-the-config-file#price>`__ \* `Unbreakable
items <./Setting-up-the-config-file#wiki-unbreakable-weaponsarmor>`__ \*
`Per-class
permissions <./Setting-up-the-config-file#wiki-per-class-permissions>`__
\* `Pet classes <./Setting-up-the-config-file#wiki-pet-classes>`__ \*
`Mounts <./Setting-up-the-config-file#wiki-mounts>`__ \*
```arenas`` <./Setting-up-the-config-file#wiki-arenas>`__ \*
```settings`` <./Setting-up-the-config-file#wiki-settings>`__ \*
```waves`` <./Setting-up-the-config-file#wiki-waves>`__ \*
```rewards`` <./Setting-up-the-config-file#wiki-rewards>`__ \*
```coords`` <./Setting-up-the-config-file#wiki-coords>`__

An Overview
~~~~~~~~~~~

*Note: When editing the config-file, you **MUST use spaces for
indentation**! Using tabs instead of spaces will give you errors!*

The config-file, ``plugins/MobArena/config.yml``, consists of 3
sections: ``global-settings``, ``classes``, and ``arenas``. The default
config-file that is generated when MobArena is first loaded looks
something like this:

::

    [...]
    global-settings:
        update-notification: true
        enabled: true
        allowed-commands: /list
        prefix: '&a[MobArena] '
    classes:
        Knight:
            items: diamond_sword, grilled_pork:2
            armor: 306,307,308,309
        Archer:
            items: wood_sword, bow, arrow:128, grilled_pork
            armor: 298,299,300,301
        [...]
    arenas:
        default:
            settings:
                prefix: ''
                world: Tundra
                enabled: true
                protect: true
                clear-wave-before-next: false
                [...]
            waves:
                [...]
            rewards:
                [...]

Note about notation: ``[true|false]`` means the setting must be "true or
false", either or. ``<time>`` means the setting must be an amount of
time (in seconds or server ticks), always a whole number, and always
``0`` or greater. ``<amount>`` is similar to time.

global-settings
---------------

The ``global-settings`` are few, but important. Note that if
``enabled: false``, no arenas can be joined, regardless of their
individual ``enabled`` status.

-  ``enabled: [true|false]`` - This determines if MobArena is enabled or
   not. If set to ``false``, players will not be able to join any arenas
   at all, regardless of what the arenas' individual statuses are.
-  ``update-notification: [true|false]`` - If true, MobArena will send a
   message to ops when they log on if a new version of MobArena is
   available.
-  ``allowed-commands: <com1>, <com2>, ...`` - A comma-separated list of
   the commands that players are allowed to use while in the lobby
   and/or arena. This is useful if you don't want players to use
   teleport-commands, flying carpets, kill commands, etc. If you write
   the command WITH its forward-slash, the entire command and all
   "sub-commands" will be allowed. For instance, writing ``/kill`` will
   allow both ``/kill``, ``/kill Sausageman22`` and ``/kill Notch``.
   Writing the command WITHOUT its forward-slash will allow only that
   specific command or "sub-command". Writing ``kill`` will thus ONLY
   allow ``/kill``, but not ``/kill Sausageman22``.
-  ``prefix: <prefix>`` - The prefix MobArena uses for all of its
   messages. The default is the classic green ``[MobArena]``, but you
   can change it to whatever you want. You can override the prefix for
   specific arenas by using the arena-specific setting with the same
   name.

I recommended leaving the update notifications on, and disabling
commands like ``/kill`` and ``/tp``.

classes
-------

The ``classes``-section is slightly more complicated. It is divided into
*class-branches*, where each branch denotes the *name of the class*, and
each branch has mandatory nodes ``items`` and ``armor``, as well as
optional slot-specific armor nodes and optional nodes ``price``,
``permissions``, ``lobby-permissions``, ``unbreakable-weapons``, and
``unbreakable-armor``.

**Note:** YAML is picky about how you type your items. Make sure you
read the short [[Item and Reward Syntax]]-page and fully understand it
before you attempt to modify the config file!

::

    classes:
        Archer:
            items: wood_sword, bow, arrow:128, grilled_pork
            armor: 298,299,300,301
            permissions:
            - EffectiveArrows.use.*
            - -mobarena.use.leave
        Tank:
            items: iron_sword
            armor: 310,311,312,313
            offhand: shield
        Knight:
            items: '276'
            armor: iron_helmet, iron_chestplate, iron_leggings, iron_boots
        Wolf Master:
            items: stone_sword, grilled_pork, bone:2
            armor: 298,299,300,301
        Crusader:
            items: iron_sword, hay_block:17
            armor: 302,303,304,305
            price: $5

Bring your own items
~~~~~~~~~~~~~~~~~~~~

MobArena allows you to just bring your own items into the arena via the
implicit 'My Items' class. What this means is that if you just place a
sign in the lobby with the text My Class, you'll get the items that you
had before joining the arena. Items are still restored on dying in or
leaving the arena.

For a smooth, own-items-only experience, ditch the signs and set the
per-arena setting ``default-class`` to ``myitems``.

Slot-specific armor nodes
~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to use off-hand items, or if you just want to be explicit
about which items go where in the armor slots, use the optional
slot-specific armor nodes: ``offhand``, ``helmet``, ``chestplate``,
``leggings``, ``boots``. In the example above, the **Tank** class gets a
shield in its off-hand slot.

Price
~~~~~

The optional ``price`` node can be used to give classes a per-session
price. When a player tries to pick a class that has a price, they will
only be able to if they can afford it. The money is withdrawn when the
arena starts, i.e. picking different priced classes in succession will
not (necessarily) result in empty player wallets. In the example above,
the **Crusader** class costs ``$5``.

Unbreakable weapons/armor
~~~~~~~~~~~~~~~~~~~~~~~~~

The optional ``unbreakable-weapons`` and ``unbreakable-armor`` nodes can
be used to toggle on or off the unbreakability of class items and armor.
The nodes *default to true*, so they are really only necessary if you
want to toggle OFF the feature, i.e. if you want items to deteriorate
and break! If that's what you want, set the nodes to false.

.. raw:: html

   <pre>
   classes:
       FrailTank:
           items: diamond_sword
           armor: 310,311,312,313
           <font color="blue">unbreakable-weapons: false</font>
           <font color="blue">unbreakable-armor: false</font>
   </pre>

Per-class permissions
~~~~~~~~~~~~~~~~~~~~~

Using the optional ``permissions``-node, you can give classes special
permissions to customize them even more. Each permission must be listed
with a dash (-) in front of it. If you want a class to *not* have a
permission, put a dash/minus at the very beginning of the permission
node. In the example above, the **Archer** class will be able to use the
EffectiveArrows plugin, but won't be able to use ``/ma leave`` (meaning
it's impossible to leave the arena without dying).

.. raw:: html

   <pre>
   classes:
       Archer:
           items: wood_sword, bow, arrow:128, grilled_pork
           armor: 298,299,300,301
           <font color="blue">permissions:</font>
           <font color="blue">- EffectiveArrows.use.*</font>
           <font color="blue">- -mobarena.use.leave</font>
   </pre>

The optional ``lobby-permissions``-node gives players special
permissions while they are in the lobby *after they have picked a
class*. This feature can be used e.g. in combination with a shop plugin
and a base class that perhaps has nothing (maybe except for a few
potions).

.. raw:: html

   <pre>
   classes:
       Basic:
           items: ''
           armor: ''
           <font color="blue">lobby-permissions:</font>
           <font color="blue">- shop.buy</font>
   </pre>

Pet classes
~~~~~~~~~~~

For every bone (Material name: ``bone``, data value: ``352``) in a
class' items-list (or class chest), one wolf pet will spawn upon arena
start. In the example above, every player that picks the **Wolf Master**
class will have 2 wolves spawn upon arena start. The wolves are
invincible, but deal less damage than normal wolves.

.. raw:: html

   <pre>
   classes:
       Wolf Master:
           items: stone_sword, grilled_pork, <font color="blue">bone:2</font>
           armor: 298,299,300,301
   </pre>

Mounts
~~~~~~

To give a class a horse mount, give it a hay block in the items-list (or
place a hay block in the class chest). The item stack amount (in the
first encountered stack) determines the variant and barding of the
horse. You can use this table to figure out which hay block amount you
need for your desired variant and barding:

+----------------+------------+------------+------------+---------------+
|                | **None**   | **Iron**   | **Gold**   | **Diamond**   |
+================+============+============+============+===============+
| **Horse**      | 1          | 9          | 17         | 25            |
+----------------+------------+------------+------------+---------------+
| **Donkey**     | 2          | -          | -          | -             |
+----------------+------------+------------+------------+---------------+
| **Mule**       | 3          | -          | -          | -             |
+----------------+------------+------------+------------+---------------+
| **Skeleton**   | 4          | -          | -          | -             |
+----------------+------------+------------+------------+---------------+
| **Zombie**     | 5          | -          | -          | -             |
+----------------+------------+------------+------------+---------------+

Note that only normal horses can have barding.

In the example above, every player that picks the **Crusader** class
will have a white horse with gold barding upon arena start. The mounts
are invincible.

.. raw:: html

   <pre>
   classes:
       Crusader:
           items: iron_sword, <font color="blue">hay_block:17</font>
           armor: 302,303,304,305
   </pre>

arenas
------

This section is by far the largest, and it is divided into several
smaller branches. In the above example, ``default`` denotes the *name*
of the default arena. This name can be altered, but it must contain no
spaces (use underscores instead). The arena name is significant when a
server has multiple arenas and no Master Lobby (will be featured later).
Let's go over the different branches:

settings
~~~~~~~~

The settings-branch is quite extensive, and besides the ``world``-node,
it is basically just a bunch of toggles (on/off, true/false), though a
few are number-based.

-  ``prefix: <prefix>`` - An arena-specific prefix to use for
   messages/announcements in this arena only. The default is the empty
   string (``''``), which means the ``global-settings`` prefix will be
   used.
-  ``world: <name>`` - The name of the world the arena resides in.
-  ``enabled: [true|false]`` - If false, players cannot join the arena.
-  ``protect: [true|false]`` - If false, the arena will not be protected
   from explosions and players breaking the blocks.
-  ``entry-fee: [$<amount>|<item>:<amount>]`` - Follows the exact same
   notation as the class items and rewards (read the [[Item and Reward
   Syntax]]-page). ``$20`` will subtract 20 of whatever currency you use
   from the players upon joining. ``$5, stick:2`` will require the
   player to have 5 currency units and 2 sticks to join the arena. The
   entry-fee will be refunded if the player leaves before the arena
   starts.
-  ``default-class: <class>`` - If non-empty, this class is
   automatically assigned to players when they join the arena. The class
   name must be all lowercase and with no spaces.
-  ``clear-wave-before-next: [true|false]`` - If true, no monsters will
   spawn before all monsters of the previous wave have been killed.
-  ``clear-boss-before-next: [true|false]`` - If true, no new waves will
   spawn before the current boss (if any) is dead.
-  ``clear-wave-before-boss: [true|false]`` - If true, a boss wave will
   not spawn until all previous monsters have been killed.
-  ``auto-equip-armor: [true|false]`` - If true, armor pieces will
   automatically be equipped upon class selection. Note that this does
   not work if a class has more than 1 of an armor piece type.
-  ``soft-restore: [true|false]`` - If true, all destroyed blocks will
   be saved in a "repair list", which will be used to restore blocks at
   arena end. No data is saved to the harddrive. Note that this setting,
   if true, ignores the ``protect`` flag.
-  ``soft-restore-drops: [true|false]`` - If true, blocks destroyed by
   players will drop as items like they normally do (using pickaxes,
   spades, etc.). Note that this makes it very easy for classes with
   pickaxes to "mine the arena" and build forts.
-  ``require-empty-inv-join: [true|false]`` - If false, players'
   inventories will be saved upon joining, and restored upon
   death/leaving.
-  ``require-empty-inv-spec: [true|false]`` - If false, players can
   spectate the arena without having to empty their inventories.
-  ``hellhounds: [true|false]`` - If true, all pet wolves in the arena
   will be in flames! This has no actual function, and is purely for the
   cool-factor. Also useful for distinguishing enemy wolves and pet
   wolves.
-  ``pvp-enabled: [true|false]`` - If true, players can damage each
   other in the arena.
-  ``monster-infight: [true|false]`` - If false, monsters will no longer
   damage each other.
-  ``allow-teleporting: [true|false]`` - If false, all warping to and
   from the arena region is blocked. Useful for preventing players from
   summoning other players into the arena for help.
-  ``spectate-on-death: [true|false]`` - If false, players will not get
   warped to the spectator area, but instead be "kicked" from the arena
   (essentially a forced /ma leave).
-  ``auto-respawn: [true|false]`` - If false, players will be greeted
   with the typical death screen upon dying in the arena, and will have
   to click the respawn button to respawn. With this setting at false,
   players will actually die in the arena, meaning plugins like Heroes
   and mcMMO will properly trigger their resetting of internal data upon
   respawn.
-  ``share-items-in-arena: [true|false]`` - If false, players will not
   be able to drop items in the arena.
-  ``min-players: <amount>`` - Gives a lower limit on how many players
   are required to start the arena. The default of ``0`` is the same as
   ``1``, which means 1 or more players may start the arena. Note that
   this feature is incompatible with ``auto-start-timer`` and
   ``start-delay-timer``!
-  ``max-players: <amount>`` - Gives an upper limit on how many players
   may join the arena. The default of ``0`` means no limit.
-  ``max-join-distance: <distance>`` - The maximum distance (in blocks)
   from which players can join or spectate the arena. If 0 (default),
   there is no limit, and players can join from any world. Note that the
   distance is calculated from every corner of the arena region, and
   that players not in the arena world won't be able to join or
   spectate.
-  ``first-wave-delay: <time>`` - The time (in seconds) before the first
   wave of monsters upon arena start.
-  ``wave-interval: <time>`` - The time (in seconds) between each new
   wave of monsters. If clear-wave-before-next: true, this setting will
   be ignored.
-  ``final-wave: <number>`` - The number of the final wave before the
   arena is force ended. This is useful if you want to set a cap on how
   many waves an arena will have.
-  ``monster-limit: <amount>`` - The maximum amount of monsters MobArena
   is allowed to spawn for this arena. The next wave, if any, will not
   spawn until there is room for more monsters.
-  ``monster-exp: [true|false]`` - If true, monsters will drop
   experience orbs. This is useful if you wish to give players the
   ability to spend the gathered experience on enchants or something
   else (using different plugins) during the session.
-  ``keep-exp: [true|false]`` - If true, players will keep the
   experience they gather in the arenas after death. This is useful if
   you want to allow players to level up or gather experience in the
   arenas. NOTE: If using ``display-waves-as-level`` or
   ``display-timer-as-level``, set ``keep-exp`` to false.
-  ``food-regen: [true|false]`` - If true, a full food bar will cause
   players to regenerate health while in the arena. Note that this
   potentially makes tank-like classes extremely overpowered, since
   diamond armor (by default) coupled with a full food bar will make a
   player very hard to kill.
-  ``lock-food-level: [true|false]`` - If true, the food bar will be
   locked for all players in the arena, meaning they will not end up
   starving, and they will be able to sprint around as they please.
-  ``player-time-in-arena: <time of day>`` - When set to anything but
   world, this setting will freeze the apparent world time for players
   in the arena to whatever value you set. This is useful for making
   time-of-day themed arenas (e.g. constant night time for a cemetery,
   broad daylight for a pirate ship). Valid values are: dawn, sunrise,
   morning, midday, noon, day, afternoon, evening, sunset, dusk, night,
   midnight.
-  ``auto-ignite-tnt: [true|false]`` - If true, TNT will be
   automatically ignited when placed. This is useful for preventing
   Oddjob-like classes from forting.
-  ``auto-start-timer: <time>`` - The time (in seconds) before the arena
   will be force started after the first player has joined the lobby
   (the default of 0 means deactivated or infinite time). Non-ready
   players will be removed from the lobby. This setting is useful to
   prevent ill-minded players from delaying or preventing other players
   from starting the arena. Note that this feature is incompatible with
   ``min-players``!
-  ``start-delay-timer: <time>`` - The time (in seconds) before the
   arena can be started after the first player has joined the lobby.
   This setting is useful if you want to give your players a fixed
   window of time to join the arena after the first player has joined,
   so they can't just start it off right away. Note that this feature is
   incompatible with ``min-players``!
-  ``display-waves-as-level: [true|false]`` - When set to true, the
   players' level counter (above the experience bar) will be used to
   display the current wave number. If the wave announcements in the
   announcements-file are silenced, this can be used to make a much less
   "spammy" MobArena experience. NOTE: Do not use this if ``keep-exp``
   is set to true!
-  ``display-timer-as-level: [true|false]`` - When set to true, the
   players' level counter (above the experience bar) will be used to
   display the auto-start timer in the lobby. NOTE: Do not use this if
   ``keep-exp`` is set to true!
-  ``auto-ready: [true|false]`` - When set to true, players are
   automatically flagged as ready when they pick a class. Useful for
   arenas with many players where hitting an iron block becomes
   difficult.
-  ``use-scoreboards: [true|false]`` - Whether to use scoreboards in
   MobArena or not.
-  ``isolated-chat: [true|false]`` - When set to true, all chat messages
   sent by arena players will be seen only by other arena players in the
   same arena. The arena players will still be able to see chat messages
   from other players on the server who aren't in an arena.
-  ``global-end-announce: [true|false]`` - When set to true, MobArena
   will announce the ``arena-end-global`` message (see
   [[Announcements]]) to all players on the server when an arena ends.
-  ``global-join-announce: [true|false]`` - When set to true, MobArena
   will announce the ``arena-join-global`` message (see
   [[Announcements]]) to all players on the server when the first player
   joins an arena.

waves
~~~~~

Please go to [[setting up the waves]] for more information.

rewards
~~~~~~~

The rewards-section denotes which rewards the arena players can win in
the arena. It uses the exact same item system as the classes-section
does, so nothing new there. You can also specify monetary rewards if you
use a major economy plugin (iConomy, BOSEconomy, Essentials Eco) in the
notation ``$<amount>``.

**Note:** YAML is picky about how you type your items. Make sure you
read the short [[Item and Reward Syntax]]-page and fully understand it
before you attempt to modify the config file!

The waves-branch is broken into ``every``- and ``after``-branches. The
``every``-branch denotes rewards that the players can receive *every* x
waves (repeated). The ``after``-branch denotes rewards that the player
can receive *after* wave x (only once) has started. Note that **only one
reward** is picked at random from the list.

In the following example, players will receive either four arrows or a
gold bar every 3 waves (3, 6, 9, 12, etc.), and a diamond every 10 waves
(10, 20, 30, etc.), as well as an iron tool on wave 7 (only on wave 7),
a diamond sword on wave 19 (only on wave 19), and 200 currency units on
wave 20:

::

    rewards:
        waves:
            every:
                '3': arrow:4, gold_ingot
                '10': diamond
            after:
                '7': iron_spade, iron_hoe, iron_axe, iron_pickaxe
                '19': diamond_sword
                '20': $200

**Note:** The wave numbers **must be enclosed by apostrophes** (e.g.
``'7':``, not ``7:``), or YAML will throw errors. If you aren't sure how
to do it, just copy one of the other lines and change the wave number
and the items.

coords
~~~~~~

The coords-section does not exist when MobArena first generates the
config-file. This is because the coordinates need to be set by the user
*in-game*. See the in-game section for more details on how to set
everything up. The coords-section consists of five key points, and an
arbitrary amount of spawnpoints:

-  ``p1`` and ``p2`` - These two points should span the entire arena
   region (including spectator areas and the lobby associated with the
   arena, if possible).
-  ``l1`` and ``l2`` - [OPTIONAL] If the lobby can't properly reside
   within the arena region for some reason, these two points should span
   the lobby region.
-  ``arena`` - This warp is where the players will be teleported upon
   arena start.
-  ``lobby`` - Where the players will be teleported upon joining the
   arena.
-  ``spectator`` - Where the players will be teleported upon death or
   spectating.
-  ``spawnpoints`` - A list of points where monsters can spawn from.

Note that editing these points manually can have some very unhappy
consequences. Always edit these points from within Minecraft to ensure
that they are generated properly.
