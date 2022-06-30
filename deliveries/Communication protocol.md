# Protocollo di comunicazione

This is a list of all possible messages that can be exchanged between client and server. We decided to use JSON to encode messages.

Every message has a **type** field which is either *ping/pong*, *login*, *action*, *end* or *update*.

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
- The *expert* field indicates whether the game in in expert or non expert mode. It is required for the CREATE_GAME message.
- The *error* field is a number. It indicates the error code an error occurred, or zero otherwise. This field is always present (it is omitted in this document in non-error messages for simplicity).
- The *displayText* field is not necessary for the functioning of the application, but can be printed in case of errors and makes the messages more human-readable
- The *gameLobby* field contains all the players who already logged in and it is sent in a broadcast message to the ones who are waiting for the game to start, either if a new game is starting or if a game is being loaded from file
- The *languageTag* is a string indicating the *Locale* of the client sending the message. Using this field the server will be able to send every user all the messages in their correct language 

## After new client connection
When a new client connects to the server, the former sends a message containing their nickname. If the username is empty or already taken the server responds with an error and the connection is closed. If the username is correct, then:

- if a game is already in progress, the server responds with an error and the connection is closed
- if a game is present but not started the server adds him to the lobby and sends a broadcast message to everyone with the new players list
- if no game is present, the player can either create a new game or ask the server to load a previous save

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
	"username": "Rick",
	"languageTag": "en"
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
    "username": "Rick",
    "languageTag": "en"
}
```

### L.3 - Load previous save

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
    |                     message L.3.3                 | 
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
	"username": "Rick",
	"languageTag": "en"
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
#### message L.3.3
```json
{
	"status": "LOGIN",
	"action": "LOAD_GAME"
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
#### message L.E.4
```json
{
	"status" : "LOGIN",
	"action" : "CREATE_GAME",
	"error" : 4,
	"displayText" : "Missing or corrupted save file, create a new game"
}
```
#### message L.E.5
This message is displayed if an user tries to load a game but the loaded game doesn't have a player with the same username
```json
{
	"status" : "LOGIN",
	"error" : 5,
	"displayText" : "Username doesn't match: login with the same username or create a new game"
}
```

### L.B - Login Broadcast messages
#### message L.B.1
```json
{
	"status": "LOGIN",
	"displayText": "A new player has joined",
	"lobby": {
		"isSaved": false,
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
		"isSaved": false,
		"players": ["Clod", "Rick", "Giuse"],
		"numPlayers": 3,
	}
}
```
#### message L.B.3
```json
{
	"status" : "LOGIN",
	"displayText" : "Loading a saved game",
	"game": {
		"isSaved": true,
		"playersFromSavedGame": ["Clod", "Rick", "Giuse"],
		"players": ["Clod", "Rick", "Giuse"],
		"numPlayers": 3,
	}
}
```

# Action
Action messages are used to communicate player choices from each client to the server. Every time a player performs a move a broadcast message is sent to every player. This functions as a confirmation message for the player who made the move and as an update for the other players.

### Fields

- The *player* field indicates which player is performing the move. A player is identified by their username.
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

# Update
Update messages are sent to every client every time the game model changes (i.e. after a player action) 

### Broadcast messages

#### message A.B.1
```json
{
	"status": "UPDATE",
	"player": "Rick",
	"displayText": "Rick played assistant 5",
	"game_status": {...}
}
```
#### message A.B.2
```json
{
	"status": "UPDATE",
	"player": "Rick",
	"displayText": "Rick moved...",
	"game_status": {...}
}
```
#### message A.B.3
```json
{
	"status": "UPDATE",
	"player": "Rick",
	"displayText": "Rick moved mother nature by 5 steps",
	"game_status": {...}
}

```
#### message A.B.4
```json
{
	"status": "UPDATE",
	"player": "Rick",
	"displayText": "Rick picked cloud 1",
	"game_status": {...}
}
```

### Game status
The *game_status* field in every update message is a JSON rappresentation of the game state, here is an example of a game status object:

```json
{
    "roundsPlayed": 0,
    "bag": [
        { "color": "RED" },
        { "color": "GREEN" },
        { "color": "PINK" },
        ...
    ],
    "roundState": {
        "playedAssistants": [
            { "numSteps": 2, "value": 4, "playerName": "giuse" },
            { "numSteps": 3, "value": 5, "playerName": "rick" }
        ],
        "firstPlayerIndex": 1,
        "currentAssistantIndex": 0,
        "isLastRound": false
    },
    "isExpert": true,
    "MNIndex": 4,
    "players": [
        {
            "name": "rick",
            "towerColor": "WHITE",
            "remainingTowers": 8,
            "numCoins": 1,
            "assistants": [1, 2, 3, 4, 6, 7, 8, 9, 10],
            "entrance": [
                { "color": "RED" },
                { "color": "RED" },
                { "color": "PINK" },
                { "color": "YELLOW" },
                { "color": "GREEN" },
                { "color": "PINK" },
                { "color": "BLUE" }
            ],
            "dining": [],
            "ownedProfessors": []
        },
        {
            "name": "giuse",
            "towerColor": "BLACK",
            "remainingTowers": 8,
            "numCoins": 1,
            "assistants": [1, 2, 3, 5, 6, 7, 8, 9, 10],
            "entrance": [
                { "color": "YELLOW" },
                { "color": "PINK" },
                { "color": "PINK" },
                { "color": "GREEN" },
                { "color": "PINK" },
                { "color": "GREEN" },
                { "color": "BLUE" }
            ],
            "dining": [],
            "ownedProfessors": []
        }
    ],
    "islands": [
        {
            "students": [{ "color": "BLUE" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        {
            "students": [{ "color": "PINK" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        {
            "students": [{ "color": "BLUE" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        { "students": [{ "color": "RED" }], "noEntryNum": 0, "numOfTowers": 0 },
        { "students": [], "noEntryNum": 0, "numOfTowers": 0 },
        { "students": [{ "color": "RED" }], "noEntryNum": 0, "numOfTowers": 0 },
        {
            "students": [{ "color": "YELLOW" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        {
            "students": [{ "color": "GREEN" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        {
            "students": [{ "color": "PINK" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        {
            "students": [{ "color": "GREEN" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        },
        { "students": [], "noEntryNum": 0, "numOfTowers": 0 },
        {
            "students": [{ "color": "YELLOW" }],
            "noEntryNum": 0,
            "numOfTowers": 0
        }
    ],
    "characters": [
        {
            "characterName": "noEntry",
            "cost": 2,
            "hasCoin": false,
            "noEntryNum": 4
        },
        {
            "characterName": "ignoreTowers",
            "cost": 3,
            "hasCoin": false,
            "noEntryNum": 0
        },
        {
            "characterName": "everyOneMove3FromDiningRoomToBag",
            "cost": 3,
            "hasCoin": false,
            "noEntryNum": 0
        }
    ],
    "clouds": [
        {
            "students": [
                { "color": "RED" },
                { "color": "PINK" },
                { "color": "BLUE" }
            ],
            "maxStudents": 3
        },
        {
            "students": [
                { "color": "YELLOW" },
                { "color": "YELLOW" },
                { "color": "RED" }
            ],
            "maxStudents": 3
        }
    ]
}
```
# End

A message with an END status is sent to every logged user when the game is finished.

### Fields
- The *status* field is always set to END
- The *displayText* field shows every user a win or defeat message - in the latter there are the username(s) of the player(s) who won
- The *gameState* field updates the current GameState to show the user the final state of the just finished game

```
+--------+                                        +--------+
| Client |                                        | Server |    
+--------+                                        +--------+
    |                                                  |
    |                  [WIN] message E.W               |
    |             <-------------------------           |
    |                 [LOSS] message E.D               |
    |             <-------------------------           |
```

#### message E.W
```json
{
	"status": "END",
	"displayText": "Congratulations, you won the game!",
	"gameState": {...}
}
```
#### message E.D
```json
{
	"status": "END",
	"displayText": "Unfortunately you lost. Clod and Giuse won the game",
	"gameState": {...}
}
```
