# PretendYoureXyzzyReborn
This is a clone of ajanata/PretendYoureXyzzy, but better.

This version of the game server can run on every platform that supports Java. It uses Websockets to communicate.

# Get up and running
All you need is a machine with Java installed and a copy of the cards database of PYX (can be found in the repo).
## Server
The server is command-line only. All you need to specify is the path of the cards database. To customize the settings look at the [Config](https://github.com/devgianlu/PretendYoureXyzzyReborn/server/src/com/gianlu/pyxreborn/server/Config.java) class.
To run the packed JAR file:

    java -jar pyx-server.jar --cards-db ./pyx.sqlite
    
By default the server will start on port 6969. To change it:

    java -jar pyx-server.jar --cards-db ./pyx.sqlite --server-port 1234
    
## Client
The client can be run either in command-line mode or in GUI mode.
### Command-line mode
To start the client in command-line mode use the --console argument:

    java -jar pyx-client.jar --console
    
**The console mode is recommended for testing purposes only.** The game is not playable on the console.
### GUI mode
To play the game you would likely start the client in GUI mode. To do that you can execute the JAR like so:

    java -jar pyx-client.jar
    
Once the client is started you'll be prompted to input a server address and a nickname. The server address will be something like this:

    ws://localhost:9696/
    
**Note the ws://, that's important as we are communicating over Websockets!**

