<p align="center">
<img src="http://cdn.wynntils.com/wynntils%20logo%20-%20w%20paintstroke.png" width=30%>
<br>
<a href="https://discord.gg/ve49m9J"><img src="https://discordapp.com/api/guilds/394189072635133952/widget.png"></a>
<a href="http://ci.wynntils.com/job/Wynntils/"><img src="http://ci.wynntils.com/buildStatus/icon?job=Wynntils"></a>
<a href="https://github.com/Wynntils/Wynntils/blob/development/LICENSE"><img src="https://img.shields.io/badge/license-AGPL%203.0-green.svg"></a>
</p>

# About Wynntils
Wynntils is a Wynncraft mod that seeks to enhance the user's gameplay with a variety of customizable options and additions.
If you find any bug or have crashed because of the mod, please report it at our <a href="https://discord.gg/SZuNem8">official Discord server</a>!

# Setup the Workspace
To set up the workspace, just import the project as a gradle project into your IDE
<br> To build the mod just call the `buildDependents` and the artifact should be generated in `build/libs`.

You can also setup a test environment. Run the task for generating run configurations for your IDE

* Eclipse - `genEclipseRuns`
* Intellij - `genIntellijRuns`
* VSCode - `genVSCodeRuns`

When launching a run configuration use the generated run configuration

* All IDEs - `runClient`

## Authenticating
To authenticate in the development environment, you should use <a href="https://github.com/DJtheRedstoner/DevAuth">DevAuth</a>.
Download and place the `forge-legacy`.jar from their <a href="https://github.com/DJtheRedstoner/DevAuth/releases">releases</a> in `run/mods`.
When you first launch the game with the generated `runClient` task, there will be a prompt in the console with a link to authenticate.
No other configuration is required.

### Alternative Accounts
This repository is pre-configured with one main and one alternative account.
To use the alternative account, you should duplicate, then edit the generated `runClient` configuration.
In the JVM arguments for your duplicated configuration, add `-Ddevauth.account=alt`.
You will be prompted to authenticate with the new account when you run the new instance.

To add more alternative accounts, you should edit `.devauth/config.toml` in your local copy of this repository and follow the existing entries as examples.
You may also refer to the DevAuth repository for more information.

# Pull Request
All pull requests are welcome. We'll analyse it and if we determine it should a part of the mod, we'll implement it.

We welcome all forms of assistance. =)

<strong>If you would like to make a pull request, please compare and merge it with Wynntils:development rather than Wynntils:production.
<br>Additionally, please name your pull request according to the <a href="https://www.conventionalcommits.org/en/v1.0.0/#summary">Conventional Commits</a> specification.</strong>

# License
Wynntils is licensed over the license [GNU Affero General Public License v3.0](https://github.com/Wynntils/Wynntils/blob/development/LICENSE)<br>
All the assets **are over Wynntils domain © Wynntils**.
