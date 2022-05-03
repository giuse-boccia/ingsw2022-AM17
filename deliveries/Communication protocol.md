# Protocollo di comunicazione

This is a list of all possible messages that can be exchanged between client and server. We decided to use JSON to encode messages.

Every message has a **type** field which is either *ping/pong*, *login* or *action*

# Ping / pong

Every time a new client connects to the server, the latter sends a ping message and awaits a pong response. This also happens every x seconds, to ensure the client is still up. If a client connection is lost during the game, all other clients are notified and the application is closed gracefully.

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

### Fields
- The *action* field indicates the required action if the message is sent from the server, or the intended action if the message is sent from the client. If this field is not present, then the message is either an error message (error ≠ 0) or a brodcast message (error=0).
- The *username* and *num players* fields are arguments for the SET_USERNAME and CREATE_GAME message.
- The *error* field is a number. It indicates the error code an error occurred, or zero otherwise. This field is always present (it is omitted in this document in non-error messages for simplicity).
- The *displayText* field is not necessary for the functioning of the application, but can be printed in case of errors and makes the messages more human-readable

## After new client connection
When a new client connects to the server, the former sends a message containing his nickname. If the username is empty or already taken the server responds with an error and the connection is closed. If the username is correct, then:

- if a game is already in progress, the server responds with an error and the connection is closed
- if a game is present but not started the server adds him to the lobby and sends a broadcast message to everyone with the new players list
- if no game is present, one is automatically created and the server asks for the number of players

### L.1 - Creating a new game

```
+--------+                                         +--------+
| Client |                                         | Server |    
+--------+                                         +--------+
    |                                                   |
    |                     message L.1.1                 | 
    |                ----------------------->           | 
    |                                                   |
    |                     message L.1.2                 |
    |                <-----------------------           |
    |                                                   |
    |                     message L.1.3                 | 
    |                ----------------------->           |
    |                                                   |
    |                   [OK] message L.B.1              | 
    |              <-------------------------           |
    |                  [ERR] message L.E.*              |
    |              <-------------------------           |
    |                                                   |
```

#### message L.1.1
```json
{
    "status": "LOGIN",
    "action": "SET_USERNAME",
	"username": "Rick"
}
```
#### message L.1.2
```json
{
	"status": "LOGIN",
	"action": "CREATE_GAME",
	"displayText": "You are the first player. Set the game parameters"
}
```
#### message L.1.3
```json
{
	"status": "LOGIN",
	"action": "CREATE_GAME",
	"num players": 3,
	"expert": true
}
```

### L.2 - Joining an existing game

```
+--------+                                         +--------+
| Client |                                         | Server |    
+--------+                                         +--------+
    |                                                   |
    |                     message L.2.1                 | 
    |               ------------------------>           |
    |                                                   |
    |                   [OK] message L.B.2              | 
    |              <-------------------------           |
    |                  [ERR] message L.E.*              |
    |              <-------------------------           |
    |                                                   |
```

#### message L.2.1
```json
{
    "status": "LOGIN",
    "action": "SET_USERNAME",
    "username": "Rick"
}
```

### L.E - Login errors
Every message contains an “error” number field. If this field in not 0, then the message is an error message and the client must act accordingly (retry, gracefull termination, ...)
#### message L.E.1
```json
{
	"status" : "LOGIN",
	"error" : 1,
	"displayText" : "A game is already in progress. The connection will be closed"
}
```
#### message L.E.2
```json
{
	"status" : "LOGIN",
	"error" : 2,
	"displayText" : "An user with this username is already logged in. Please select another username"
}
```

#### message L.E.3
```json
{
	"status" : "LOGIN",
	"error" : 3,
	"displayText" : "Bad request"
}
```

### L.B - Login Broadcast messages
#### message L.B.1
```json
{
	"status": "LOGIN",
	"displayText": "A new player has joined",
	"game": {
		"players": ["Clod", "Rick"],
		"num layers": 3,
	}
}
```
#### message L.B.2
```json
{
	"status" : "LOGIN",
	"displayText" : "A new game is starting",
	"game": {
		"players": ["Clod", "Rick", "Giuse"],
		"numPlayers": 3,
	}
}
```

# Action
Action messages are used to communicate player choices from each client to the server. Every time a player performs a move a broadcast message is sent to every player. This functions as a confirmation message for the player who made the move and as an update for the other players.

### Fields

- The *player* field indicates which player is performing the move. A player is identified by his username.
- Messages from the server contain an *actions* field, which is an array of all the possible actions the player can perform
- Messages from the client and broadcast messages from the server contain a *action* field, which describes the action which has to made (or has been made in case of a broadcast message):
    - *action.name* indicates the type of the action to be performed (es: move mother nature)
    - *action.args* indicates the arguments of the action (es: a color and/or an island)
- The *error* field is a number. It indicates the error code an error occurred, or zero otherwise. This field is always present (it is omitted in this document in non-error messages for simplicity).
- The *displayText* field is not necessary for the functioning of the application, but can be printed in case of errors and makes the messages more human-readable

## Planning Phase

Every player has to play an assistant.

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
	"status": "ACTION",
	"player": "Rick",
	"actions": {
		["PLAY_ASSISTANT"]
	}
}
```
#### message A.1.1
```json
{
	"status": "ACTION",
	"player": "Rick",
	"action": {
		"name": "PLAY_ASSISTANT",
		"args": {
			"value": 5
		}
	}
}
```

## Action Phase

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
	"status": "ACTION",
	"player": "Rick",
	"actions": {
		["MOVE_STUDENT", "PLAY_CHARACTER"]
	}
}
```

#### message A.2.1a
```json
{
	"status": "ACTION",
	"player": "Rick",
	"action": {
		"name": "MOVE_STUDENT_TO_DINING",
		"args": {
			"color": "GREEN",
		}
	}
}
```

#### message A.2.1b
```json
{
	"status": "ACTION",
	"player": "Rick",
	"action": {
		"name": "MOVE_STUDENT_TO_ISLAND",
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
	"status": "ACTION",
	"player": "Rick",
	"actions": {
		["MOVE_MN", "PLAY_CHARACTER"]
	}
}
```
#### message A.2.2
```json
{
	"status": "ACTION",
	"player": "Rick",
	"action": {
		"name": "MOVE_MN",
		"args": {
			"num_steps": 5
		}
	}
}
```

#### message A.0.4
```json
{
	"status": "ACTION",
	"player": "Rick",
	"actions": {
		["FILL_FROM_CLOUD", "PLAY_CHARACTER"]
	}
}
```
#### message A.2.3
```json
{
	"status": "ACTION",
	"player": "Rick",
	"action": {
		"name": "FILL_FROM_CLOUD",
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
	"status": "ACTION",
	"player": "Rick",
	"displayText": "Rick played assistant 5",
	"action": {
		"name": "PLAY_ASSISTANT",
		"args": {
			"value": 5
		}
	}
}
```

#### message A.B.2
```json
{
	"status": "ACTION",
	"player": "Rick",
	"displayText": "Rick moved...",
	"action": {
		"name": "MOVE_STUDENT_TO_ISLAND",
		"args": {
			"color": "GREEN"
			"island": 0
		}
	}
}
```

#### message A.B.3
```json
{
	"status": "ACTION",
	"player": "Rick",
	"displayText": "Rick moved mother nature by 5 steps",
	"action": {
		"name": "MOVE_MN",
		"args": {
			"num_steps": 5
		}
	}
}

```

#### message A.B.4
```json
{
	"status": "ACTION",
	"player": "Rick",
	"displayText": "Rick picked cloud 1",
	"action": {
		"name": "FILL_FROM_CLOUD",
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
	"status" : "ACTION",
	"error" : 1,
	"displayText" : "Invalid action name"
}
```
#### message A.E.2
```json
{
	"status" : "ACTION",
	"error" : 2,
	"displayText" : "Bad args"	
}
```
