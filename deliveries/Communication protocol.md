# Protocollo di comunicazione

This is a list of all possible messages that can be exchanged between client and server. We decided to use JSON to encode messages.

Every message has a **type** field which is either *ping/pong*, *login* or *action*

# Ping / pong

Every time a new client connects to the server, the latter sends a ping message and awaits a pong response. This also happens every x seconds, to ensure the client is still up.

```
+--------+                                         +--------+
| Client |                                         | Server |    
+--------+                                         +--------+
    |                                                   |
    |                    { "type": "ping" }             |
    |                <-------------------------         |
    |                                                   |
    |                                                   |
    |                    { "type": "pong" }             |
    |                ------------------------->         |
    |                                                   |

```

# Login

Login messages are used to communicate information regarding the login phase of the game

## After new client connection

When a new client connects to the server, the latter sends a message which:

- If there is no game → asks for username and num of players
- If there is a game not full and not started → asks for username
- If there is already started → send *message L.E.1* and closes the connection

### L.1 - Creating a new game

```
+--------+                                         +--------+
| Client |                                         | Server |    
+--------+                                         +--------+
    |                                                   |
    |                    { "type": "ping" }             |
    |                <------------------------          |
    |                                                   |
    |                    { "type": "pong" }             |
    |                ------------------------>          |
    |                                                   |
    |                     message L.1.1                 |
    |                <-----------------------           |
    |                                                   |
    |                     message L.1.2                 | 
    |                ----------------------->           |
    |                                                   |
    |                     [OK] message L.B.1            | 
    |              <-------------------------           |
    |                  [ERR] message L.E.*              |
    |              <-------------------------           |
    |                                                   |
```

#### message L.1.1
```json
{
    "type": "login",
    "action": "create game",
    "message": "insert username and desired number of players"
}
```
#### message L.1.2
```json
{
    "type": "login",
    "action": "create game",
    "username": "Rick",
    "num players": 3
}
```

### L.2 - Joining an existing game

```
+--------+                                         +--------+
| Client |                                         | Server |    
+--------+                                         +--------+
    |                                                   |
    |                    { "type": "ping" }             |
    |                <------------------------          |
    |                                                   |
    |                    { "type": "pong" }             |
    |                ------------------------>          |
    |                                                   |
    |                     message L.2.1                 |
    |                <-----------------------           |
    |                                                   |
    |                     message L.2.2                 | 
    |                ----------------------->           |
    |                                                   |
    |                     [OK] message L.B.2            | 
    |              <-------------------------           |
    |                  [ERR] message L.E.*              |
    |              <-------------------------           |
    |                                                   |
```


#### message L.2.1
```json
{
    "type": "login",
    "message": "insert username",
    "action": "join game",
    "game": {
        "players": ["Clod"],
        "num players": 3,
    }
}
```

#### message L.2.2
```json
{
    "type": "login",
    "action": "join game",
    "username": "Rick"
}
```

### L.E - Login errors

#### message L.E.1
```json
{
    "type" : "login",
    "error" : {
        "code" : 1,
        "message" : "A game is already in progress. The connection will be closed"
    }
}
```
#### message L.E.2
```json
{
    "type" : "login",
    "error" : {
        "code" : 2,
        "message" : "An user with this username is already logged in. Please select another username"
    }
}
```

#### message L.E.3
```json
{
    "type" : "login",
    "error" : {
        "code" : 3,
        "message" : "Invalid action"
    }
}
```

### L.B - Login Broadcast messages


#### message L.B.1
```json
{
    "type": "login",
    "message": "game created",
    "game": {
        "players": ["Rick"],
        "num players": 3,
    }
}
```
#### message L.B.2
```json
{
    "type": "login",
    "message": "player has joined",
    "game": {
        "players": ["Clod", "Rick"],
        "num layers": 3,
    }
}
```
#### message L.B.3
```json
{
    "type" : "login",
    "message" : "A new game is starting",
    "game": {
        "players": ["Clod", "Rick", "Giuse"],
        "numPlayers": 3,
    }
}
```

# Action

Action messages are used to communicate player choices from each client to the server, with a broadcast response from it.

Here are some example of possible moves:

## Planning Phase

Every player has to play an assistant.
> NOTE: ping/pong is omitted for brevity

```
+--------+                                        +--------+
| Client |                                        | Server |    
+--------+                                        +--------+
    |                                                  |
    |                   message A.0.1                  |
    |             <-------------------------           |
    |                                                  |
    |                   message A.1.1                  |
    |             ------------------------->           |
    |                                                  |
    |                 [OK] message A.B.1               | 
    |             <-------------------------           |
    |                [ERR] message A.E.*               |       
    |             <-------------------------           |
    |                                                  |
```


#### message A.0.1
```json
{
    "type": "action",
    "player": "Rick",
    "actions": {
        ["play assistant"]
    }
}
```
#### message A.1.1
```json
{
    "type": "action",
    "player": "Rick",
    "action": {
        "name": "play assistant",
        "args": {
            "value": 5
        }
    }
}
```

## Action Phase
Example of a player action phase
> NOTE: ping/pong is omitted for brevity

```
+--------+                                        +--------+
| Client |                                        | Server |    
+--------+                                        +--------+
    |                                                  |
    |                   message A.0.2                  |
    |             <-------------------------           |
    |                                                  |
    |                   [PLAYER CHOICE]                |
    |                   message A.2.1a                 |
    |             ------------------------->           |
    |                   message A.2.1b                 |
    |             ------------------------->           |
    |                                                  |
    |                 [OK] message A.B.2               | 
    |             <-------------------------           |
    |                [ERR] message A.E.*               |       
    |             <-------------------------           |
    |                                                  |
    |                                                  |
    |                        [ ... ]                   |
    |                                                  |
    |                                                  |
    |                   message A.0.3                  |
    |             <-------------------------           |
    |                                                  |
    |                   message A.2.2                  |
    |             ------------------------->           |
    |                                                  |
    |                 (OK) message A.B.3               | 
    |             <-------------------------           |
    |                (ERR) message A.E.*               |       
    |             <-------------------------           |
    |                                                  |
    |                                                  |
    |                   message A.0.4                  |
    |             <-------------------------           |
    |                                                  |
    |                   message A.2.3                  |
    |             ------------------------->           |
    |                                                  |
    |                 (OK) message A.B.4               | 
    |             <-------------------------           |
    |                (ERR) message A.E.*               |
    |             <-------------------------           |
    |                                                  |
```


#### message A.0.2
```json
{
    "type": "action",
    "player": "Rick",
    "actions": {
        ["move student", "play character"]
    }
}
```

#### message A.2.1a
```json
{
    "type": "action",
    "player": "Rick",
    "action": {
        "name": "move student to dining",
        "args": {
            "color": "GREEN",
        }
    }
}
```

#### message A.2.1b
```json
{
    "type": "action",
    "player": "Rick",
    "action": {
        "name": "move student to island",
        "args": {
            "color": "GREEN",
            "island": 3,
        }
    }
}
```
#### message A.0.3
```json
{
    "type": "action",
    "player": "Rick",
    "actions": {
        ["move mother nature", "play character"]
    }
}
```
#### message A.2.2
```json
{
    "type": "action",
    "player": "Rick",
    "action": {
        "name": "move mother nature",
        "args": {
            "num_steps": 5
        }
    }
}
```

#### message A.0.4
```json
{
    "type": "action",
    "player": "Rick",
    "actions": {
        ["fill from cloud", "play character"]
    }
}
```
#### message A.2.3
```json
{
    "type": "action",
    "player": "Rick",
    "action": {
        "name": "fill from cloud",
        "args": {
            "cloud": 0
        }
    }
}
```

### A.B - Action Broadcast messages


#### message A.B.1
```json
{
    "type": "action",
    "player": "Rick",
    "message": "Rick played assistant 5",
    "action": {
        "name": "play assistant",
        "args": {
            "value": 5
        }
    }
}
```

#### message A.B.2
```json
{
    "type": "action",
    "player": "Rick",
    "message": "Rick moved...",
    "action": {
        "name": "move student [to dining | to island]",
        "args": {
            "color": "GREEN",
            ["island": 3]
        }
    }
}
```

#### message A.B.3
```json
{
    "type": "action",
    "player": "Rick",
    "message": "Rick moved mother nature by 5 steps",
    "action": {
        "name": "move mother nature",
        "args": {
            "num_steps": 5
        }
    }
}
```

#### message A.B.4
```json
{
    "type": "action",
    "player": "Rick",
    "message": "Rick picked cloud 1",
    "action": {
        "name": "fill from cloud",
        "args": {
            "cloud": 1
        }
    }
}
```

### A.E - Action Error messages

#### message A.E.1
```json
{
    "type" : "action",
    "error" : {
        "status" : 1,
        "message" : "Wait for your turn"
    }
}
```
#### message A.E.2
```json
{
    "type" : "action",
    "error" : {
        "status" : 2,
        "message" : "This action is not valid"
    }
}
```