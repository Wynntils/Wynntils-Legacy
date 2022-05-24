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

When launching a run configuration use the generated run configuration

* All IDEs - `runClient`

<h2>Authenticating</h2>

You will need to edit your `GRADLE_HOME/gradle.properties`,

* Windows - `C:\Users\<your username>\.gradle\gradle.properties`
* Unix - `~/.gradle/gradle.properties`

to include your UUID, username, and access token.

* `mc_uuid=` is your minecraft uuid, trimmed / without the dashes
* `mc_username=` is your minecraft username, not email
* `mc_accessToken=` is your access token, you may be able to find it at `.minecraft/launcher_accounts.json` or `.minecraft/launcher_profiles.json`

Alternatively, you may use <a href="https://github.com/DJtheRedstoner/DevAuth">DevAuth</a> to authenticate.
<br> Place the `forge-legacy` .jar in `run/mods` and configure as documented in the DevAuth readme.

Pull Request
========
All pull requests are welcome. We'll analyse it and if we determine it should a part of the mod, we'll implement it.

We welcome all forms of assistance. =)

<strong>If you would like to make a pull request, please compare and merge it with Wynntils:development rather than Wynntils:production.</strong>

License
========

Wynntils is licensed over the license [GNU Affero General Public License v3.0](https://github.com/Wynntils/Wynntils/blob/development/LICENSE)<br>
All the assets **are over Wynntils domain © Wynntils**.
