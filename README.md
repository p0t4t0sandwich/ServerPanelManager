# ServerPanelManager

## About

A plugin that allows you to manage your Panel's game servers from within minecraft.

Link to our support: [Discord](https://discord.gg/NffvJd95tk)

Feel free to create an issue/PR if:

- you find a bug
- you need to port this mod/plugin to an unsupported platform/version. Older MC implementations are built on an as-needed basis.
- your panel isn't supported yet. We can do some digging to see if it's possible to add support for it.
- the game event you want to listen for hasn't been implemented yet.

## Download

- [GitHub](https://github.com/p0t4t0sandwich/ServerPanelManager/releases)
- [Spigot](https://www.spigotmc.org/resources/template.xxxxxx/)
- [Hangar](https://hangar.papermc.io/p0t4t0sandwich/Template)
- [Modrinth](https://modrinth.com/plugin/template)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/template)

### Compatibility Cheatsheet

| Server type | Versions    | Jar Name                                       |
|-------------|-------------|------------------------------------------------|
| All 1.19    | 1.19-1.19.4 | `ServerPanelManager-<version>-1.19.jar`        |
| All 1.20    | 1.20-1.20.1 | `ServerPanelManager-<version>-1.20.jar`        |
| Bukkit      | 1.8-1.20.1  | `ServerPanelManager-<version>-bukkit.jar`      |
| BungeeCord  | 1.20-1.20.1 | `ServerPanelManager-<version>-bungee.jar`      |
| Velocity    | API v3      | `ServerPanelManager-<version>-velocity.jar`    |
| Fabric 1.17 | 1.17-1.19.4 | `ServerPanelManager-<version>-fabric-1.17.jar` |
| Fabric 1.20 | 1.20-1.20.1 | `ServerPanelManager-<version>-fabric-1.20.jar` |
| Forge 1.19  | 1.19-1.19.4 | `ServerPanelManager-<version>-forge-1.19.jar`  |
| Forge 1.20  | 1.20-1.20.1 | `ServerPanelManager-<version>-forge-1.20.jar`  |

## Dependencies

### Required Dependencies

- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) - For Fabric

### Optional Dependencies

- [LuckPerms](https://luckperms.net/) - For user prefix/suffix support and permission support on Forge/Fabric

## Supported Panels

- [CubeCoders AMP](https://cubecoders.com/AMP)

## Supported Game Events

- Player Advancement
- Player Death
- Player Join
- Player Leave
- Player Message
- Player Server Change (BungeeCord and Velocity only)
- Server Start
- Server Starting
- Server Stop

## Commands and Permissions

| Command | Permission    | Description                 |
|---------|---------------|-----------------------------|
| `/spm`  | `spm.command` | Main command for the plugin |
| `/spmb` | `spm.command` | Main command for the plugin |
| `/spmv` | `spm.command` | Main command for the plugin |
| `/spmc` | `N/A`         | Main command for the plugin |

### Usage

```
# General
start <server>
stop <server>
restart <server>
kill <server>
send <server> <command>
status <server>

# AMP only
sleep <server>
players <server>
backup <server> [name] [description] [sticky <- true or false]
```

## Configuration

### CubeCoders AMP

#### ADS
```yaml
---
panels:
  <panel name>:
    type: cubecodersamp
    host: http://localhost:8080/
    username: admin
    password: admin
```

#### Servers

```yaml
servers:
  # Server using the ADS for authentication
  <server name>:
    panel: <panel name>
    # Instance Name -- found in AMP
    name: Minecraft01
    # instance ID -- right click and manage in new tab, this will be the bit after the ?id= in the URL
    # The plugin can also fetch this automatically if you leave it blank
    id: 1ty3j38u

  # Server using the instance's web port for authentication
  <server name 2>:
    panel: ampinstance
    host: http://localhost:8081/
    username: admin
    password: admin
```

### Groups

```yaml
groups:
  <group name>:
    name: Group1
    servers:
      - <server name>
      - <server name>
```

#### Placeholders

- playercount -- the number of players on the server

## TODO:

### General

- Fabric client side chat mixin -- do it like this? https://www.reddit.com/r/fabricmc/comments/wg7jrx/onchat/
- Add webhook support here and there
- various in-game event triggers
  - integrate with the AMP scheduler
  - webhooks

### Additional Panels

- Pterodactyl: https://github.com/mattmalec/Pterodactyl4J
- Multicraft: https://github.com/pavog/Multicraft-api / https://www.multicraft.org/site/docs/api
- Pebblehost: https://panel.pebblehost.com/support/apiDocs / https://help.pebblehost.com/en/minecraft/using-the-pebblehost-game-panel-api
- PufferPanel: https://speca.io/PufferPanel/pufferd/2.0

### Misc

- Update mods/plugins from url? -- maybe add a `update` command to the group system?
  - Optional: include regex to delete old files

- Server console regex trigger -- maybe add a `regex` command to the group system?

- No-start status fix -- plop a proper `server started` message in the console -- Fix for Forge 1.12.2 v14.23.5.2858 and FTB Revelation

- Add the ability to sync the config with a database? -- would need one AMPServerManager process to act as a main process and handle all the group tasks.
  - hot reload the config when it changes -- how? -- LuckPerms does it, maybe subscribe to database changes?

## Release Notes
- Near complete rewrite
- Implemented game events across all platforms
- Generalized the command a bit better
- Created Abstract player class to handle permissions and anything player related
- Created dynamic permission system to allow for more granular permissions
- Added a reload command
