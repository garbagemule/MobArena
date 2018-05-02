##############
Using MobArena
##############

**On this page:** \* `Overview <#overview>`__ \* `Joining <#joining>`__
\* `Getting a list of arenas <#getting-a-list-of-arenas>`__ \* `Picking
a class <#the-class-command>`__ \* `Finding out who isn't
ready <#finding-out-who-isnt-ready>`__ \* `Leaving <#leaving>`__ \*
`Spectating <#spectating>`__ \* `A note on
inventories <#a-note-on-inventories>`__

Overview
~~~~~~~~

This page briefly describes the usage-commands of MobArena. Make sure to
check out the [[MobArena Commands]] page for a list of all the commands.
Remember that typing ``/ma help`` will get you a list of all available
commands in-game, along with a (very) brief description of what the
command does.

The commands covered on this page pertain to the usage-commands only,
i.e. the commands that the players will use to interact with MobArena.
The most basic commands are:

-  ``/ma join`` - for `joining <#joining>`__ arenas to start playing
-  ``/ma leave`` - for `leaving <#leaving>`__ lobbies or arenas
-  ``/ma spec`` - for `spectating <#spectating>`__ arenas in progress
-  ``/ma arenas`` - for getting a `list of
   arenas <#getting-a-list-of-arenas>`__

Additionally, there are a few commands that might prove useful:

-  ``/ma class`` - for `picking classes <#the-class-command>`__ in the
   lobby instead of punching signs
-  ``/ma notready`` - for finding out `who isn't ready
   yet <#finding-out-who-isnt-ready>`__

Joining
~~~~~~~

To join an arena, use the ``/ma join`` command. If you have more than
one arena, you will also need to specify an arena name. Let's say we
have arenas ``cave`` and ``ship``. To join the Ship arena, simply type
``/ma join ship``.

Upon joining, you will be taken to the lobby of the given arena. In the
lobby, you will have to pick a class, which is traditionally done by
punching a sign with a class name on it. You can also `use a
command <#the-class-command>`__ directly to manually pick a class if you
know its name, or indirectly via buttons powering command blocks, or
perhaps something more complex (like NPCs).

Once you've picked a class, you will need to ready up, which is
traditionally done by punching an iron block. However, there is also a
per-arena config-file setting that automatically flags you as ready once
you've picked a class (check the [[Setting up the config-file]] page).

Getting a list of arenas
^^^^^^^^^^^^^^^^^^^^^^^^

To get a list of available arenas, you simply type ``/ma arenas``.
Arenas that have been set up and are ready for use will be green, and
unavailable arenas (disabled or not yet set up) will be gray. Note that
you won't see arenas for which you don't have permission, just like you
won't be able to actually join an arena for which you don't have
permission. Check the [[Permissions]] page for more information.

The class command
^^^^^^^^^^^^^^^^^

Normally, you would punch class signs to pick classes, but MobArena also
supports picking a class with the ``/ma class`` command. If you want to
pick the Knight class, for example, simply type ``/ma class knight``.
Forcing manual class selection like this is not recommended, because
it's unintuitive and there is no way of listing available classes.
Instead, this command is useful if you want to set up command blocks or
something more advanced like NPCs wearing the class items and armor, and
allowing players to pick the class by interacting with the NPCs.

Finding out who isn't ready
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sometimes it's hard to keep track of who hit the iron block and who
didn't. To figure out which of the players in the lobby have not yet
readied up, you can use the ``/ma notready`` command. This is useful if
everyone thinks they've readied up and are waiting for everyone else,
but in fact one or more people haven't readied up yet.

Leaving
~~~~~~~

When you're done playing in the arena, you have to leave it with the
``/ma leave`` command (unless the ``spectate-after-death`` setting in
the config-file is set to ``false``, in which case the command will be
executed for you). You may also leave the lobby of an arena you didn't
actually want to join, or if you just don't want to play anymore, and
you don't want to wait for death.

Leaving an arena in progress has no consequences other than missing out
on the fun! You'll still be given the rewards you earned up to this
point. If you don't want your players to be able to leave arenas in
progress, you can revoke the permission on a per-class basis. See the
[[Setting up the config-file]] page for more information.

Spectating
~~~~~~~~~~

If you want to spectate an arena already in progress, use the
``/ma spec`` command, just like you would use the ``/ma join`` command.
Spectating an arena means you will be taken to the spectator area of the
arena so you have a great overlook of the arena.

Spectators cannot interact with the arena players or the monsters in the
arena, nor with each other, so there should be no hooligan fights
whatsoever.

Note that when you die in an arena, you will automatically become a
spectator, unless ``spectate-after-death`` is set to ``false``.

A note on inventories
~~~~~~~~~~~~~~~~~~~~~

Once you have joined an arena, either for playing or for spectating, you
are considered as *in the arena*. What this means is that MobArena will
hold on to your inventory until you leave. You leave by typing
``/ma leave``, either from the lobby or the arena, or after you've died.
If the ``spectate-after-death`` option in the config-file is ``false``,
you are automatically kicked out of the arena when you die, so in that
case, you won't have to do anything.

Inventories are stored on a per-player basis until they leave. Joining a
new arena (or the same arena again) after finishing a session will not
cause your inventory to be overwritten, even though it seems like you
have no items. When you leave with the ``/ma leave`` command, all of
your earned rewards from all sessions since you first joined will be
granted. Of course, with ``spectate-after-death`` set to ``false``, you
will automatically leave after every session.
