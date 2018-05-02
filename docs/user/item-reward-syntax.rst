######################
Item and reward syntax
######################

This page explains the syntax for items and rewards for completing MobArena
waves.


**********
Item types
**********

MobArena items must use a specific syntax to work as expected. The plugin allows
three different ways of defining items:

- **Single**: ``[<id>|<name>]``
- **Multiple**: ``[<id>|<name>]:<amount>``
- **Sub-types**: ``[<id>|<name>]:<data>:<amount>``

Single items
============

``[<id>|<name>]``

*Either* an item ID number [#]_ (``<id>``) or item name (``<name>``) can be used.
Item names are defined in the `Material enum`_ of the Bukkit API. Item names are
case-insensitive.

The Material enum changes when items are added, changed, or reworked. The
Minecraft server version determines what items are available. Check the
documentation for what item names are available.

This example gives the ``barbarian`` class a bow and leather leggings.

.. code-block:: yaml

   classes:
     barbarian:
       items: bow
       armor: leather_leggings

.. [#] Each item ID number must be wrapped in apostrophes (``''``) to work
   correctly. 

Multiple items
==============

``[<id>|<name>]:<amount>``

This method lets you add an amount to an item. This is useful for items like
arrows or potions.

Now, our example also gives 64 arrows and four pieces of cooked porkchops.

.. code-block:: yaml

   classes:
     barbarian:
       items: bow, arrow:64, grilled_pork:4
       armor: leather_leggings

Item sub-types
==============

``[<id>|<name>]:<data>:<amount>``

To add an item sub-type, both the sub-type name and an amount must be specified.
This is helpful for items like potions, wool, and/or dyes.

- `Potion effect types`_
- `Color names`_

Now, our example gives one Potion of Strength I and one piece of purple wool.

.. code-block:: yaml

   classes:
     barbarian:
       items: bow, arrow:64, grilled_pork:4, potion:increase_damage:1,
              wool:purple:1
       armor: leather_leggings


************
Enchantments
************

``<item> <eid>:<level>;<eid>:<level>;...``

Add enchantments to an item by adding a space with a semi-colon separated list
with the enchantment name and the strength. Find valid enchantment names in the
`Enchantment class`_ in the Spigot API.

Now, our example adds Power II and Flame I to the bow and a diamond sword with
Sharpness I and Knockback II.

.. code-block:: yaml

   classes:
     barbarian:
       items: bow arrow_damage:2;arrow_fire:1, arrow:64, grilled_pork:4,
              potion:increase_damage:1, wool:purple:1,
              diamond_sword damage_all:1;knockback:2
       armor: leather_leggings


*************
Economy Money
*************

``$<amount>``

MobArena supports entry fees and rewards with economy plugins. You must use
`Vault`_ for this to work.

Since v0.96, floating point numbers are also supported.

Examples:

- ``$1``
- ``$5``
- ``$3.14``
- ``$0.99``


***************
Command Rewards
***************

.. code-block:: yaml

   cmd:/give <player> dirt
   cmd(description of reward):/give <player> dirt

You can run a command to give a special reward to a player. Command rewards are
supported since v0.99. Useful examples might be to give a permission (maybe
unlocking a new arena) or integrating with other plugins.

It is possible to customize the message a player receives when they receive a
command reward. By default, the plugin prints the command. You can specify a
more user-friendly message in the second format listed above.

For example, ``cmd(New arena to play!)/perm add <player> <permission>``
appears to the player as ``You just earned a reward: New arena to play!``.


.. _`Material enum`: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
.. _`Potion effect types`: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html
.. _`Color names`: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html
.. _`Enchantment class`: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html
.. _`Vault`: https://dev.bukkit.org/projects/vault
