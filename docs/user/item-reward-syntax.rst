######################
Item and reward syntax
######################

**On this page:** \* `Overview <./Item-and-Reward-Syntax#an-overview>`__
\* `Single Items <./Item-and-Reward-Syntax#1-single-items>`__ \*
`Multiple Items <./Item-and-Reward-Syntax#2-multiple-items>`__ \* `Item
Sub-Types <./Item-and-Reward-Syntax#3-item-sub-types-potions-wool-dyes-etc>`__
\* `Note about item
IDs <./Item-and-Reward-Syntax#an-important-note-on-using-item-ids>`__ \*
`Enchantments <./Item-and-Reward-Syntax#enchantments>`__ \* `Economy
Money <./Item-and-Reward-Syntax#economy-money>`__ \* `Command
Rewards <./Item-and-Reward-Syntax#command-rewards>`__

An Overview
~~~~~~~~~~~

Items in MobArena follow a very specific syntax that you **must** obey,
or you will experience missing items or errors. Be attentive to the
details, because if you aren't, there will be consequences.

MobArena allows these three different ways of defining items:

::

    Single:    [<id>|<name>]
    Multiple:  [<id>|<name>]:<amount>
    Sub-types: [<id>|<name>]:<data>:<amount>

Confusing? Let's break them down one by one.

1. Single Items
~~~~~~~~~~~~~~~

::

    [<id>|<name>]

This means you can use either the item ID (``<id>``), or the item name
(``<name>``) as defined in the `Material enum of the Bukkit
API <https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html>`__.
The item names are case-insensitive.

Note that the Material enum changes when there are new items or
sometimes if items were reworked. As an example, the ``SPLASH_POTION``
enum value is not available in versions prior to Minecraft 1.9. Before
then, splash potions were simply ``POTION``\ s with a higher data value.

**Make sure to read `the important note on item
IDs <./Item-Syntax#an-important-note-on-using-item-ids>`__!**

Examples: ``diamond_sword``, ``stone``, ``42`` (iron block), ``322``
(snowball)

2. Multiple Items
~~~~~~~~~~~~~~~~~

::

    [<id>|<name>]:<amount>

This way, you append an ``<amount>`` to the item, specifying how many of
the given item you want. This is useful for giving out stuff like arrows
or potions that you generally want to give more than one of.

Note that if you use this syntax, it is indeed the *amount* you specify,
not the item sub-type. We go over sub-types in the `next
section <./Item-Syntax#3-item-sub-types-potions-wool-dyes-etc>`__.

**Make sure to read `the important note on item
IDs <./Item-Syntax#an-important-note-on-using-item-ids>`__!**

Examples: ``arrow:64``, ``grilled_pork:4``, ``46:10`` (10x TNT),
``142:5`` (5x potato)

3. Item Sub-Types (potions, wool, dyes etc.)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

::

    [<id>|<name>]:<data>:<amount>

This way, you append **BOTH** a ``<data>`` value (the sub-type) and an
``<amount>``, regardless of how many of the given item you want. This
syntax is mostly used with potions, which have special sub-type values
in the 8000's and 16000's. Check out `this
page <http://www.minecraftwiki.net/wiki/Potions#Primary_potions>`__ for
the potion sub-types.

For wool and dyes (ink sacks), you don't have to use the numeric data
value. Instead, you can use the *color names* as defined in the
`DyeColor *enum* of the Bukkit
API <http://jd.bukkit.org/rb/apidocs/src-html/org/bukkit/DyeColor.html#line.10>`__.

**Make sure to read `the important note on item
IDs <./Item-Syntax#an-important-note-on-using-item-ids>`__!**

Examples: ``wool:blue:1`` (one blue wool), ``ink_sack:brown:10`` (ten
cocoa beans), ``potion:8201:1`` (one strength potion), ``373:8197:2``
(two health potions)

An important note on using item IDs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you choose to use item IDs, there is a catch. If an item is *alone*
in its node, it **MUST** be enclosed in apostrophes, or YAML will crap
itself. For example, if you decide to have a Barbarian class that only
has a pair of *lederhosen* as armor, and nothing put a couple of health
potions, you need to do it like this:

::

    classes:
        Barbarian:
            items: '373:8197:2'
            armor: '300'

In other words, you have to put ``'300'`` in the ``armor``-node, *not
just* ``300``, and you have to put ``'373:8197:2'`` in the
``items``-node, *not just* ``373:8197:2``. This requirement is removed
if you use the item names instead, or if you add
`enchantments <./Item-Syntax#enchantments>`__ to the given item.

Enchantments
~~~~~~~~~~~~

Enchantments can be added to items by appending a space, followed by a
*semi-colon separated list* of pairs ``<eid>:<level>``, where ``<eid>``
is an enchantment ID as defined by the `Enchantment Wrappers in the
Bukkit
API <http://jd.bukkit.org/rb/apidocs/src-html/org/bukkit/enchantments/Enchantment.html#line.12>`__
(the numbers in the parentheses at the end of each line), i.e.:

::

    <item> <eid>:<level>;<eid>:<level>;...

The ``<item>`` is any item following the normal item syntax as described
above. Here is an example:

::

    diamond_sword 16:2;19:5

This line gives a diamond sword with sharpness (ID 16) level 2, and
knockback (ID 19) level 5.

Economy Money
~~~~~~~~~~~~~

::

    $<amount>

MobArena supports entry fees and rewards in the form of money from
economy plugins. This feature *requires Vault*. The format quite simply
means that you type in a dollar sign followed by a valid monetary value.

Examples: ``$1``, ``$5``, ``$3.14`` (v0.96+), ``$0.99`` (v0.96+)

Command Rewards
~~~~~~~~~~~~~~~

Since v0.99, MobArena supports commands as rewards. This means that you
can have the server run a command that targets the recipient player.
This is useful for granting persistent permissions via your permissions
plugin, or integrating with rewards from other plugins, like rewarding
tokens or special items with lore. The basic syntax is this:

::

    cmd:/give <player> dirt

If the player is "garbagemule", this setting will run the command "/give
garbagemule dirt" when the arena session ends. Note that this syntax
will display as
``[MobArena] You just earned a reward: /give <player> dirt``, which
isn't very pretty. If you want the reward to have a title, simply expand
the ``cmd`` with the name in a parenthesis:

::

    cmd(a very nice thing):/give <player> dirt

This will display as
``[MobArena] You just earned a reward: a very nice thing``
