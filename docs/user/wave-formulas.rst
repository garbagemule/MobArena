#############
Wave formulas
#############

This page holds an overview of all the formulas used in the MobArena
waves system. Customizing the different properties of the waves should
be somewhat easier if they can be calculated, so here they all are!

About notation: Each variable used in the formulas will have its own
name. A variable that starts with a ``#`` denotes "number (of)", so
``#players`` means "number of players", and ``#wave`` means "wave
number". The function ``min(a,b)`` returns the lowest of the values
``a`` and ``b``, and ``max(a,b)`` returns the highest.

Wave growth
~~~~~~~~~~~

The wave growth node ``growth``, used in default waves, denotes how fast
monster amounts grow over time. The base is calculated by half of the
number of players, but at most 13 (i.e. there is no difference between
25 and 50 players). The amounts can be altered further using the
``amount-multiplier`` (see the [[wave setup page\|Setting up the
waves]]).

::

    #monsters = base * #wave^exp
    base = min(#players/2 + 1 , 13)

The ``exp`` variable is defined by the growth node, and has the
following values:

::

    slow   = 0.5
    medium = 0.65
    fast   = 0.8
    psycho = 1.2

Note that with the node value ``old`` (which is the default), the
monster count is ``#wave + #players``.

Swarm Amount
~~~~~~~~~~~~

The swarm amount node ``amount``, used in swarm waves, denotes how many
monsters should spawn in the swarm waves. There will always be at least
10 monsters due to the max function and the lowest multiplier value
being 10, however this can be further customized with the
``amount-multiplier`` (see the [[wave setup page\|Setting up the
waves]]).

::

    #monsters = max(1, #players/2) * multiplier

The ``multiplier`` variable is defined by the amount node, and has the
following values:

::

    low    = 10
    medium = 20
    high   = 30
    psycho = 60

Boss Health
~~~~~~~~~~~

The boss health node ``health``, used in boss waves, denotes how much
health the boss has. Note that the ``health-multiplier`` node (see the
[[wave setup page\|Setting up the waves]]) **does NOT** affect boss
waves at all, so these are the only values that can be used. The minimum
health a boss can have is 320 health points (~160 hearts), which is with
``low`` health and only 1 player fighting. With 10 players and ``high``
health, the boss will have 5500 health points (~2750 hearts).

::

    health = (#players + 1) * 20 * multiplier

The ``multiplier`` variable is defined by the health node, and has the
following values:

::

    verylow   = 4
    low       = 8
    medium    = 15
    high      = 25
    veryhigh  = 40
    psycho    = 60
