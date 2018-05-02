############
Class chests
############

**On this page:** \* `About Class Chests <#about-class-chests>`__ \*
`Linked Class Chests <#linked-class-chests>`__

About Class Chests
------------------

If some of your favorite items aren't supported by MobArena's internal
[[item parser\|Item and Reward Syntax]], or if you just want to be able
to configure your class items from in-game, the **class chests** may be
what you're looking for!

--------------

**Note: The Class Chests will only work for arenas with
``use-class-chests: true``, and the classes *MUST* exist in the
config-file for MobArena to recognize them, however the items and armor
lists can be empty.** \* \* \*

The idea behind the class chests is to simply place some **chests below
the corresponding class signs** in the lobby, and fill them with
whatever items you want the given class to have. When the players
activate the class signs, the **contents of the chests are copied to the
player inventory**. This suggests a type of "control room" setup, where
an admin-only access room below the lobby contains the chests, allowing
admins to warp down there and change the contents of the chests.

.. figure:: img/1.png
   :alt: Lobby and Control Room

   Lobby and Control Room

For easier access and modification of the class chests, omitting the
control room from the arena or lobby region may prove useful. Otherwise,
arenas may have to be temporarily disabled or put into edit mode to
allow warping to and changing the contents of the chests.

The class chests can be located **up to 6 blocks below the sign** itself
or below the block right behind the sign (for wall signs, this would be
the block the sign is attached to). The chest may also be in the block
directly behind the sign itself - this is safe, because MobArena
prevents players in the lobby from opening inventories, so if your lobby
is in a tight spot, this might be the better option.

**Multiple sign rows:** It is possible to have two rows of class signs
in the lobby and still use this feature. Simply place the class chest
for the sign of the bottom row exactly at the 6-block limit, and the
class chest for the sign of the top row one block up and behind the
other chest (in a stair-like fashion). The blocks are searched in a
vertical/pillar-like fashion, which is the reason this works.

.. figure:: img/2.png
   :alt: Chests Below

   Chests Below

To get **auto-equipped armor** from the class chests, place the armor
pieces in the **last four slots of the third row** in the chest.
MobArena will check these four slots, and if any of them are armor
pieces, they will be equipped. Note that the item placed in the very
last slot (bottom right), will always be equipped as a helmet (this
allows wool blocks, pumpkins, etc. to be used as helmets). The order of
the other three slots doesn't matter.

The **fifth last slot**, right next to the armor slots, will be equipped
as an **off-hand** item.

.. figure:: img/3.png
   :alt: Armor Slots

   Armor Slots

The class chests are the best way to add items that are not currently
supported by the MobArena [[item parser\|Item Syntax]]. This is because
the class chests **simply copy the contents of the chests** to the
player inventories, thus making any items supported by Bukkit supported
by MobArena.

.. figure:: img/4.png
   :alt: Dyed Armor

   Dyed Armor

Linked Class Chests
-------------------

If per-arena class chest setups is too troublesome (e.g. if you have
many arenas), if you don't need per-arena setups, or if you simply want
a single, global class chest for each class, *linked class chests* are
what you're looking for.

When you link a chest to a class, MobArena will always copy the contents
of that chest to the player's inventory, when they pick the given class,
regardless of any local class chests (note that the arena must still
have ``use-class-chests: true``).

To link a chest to a class, simply look at the chest and type
``/ma classchest <class>``, and you're done! The linked class chests may
exist in any world, but remember that there can only be one class chest
per class, and that local class chests will be ignored!

To unlink a class chest, you will have to open the config-file and
remove the ``classchest`` node from the given class.
