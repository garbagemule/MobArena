#############
Monster types
#############

MobArena supports any monster available in the `EntityType
enum <https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html>`__.
You don't have to write the names in all caps, and you can omit or
include underscores (``_``), hyphens (``-``), and periods (``.``) as you
please.

Some monsters are a little special. Creepers, for instance, can be
charged or powered, meaning their explosions become much more powerful,
and their appearance changes. Another example is slimes and magma cubes,
which have different sizes.

MobArena supports some of the variations of these different monster
types, and they are listed here:

-  ``explodingsheep`` is a MobArena-specific type of sheep that bounces
   around and explodes when in the proximity of a player
-  ``poweredcreeper`` is a charged/powered creeper
-  ``angrywolf`` is an aggressive wolf with red eyes
-  ``babyzombie`` is a baby-version of a zombie
-  ``babypigman`` is a baby-version of a pigman
-  ``babyzombievillager`` is a baby-version of a zombie villager
-  ``killerbunny`` is a killer bunny version of a rabbit

As for slimes and magma cubes, both monster types are assigned a random
size when they spawn. However, they also both support size suffixes that
force them to be a specific size. They are:

-  ``tiny`` size 1
-  ``small`` size 2
-  ``big`` size 3
-  ``huge`` size 4

As an example, ``slimehuge`` will spawn a size 4 slime, while
``magmacubetiny`` will spawn a size 1 magma cube.
