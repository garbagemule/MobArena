####################
Announcement strings
####################

The ``announcements.yml`` file is where all of MobArena's announcements
and messages are stored. You can freely edit this file however you see
fit, and color codes are supported. **To add color to a message**, use
the ``&``-character followed by a valid color code. You can find a list
of valid codes `right
here <http://minecraft.gamepedia.com/Formatting_codes#Color_codes>`__,
and you can find examples in the default file.

Note that some of the announcements take a variable, represented in the
message by a ``%``-character. You can leave this character out if you
don't want the variable in the message. It is not possible to add
variables to announcements that don't take them by default, and
announcements that take one variable cannot take more than that one
variable.

If you **don't want to see a specific announcement**, you have to set
its value to ``''`` (note: two single-quotes, not one double-quote).
This will cause MobArena to ignore the announcement. Note that simply
removing the node will not work, as MobArena forcefully adds it back in.
