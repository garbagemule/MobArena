###########
Permissions
###########

A permissions plugin **is NOT required** for MobArena to work, but if
you want that extra bit of control, here's a rundown of the different
types of permission nodes you can use with MobArena.

**NOTE: MobArena uses sane defaults. This means that by default, all
players can use all arenas and all classes, and ops can use all admin
and setup commands. Unless you want to prevent some groups from
accessing certain arenas or classes, or you want to give non-ops admin
and setup permissions, there is no need to mess with any permissions at
all, so go away from this page and remove all occurrences of
``mobarena`` in your permissions-file!**

Arenas
~~~~~~

Did you read the note at the top? If not, read it before you continue.

So, you want to remove permissions for certain arenas from certain
users? Alright, that means you will have to *negate* or *revoke* the
permissions in your permissions plugin. In bPermissions, the negation
modifier is a caret, ``^``, in GroupManager and PermissionsEx it is a
minus, ``-``, and in zPermissions it is by setting the permission to
``false``. The examples below revoke the permission for the default
arena.

| bPermissions: ``^mobarena.arenas.default``
| GroupManager: ``-mobarena.arenas.default``
| zPermissions: ``mobarena.arenas.default: false``

I recommend letting everyone enjoy all your arenas, but this could be
used in combination with "leveling" plugins to allow players to use
"harder" arenas at higher levels. It could also be used for
sponsors-only arenas.

Still confused? Check the `sample setup <#sample-setup>`__ at the bottom
of the page!

Classes
~~~~~~~

Did you read the note at the top? If not, read it before you continue.

Alright, if you're reading this, you want to remove permissions for
certain classes from certain users. As with the arena permissions, you
need to *negate* or *revoke* the permissions in your permissions plugin.
In bPermissions, the negation modifier is a caret, ``^``, in
GroupManager and PermissionsEx it is a minus, ``-``, and in zPermissions
it is by setting the permission to ``false``. The examples below revoke
the permission for the Knight class.

| bPermissions: ``^mobarena.classes.knight``
| GroupManager: ``-mobarena.classes.knight``
| zPermissions: ``mobarena.classes.knight: false``

**Note how the class name is lowercase.** This is important. Even if the
Knight class is called ``KnIGhT`` in your config-file, it MUST be all
lowercase in your permissions-file.

As with arenas, I recommend letting everyone enjoy all the classes,
unless you have a special reason not to.

Still confused? Check the `sample setup <#sample-setup>`__ at the bottom
of the page!

Commands
~~~~~~~~

Did you read the note at the top? If not, read it before you continue.

If you're reading this, you want to either give certain users access to
some of the admin and/or setup commands, or you want to remove some of
the user commands from some groups. If this is not the case, stop
reading and leave this page!

The first group of commands are the user commands. They are accessible
by all players by default, so don't put ``mobarena.use.*`` or something
stupid like that in your permissions-file! If you want a group to not
have access to the user commands, *negate* the permission
``mobarena.use``, which is the *parent permission node* for all the user
commands. See the classes and arenas sections for information on how to
negate permissions. If that doesn't work, negate the
``mobarena.use.join`` and ``mobarena.use.spec`` permissions. That should
be enough.

::

    mobarena.use.join
    mobarena.use.leave
    mobarena.use.spec
    mobarena.use.arenalist
    mobarena.use.playerlist
    mobarena.use.notready
    mobarena.use.class

The admin commands are simple. They allow disabling/enabling MobArena
and individual arenas, kicking players from the arenas, restoring player
inventories if they got lost somehow, forcing arenas to start or end,
and teleporting in and out of arenas regardless of what the arena state
is. If you want to grant all of these permissions, use the *parent
permission node* ``mobarena.admin``. Don't mess around with ``*`` or
something stupid like that.

::

    mobarena.admin.enable
    mobarena.admin.kick
    mobarena.admin.restore
    mobarena.admin.force
    mobarena.admin.teleport

Setup commands are only for ops, just like admin commands. **Do not**
give these permissions to random people, because they can remove your
arenas and destroy your config-files, if they do something stupid. The
setup commands allow you to manage arenas, regions, spawnpoints, chests,
leaderboards, etc. They also allow you to set up new classes in-game. If
you want to grant all of these permissions, use the *parent permission
node* ``mobarena.setup``. Don't mess around with ``*`` or something
stupid like that.

::

    mobarena.setup.config
    mobarena.setup.setup
    mobarena.setup.setting
    mobarena.setup.addarena
    mobarena.setup.removearena
    mobarena.setup.editarena
    mobarena.setup.spawnpoints
    mobarena.setup.containers
    mobarena.setup.checkdata
    mobarena.setup.checkspawns
    mobarena.setup.classchest
    mobarena.setup.classes
    mobarena.setup.leaderboards
    mobarena.setup.autogenerate
    mobarena.setup.autodegenerate

Sample setup
~~~~~~~~~~~~

Assume you have a class called DiamondKnight that you only want your
donors to be able to use (very common use case). How do you set up your
permissions plugin when you have to revoke the class permission from the
default group, but the donor group inherits from the default group? It's
very simple: You're doing it wrong...

What you have to do instead is make an *auxiliary default*-group that
contains all your default permissions, and have your default group
inherit from that group, and furthermore revoke the DiamondKnight class
permission in MobArena. Your donor group then also inherits from the
auxiliary group, and everything is wonderful. Confusing? Here's a
pseudo-code example:

::

    default-aux:                            <-- This is the auxiliary group that is to
      permissions:                              be inherited by the default group and
      - essentials.balance                      the donor group. It is not used for
      - essentials.pay                          anything else.
      - essentials.sell

    default:                                <-- This is the default group. It inherits
      inherits: default-aux                     from default-aux, but also revokes the
      permissions:                              permission for the special class.
      - -mobarena.classes.diamondknight

    donor:                                  <-- This is the donor group, which also
      inherits: default-aux                     inherits from default-aux, but it
      permissions:                              does not revoke any class permissions,
      - essentials.balance.others               which means it has access to all of
      - essentials.kit                          them by default.

This sample setup is **pseudo code** and cannot be simply copy/pasted
into your own permissions file. It's your job to figure out how your
permissions plugin works, and what its syntax is.
