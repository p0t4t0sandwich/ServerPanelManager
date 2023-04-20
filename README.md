# AMPServerManagerPlugin

## About

A plugin that allows you to manage your AMP game servers from within minecraft. This plugin is still in development and is not quite ready for production use. Feel free to mess around with it and report any bugs you find, or any features you would like to see.

## Commands

### /amp

`/bamp` for BungeeCord

`/camp` for Client-side

```
/amp start <server>
/amp stop <server>
/amp restart <server>
/amp kill <server>
/amp sleep <server>
/amp send <server> <command>
/amp status <server>
```

## Permissions

```
ampservermanager.amp -- Allows access to all commands
```

## Configuration

### AMP

```yaml
amp:
  # The URL to your main AMP controller/ADS
  host: http://localhost:8080
  # The username to use for authentication -- recommended to use a service account with limited permissions
  username: admin
  password: admin
```

### Servers

```yaml
servers:
  <server name>:
    # Instance Name -- found in AMP
    name: Minecraft01
    # instance ID -- right click and manage in new tab, this will be the bit after the ?id= in the URL
    # The plugin can also fetch this automatically if you leave it blank
    id: 1ty3j38u
```

### Groups

```yaml
# Experimental feature, still a work in progress
groups:
  <group name>:
    name: Group1
    servers:
      - <server name>
      - <server name>
    tasks:
      - name: Restart
        command: restart
        interval: 60 # in seconds
        conditions:
          - placeholder: playercount
            operator: ==
            value: 0
```

#### Placeholders

- playercount -- the number of players on the server

## TODO:
- [ ] Add permissions for each command -- `ampservermanager.amp.command.<command>`
- [ ] Add permissions for each command for each server -- `ampservermanager.amp.command.<command>.<server>`
- [ ] Finalize configuration for groups
  - [ ] Add timed tasks
  - [ ] Add conditionals
  - [ ] think of a placeholder spec for conditionals
  - [ ] Build a parser for conditionals and placeholders
- [ ] Set up group commands -- `/amp group <group> <command>`
- [ ] Add a command to reload the config
- [ ] Design an API for other plugins to use -- that way this plugin doesn't become a spaghetti mess
- [ ] Add commands to manage/add to the config from within the game
  - [ ] Add a command to list all servers -- `/amp server list`
  - [ ] Add commands to add/remove servers
    - [ ] `/amp server add <server name> <instance name> [instance id]`
    - [ ] `/amp server remove <server name>`

  - [ ] Add a command to list all groups -- `/amp group list`
  - [ ] Add commands to add/remove servers from groups
    - [ ] `/amp group <group> server add <server name>`
    - [ ] `/amp group <group> server remove <server name>`

  - [ ] Add a command to list all tasks in a group -- `/amp group <group> task list`
  - [ ] Add commands to add/remove tasks from groups
    - [ ] `/amp group <group> task add <task name> <command> <interval> <conditions>`
    - [ ] ^A parser to make that possible
    - [ ] `/amp group <group> task remove <task name>`

  - [ ] Add a command to list all conditionals in a task -- `/amp group <group> task <task> condition list`

  - [ ] Make AMPServerManagerPlugin function as a standalone terminal program, since it's all Java 8 anyway.