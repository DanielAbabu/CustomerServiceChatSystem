# CustomerServiceChatSystem
This repository contains a simple customer service chat system implemented in Java. The system consists of a multi-threaded chat server that can handle multiple clients simultaneously and an admin interface that allows an administrator to respond to clients individually.

## Instructions to Run
Compile all Java files:
```
   javac ChatServer.java ClientHandler.java AdminHandler.java ChatClient.java
```
Run the ChatServer:
```
java ChatServer
```

Now open multiple terminals and run the ChatClient in each to simulate multiple clients:
```
java ChatClient
```
