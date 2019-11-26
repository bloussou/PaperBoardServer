# PaperboardServer
Interactive paperboard websocket server. 

## Install and run
- clone the repo
- download the maven dependencies written in the pom.xml file
- start the server with run
- setup your jdk to 11.0.4
- Run the app in your IDE, should listen socket request on localhost:8025

## BEFORE USE before november 26th
DO `git pull`

## Run the tests
Use your IDE to run the different junit test and understand what they are doing.

## Project description

### Folders and packages 

* **src** : Folder containing the code
    * **main/java/com.paperboard** :  package paperboard
        * *drawings* : Package containing all the drawings class and a subpackage shape
            * Drawing : Abstract class, all the drawings extends this class
            * *shapes* : Package containing the shapes (object with line, you can set the color, change line style
             and line width)
                * Shape : Abstract class, all the shapes extends this class
                * ...
            * DrawingType : enum of the different possible shapes
            * ModificationType : enum of the different editions you can do on Drawings
            * ...
         * *server* : Package containing server code
            * *error* : Package containing customized Exception
            * *events* : Package containing Event management
                * EventType : enum of the different eventType
                * ...
            * *socket* : Package containing server code
                * MessageType : Enum containing 
                * ...
            * Paperboard : PaperboardClass
            * PaperboardApplication : main class
            * User : User Class
- **test/java** : contains the junit test
    
### Architecture and Design

#### Observer Pattern
#### Socket message logic
#### Drawings heritage

### Technical choices

#### Socket with strings 


## Route HTTP
- get pseudo availability
- get all rooms available
- create rooms
- export board

## Socket
- join room
- draw
- edit
- Delete
- move ?
- leave

