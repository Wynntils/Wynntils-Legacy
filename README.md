<p align="center">
<img src="http://cdn.wynntils.com/wynntils%20logo%20-%20w%20paintstroke.png" width=30%>
<br>
<a href="https://discord.gg/ve49m9J"><img src="https://discordapp.com/api/guilds/394189072635133952/widget.png"></a>
<a href="http://ci.wynntils.com/job/Wynntils/"><img src="http://ci.wynntils.com/buildStatus/icon?job=Wynntils"></a>
<a href="https://github.com/Wynntils/Wynntils/blob/development/LICENSE"><img src="https://img.shields.io/badge/license-AGLP%203.0-green.svg"></a>
</p>

About Wynntils
========
Wynntils is a Wynncraft mod that seeks to enhance the user's gameplay with a variety of customizable options and additions.
If you find any bug or have crashed because of the mod, please report it at our <a href="https://discord.gg/SZuNem8">official Discord server</a>!

Setup the Workspace
========
To set up the workspace, you'll need to run the Forge ``setupDecompWorkspace`` gradle command, with that all the Forge source will be generated. It's that easy!
<br> Building the mod is even simpler. Just call the ``buildDependents`` and the artifact should be generated in `build/libs`.

You can also setup a test environment. Using IntelliJ, execute the ``genIntellijRuns`` task, which will generate a run environment for you.
Then go to `Preferences` > `Build, Execution, Deployment` > `Gradle`, select `Wynntils` in the list of Gradle projects, and make sure `Build and run using` is set to `IntelliJ IDEA`.
Go to `Edit Configurations`, select `Minecraft Client`, and in `Program Arguments` add `--username <username> --password <password>` with your Minecraft username and password.
 
Pull Request
========
All pull requests are welcome. We'll analyse it and if we determine it should a part of the mod, we'll implement it.

We welcome all forms of assistance. =)

<strong>If you would like to make a pull request, please compare and merge it with Wynntils:development rather than Wynntils:master.</strong>

License
========

Wynntils is licensed over the license [GNU Affero General Public License v3.0](https://github.com/Wynntils/Wynntils/blob/development/LICENSE)<br>
All the assets **are over Wynntils domain © Wynntils**.
