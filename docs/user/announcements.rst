####################
Announcement strings
####################

MobArena supports custom strings for announcement and messages. This lets you
create custom messages unique to your server based on certain actions, like a
game ending. The ``announcements.yml`` file stores MobArena announcement and
message strings. [#]_

.. [#] Default strings are found in the `Msg.java`_ class


*************
Color support
*************

Color codes are supported. **To add color to a message**, use an ampersand
(``&``) followed by a valid color code. `Color codes`_ are available in the
Gamepedia Minecraft wiki. Examples are in the default file.


*********
Variables
*********

Some announcements use a variable. In the string, variables are represented by a
percent sign (``%``). Remove it if you do not want a variable in the message.

There are some limitations to variables:

- Not possible to add variables to announcements that don't take them by default
- Announcements that take one variable cannot take more than that variable


***********************
Disable an announcement
***********************

If you want to **disable a specific announcement**, set its value to two single
quotes (``''``). MobArena ignores announcements set to an empty value. To
disable one, you must override it. Deleting the option does not disable an
announcement since MobArena adds them in by default.


********
Examples
********

Three examples below show all of the features explained above.

.. code-block:: yaml
   :emphasize-lines: 2, 5, 8

   # Use a red message for the start of a new game
   arena-start: 'Let the games begin! &cMay the odds be ever in your favor!'

   # Use a variable for the number of seconds until the game begins
   arena-auto-start: 'Arena will auto-start in &c%&r seconds.'

   # Turn off the golem-died message
   golem-died: ''

.. _`Msg.java`: https://github.com/garbagemule/MobArena/blob/master/src/main/java/com/garbagemule/MobArena/Msg.java
.. _`color codes`: https://minecraft.gamepedia.com/Formatting_codes#Color_codes
