# TypeRacer

TypeRacer is a multiplayer online typing game. The player requested to complete a given Text as fast as possible.


## Run the Program (Client and Server)

- open terminal
- goto directory where the project is located 
- go to folder (/releases)
- run the following command: `java -jar client.jar & java -jar server.jar`


## Dependencies

The project requires [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)


The project can also built with `gradle`, version 5.6.4. The provided `gradlew` wrapper automatically downloads and uses
the correct gradle version.


## Building the Project from Source 

On Linux and Mac OS, run the following command from the project's root directory to compile the program,
run all checks and create an executable jar:

```
./gradlew build jar
```

On Windows, run the following command from the project's root directory to compile the program,
run all checks and create an executable jar:

```
./gradlew.bat build jar
```

If the command succeeds, the jars are found in `build/libs/client.jar` and `build/libs/server.jar`.
These jars can be executed with `java -jar build/libs/client.jar & java -jar build/libs/server.jar `


## Developers

Laura Moll, Tom Bintener, Xi Zhou

07.June.2021 - 04.July.2021

Ludwig-Maximilians-Universität München