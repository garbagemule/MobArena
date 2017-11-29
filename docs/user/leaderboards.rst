############
Leaderboards
############

MobArena supports so-called *leaderboards* (although technically they
are more like scoreboards or session signs). By arranging signs in a
two-dimensional grid on a wall, spectators can see which classes each
player has chosen, which wave they died on, how many kills and how much
damage they have done, etc.

.. figure:: img/leaderboards/1.png
   :alt: Sign Grid

   Sign Grid

The requirements for leaderboards are at the very least two rows of
signs on a wall as seen in the screenshot above. The top row should not
be empty, however, as it should contain the leaderboard *headers*, which
denote what kind of information the signs below them display. How many
headers (and which) you want is entirely up to you.

.. figure:: img/leaderboards/2.png
   :alt: Top Left Sign

   Top Left Sign

To get started, replace the top left empty sign (or place it if you
haven't already), and write ``[MA]<name>``, where ``<name>`` is the name
of your arena, on the first line. In the screenshot above, I have set up
the top left sign for the arena named Jail by writing ``[MA]jail`` on
it. MobArena automatically fills in the rest of the text and the colors
for you.

.. figure:: img/leaderboards/3.png
   :alt: Sign Text Screen

   Sign Text Screen

MobArena will then tell you that the sign has been created, and that you
should set up the rest of the signs. The rest of the headers follow the
same kind of format as the top left sign, so you simply write
``[MA]<stat>``, where ``<stat>`` is one of the following:

-  ``class`` - The class name of the player
-  ``lastWave`` - The last wave the player was part of (current wave if
   still alive)
-  ``kills`` - The number of monsters the player has killed
-  ``dmgDone`` - The amount of damage the player has dealt
-  ``dmgTaken`` - The amount of damage the player has taken
-  ``swings`` - The number of times the player has swung their weapon
-  ``hits`` - The number of times the player has swung their weapon and
   successfully hit a monster

In the screenshot above, I have already set up a couple of signs, and
I'm about to set up the sign for damage done. As with the top left sign,
your only job is to tell MobArena which stat you want - it'll take care
of colors and formatting automatically. Note that MobArena's sign
handling is case sensitive, so make sure you get it right.

.. figure:: img/leaderboards/4.png
   :alt: Final Setup

   Final Setup

When you're done setting up the leaderboards, they should look something
like the screenshot above, and you should be good to go! Leaderboards
can be set up anywhere (even outside of the world the arena is in), but
you can only have a single leaderboard per arena.
