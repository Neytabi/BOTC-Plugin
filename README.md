# 🩸 Blood on the Clocktower - Minecraft Plugin (BOTC)

![Modrinth Version](https://img.shields.io/badge/Modrinth-v2.0--stable-green?style=for-the-badge&logo=modrinth)
![PaperMC](https://img.shields.io/badge/PaperMC-1.20%20%7C%201.21-blue?style=for-the-badge&logo=papermc)
![Java Version](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

A complete and immersive Minecraft plugin to play **Blood on the Clocktower (BOTC)**, built for **PaperMC**. Custom-designed to drastically simplify the **Storyteller's (GM)** tasks through interactive GUIs, automated Day/Night cycles, and advanced voice chat integrations.

> 🛠️ **Developers**: [Neytabi](https://github.com/Neytabi) (Lead Developer) & [Pill0N_](https://github.com/Pill0N_) (Contributor)  
> 📜 **License**: Open-Source under the MIT License

---

## 📌 Summary
- [📋 Prerequisites & Versions](#-prerequisites--versions)
- [🔊 Voice Integration (Simple Voice Chat & Sound Physics)](#-voice-integration-simple-voice-chat--sound-physics-remastered)
- [📖 Grimoire & Registry System (Zero-Command Interactivity)](#-grimoire--registry-system-zero-command-interactivity)
- [✨ Core Features](#-core-features)
- [🗺️ Multi-Map Support & Presets](#%EF%B8%8F-multi-map-support--presets)
- [⌨️ Command Guide](#%EF%B8%8F-command-guide)
- [⚙️ Installation & Quick Start](#%EF%B8%8F-installation--quick-start)
- [🤝 Open Source & Contributions](#-open-source--contributions)

---

## 📋 Prerequisites & Versions

| Element | Version / Requirement | Note |
| :--- | :--- | :--- |
| **Minecraft Server** | **PaperMC** `1.20.x` to `1.21.x` (Tested on `1.21.1`) | Spigot/CraftBukkit not recommended (requires Paper / Adventure API) |
| **Java** | **Java 21** | Required to compile and run the server |
| **Voice Plugin (Server)** | **[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)** `v2.5.0+` | **Required** for voice moderation, automated channels, and muting |
| **Audio Mod (Clients)** | **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** | **HIGHLY RECOMMENDED** (Blocks voice traversal through walls) |

---

## 🔊 Voice Integration (Simple Voice Chat & Sound Physics Remastered)

### 🎙️ Native Integration with Simple Voice Chat API
The plugin intercepts audio packets directly at the network level (`MicrophonePacketEvent`) using the Simple Voice Chat API:
- **Global Silence Mute** (`/botc silence`): Instantly mutes all players' microphones during Storyteller announcements.
- **Dead & Ghost Management**: Dynamic muting/unmuting on the fly.
- **Automated Tribunal Channels**: During the sitting phase (`/botc assis`), players are automatically grouped into a dedicated voice channel while preserving Storyteller control.
- **Storyteller Priority Access (OP)**: Storytellers (OPs) systematically bypass mutes and retain priority voice access.
- **Reload Resilience (PlugMan Fix)**: Includes a reflection reconnect mechanism to restore the voice API following hot reloads.

### 🔇 Why install *Sound Physics Remastered* on the Client side?
In Blood on the Clocktower, night is a critical phase where every player retreats to their individual room. In vanilla Simple Voice Chat, proximity audio calculates distances in a straight line, meaning **players can hear each other whispering through bedroom walls**.

> ⚠️ **IMPORTANT: The necessity of Sound Physics Remastered**  
> By asking players to install the client mod **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** (Fabric/NeoForge/Forge):
> 1. Sound calculations account for block geometry and wall thickness.
> 2. Walls absorb and block sound attenuation: **no whispers cross closed doors or bedroom partitions**.
> 3. The acoustic atmosphere becomes fully realistic, guaranteeing total secrecy for night discussions and whispers.

---

## 📖 Grimoire & Registry System (Zero-Command Interactivity)

To keep the game smooth, **neither the Storyteller nor the Players need to memorize complex commands**. Everything is controllable via interactive inventory items.


| 🧙‍♂️ STORYTELLER (GM) | 🧑‍🌾 PLAYERS |
| :--- | :--- |
| **🔮 The Storyteller's Grimoire** | **📕 Tribunal Registry** |
| • Player heads & seating order | • Raise hand (Vote YES) |
| • Statuses (Alive / Dead / Tokens) | • Nominate a suspect (Accusation) |
| • Click: Action Menu (Roles/Exec) | • Secret Role Reminder |
| • Shift+Click: Teleport to player | • Ask GM a private question |
| • Tools: Flash-TP, Nametags, Setup | • Request speech time (Ghosts) |


### 🔮 For the Storyteller (GM): *The Storyteller's Grimoire*
Upon joining or resetting (`/botc reset`), the GM receives an **Enchanted Book** named **"📖 The Storyteller's Grimoire"**.
Right-clicking with the book in hand opens:
1. **Main GUI Menu**: 1-click access to the Day/Night cycle, death announcements, seating gather, voice moderation, nametag visibility, and map votes.
2. **Grimoire View (`GrimoireView`)**: An interactive double chest UI displaying the head of each player (in their exact seating order around the table).
   - **Left Click**: Opens the action submenu (`ActionMenuView`) to assign secret roles, kill/revive players, or manage ghost vote tokens.
   - **Shift + Click**: Instantly teleports the Storyteller behind the selected player.
   - **Ender Pearl (Slot 45)**: Flash-teleports the Storyteller to the center of the Tribunal.
   - **Nametag (Slot 46)**: Toggles player nametag anonymity/visibility above their heads.

### 📕 For Players: *The Tribunal Registry & Role Book*
Players receive two books upon arrival:
1. **"Role Book" (Written Book)**: A complete summary of all script roles (Demons, Minions, Townsfolk, Outsiders) along with the official wake order for the First Night and subsequent Nights.
2. **"Tribunal Registry" (Book)**: An interactive 9-slot menu (`PlayerRegistryView`):
   - 🟢 **Raise Hand (Vote YES)**: Registers a vote for the active nomination (automatically handles ghost vote token consumption).
   - 📜 **Nominate a Suspect**: Opens a registry (`NominationView`) listing living players to launch a public accusation (includes automated handling for **Virgin** and **Drunk** abilities).
   - 📖 **Secret Role Reminder**: Displays the GM-assigned role and its description (displays the false role if the player is the Drunk!).
   - 🪶 **Ask a Question**: Allows the player to type a question in chat that is transmitted **exclusively in private to the Storyteller**.
   - 📄 **Request Speech**: Allows ghosts to send a clickable `[GRANT]` alert to Storytellers to speak at the Council.

---

## ✨ Core Features

- 🌗 **Animated & Guided Day / Night Cycle**:
  - Smooth sky transitions (accelerated 60 FPS sun movement with atmospheric drumbeats).
  - **3D Particle Compass / GPS**: At nightfall, every player is guided by a glowing particle arrow toward their assigned room. At daybreak, the arrow guides everyone back to the Tribunal.
  - **Door Head System**: At night, each player's head automatically appears on their bedroom door.
- 🪑 **Secure Invisible Chairs & Tribunal**:
  - Automatic seating of players onto invisible seats (`botc_chair`) ordered around the discussion circle.
  - **Anti-Dismount Lock**: Prevents players from standing up during official debates.
  - **Secure Reconnection**: If a player crashes or disconnects, they are automatically placed back onto their chair upon reconnecting.
- 👑 **Role System & Real vs Fake BossBar**:
  - 1-click role assignment via themed menus (Blue Wool = Townsfolk, Yellow = Outsiders, Orange = Minions, Red = Demons).
  - **Drunk Management**: The Storyteller can right-click assign a role to create a Drunk (the GM sees their true "Drunk" role in the Grimoire, but the player sees a real Townsfolk role on their screen and BossBar!).
  - **Automatic Scarlet Woman Transfer**: If the Imp dies during a vote while 5 or more players remain alive, the Scarlet Woman automatically inherits the Imp role with a private alert.
  - **Permanent BossBar**: A subtle purple/gold banner at the top of the screen reminding each player of their secret role.
- ⚡ **Execution Animations & Lightning**:
  - Dramatic deaths with Wither sounds, dark smoke particles, and a Barrier helmet head.
  - Configurable lightning modes (`player` to strike the player, or `local` to strike the execution platform).
- 🏷️ **Anonymity Management (Nametags)**:
  - Global Scoreboard team-based nametag hiding to prevent players from spotting each other through walls at night.

---

## 🗺️ Multi-Map Support & Game Presets

The plugin supports an unlimited number of maps and game arenas saved independently in the configuration.

- **Easy Creation**: Create presets (`/botc preset create <name>`) directly in-game or via interactive chat (Anvil menu).
- **Independent Saves**: Each preset retains its own chair positions, room locations, tribunal center, and execution platform.
- **🗳️ In-Chat Map Voting (`/botc mapvote`)**:
  - The Storyteller can launch a 30-second interactive vote in chat (`/botc mapvote start`).
  - Players click on the arena of their choice.
  - At the end of the sound countdown, the map with the most votes is automatically loaded by the plugin!

---

## ⌨️ Command Guide

All commands run through the root command `/botc`:

### 🧙‍♂️ Storyteller Commands (OP)

| Command | Description |
| :--- | :--- |
| `/botc setup` / `/botc admin` | Opens the main administration and configuration menu |
| `/botc start` / `/botc reset` | Resets the game (revives players, issues books, resets roles and tokens) |
| `/botc jour` | Triggers morning (celestial animation + compass pointing to Tribunal) |
| `/botc nuit` | Triggers night (celestial animation + compass pointing to rooms + door heads) |
| `/botc tempslibre` | Frees player movements and voice chat |
| `/botc conseil` | Displays the compass pointing to the council room |
| `/botc assis` | Sits all players on their assigned seats and creates the voice channel |
| `/botc debout` | Frees players from their seats |
| `/botc silence` | Mutes all villagers' microphones via the SVC API |
| `/botc paroleall` | Restores voice speech permissions to all players |
| `/botc grantparole <player>` | Grants speech time to a specific player |
| `/botc vcmute <player>` | Mutes a specific player in voice chat |
| `/botc vcunmute <player>` | Unmutes a specific player in voice chat |
| `/botc execute <player>` | Officially executes a condemned player (animations + platform) |
| `/botc mort` | Announces the list of daily deaths with a sound effect |
| `/botc hidenames` | Hides player nametags above their heads |
| `/botc shownames` | Restores player nametags |
| `/botc mapvote start` | Starts a 30s map election in chat |
| `/botc mapvote stop` | Immediately closes the map election and loads the winner |
| `/botc preset <select/create/delete/list>` | Manages map presets |
| `/botc settribunal` | Sets your current position as the center of the Tribunal |
| `/botc addchair` | Registers a new chair at your current position |
| `/botc delchairs` | Deletes all chairs for the active preset |
| `/botc showchairs` | Displays chairs for the active preset with particles |
| `/botc addroom` | Registers a bedroom door at your current position |
| `/botc delrooms` | Deletes all rooms for the active preset |
| `/botc showrooms` | Displays rooms for the active preset with particles |
| `/botc setplayerdeath` | Registers your position as the execution platform |
| `/botc setlightning <player\|local>` | Sets the execution lightning mode |

### 🧑‍🌾 Player Commands (Everyone)

| Command | Description |
| :--- | :--- |
| `/botc help` | Displays in-game help |
| `/botc voteoui` | Raises hand to vote YES on the active nomination |
| `/botc mapvote choose <map>` | Votes for a map during an active election |

---

## ⚙️ Installation & Quick Start

1. Download the file `botc-plugin-2.0-stable.jar`.
2. Drop the `.jar` file into your **PaperMC** server's `plugins/` directory (1.20 or 1.21).
3. Make sure **[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)** is installed in the `plugins/` folder.
4. Start your server. The configuration file `plugins/BotcPlugin/config.yml` will be generated automatically.
5. *(Optional but highly recommended)* Encourage your players to install the **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** mod on their Fabric/Forge client for perfect sound occlusion through walls.

---

## 🤝 Open Source & Contributions

This project is **100% open-source** distributed under the **MIT License**.  
You are free to use it, modify it, or contribute to the source code!

- 📦 **GitHub Source Code**: [https://github.com/Neytabi/BOTC-Plugin](https://github.com/Neytabi/BOTC-Plugin)
- 🐛 **Report a Bug / Suggest an Idea**: [GitHub Issues](https://github.com/Neytabi/BOTC-Plugin/issues)
- 🤝 **Pull Requests**: Contributions are welcome!
