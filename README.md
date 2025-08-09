# Overlord

![License](https://img.shields.io/badge/license-CC%20BY--NC%204.0-lightgrey.svg)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-green.svg)
![Spigot Version](https://img.shields.io/badge/Spigot-1.21-orange.svg)

## Description

**Overlord** is a Minecraft plugin designed to provide advanced management of protected regions, custom commands, and player statistics tracking. It was created to offer enhanced control over player interactions on a **Spigot/PaperMC 1.21** server.

### Features

- Protected region management with customizable settings (PVP, building, explosions, etc.).
- Custom commands for managing regions and players.
- Scoreboard system to track players' deaths.
- Save and load protected regions using `config.yml`.
- Integration with server events through a main listener.

### Installation

1. Download the latest version of the plugin [here](#).
2. Place the `.jar` file in your Minecraft server's `plugins` folder.
3. Restart the server.
4. The plugin is now installed and ready to use!

### Commands

| Command              | Description                                | Permission         |
|----------------------|--------------------------------------------|--------------------|
| `/resetdeaths`       | Resets the death scoreboard                | `permission.op`    |
| `/createnewworld <name>` | Creates a new world                     | `permission.op`    |
| `/wando`             | Gives a protection axe                  | `permission.op`    |
| `/protect`           | Protects a region                         | `permission.op`    |
| `/region`            | Advanced region management                | `permission.op`    |

### Configuration

The plugin uses a `config.yml` configuration file to store protected regions and their settings. Here’s an example configuration:

```yaml
protectedRegions:
  0:
    name: "Zone1"
    firstPoint: "world, 100, 64, 100"
    secondPoint: "world, 200, 64, 200"
    pvpAllowed: false
    buildAllowed: false
    breakBlockAllowed: false
    explosionTNTAllowed: false
    explosionCreeperAllowed: false
```

## Project Status
- 🚧 In Development
- ✅ Stable

### Dependencies

- Spigot/PaperMC 1.21 or higher.

### Authors

- **SKZ_azerty** - Lead Developer, responsible for overall plugin architecture and implementation.
- **Alpha** - Tutor and architecture

### Contact

If you have any questions or need support, feel free to reach out via:

- **SKZ_azerty** - Lead Developer:
    - **Email**: [SKZ_azerty@gmail.com](github.managing536@passinbox.com)
    - **GitHub**: [https://github.com/SKZazertyOF](https://github.com/SKZazertyOF)
- **Alpha** - Developer:
    - **Nothing sry**

## License

![License](https://img.shields.io/badge/license-CC%20BY--NC%204.0-lightgrey.svg)

This project is licensed under the **Creative Commons Attribution - Non Commercial 4.0 International (CC BY-NC 4.0)** license. This means that:

- You may **share**, **adapt**, and **modify** the work for **non-commercial** purposes.
- You must **credit the author** appropriately (i.e., mention their name and a reference to the original work, but not in a way that suggests the author endorses you or your use).
- You may not use this work for **commercial** purposes.

The full terms of the license can be viewed on the [Creative Commons website](https://creativecommons.org/licenses/by-nc/4.0/).

#### Specific Conditions:

- You may not use the work for commercial purposes.
- Modifications must be distributed under the same license.
