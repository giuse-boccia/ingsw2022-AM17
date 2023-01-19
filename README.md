# Prova Finale Ingegneria del Software 2022 

## Group AM17
| Name     | Surname    | Email                              |  GitHub                                            |
|:---------|:-----------|:-----------------------------------|:--------------------------------------------------|
| Claudio  | Arione     | claudio.arione@mail.polimi.it      | [claudioarione](https://github.com/claudioarione) |
| Riccardo | Begliomini | riccardo.begliomini@mail.polimi.it | [iVoid73](https://github.com/iVoid73)             |
| Giuseppe | Boccia     | giuseppe.boccia@mail.polimi.it     | [giuse-boccia](https://github.com/giuse-boccia)   |

## Progress
| Functionality    | Advanced | State |
| :--------------- | :------: | :---: |
| Complete rules   | No       | 🟢 |
| Socket           | No       | 🟢 |
| GUI              | No       | 🟢 |
| CLI              | No       | 🟢 |
| 12 Characters    | Yes      | 🟢 |
| 4 Players Game   | Yes      | 🟢 |
| Multiple Games   | Yes      | ⚫ |
| Persistence      | Yes      | 🟢 |
| Disconnection resilience      | Yes      | ⚫ |

🔴 -> Not started

🟡 -> In progress

🟢 -> Complete

⚫ -> Will not implement

### Extra features
- **Random nickname generator**: allows the user to participate in a match with a random-chosen username. This is possible either clicking on the apposite button
in GUI or leaving blank the *username* field when asked
- **Internationalization**: it's possible to play Eriantys in different languages - currently Italian and English - according to the choice of the user.
Game language can be chosen from the apposite popup in the login screen (for GUI) or setting a language tag (two letters) in the *game_language* field in the `settings.json` file
- **Game chat**: in GUI, players can communicate through a chat panel which opens up when the corresponding button - in the bottom right of the screen - is pressed
- **Basic security of the communication**: the user, playing accordingly to the game rules, cannot send "wrong" messages, but, in any case, the server blocks corrupted messages. For example, messages coming from not logged-in users are blocked (even if they pretend to be a logged-in player)
 
## Instructions for use
To use the application launch the JAR file from a terminal (Linux, MacOS) or Powershell (Windows).
> **Note**
> We cannot ensure an optimal CLI game experience on Windows Command Prompt (cmd.exe)  
```
java -jar ./deliveries/Jar/Eriantys.jar [SERVER | GUI | CLI] [PORT] [ADDRESS]
```
> **Warning**        
> JDK 18 is required to run the application - to install it you can follow the steps in [Installation](https://github.com/giuse-boccia/ingsw2022-AM17/wiki/Installation) page 
       
> **Note**
>  In the [/deliveries/jar](https://github.com/giuse-boccia/ingsw2022-AM17/tree/main/deliveries/jar) folder you will find `Eriantys.jar` and another folder containing `Eriantys_b4m1.jar`. `Eriantys.jar` is tested and working on Windows, Linux and MacOs with M1 architecture; however, this jar can show compatibility problems on MacOs with older architectures. `Eriantys_b4m1.jar` works fine on all Intel Apple PCs.   

> 🔧 In case of compatibility problems with provided *jar* files just clone the repository and run 
 ` mvn package `  
 
All arguments are optional. 
- If the `[SERVER | GUI | CLI]` is omitted than GUI is launched
- If both the `PORT` and `ADDRESS` parameters are omitted then those values are taken from the configuration file in `settings.json`. An example `settings.json` file can be found in `settings.json`
### Server
To launch the server using the port found in `settings.json`
```
java -jar ./deliveries/Jar/Eriantys.jar server
```
Alternatively the port can be provided by argument. For example, to launch a server
listening on port 7373:
```
java -jar ./deliveries/Jar/Eriantys.jar server 7373
```

### Client (CLI)
To launch the cli using the server port and server address found in `settings.json`
```
java -jar ./deliveries/Jar/Eriantys.jar cli
```
Alternatively port and address can be provided by argument. For example, to launch a CLI client
searching for a server at 192.168.1.10:7373 (note: local address, to play from different LANs port forwarding is required)
```
java -jar ./deliveries/Jar/Eriantys.jar cli 7373 192.168.1.10
```

### Client (GUI)
To launch the gui using the server port and server address found in `settings.json`
```
java -jar ./deliveries/Jar/Eriantys.jar gui
```

Alternatively port and address can be provided by argument. For example, to launch a CLI client
searching for a server at 192.168.1.10:7373 (note: local address, to play from different LANs port forwarding is required)
Note: if port and address are provided as arguments (without using the settings.json file) the gui argument cannot be omitted
```
java -jar ./deliveries/Jar/Eriantys.jar cli 7373 192.168.1.10
```


## Test coverage
| Package    |        Method  |           Line |
|:-----------|---------------:|---------------:|
| Model      |  96% (235/243) |  95% (813/849) |
| Controller |  92% (50/54)   |  90% (420/462) |
| Exceptions |  100% (12/12)  |  100% (12/12)  |
