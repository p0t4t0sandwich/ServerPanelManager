# PanelServerManagerPlugin

## About

A plugin that allows you to manage your Panel's game servers from within minecraft. This plugin is still in development and is not quite ready for production use. Feel free to mess around with it and report any bugs you find, or any features you would like to see.

Feel free to create an issue/PR if:

- you find a bug
- you need to port this mod/plugin to an unsupported platform/version. Older MC implementations are built on an as-needed basis.
- your panel isn't supported yet. We can do some digging to see if it's possible to add support for it.
- you have suggestions around the group/task system

## Commands

### /psm

`/psmb` for BungeeCord

`/psmc` for Client-side

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

## Permissions

```
psm -- Allows access to all commands
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
    tasks:
      sendboop:
        command: send {server} say boop!
        interval: 60 # in seconds
        conditions:
          1:
            placeholder: playercount
            operator: ==
            value: 0
```

#### Placeholders

- playercount -- the number of players on the server

## TODO:

Contributions and suggestions are welcome! Just open an issue or a pull request, and I'll get to it as soon as I can.

### Bugs

- [ ] Tasks don't pause, the thread just keeps running

### General

- [ ] Add a command to reload the config
- [ ] Forge support -- try and generalize the fabric command and see if I can get it to work with forge, as both use brigadier
- [ ] Multi-Fabric version support -- some gradle project voodoo required
- [ ] Fabric client side chat listener -- do it like this? https://www.reddit.com/r/fabricmc/comments/wg7jrx/onchat/
- [ ] Quilt support
- [ ] Sponge support
- [ ] Velocity support
- [ ] Set up proper gradle projects for each platform -- need help with this
- [ ] Add webhook support here and there

### Permissions

- [ ] Design a permission scema -- `psm.<command>.<server>` - `psm.<command>.<group>`
- [ ] Add permissions for each command -- `psm.command.<command>`
- [ ] Add permissions for each command for each server -- `psm.<command>.<server>`
- [ ] Add permissions for each command for each group -- `psm.<command>.<group>`

- [ ] Set up dynamic permissions checks
  - [ ] Bukkit and BungeeCord will be easy, just use the `hasPermission` method
  - [ ] Fabric will be more fun to implement -- either look into how Fabric does it, or require LuckPerms to doll out specific permissions
  - [ ] Similar dealio for Forge

### Misc

- [ ] various in-game event triggers
  - [ ] integrate with the AMP scheduler
  - [ ] set up webhook support

- [ ] Update mods/plugins from url? -- maybe add a `update` command to the group system?
  - [ ] Optional: include regex to delete old files

- [ ] Server console regex trigger -- maybe add a `regex` command to the group system?

- [ ] No-start status fix -- plop a proper `server started` message in the console -- Fix for Forge 1.12.2 v14.23.5.2858 and FTB Revelation

- [ ] Add the ability to sync the config with a database? -- would need one AMPServerManager process to act as a main process and handle all the group tasks.
  - [ ] hot reload the config when it changes -- how? -- LuckPerms does it, maybe subscribe to database changes?

- [ ] WatchFerret?
  - [ ] Should be able to hook into the Groups system and run the checks on a timer -- maybe add a `watch` command to the group system? -- Store in Groups class in some sort of variables property