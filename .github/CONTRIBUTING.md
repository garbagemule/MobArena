# Contributing to MobArena

:heart: First of all, thank you for your interest in contributing :heart:

MobArena is a community-driven project. From general feedback, bug reports, and feature suggestions to pull requests and full-fledged code revamps, nothing happens in this project without some sort of contribution from members of the community.

If you have questions or just want to get in touch for a chat or to provide some general feedback, please visit the MobArena Discord server or the MobArena IRC channel:

- Discord: [Instant Invite](https://discordapp.com/invite/sddqVJd)
- IRC (#mobarena @ EsperNet): [KiwiIRC Web Client](https://kiwiirc.com/nextclient/#irc://irc.esper.net/#mobarena)

Before you get started, please take a few moments to go through the relevant parts of this document to make sure the process goes as smoothly as possible :relaxed:

## How can I contribute?

There are plenty of things you can do to contribute, and they generally fall into these categories:

- Bug reports
- Feature suggestions
- Pull requests

If what you wish to contribute falls into a different category, please hop on Discord or IRC and let's have a chat about it!

## Ground rules

MobArena is an old project, and it has survived a great deal of changes both in-project and upstream in Minecraft and Bukkit/Spigot. Part of the reason for this is a strong focus on maintainability.

Contributions that significantly reduce maintainability of the project must have an extremely good case to be accepted, because such contributions may result in stagnation.

Some things that reduce maintainability:

- Direct integration with other plugins
  - Requires fallback behavior to function without the other plugin
  - Requires unplanned updates if the other plugin's API changes
- Implementation-specific features and NMS
  - Reduces server compatibility (CraftBukkit, Spigot, Paper)
  - Requires updates on every Minecraft update (NMS)
- Minecraft version-specific features
  - Removes compatibility with older versions

Other than that,

## Bug reports

Found a bug in the plugin? :bug:

Please have a look through the [list of issues](https://github.com/garbagemule/MobArena/issues) to see if it has already been reported. If not, feel free to [submit a new issue](https://github.com/garbagemule/MobArena/issues/new). Make sure your bug report follows the template - if it doesn't, it may be closed with a message asking for one that does.

Bug reports should contain as much information as possible. The more information there is about the bug, the easier it is to fix. Be sure to include the following when applicable:

- A description of the bug - what happens, and what should happen instead?
- Reproduction steps - how can we reproduce the bug?
- Config-file
- Stacktrace - if there is an error in the server log

## Feature suggestions

Got a cool idea for a feature? :bulb:

Please have a look through the [list of issues](https://github.com/garbagemule/MobArena/issues) to see if something similar has already been suggested. If not, feel free to [submit a new issue](https://github.com/garbagemule/MobArena/issues/new). Make sure your feature suggestion follows the template - if it doesn't, it may be closed with a message asking for one that does.

Feature suggestions should be thorough. If it involves new commands or config-file options, give an example of how you would expect them to look and behave. If it involves plugin integration, think of a examples of how things would work with and without that plugin.

## Pull requests

Worked hard on a bug fix or new feature? :muscle:

Feel free to submit a pull request for review, and the core team will give it a whirl and help you get it merged in.

Pull requests come in many shapes and sizes, and so does code. It's difficult to formulate precise guidelines for code style and architectural approaches, so the viability of a pull request will often be determined by the core team on a case-by-case basis.

For pull requests in general:

- Run unit tests before submitting a pull request.
- Make sure your pull request addresses an existing issue ([example](https://github.com/garbagemule/MobArena/pull/410)).
  - If there isn't one, create it.
  - Use the issue to discuss the changes and get feedback from the community and the core team about your approach.
- Each pull request should address one specific issue.
  - Submit multiple pull requests if you wish to address multiple things.
- Submit only what is necessary.
  - Don't blindly submit automatic changes made by your IDE ([example](https://github.com/garbagemule/MobArena/pull/314/files)).
- Try to follow the official [Java Code Conventions](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html).

For commit messages:

- Use present tense, imperative style ([explanation](https://stackoverflow.com/a/3580764/2221849)).
- Try to be descriptive. Explain the motivation for the changes and what impact they have on the general use of the plugin ([example](https://github.com/garbagemule/MobArena/commit/8d9764d8e9fc0b2af5d7a331728eac6a9e0f220c), [example](https://github.com/garbagemule/MobArena/commit/94d198c4d0c9cd492980fe28de3255bb932000dc)).
- Refer to the issue you are addressing in the description ([example](https://github.com/garbagemule/MobArena/commit/31aa4c15a1daa886d2f82e40736d5e45325d9be5)).

## The review process

Every contribution you make is open to review by the community itself, and healthy discussion is encouraged - who knows, maybe someone else can help contribute to your contribution!

The core team will decide when a given contribution is "ripe", and until then, the contributor may be requested to make some changes. These changes are typically requests for clarification of descriptions or cleanup of code.

Once a contribution is ripe, it progresses to the next stage. This typically means that it will be included in an upcoming sweep, in which issues are addressed, and pull requests are merged.
