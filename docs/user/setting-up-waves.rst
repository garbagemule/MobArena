################
Setting up waves
################

**On this page:** \* `About
Modules <./Setting-up-the-waves#wiki-about-modules>`__ \* `Wave
Branches <./Setting-up-the-waves#wiki-wave-branches>`__ \* `Common
Nodes <./Setting-up-the-waves#wiki-common-nodes>`__ \*
```recurrent`` <./Setting-up-the-waves#wiki-recurrent-waves>`__ \*
```single`` <./Setting-up-the-waves#wiki-single-waves>`__ \* `Wave
Types <./Setting-up-the-waves#wiki-wave-types>`__ \*
```default`` <./Setting-up-the-waves#wiki-default-waves>`__ \*
```special`` <./Setting-up-the-waves#wiki-special-waves>`__ \*
```swarm`` <./Setting-up-the-waves#wiki-swarm-waves>`__ \*
```supply`` <./Setting-up-the-waves#wiki-supply-waves>`__ \*
```upgrade`` <./Setting-up-the-waves#wiki-upgrade-waves>`__ \*
```boss`` <./Setting-up-the-waves#wiki-boss-waves>`__ \* `A Sample
Setup <./Setting-up-the-waves#wiki-sample-config-file-setup>`__

**Note: If you are impatient, go to the bottom of this page for an
example config-file setup to see what the waves could look like. Modify
them as you please, but make sure to read this page before asking any
questions!**

Make sure to check out `Agnate's MobArena Bosses and Waves
Thread <http://forums.bukkit.org/threads/mobarena-boss-and-wave-thread.31797/>`__
if you need inspiration for adding some cool bosses to your arenas!

About Modules
-------------

The new MobArena waves-system is extremely modular, meaning every time
you plug in a new wave, you only have to provide the nodes required by
the specific modules you are using. The modules can be broken into *wave
branches* and *wave types*. The structure of the waves-section in
config-file is the following:

::

        waves:
            recurrent:                   <-- Wave branch
                <wave name>:
                    type: <wave type>    <-- Wave type
                    frequency: #
                    priority: #
            single:                      <-- Wave branch
                <wave name>:
                    type: <wave type>    <-- Wave type
                    wave: #

Wave Branches
-------------

The waves are split into two branches, ``recurrent`` and ``single``.
*Recurrent waves* (may) occur more than once (as in, they repeat), given
a frequency (how often they occur) and a priority (how "important" they
are, i.e. which wave should spawn if two recurrent waves clash). *Single
waves* occur just once, on the given wave (and always occur over
recurrent waves, should they clash).

Common Nodes
~~~~~~~~~~~~

As you can see, the two branches have one thing in common, the
``type``-node. Other than that, their other nodes differ. However, there
are two additional nodes that can be used regardless of branch and type
(doesn't work for boss waves, though):

``amount-multiplier: <decimal value>`` (optional) minimum value of 0.1,
this multiplier helps determine how many monsters spawn per wave
(minimum 1). If 8 mobs are supposed to spawn, and the value is ``0.5``,
only 4 mobs will spawn. If the value is ``3``, 24 will spawn.

``health-multiplier: <decimal value>`` (optional) minimum value of 0.1,
this multiplier helps determine the health for each monster in a wave.
If a zombie spawns with the default of 20 health points and the value is
``0.5``, the zombie will have 10 health points. If the value is ``4``,
it will be 80 health points.

These two common nodes can be used to greatly customize the difficulty
of the monsters in each wave. If you want more monsters, just set the
amount-multiplier higher than 1, and maybe adjust the health with the
health-multiplier accordingly. If you want the monsters to be tougher to
kill, just up the health-multiplier.

An additional node can be used to help determine where enemies will
spawn:

``spawnpoints: <semi-colon separated list of spawnpoints>``

For example, we can make a swarm wave spawn monsters only on spawns
``5,53,198``, ``-16,54,185``, and ``-7,53,179`` if players are in range:

::

    swarm3:
        type: swarm
        wave: 11
        monster: zombie_pigman
        spawnpoints: 5,53,198; -16,54,185; -7,53,179

Note that these spawnpoints must exist in the ``spawnpoints``-list of
the ``coords``-section to work.

Recurrent Waves
~~~~~~~~~~~~~~~

``type: [default|special|swarm|supply|upgrade|boss]`` (required)
determines the wave type. Read the **Wave types** section further down
for more details.

``frequency: #`` (required) determines how often the wave will/can
spawn. With a frequency of 1, the wave can potentially spawn on every
single wave number. The implicit default waves in MobArena have a
frequency of 1, and the implicit special waves have a frequency of 4,
which means the default waves (can) spawn every wave, and the special
waves (can) spawn every 4th wave.

``priority: #`` (required) determines how "important" the wave is. If
two recurrent waves clash, the wave with the highest priority will
spawn. The implicit default waves in MobArena have a priority of 1, and
the implicit special waves have a priority of 2, which means if the
default and special waves clash, the special waves will spawn because
their priority is higher.

``wave: #`` (optional) determines the first wave number on which this
wave can/will spawn. This is useful for offsetting waves with similar
frequencies. Note that if no wave is specified, it will default to the
value of the (required) frequency-node. The implicit default waves in
MobArena have wave value of 1, and the implicit special waves have a
wave value of 4 (same as the frequency), which means the default waves
may begin spawning from wave 1, and the special waves may begin spawning
from wave 4.

Single Waves
~~~~~~~~~~~~

``type: [default|special|swarm|supply|upgrade|boss]`` (required)
determines the wave type. Read the **Wave types** section further down
for more details.

``wave: #`` (required) determines the wave on which this wave *will*
spawn. No matter what priority a recurrent wave have, if it clashes with
a single wave, the single wave will always spawn instead of the
recurrent waves. Single waves are good for extraordinary waves like
"swarm" waves, "boss" waves or even normal waves with specific monster
types, for instance.

Wave Types
----------

All MobArena waves must specify a *wave type*, which must be either
``default``, ``special``, ``swarm``, ``supply``, ``upgrade`` or
``boss``. These different wave type modules introduce some new required
and optional nodes. Note that no matter what the wave type is, any wave
*must* adhere to the requirements of the wave branch (read above).

Default Waves
~~~~~~~~~~~~~

Default waves are waves that spawn an amount of monsters picked
semi-randomly from an optional list of monsters. The amount of monsters
grow at a configurable (but optional) rate. If no growth or monster-list
is specified, default waves will consist of 5 different monster types
(zombie, skeleton, spider, creeper, wolf), all equally likely to spawn,
spawned at the "old" growth rate (player count + wave number). Nodes:

``growth: [old|slow|medium|fast|psycho]`` (optional) determines how fast
the monster count grows with every wave. ``old`` means (player count +
wave number), but the other four use a mathematical function to
determine the monster count, also based on player count and wave number.
See [[Formulas]] for more info.

``monsters: <list of <monster>: <probability>>`` (optional) determines
[[monster types]], and their individual probabilities of spawning on
each wave. Note that the probabilities are just that, probabilities.
They do not specify exact amounts, but only chance of spawning. The
following sample will statistically spawn twice as many zombies as
skeletons:

::

        monsters:
            zombies: 10
            skeletons: 5

``fixed: [true|false]`` (optional) the probability values in the
monsters list becomes amount values instead, such that the above wave
will spawn exactly 10 zombies and 5 skeletons, regardless of player
count and wave number.

Special Waves
~~~~~~~~~~~~~

Special waves are waves that spawn *one type* of monster, and always a
fixed amount. Unlike with *default waves*, the (optional) monster list
with probabilities determines which monster out of the entire list
should spawn. The monsters-node's notation is identical to that of
*default waves*.

``monsters: <list of <monster>: <probability>>`` (optional) determines
[[monster types]], and their probabilities of spawning on each wave. The
following sample will statistically spawn powered-creepers twice as
often as slimes:

::

        monsters:
            powered-creepers: 4
            slimes: 2

Swarm Waves
~~~~~~~~~~~

Like *special waves*, swarm waves spawn just *one type* of monster, but
in a configurable (but optional) amount. The swarm wave monsters only
have *1 health point*, meaning they will die with just one blow from
anything. Their numbers are vast compared to default and special waves,
however, so they may be a bit hard on some servers. Use with caution!

``monster: <monster>`` (required) which [[monster types]] the swarm
consists of. Note that this is different from special waves, in that
only one type is specified, and no probability value.

``amount: [low|medium|high|psycho]`` (optional) how many monsters should
spawn. Defaults to low (which is still a lot). See [[Formulas]] for more
info.

Supply Waves
~~~~~~~~~~~~

These waves spawn one monster per player, and will drop a random item
from a customized drop list (same notation as the class items). The
monster list notation is identical to that of default and special waves.

::

        drops: grilled_pork, cooked_chicken, cooked_beef, cooked_fish:2

Upgrade Waves
~~~~~~~~~~~~~

These waves don't spawn any monsters, but will give or upgrade items.
The class names are optional (you don't have to give items to all
classes), and it is possible to use the ``all`` identifier to specify
items that will be given to all players regardless of class. The
``give-all-items`` flag determines if all items in the list should be
given, or just a random item off the list (like with rewards and supply
waves).

**Legacy setup**: In the following example, all players get a healing
potion, and on top of that, all Archers get 64 arrows, and all Oddjobs
get either 2 TNT or a Netherrack:

::

            upgrades:
              all: potion:8197:1
              Archer: arrow:64
              Oddjob: tnt:2, netherrack
            give-all-items: false

**Advanced setup**: Since MobArena v0.95, the Upgrade Waves can be set
up to upgrade/replace certain weapons and armor, as well as add/remove
permissions. The setup follows the setup of the classes-section. In the
following example, the Knight class gets its diamond sword enchanted and
its iron chestplate replaced with a diamond chestplate. The Archer just
gets some more arrows (legacy setup) while the Wizard class gets the
permission to cast the Forcepush spell from MagicSpells:

::

    classes:
      Knight:
        armor: iron_helmet, iron_chestplate, iron_leggings, iron_boots
        items: diamond_sword

    ...

    arenas:
      ...
        waves:
          ...
            upgrades:
              Archer: arrow:64
              Knight:
                armor: diamond_chestplate
                items: diamond_sword 16:2
              Wizard:
                permissions:
                - magicspells.cast.forcepush
            give-all-items: true

Explanation: Items listed in the ``armor`` node will be considered
armor, and (if valid) will replace any item currently in the armor slots
of the players. Items in the ``items`` node will be checked if they are
weapons or not; if they are weapons, then MobArena will search through
the players' inventories for weapons with the same ID, and then replace
the first weapon that matches it (automatic upgrades). If no weapon is
found, it will default to a generic item, which will just be added to
the inventory.

Boss Waves
~~~~~~~~~~

Boss waves consist of *one monster* with a configurable (but optional)
amount of health, and a configurable (but optional) list of special
abilities. The health of a boss monster is significantly higher than
that of normal monsters, and thus take much longer to kill. The special
abilities help increase the difficulty (and fun!) of a boss wave.

``monster: <monster>`` (required) the boss [[monster types]]. Note that
only one monster will spawn.

``name: <name>`` (optional) the name of the boss. Shows the given name
in a name tag above the boss' head.

``health: <amount>|[verylow|low|medium|high|veryhigh|psycho]``
(optional) how much health the boss has. Can be either a flat value,
e.g. 40 or 800, or one of the scaling values. Defaults to the scaling
value medium. See [[Formulas]] for more info about the scaling values.

``reward: <item>`` (optional) a reward for getting the killing blow on
the boss. This reward will only be given to one player (the killer, if
any).

``drops: <item list>`` (optional) a comma-separated list of items
dropped by the boss when killed. The boss will drop exactly the items
listed. This could be used to have the boss drop a "key" to advance in
the arena, or to gain access to a shed full of weapon chests or
something wonderful like that. The item syntax is the same as the one
for Supply Waves.

``potions: <potion list>`` (optional) a comma-separated list of potion
effects that will be applied to the boss when it spawns. Use this to
slow down or speed up bosses that don't move at quite the speed you
want, or perhaps to give a boss the wither effect to limit the amount of
time it will stay alive. The potion syntax is
``<effect>:<amplifier>:<seconds>``. The amplifier and duration are
optional, and will default to 0 (level 1) and pseudo-infinity,
respectively. Note that ``slow``, ``slow:0``, and ``slow:0:600`` are
identical, except the last one will only last 10 minutes (600 seconds).
Check the sample config-file at the bottom for more examples.

``abilities: <comma-separated list of boss abilities>`` (optional)
determines which (if any) boss abilities this boss has. The boss can
have several abilities; just separate each ability with a comma (e.g.
``arrows, fire-aura, throw-target``). Note that the abilities happen in
a cycle every few seconds, so the more abilities, the longer it takes
before each ability is used again. Here is an overview of the different
abilities bosses can have:

::

        NAME                DESCRIPTION
        arrows              Shoots arrows
        fireballs           Hurls fireballs
        fire-aura           Burns all nearby (5 blocks radius) players
        lightning-aura      Strikes lightning 4 places around itself (3-block radius)
        living-bomb         A random player is set on fire, and explodes after 3 seconds
        obsidian-bomb       Spawns an Obsidian block which explodes after 4 seconds
        chain-lightning     Lightning strikes the target and jumps to a nearby player
        disorient-target    Spins the target around 45-315 degrees
        disorient-nearby    Spins all nearby (5 blocks radius) players
        disorient-distant   Spins all distant (8+ blocks) players
        root-target         Locks the target in place for a couple of seconds
        warp-to-player      Picks a random player in the arena to warp to
        shuffle-positions   Swaps everyone's (including the boss) positions around
        flood               Places a water block on a random player's location
        throw-target        Throws the target backwards (if in distance)
        throw-nearby        Throws all nearby (5 blocks radius) players
        throw-distant       Throws all distant (8+ blocks) players
        pull-target         Pulls the target towards the boss' location
        pull-nearby         Pulls all nearby (5 blocks radius) players towards the boss' location
        pull-distant        Pulls all distant (8+ blocks) players towards the boss' location
        fetch-target        Warps the target to the boss' location
        fetch-nearby        Warps all nearby (5 blocks radius) players to the boss' location
        fetch-distant       Warps all distant (8+ blocks) players to the boss' location

``ability-announce: [true|false]`` (optional) should boss abilities be
announced to arena players? Defaults to true.

``ability-interval: <seconds>`` (optional) time between each ability.
Defaults to 3.

Sample config-file setup
------------------------

If you want to try a sample setup, here's one that you can use. Simply
copy this block of text, and paste it into your own config-file,
replacing the waves-section.

::

            waves:
                recurrent:
                    def1:
                        type: default
                        priority: 1
                        frequency: 1
                        monsters:
                            zombies: 10
                            skeletons: 4
                            exploding_sheep: 5
                    def2:
                        type: default
                        priority: 2
                        frequency: 1
                        wave: 5
                        monsters:
                            zombies: 10
                            skeletons: 6
                            creepers: 4
                    spec1:
                        type: special
                        priority: 5
                        frequency: 4
                        wave: 4
                        monsters:
                            powered_creepers: 10
                            angry_wolves: 10
                            zombie_pigmen: 10
                    upgrade1:
                        type: upgrade
                        priority: 7
                        frequency: 10
                        wave: 10
                        upgrades:
                            all: potion:8197:2
                            Archer: arrow:64
                            Oddjob: tnt:2, netherrack
                        give-all-items: true
                single:
                    swarm1:
                        type: swarm
                        wave: 7
                        monster: slimes
                        amount: medium
                    boss1:
                        type: boss
                        wave: 9
                        monster: spider
                        health: medium
                        abilities: fire-aura, disorient-target, fireballs, throw-nearby
                        potions: speed:3:20, wither, increase_damage:1
                        ability-interval: 5
                    boss2:
                        type: boss
                        wave: 13
                        monster: zombie_pigman
                        health: high
                        abilities: root-target, arrows, fetch-distant, fire-aura
                        drops: lever, stone_button
                    upgrade2:
                        type: upgrade
                        wave: 14
                        upgrades:
                            all: potion:8197:2
                            Knight:
                                armor: diamond_helmet
                                items: diamond_sword 16:2;19:1
                            Tank:
                                items: iron_sword 19:3
                            Oddjob:
                                armor: iron_chestplate, iron_leggings
                            Wizard:
                                permissions:
                                - magicspells.cast.ChainLightning
                        give-all-items: true
                    boss3:
                        type: boss
                        wave: 16
                        monster: wolf
                        health: psycho
                        abilities: warp-to-player, fire-aura, throw-nearby, fireballs, fetch-target, arrows
                        potions: slow:1
                        ability-interval: 1
                        reward: diamond_chestplate
                    supply1:
                        type: supply
                        wave: 19
                        monsters:
                            cows: 10
                            pigs: 5
                        drops: grilled_pork, cooked_chicken, cooked_beef, cooked_fish:2
                    boss4:
                        type: boss
                        wave: 20
                        monster: blaze
                        health: low
                        abilities: fire-aura, throw-nearby
                        potions: speed
                        reward: diamond_helmet
