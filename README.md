<p align="center">
<img src="http://cdn.wynntils.com/wynntils%20logo%20-%20w%20paintstroke.png" width=30%>
<br>
<a href="https://discord.gg/ve49m9J"><img src="https://discordapp.com/api/guilds/394189072635133952/widget.png"></a>
<a href="http://ci.wynntils.com/job/Wynntils/"><img src="http://ci.wynntils.com/buildStatus/icon?job=Wynntils"></a>
<a href="https://github.com/Wynntils/Wynntils/blob/development/LICENSE"><img src="https://img.shields.io/badge/license-AGPL%203.0-green.svg"></a>
</p>

About Wynntils
========
Wynntils is a Wynncraft mod that seeks to enhance the user's gameplay with a variety of customizable options and additions.
If you find any bug or have crashed because of the mod, please report it at our <a href="https://discord.gg/SZuNem8">official Discord server</a>!

Setup the Workspace
========
To set up the workspace, just import the project as a gradle project into your IDE
<br> To build the mod just call the ``buildDependents`` and the artifact should be generated in `build/libs`.

You can also setup a test environment. Run the task for generating run configurations for your IDE

* Eclipse - `genEclipseRuns` 
* Intellij - `genIntellijRuns`
* VSCode - `genVSCodeRuns`

When launching a run configuration use the run configuration for your IDE

* Eclipse - `runEclipseClient`
* Intellij - `runIntellijClient`

two input boxes will appear

1.  Email that you use to log into your mojang account
2.  Password that you use to log into your mojang account

If you want to have atleast some of your information preentered or you are using VSCode edit the `gradle.properties` in the `.gradle` directory in your user directory and add either or both:

* `minecraftEmailUsername=` then your email that you use to login to your mojang account
* `minecraftPassword=` then your password that you use to login to your mojang account 

 
Pull Request
========
All pull requests are welcome. We'll analyse it and if we determine it should a part of the mod, we'll implement it.

We welcome all forms of assistance. =)

<strong>If you would like to make a pull request, please compare and merge it with Wynntils:development rather than Wynntils:production.</strong>

License
========

Wynntils is licensed over the license [GNU Affero General Public License v3.0](https://github.com/Wynntils/Wynntils/blob/development/LICENSE)<br>
All the assets **are over Wynntils domain © Wynntils**.
